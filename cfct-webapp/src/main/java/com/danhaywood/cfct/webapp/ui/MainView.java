package com.danhaywood.cfct.webapp.ui;

import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.ComparisonRowStatus;
import com.danhaywood.cfct.model.ComparisonRowView;
import com.danhaywood.cfct.model.TableComparisonViewResult;
import com.danhaywood.cfct.model.TableRef;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;
import com.danhaywood.cfct.service.ComparisonProgressEvent;
import com.danhaywood.cfct.service.ComparisonProgressPhase;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContext;
import com.danhaywood.cfct.webapp.auth.AuthenticatedConnectionContextHolder;
import com.danhaywood.cfct.webapp.auth.ConnectionLoginRequest;
import com.danhaywood.cfct.webapp.auth.WebappAuthenticationService;
import com.danhaywood.cfct.webapp.comparison.WebappComparisonExecutionService;
import com.danhaywood.cfct.webapp.config.WebappComparisonProperties;
import com.danhaywood.cfct.webapp.selection.CommandCatalogEntry;
import com.danhaywood.cfct.webapp.selection.CommandSelectionState;
import com.danhaywood.cfct.webapp.selection.CommandDrivenTableSelectionService;
import com.danhaywood.cfct.webapp.selection.ManualTableSelectionState;
import com.danhaywood.cfct.webapp.selection.SqlServerCommandCatalogService;
import com.danhaywood.cfct.webapp.selection.SqlServerTableCatalogService;
import com.danhaywood.cfct.webapp.selection.TableCatalogEntry;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationStatusHolder;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.ColorScheme;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Route("")
@CssImport("./styles/comparison-grid.css")
public class MainView extends AppLayout implements BeforeEnterObserver {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String PROGRESS_STYLE_NEUTRAL = "comparison-progress-summary-neutral";
    private static final String PROGRESS_STYLE_SUCCESS = "comparison-progress-summary-success";
    private static final String PROGRESS_STYLE_FAILURE = "comparison-progress-summary-failure";

    private ManualTableSelectionState selectionState;
    private CommandSelectionState commandSelectionState;
    private final WebappComparisonExecutionService comparisonExecutionService;
    private final AuthenticatedConnectionContextHolder authenticatedContextHolder;
    private final WebappAuthenticationService authenticationService;
    private final SqlServerTableCatalogService tableCatalogService;
    private final SqlServerCommandCatalogService commandCatalogService;
    private final CommandDrivenTableSelectionService commandDrivenTableSelectionService;
    private final ConnectionValidationStatusHolder statusHolder;
    private final WebappComparisonProperties properties;

    private final List<TableCatalogEntry> tableCatalogEntries = new ArrayList<>();
    private final List<CommandCatalogEntry> commandCatalogEntries = new ArrayList<>();
    private final ListDataProvider<TableCatalogEntry> selectionDataProvider;
    private final ListDataProvider<CommandCatalogEntry> commandSelectionDataProvider;
    private final Dialog loginDialog = new Dialog();

    private final Button compareButton = new Button("Compare");
    private final Span compareProgressCounter = new Span();
    private final Button clearSelectionsButton = new Button("Clear");
    private final Span comparisonError = new Span();
    private final Div comparisonResultsContainer = new Div();
    private final TextField comparedTableFilter = new TextField();
    private final Checkbox differencesOnlyFilter = new Checkbox("Diffs only");
    private final Checkbox selectedOnlyFilter = new Checkbox("Selected only");
    private final Anchor downloadAction = new Anchor();
    private final Select<DownloadFormat> downloadFormatSelect = new Select<>();
    private final HorizontalLayout resultActions = new HorizontalLayout();

    private final Span connectionDatabases = new Span();
    private final Span comparisonProgressSummary = new Span();
    private final MenuBar accountMenu = new MenuBar();
    private Grid<CommandCatalogEntry> commandSelectionGrid;
    private Grid<TableCatalogEntry> tableSelectionGrid;
    private DateTimePicker commandBaselineFilterField;
    private String focusedCommandInteractionId;
    private String commandRangeAnchorInteractionId;
    private String skipNextCommandCheckboxClientEventInteractionId;
    private TableRef focusedBusinessTable;

    private WebappComparisonExecutionService.ComparisonExecutionOutcome latestOutcome;
    private int drawerWidthPx = 512;
    private String tableFilterValue = "";
    private final Set<TableRef> completedTablesInActiveRun = new LinkedHashSet<>();
    private int compareProgressTotal;
    private String commandMemberFilterValue = "";
    private String commandInteractionFilterValue = "";
    private LocalDateTime commandBaselineFilterTimestamp;
    private final Set<String> commandReplayStateFilters = new LinkedHashSet<>();
    private static final List<String> REPLAY_STATE_FILTER_OPTIONS = List.of("OK", "PENDING", "FAILED");
    private static final int MIN_DRAWER_WIDTH_PX = 360;
    private static final int MAX_DRAWER_WIDTH_PX = 860;

    public MainView(
            final ConnectionValidationStatusHolder statusHolder,
            final SqlServerTableCatalogService tableCatalogService,
            final SqlServerCommandCatalogService commandCatalogService,
            final WebappComparisonProperties properties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder,
            final WebappAuthenticationService authenticationService) {
        this(
                statusHolder,
                tableCatalogService,
                commandCatalogService,
                new CommandDrivenTableSelectionService(),
                properties,
                comparisonExecutionService,
                authenticatedContextHolder,
                authenticationService);
    }

    @Autowired
    public MainView(
            final ConnectionValidationStatusHolder statusHolder,
            final SqlServerTableCatalogService tableCatalogService,
            final SqlServerCommandCatalogService commandCatalogService,
            final CommandDrivenTableSelectionService commandDrivenTableSelectionService,
            final WebappComparisonProperties properties,
            final WebappComparisonExecutionService comparisonExecutionService,
            final AuthenticatedConnectionContextHolder authenticatedContextHolder,
            final WebappAuthenticationService authenticationService) {
        this.comparisonExecutionService = comparisonExecutionService;
        this.authenticatedContextHolder = authenticatedContextHolder;
        this.authenticationService = authenticationService;
        this.tableCatalogService = tableCatalogService;
        this.commandCatalogService = commandCatalogService;
        this.commandDrivenTableSelectionService = commandDrivenTableSelectionService;
        this.statusHolder = statusHolder;
        this.properties = properties;

        if (authenticatedContextHolder.isAuthenticated()) {
            tableCatalogEntries.addAll(tableCatalogService.discoverTableCatalog());
            commandCatalogEntries.addAll(sortCommandCatalog(commandCatalogService.discoverCommandCatalog()));
        }
        this.selectionState = new ManualTableSelectionState(tableCatalogEntries);
        this.commandSelectionState = new CommandSelectionState(commandCatalogEntries);
        this.selectionDataProvider = new ListDataProvider<>(tableCatalogEntries);
        this.commandSelectionDataProvider = new ListDataProvider<>(commandCatalogEntries);
        applyCommandDrivenSelection();

        setPrimarySection(Section.DRAWER);
        getElement().setAttribute("data-testid", "main-app-layout");
        addToNavbar(buildDrawerToggle(), buildNavbarBranding(), buildNavbarSpacer(), buildAccountMenu());
        addToDrawer(buildSelectionPanel());
        setContent(buildMainContent());

        configureLoginDialog();
        refreshConnectionFooter();
        refreshAuthUiState();
        renderEmptyComparisonState();
    }

    public List<String> selectedCommandInteractionIdsForStageOne() {
        return commandSelectionState.selectedInteractionIds();
    }

    public List<TableRef> selectedTablesForStageTwo() {
        return selectionState.selectedTables();
    }

    private DrawerToggle buildDrawerToggle() {
        final DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Open navigation menu");
        toggle.getElement().setAttribute("data-testid", "hamburger-menu");
        toggle.getElement().setAttribute("title", "Open navigation menu");
        return toggle;
    }

    private Component buildNavbarBranding() {
        final HorizontalLayout branding = new HorizontalLayout();
        branding.getElement().setAttribute("data-testid", "navbar-branding");
        branding.setPadding(false);
        branding.setSpacing(true);
        branding.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        final Image logo = new Image("/images/cfct-logo.png", "CFCT logo");
        logo.getElement().setAttribute("data-testid", "navbar-branding-logo");
        logo.setWidth("24px");
        logo.setHeight("24px");

        final Span name = new Span("CFCT");
        name.getElement().setAttribute("data-testid", "navbar-branding-name");
        name.getStyle().set("font-weight", "800");

        branding.add(logo, name);
        return branding;
    }

    private Component buildNavbarSpacer() {
        final Div spacer = new Div();
        spacer.getStyle().set("flex-grow", "1");
        return spacer;
    }

    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        refreshAuthUiState();
    }

    @Override
    public void beforeEnter(final BeforeEnterEvent event) {
        refreshAuthUiState();
    }

    private VerticalLayout buildMainContent() {
        final VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setSpacing(true);
        content.getStyle()
                .set("gap", "var(--lumo-space-l)")
                .set("padding-bottom", "5rem");
        content.getElement().setAttribute("data-testid", "main-content");
        content.add(buildComparisonStage(), buildFooter());
        return content;
    }

    private Component buildComparisonStage() {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "comparison-stage-placeholder");
        panel.addClassName("comparison-stage-panel");
        panel.getStyle()
                .setPadding("var(--lumo-space-l. 1em)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "var(--lumo-border-radius-m)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "var(--lumo-space-m)")
                .set("width", "100%")
                .set("min-width", "0");

        comparedTableFilter.setPlaceholder("Filter compared tables");
        comparedTableFilter.setClearButtonVisible(true);
        comparedTableFilter.setValueChangeMode(ValueChangeMode.EAGER);
        comparedTableFilter.getElement().setAttribute("data-testid", "comparison-table-filter");
        comparedTableFilter.addValueChangeListener(event -> renderComparisonTabs());

        differencesOnlyFilter.getElement().setAttribute("data-testid", "comparison-differences-only-filter");
        differencesOnlyFilter.setValue(false);
        differencesOnlyFilter.getStyle().setPaddingLeft(".3em");
        differencesOnlyFilter.getStyle().setPaddingRight(".3em");
        differencesOnlyFilter.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        differencesOnlyFilter.addValueChangeListener(event -> renderComparisonTabs());

        downloadFormatSelect.setItems(DownloadFormat.JSON, DownloadFormat.YAML, DownloadFormat.EXCEL);
        downloadFormatSelect.setItemLabelGenerator(DownloadFormat::label);
        downloadFormatSelect.setValue(DownloadFormat.JSON);
        downloadFormatSelect.getElement().setAttribute("data-testid", "download-format-select");
        downloadFormatSelect.addValueChangeListener(event -> refreshDownloadLinks());

        downloadAction.getElement().setAttribute("data-testid", "download-action");
        downloadAction.add(new Button("Download"));
        downloadAction.getElement().setAttribute("download", true);

        resultActions.getElement().setAttribute("data-testid", "comparison-result-actions");
        resultActions.addClassName("comparison-result-actions");
        resultActions.add(comparedTableFilter, differencesOnlyFilter, downloadFormatSelect, downloadAction);
        resultActions.expand(comparedTableFilter);
        resultActions.setVisible(false);
        resultActions.setPadding(true);

        comparisonError.getElement().setAttribute("data-testid", "comparison-stage-error");
        comparisonError.getStyle().set("color", "var(--lumo-error-text-color)");

        comparisonResultsContainer.getElement().setAttribute("data-testid", "comparison-results-container");
        comparisonResultsContainer.addClassName("comparison-results-container");
        comparisonResultsContainer.setWidthFull();

        panel.add(resultActions, comparisonError, comparisonResultsContainer);
        return panel;
    }

    private MenuBar buildAccountMenu() {
        accountMenu.getElement().setAttribute("data-testid", "account-menu");

        final MenuItem accountItem = accountMenu.addItem("Account");
        accountItem.getElement().setAttribute("data-testid", "account-menu-label");
        final MenuItem logoutItem = accountItem.getSubMenu().addItem("Logout", event -> handleLogout());
        logoutItem.getElement().setAttribute("data-testid", "logout-menu-item");

        return accountMenu;
    }

    private Footer buildFooter() {
        final Footer footer = new Footer();
        footer.getElement().setAttribute("data-testid", "connection-details-footer");
        footer.getStyle()
                .set("position", "fixed")
                .set("left", "0")
                .set("right", "0")
                .set("bottom", "0")
                .set("z-index", "100")
                .setHeight("2em")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-l, 1em)")
                .set("padding", "var(--lumo-space-s, 1em) var(--lumo-space-l, 1em)")
                .set("background", "var(--lumo-base-color)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        final Div connectionDetails = new Div();
        connectionDetails.getElement().setAttribute("data-testid", "connection-details-inline");
        connectionDetails.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)");

        connectionDatabases.getElement().setAttribute("data-testid", "connection-database-pair");
        connectionDetails.add(connectionDatabases);

        final Div statusPanel = new Div();
        statusPanel.getElement().setAttribute("data-testid", "connection-status-panel");
        statusPanel.getStyle()
                .set("display", "inline-flex")
                .set("justify-content", "flex-end")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-l)")
                .set("padding-left", "var(--lumo-space-l, .5em)")
                .set("padding-right", "var(--lumo-space-s .5em)")
                .set("margin-left", "auto");

        comparisonProgressSummary.getElement().setAttribute("data-testid", "comparison-progress-summary");
        comparisonProgressSummary.getStyle().set("padding", "0.15rem 0.5rem");
        comparisonProgressSummary.getStyle().set("border-radius", "var(--lumo-border-radius-s)");
        statusPanel.add(comparisonProgressSummary);

        footer.add(connectionDetails, statusPanel);
        return footer;
    }

    private Div buildSelectionPanel() {
        final Div panel = new Div();
        panel.getElement().setAttribute("data-testid", "table-selection-panel");
        panel.getElement()
                .addEventListener("keydown", event -> handleEnterShortcut(
                        asText(event.getEventData().get("event.target.tagName")),
                        asText(event.getEventData().get("event.target.getAttribute('type')"))))
                .setFilter("event.key === 'Enter'")
                .addEventData("event.target.tagName")
                .addEventData("event.target.getAttribute('type')");
        panel.getStyle()
                .set("width", drawerWidthPx + "px")
                .set("min-width", MIN_DRAWER_WIDTH_PX + "px")
                .set("max-width", MAX_DRAWER_WIDTH_PX + "px")
                .set("max-width", "100%")
                .set("box-sizing", "border-box")
                .set("height", "100%")
                .set("position", "relative");

        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle()
                .set("gap", "var(--lumo-space-m)")
                .set("height", "100%")
                .set("padding-bottom", "4.5rem")
                .set("overflow", "auto");

        final DateTimePicker baselineFilter = buildCommandBaselineFilterField();
        final Grid<CommandCatalogEntry> commandGrid = buildCommandSelectionGrid();

        compareProgressCounter.getElement().setAttribute("data-testid", "compare-progress-counter");
        compareProgressCounter.getStyle().set("color", "var(--lumo-secondary-text-color)");
        compareProgressCounter.getStyle().set("display", "inline-flex");
        compareProgressCounter.getStyle().set("align-items", "center");
        compareProgressCounter.getStyle().set("padding-right", "var(--lumo-space-s)");
        compareProgressCounter.setVisible(false);

        final Div actionBar = new Div(compareProgressCounter, compareButton);
        actionBar.getElement().setAttribute("data-testid", "navigation-compare-action-bar");
        actionBar.addClassName("navigation-compare-action-bar");
        actionBar.getStyle()
                .setDisplay(Style.Display.FLEX)
                .set("align-items", "center")
                .setJustifyContent(Style.JustifyContent.FLEX_END)
                .setWidth("100%")
                .set("position", "sticky")
                .set("bottom", "0")
                .setZIndex(3)
                .setBackground("var(--lumo-base-color)")
                .setPaddingTop("var(--lumo-space-m, .3em)")
                .set("padding-bottom", "calc(var(--lumo-space-m) + 3.2rem)")
                .set("margin-top", "var(--lumo-space-xs)");

        compareButton.getElement().setAttribute("data-testid", "compare-button");
        compareButton.getElement().setAttribute("data-default-action", "compare");
        compareButton.getElement().setAttribute("title", "Execute comparison for selected eligible tables.");
        compareButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        compareButton.addClickListener(event -> executeComparison());

        clearSelectionsButton.getElement().setAttribute("data-testid", "clear-selections-button");
        clearSelectionsButton.getElement().setAttribute("title", "Clear command and business table selections.");
        clearSelectionsButton.addClickListener(event -> clearAllSelections());

        final Div clearActionBar = new Div(clearSelectionsButton);
        clearActionBar.getElement().setAttribute("data-testid", "command-clear-action-bar");
        clearActionBar.getStyle()
                .set("display", "flex")
                .setPaddingTop("var(--lumo-space-m, .3em)")
                .set("justify-content", "flex-end")
                .set("width", "100%");

        final TextField tableFilter = new TextField();
        tableFilter.setPlaceholder("Filter table");
        tableFilter.setClearButtonVisible(true);
        tableFilter.setValueChangeMode(ValueChangeMode.EAGER);
        tableFilter.getElement().setAttribute("data-testid", "table-filter-table");
        tableFilter.setWidthFull();
        tableFilter.addValueChangeListener(event -> {
            tableFilterValue = event.getValue();
            clearComparisonProgressStatus();
            applySelectionFilters();
        });

        selectedOnlyFilter.getElement().setAttribute("data-testid", "selected-only-checkbox");
        selectedOnlyFilter.setValue(selectionState.isSelectedOnly());
        selectedOnlyFilter.addValueChangeListener(event -> {
            selectionState.setSelectedOnly(Boolean.TRUE.equals(event.getValue()));
            applySelectionFilters();
        });

        final Grid<TableCatalogEntry> tableGrid = buildSelectionGrid();

        final HorizontalLayout tableFilterRow = new HorizontalLayout(tableFilter, selectedOnlyFilter);
        tableFilterRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        tableFilterRow.expand(tableFilter);
        tableFilterRow.setWidthFull();
        tableFilterRow.setPadding(true);

        final Div commandSelectionSpacer = new Div();
        commandSelectionSpacer.getElement().setAttribute("data-testid", "command-selection-spacer");
        commandSelectionSpacer.getStyle().setHeight("0.25rem");

        final Div resizeHandle = new Div();
        resizeHandle.getElement().setAttribute("data-testid", "navigation-drawer-resize-handle");
        resizeHandle.addClassName("navigation-drawer-resize-handle");
        enableDrawerResize(panel, resizeHandle);

        layout.add(baselineFilter, commandGrid, commandSelectionSpacer, clearActionBar, tableFilterRow, tableGrid, actionBar);
        panel.add(layout, resizeHandle);
        applySelectionFilters();
        applyCommandFilters();
        return panel;
    }

    private Grid<TableCatalogEntry> buildSelectionGrid() {
        final Grid<TableCatalogEntry> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "table-selection-grid");
        grid.getElement().setAttribute("tabindex", "0");
        grid.setAllRowsVisible(false);
        grid.setHeight("6rem");
        grid.setWidthFull();
        this.tableSelectionGrid = grid;
        grid.setPartNameGenerator(this::selectionRowPartName);
        grid.setDataProvider(selectionDataProvider);

        grid.addColumn(new ComponentRenderer<>(entry -> {
            final Checkbox checkbox = new Checkbox();
            checkbox.setEnabled(entry.eligible() && authenticatedContextHolder.isAuthenticated());
            checkbox.setValue(selectionState.isSelected(entry.table()));
            checkbox.getElement().setAttribute("data-testid", "table-checkbox-" + selectorToken(entry.table()));
            if (!entry.eligible()) {
                checkbox.getElement().setAttribute("disabled", true);
                if (entry.eligibilityReason() != null && !entry.eligibilityReason().isBlank()) {
                    checkbox.getElement().setAttribute("title", entry.eligibilityReason());
                }
            }
            checkbox.addValueChangeListener(event -> {
                focusedBusinessTable = entry.table();
                selectionState.updateSelection(entry.table(), event.getValue());
                clearComparisonProgressStatus();
                selectionDataProvider.refreshAll();
                refreshActionButtons();
            });
            return checkbox;
        })).setHeader("").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(entry -> entry.table().schemaName())
                .setHeader("Schema")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(entry -> entry.table().schemaName(), String.CASE_INSENSITIVE_ORDER))
                .setKey("schema");
        grid.addColumn(new ComponentRenderer<>(entry -> {
                    final Span tableName = new Span(entry.table().tableName());
                    tableName.getElement().setAttribute("data-testid", "table-label-" + selectorToken(entry.table()));
                    if (!entry.eligible() && entry.eligibilityReason() != null && !entry.eligibilityReason().isBlank()) {
                        tableName.getElement().setAttribute("title", entry.eligibilityReason());
                    }
                    return tableName;
                }))
                .setHeader("Table")
                .setSortable(true)
                .setComparator(Comparator.comparing(entry -> entry.table().tableName(), String.CASE_INSENSITIVE_ORDER))
                .setKey("table");

        grid.addItemClickListener(event -> focusedBusinessTable = event.getItem().table());
        grid.addCellFocusListener(event -> event.getItem().ifPresent(item -> focusedBusinessTable = item.table()));
        grid.getElement()
                .addEventListener("keydown", event -> toggleFocusedBusinessTableSelection())
                .setFilter("event.code === 'Space' || event.key === ' '")
                .addEventData("event.code");

        return grid;
    }

    private Grid<CommandCatalogEntry> buildCommandSelectionGrid() {
        final Grid<CommandCatalogEntry> grid = new Grid<>();
        this.commandSelectionGrid = grid;
        grid.getElement().setAttribute("data-testid", "command-selection-grid");
        grid.getElement().setAttribute("data-testid-focus-target", "command-selection-grid");
        grid.getElement().setAttribute("tabindex", "0");
        grid.setAllRowsVisible(false);
        grid.setHeight("22rem");
        grid.setWidthFull();
        grid.setDataProvider(commandSelectionDataProvider);

        grid.addColumn(new ComponentRenderer<>(entry -> {
            final Checkbox checkbox = new Checkbox();
            checkbox.setEnabled(authenticatedContextHolder.isAuthenticated());
            checkbox.setValue(commandSelectionState.isSelected(entry.interactionId()));
            checkbox.getElement().setAttribute("data-testid", "command-checkbox-" + entry.interactionId().toLowerCase(Locale.ROOT));
            checkbox.getElement()
                    .addEventListener("click", domEvent -> {
                        if (domEvent.getEventData().get("event.shiftKey").asBoolean(false)) {
                            skipNextCommandCheckboxClientEventInteractionId = entry.interactionId();
                        }
                    })
                    .addEventData("event.shiftKey");
            checkbox.addValueChangeListener(event -> {
                if (!event.isFromClient()) {
                    return;
                }
                focusedCommandInteractionId = entry.interactionId();
                if (entry.interactionId().equals(skipNextCommandCheckboxClientEventInteractionId)) {
                    skipNextCommandCheckboxClientEventInteractionId = null;
                    return;
                }
                applySingleCommandSelection(entry.interactionId(), event.getValue(), true);
            });
            return checkbox;
        })).setHeader("").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

        final Grid.Column<CommandCatalogEntry> replayStateColumn = grid.addColumn(CommandCatalogEntry::replayState)
                .setHeader("Replay state")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::replayState, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("replay-state");
        final Grid.Column<CommandCatalogEntry> memberColumn = grid.addColumn(CommandCatalogEntry::logicalMemberIdentifier)
                .setHeader("Member")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::logicalMemberIdentifier, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("member");
        final Grid.Column<CommandCatalogEntry> timestampColumn = grid.addColumn(CommandCatalogEntry::timestamp)
                .setHeader("Timestamp")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("timestamp");
        final Grid.Column<CommandCatalogEntry> interactionColumn = grid.addColumn(CommandCatalogEntry::interactionId)
                .setHeader("Interaction")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::interactionId, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("interaction");

        final var filterHeader = grid.appendHeaderRow();

        final TextField memberFilter = filterField("Member", "command-filter-member-id", value -> {
            commandMemberFilterValue = value;
            clearComparisonProgressStatus();
            applyCommandFilters();
        });
        filterHeader.getCell(memberColumn).setComponent(memberFilter);

        final TextField interactionFilter = filterField("Interaction", "command-filter-interaction-id", value -> {
            commandInteractionFilterValue = value;
            clearComparisonProgressStatus();
            applyCommandFilters();
        });
        filterHeader.getCell(interactionColumn).setComponent(interactionFilter);

        final HorizontalLayout replayFilterLayout = new HorizontalLayout();
        replayFilterLayout.getElement().setAttribute("data-testid", "command-filter-replay-state-layout");
        replayFilterLayout.setPadding(false);
        replayFilterLayout.setSpacing(true);
        replayFilterLayout.setWidthFull();
        replayFilterLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        replayFilterLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        for (String replayState : REPLAY_STATE_FILTER_OPTIONS) {
            final Checkbox replayStateCheckbox = new Checkbox(replayStateAbbreviation(replayState));
            replayStateCheckbox.getElement().setAttribute("data-testid", "command-filter-replay-state-" + replayState.toLowerCase(Locale.ROOT));
            replayStateCheckbox.getElement().setAttribute("title", replayState);
            replayStateCheckbox.setValue(commandReplayStateFilters.contains(replayState));
            replayStateCheckbox.addValueChangeListener(event -> {
                if (Boolean.TRUE.equals(event.getValue())) {
                    commandReplayStateFilters.add(replayState);
                } else {
                    commandReplayStateFilters.remove(replayState);
                }
                clearComparisonProgressStatus();
                applyCommandFilters();
            });
            replayFilterLayout.add(replayStateCheckbox);
        }
        filterHeader.getCell(replayStateColumn).setComponent(replayFilterLayout);

        grid.sort(List.of(new GridSortOrder<>(timestampColumn, com.vaadin.flow.data.provider.SortDirection.ASCENDING)));

        final GridContextMenu<CommandCatalogEntry> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Set baseline from selected command", event ->
                event.getItem().ifPresent(item -> setCommandBaselineFilter(item.timestamp())));

        grid.addItemClickListener(event -> {
            focusedCommandInteractionId = event.getItem().interactionId();
            if (event.isShiftKey()) {
                applyCommandRangeSelection(event.getItem().interactionId());
                return;
            }
            commandRangeAnchorInteractionId = event.getItem().interactionId();
        });
        grid.addCellFocusListener(event -> event.getItem().ifPresent(item -> focusedCommandInteractionId = item.interactionId()));
        grid.addSortListener(event -> clearCommandRangeAnchorIfInvalid());
        grid.getElement()
                .addEventListener("keydown", event -> toggleFocusedCommandSelection(event.getEventData().get("event.shiftKey").asBoolean(false)))
                .setFilter("event.code === 'Space' || event.key === ' '")
                .addEventData("event.code")
                .addEventData("event.shiftKey");

        return grid;
    }

    private void enableDrawerResize(final Div panel, final Div resizeHandle) {
        resizeHandle.getElement().executeJs("""
            const panel = this.parentElement;
            const handle = this;
            if (!panel || !handle || handle.__drawerResizeBound) return;
            handle.__drawerResizeBound = true;
            let active = false;
            const onMove = (event) => {
              if (!active) return;
              const width = Math.max($1, Math.min($2, event.clientX - panel.getBoundingClientRect().left));
              panel.style.width = `${width}px`;
              $0.$server.onDrawerWidthChanged(Math.round(width));
            };
            const stop = () => {
              active = false;
              document.body.style.userSelect = '';
            };
            handle.addEventListener('pointerdown', (event) => {
              active = true;
              document.body.style.userSelect = 'none';
              handle.setPointerCapture(event.pointerId);
            });
            handle.addEventListener('pointerup', stop);
            window.addEventListener('pointermove', onMove);
            window.addEventListener('pointerup', stop);
            """, getElement(), MIN_DRAWER_WIDTH_PX, MAX_DRAWER_WIDTH_PX);
    }

    @com.vaadin.flow.component.ClientCallable
    private void onDrawerWidthChanged(final int requestedWidthPx) {
        drawerWidthPx = Math.max(MIN_DRAWER_WIDTH_PX, Math.min(MAX_DRAWER_WIDTH_PX, requestedWidthPx));
    }

    private void applyCommandFilters() {
        commandSelectionDataProvider.clearFilters();
        commandSelectionDataProvider.addFilter(entry -> commandSelectionState.matchesFilter(
                entry,
                commandMemberFilterValue,
                commandInteractionFilterValue,
                commandReplayStateFilters,
                commandBaselineFilterTimestamp));
        clearCommandRangeAnchorIfInvalid();
        commandSelectionDataProvider.refreshAll();
    }

    private void applySelectionFilters() {
        selectionDataProvider.clearFilters();
        selectionDataProvider.addFilter(entry -> selectionState.matchesFilter(entry, tableFilterValue));
        selectionDataProvider.addFilter(selectionState::matchesSelectedVisibility);
        selectionDataProvider.refreshAll();
    }

    private TextField filterField(final String placeholder, final String testId, final Consumer<String> onValueChanged) {
        final TextField filter = new TextField();
        filter.setPlaceholder(placeholder);
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.getElement().setAttribute("data-testid", testId);
        filter.setWidthFull();
        filter.addValueChangeListener(event -> onValueChanged.accept(event.getValue()));
        return filter;
    }

    private DateTimePicker buildCommandBaselineFilterField() {
        final DateTimePicker baselineFilter = new DateTimePicker();
        this.commandBaselineFilterField = baselineFilter;
        baselineFilter.setLabel("Baseline timestamp");
        baselineFilter.getStyle().setPaddingTop(".25em");
        baselineFilter.getStyle().setPaddingBottom(".55em");
        baselineFilter.setWidthFull();
        baselineFilter.getElement().setAttribute("data-testid", "command-filter-baseline-timestamp");
        baselineFilter.addValueChangeListener(event -> applyBaselineFilterValue(event.getValue(), baselineFilter));
        return baselineFilter;
    }

    private void applyBaselineFilterValue(final LocalDateTime value, final DateTimePicker sourceField) {
        commandBaselineFilterTimestamp = value;
        sourceField.setInvalid(false);
        sourceField.setErrorMessage(null);
        clearComparisonProgressStatus();
        applyCommandFilters();
    }

    private void setCommandBaselineFilter(final String commandTimestamp) {
        if (commandTimestamp == null || commandTimestamp.isBlank()) {
            return;
        }
        try {
            commandBaselineFilterTimestamp = LocalDateTime.parse(commandTimestamp);
        } catch (DateTimeParseException ex) {
            return;
        }
        clearComparisonProgressStatus();
        applyCommandFilters();
        if (commandBaselineFilterField != null && !commandBaselineFilterTimestamp.equals(commandBaselineFilterField.getValue())) {
            commandBaselineFilterField.setValue(commandBaselineFilterTimestamp);
        }
    }

    private void applyResultGridFilters(
            final ListDataProvider<ComparisonRowView> provider,
            final String keyFilter,
            final String statusFilter,
            final Map<ColumnRef, AtomicReference<String>> valueFilters) {
        provider.clearFilters();
        provider.addFilter(row -> {
            if (!containsIgnoreCase(row.key().display(), keyFilter)) {
                return false;
            }
            if (!containsIgnoreCase(row.status().name(), statusFilter)) {
                return false;
            }
            for (Map.Entry<ColumnRef, AtomicReference<String>> entry : valueFilters.entrySet()) {
                final String left = row.leftValues().getOrDefault(entry.getKey(), "");
                final String right = row.rightValues().getOrDefault(entry.getKey(), "");
                if (!containsIgnoreCase(compactValue(row.status(), left, right), entry.getValue().get())) {
                    return false;
                }
            }
            return true;
        });
    }

    private boolean containsIgnoreCase(final String source, final String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return source != null && source.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private void applyCommandDrivenSelection() {
        final List<String> selectedInteractionIds = commandSelectionState.selectedInteractionIds();
        final var touchedTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(selectedInteractionIds, tableCatalogEntries);
        selectionState.applyProgrammaticSelections(touchedTables);
        applySelectionFilters();
        refreshActionButtons();
    }

    private void clearAllSelections() {
        focusedCommandInteractionId = null;
        commandRangeAnchorInteractionId = null;
        skipNextCommandCheckboxClientEventInteractionId = null;
        focusedBusinessTable = null;
        commandSelectionState.clearSelections();
        selectionState.clearSelections();
        clearComparisonProgressStatus();
        selectedOnlyFilter.setValue(selectionState.isSelectedOnly());
        commandSelectionDataProvider.refreshAll();
        applyCommandDrivenSelection();
    }

    private void toggleFocusedCommandSelection() {
        toggleFocusedCommandSelection(false);
    }

    private void toggleFocusedCommandSelection(final boolean shiftPressed) {
        final String interactionId = focusedOrFirstVisibleCommandInteractionId();
        if (interactionId == null || interactionId.isBlank()) {
            return;
        }
        if (shiftPressed) {
            applyCommandRangeSelection(interactionId);
            return;
        }
        final boolean nextSelected = !commandSelectionState.isSelected(interactionId);
        applySingleCommandSelection(interactionId, nextSelected, true);
    }

    private void applySingleCommandSelection(
            final String interactionId,
            final boolean selected,
            final boolean updateAnchor) {
        commandSelectionState.updateSelection(interactionId, selected);
        if (updateAnchor) {
            commandRangeAnchorInteractionId = interactionId;
        }
        clearComparisonProgressStatus();
        commandSelectionDataProvider.refreshAll();
        applyCommandDrivenSelection();
    }

    private void applyCommandRangeSelection(final String targetInteractionId) {
        if (targetInteractionId == null || targetInteractionId.isBlank()) {
            return;
        }
        final List<CommandCatalogEntry> visibleEntries = visibleCommandEntries();
        final List<String> visibleIds = visibleEntries.stream().map(CommandCatalogEntry::interactionId).toList();
        if (visibleIds.isEmpty() || !visibleIds.contains(targetInteractionId)) {
            return;
        }

        final String effectiveAnchor = visibleIds.contains(commandRangeAnchorInteractionId)
                ? commandRangeAnchorInteractionId
                : null;

        if (effectiveAnchor == null) {
            commandSelectionState.updateSelection(targetInteractionId, true);
            commandRangeAnchorInteractionId = targetInteractionId;
            clearComparisonProgressStatus();
            commandSelectionDataProvider.refreshAll();
            applyCommandDrivenSelection();
            return;
        }

        final int anchorIndex = visibleIds.indexOf(effectiveAnchor);
        final int targetIndex = visibleIds.indexOf(targetInteractionId);
        if (anchorIndex < 0 || targetIndex < 0) {
            return;
        }

        final int start = Math.min(anchorIndex, targetIndex);
        final int end = Math.max(anchorIndex, targetIndex);
        for (int idx = start; idx <= end; idx++) {
            commandSelectionState.updateSelection(visibleIds.get(idx), true);
        }
        clearComparisonProgressStatus();
        commandSelectionDataProvider.refreshAll();
        applyCommandDrivenSelection();
    }

    private String focusedOrFirstVisibleCommandInteractionId() {
        if (focusedCommandInteractionId != null && !focusedCommandInteractionId.isBlank()) {
            return focusedCommandInteractionId;
        }
        return visibleCommandEntries().stream()
                .findFirst()
                .map(CommandCatalogEntry::interactionId)
                .orElse(null);
    }

    private void clearCommandRangeAnchorIfInvalid() {
        if (commandRangeAnchorInteractionId == null || commandRangeAnchorInteractionId.isBlank()) {
            return;
        }
        final boolean stillVisible = visibleCommandEntries().stream()
                .map(CommandCatalogEntry::interactionId)
                .anyMatch(commandRangeAnchorInteractionId::equals);
        if (!stillVisible) {
            commandRangeAnchorInteractionId = null;
        }
    }

    private List<CommandCatalogEntry> visibleCommandEntries() {
        final List<CommandCatalogEntry> filtered = commandCatalogEntries.stream()
                .filter(entry -> commandSelectionState.matchesFilter(
                        entry,
                        commandMemberFilterValue,
                        commandInteractionFilterValue,
                        commandReplayStateFilters,
                        commandBaselineFilterTimestamp))
                .toList();

        final Comparator<CommandCatalogEntry> comparator = commandSelectionComparator();
        if (comparator == null) {
            return filtered;
        }
        return filtered.stream().sorted(comparator).toList();
    }

    private Comparator<CommandCatalogEntry> commandSelectionComparator() {
        if (commandSelectionGrid == null || commandSelectionGrid.getSortOrder().isEmpty()) {
            return Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(CommandCatalogEntry::interactionId, String.CASE_INSENSITIVE_ORDER);
        }
        Comparator<CommandCatalogEntry> comparator = null;
        for (GridSortOrder<CommandCatalogEntry> sortOrder : commandSelectionGrid.getSortOrder()) {
            final Comparator<CommandCatalogEntry> columnComparator = comparatorForColumnKey(sortOrder.getSorted().getKey());
            final Comparator<CommandCatalogEntry> normalizedComparator =
                    sortOrder.getDirection() == com.vaadin.flow.data.provider.SortDirection.DESCENDING
                            ? columnComparator.reversed()
                            : columnComparator;
            comparator = comparator == null ? normalizedComparator : comparator.thenComparing(normalizedComparator);
        }
        return comparator == null
                ? null
                : comparator.thenComparing(CommandCatalogEntry::interactionId, String.CASE_INSENSITIVE_ORDER);
    }

    private Comparator<CommandCatalogEntry> comparatorForColumnKey(final String columnKey) {
        if ("replay-state".equals(columnKey)) {
            return Comparator.comparing(CommandCatalogEntry::replayState, String.CASE_INSENSITIVE_ORDER);
        }
        if ("member".equals(columnKey)) {
            return Comparator.comparing(CommandCatalogEntry::logicalMemberIdentifier, String.CASE_INSENSITIVE_ORDER);
        }
        if ("interaction".equals(columnKey)) {
            return Comparator.comparing(CommandCatalogEntry::interactionId, String.CASE_INSENSITIVE_ORDER);
        }
        return Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER);
    }

    private void toggleFocusedBusinessTableSelection() {
        final TableCatalogEntry focusedEntry = focusedBusinessTable != null
                ? tableCatalogEntries.stream().filter(entry -> entry.table().equals(focusedBusinessTable)).findFirst().orElse(null)
                : tableCatalogEntries.stream().findFirst().orElse(null);
        if (focusedEntry == null || !focusedEntry.eligible()) {
            return;
        }
        final TableRef tableRef = focusedEntry.table();
        final boolean nextSelected = !selectionState.isSelected(tableRef);
        selectionState.updateSelection(tableRef, nextSelected);
        clearComparisonProgressStatus();
        selectionDataProvider.refreshAll();
        refreshActionButtons();
    }

    private void refreshActionButtons() {
        final boolean authenticated = authenticatedContextHolder.isAuthenticated();
        compareButton.setEnabled(selectionState.isCompareEnabled() && authenticated);
        clearSelectionsButton.setEnabled(authenticated && (selectionState.selectedCount() > 0 || commandSelectionState.selectedCount() > 0));
    }

    private String asText(final tools.jackson.databind.JsonNode node) {
        return node == null || node.isNull() ? null : node.asText();
    }

    private void handleEnterShortcut(final String targetTagName, final String targetType) {
        if (loginDialog.isOpened() || !compareButton.isEnabled()) {
            return;
        }

        final String tag = targetTagName == null ? "" : targetTagName.toUpperCase(Locale.ROOT);
        if ("INPUT".equals(tag) || "TEXTAREA".equals(tag) || "SELECT".equals(tag) || "BUTTON".equals(tag)) {
            return;
        }

        if (targetType != null && !targetType.isBlank()) {
            final String type = targetType.toLowerCase(Locale.ROOT);
            if ("text".equals(type) || "password".equals(type) || "search".equals(type) || "email".equals(type)) {
                return;
            }
        }

        executeComparison();
    }

    private void executeComparison() {
        if (!authenticatedContextHolder.isAuthenticated()) {
            refreshAuthUiState();
            return;
        }

        final List<TableRef> selectedTables = selectionState.selectedTables();
        if (selectedTables.isEmpty()) {
            return;
        }

        compareButton.setEnabled(false);
        comparisonError.setText("");
        clearRunVisualProgress();
        compareProgressTotal = selectedTables.size();
        compareProgressCounter.setText("0 of " + compareProgressTotal);
        compareProgressCounter.setVisible(true);
        showComparisonProgress("Comparison running...", PROGRESS_STYLE_NEUTRAL);

        final UI ui = getUI().orElse(null);
        if (ui == null) {
            executeComparisonSynchronously(selectedTables);
            return;
        }

        final AuthenticatedConnectionContext authenticatedContext = authenticatedContextHolder.required();
        ui.setPollInterval(200);
        CompletableFuture
                .supplyAsync(() -> comparisonExecutionService.compare(
                        MultiTableComparisonRequest.forTables(selectedTables),
                        event -> ui.access(() -> onComparisonProgress(event)),
                        authenticatedContext))
                .whenComplete((outcome, throwable) -> ui.access(() -> {
                    try {
                        if (throwable == null) {
                            latestOutcome = outcome;
                            showComparisonProgress("Comparison complete.", PROGRESS_STYLE_SUCCESS);
                            downloadFormatSelect.setValue(DownloadFormat.JSON);
                            resultActions.setVisible(true);
                            refreshDownloadLinks();
                            renderComparisonTabs();
                        } else {
                            latestOutcome = null;
                            resultActions.setVisible(false);
                            comparisonResultsContainer.removeAll();
                            final RuntimeException runtimeException = throwable instanceof RuntimeException re
                                    ? re
                                    : new RuntimeException(throwable);
                            comparisonError.setText(safeError(runtimeException));
                            showComparisonProgress("Comparison failed.", PROGRESS_STYLE_FAILURE);
                            renderEmptyComparisonState();
                        }
                    } finally {
                        ui.setPollInterval(-1);
                        compareButton.setEnabled(selectionState.isCompareEnabled() && authenticatedContextHolder.isAuthenticated());
                    }
                }));
    }

    private void refreshDownloadLinks() {
        if (latestOutcome == null) {
            return;
        }
        final String ts = LocalDateTime.now().format(FILE_TS);
        final DownloadFormat selectedFormat = downloadFormatSelect.getValue() == null
                ? DownloadFormat.JSON
                : downloadFormatSelect.getValue();

        final StreamResource resource = switch (selectedFormat) {
            case JSON -> new StreamResource("comparison-" + ts + ".json",
                    () -> new ByteArrayInputStream(latestOutcome.json().getBytes(StandardCharsets.UTF_8)));
            case YAML -> new StreamResource("comparison-" + ts + ".yml",
                    () -> new ByteArrayInputStream(latestOutcome.yaml().getBytes(StandardCharsets.UTF_8)));
            case EXCEL -> new StreamResource("comparison-" + ts + ".xlsx",
                    () -> new ByteArrayInputStream(latestOutcome.excel()));
        };
        downloadAction.setHref(resource);
    }

    private void renderComparisonTabs() {
        comparisonResultsContainer.removeAll();
        if (latestOutcome == null || latestOutcome.viewResult().tableResults().isEmpty()) {
            renderEmptyComparisonState();
            return;
        }

        final List<TableComparisonViewResult> filtered = latestOutcome.viewResult().tableResults().stream()
                .filter(this::matchesComparedTableFilter)
                .toList();

        if (filtered.isEmpty()) {
            comparisonResultsContainer.add(new Span("No compared tables match the filter."));
            return;
        }

        final Tabs tabs = new Tabs();
        tabs.getElement().setAttribute("data-testid", "comparison-results-tabs");
        tabs.setWidthFull();

        final Div tabContent = new Div();
        tabContent.getElement().setAttribute("data-testid", "comparison-results-tab-content");
        tabContent.addClassName("comparison-results-tab-content");
        tabContent.setWidthFull();

        final Map<Tab, TableComparisonViewResult> mapping = new LinkedHashMap<>();
        for (TableComparisonViewResult tableResult : filtered) {
            final Tab tab = new Tab(tableResult.tableDisplayName());
            final boolean hasDifferences = hasDifferences(tableResult);
            tab.getElement().setAttribute("data-testid", "comparison-result-tab-" + selectorToken(tableResult.table()));
            tab.getElement().setAttribute("data-has-differences", Boolean.toString(hasDifferences));
            if (hasDifferences) {
                tab.addClassName("comparison-result-tab-different");
            }
            mapping.put(tab, tableResult);
            tabs.add(tab);
        }

        tabs.addSelectedChangeListener(event -> {
            final TableComparisonViewResult selected = mapping.get(event.getSelectedTab());
            if (selected != null) {
                tabContent.removeAll();
                tabContent.add(buildResultGrid(selected));
            }
        });

        final TableComparisonViewResult first = mapping.get(tabs.getSelectedTab());
        if (first != null) {
            tabContent.add(buildResultGrid(first));
        }

        comparisonResultsContainer.add(tabs, tabContent);
    }

    private boolean matchesComparedTableFilter(final TableComparisonViewResult result) {
        if (Boolean.TRUE.equals(differencesOnlyFilter.getValue()) && !hasDifferences(result)) {
            return false;
        }
        final String filter = comparedTableFilter.getValue();
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return result.tableDisplayName().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT));
    }

    private boolean hasDifferences(final TableComparisonViewResult result) {
        return result.rows().stream().anyMatch(row -> row.status() != ComparisonRowStatus.MATCH);
    }

    private void renderEmptyComparisonState() {
        comparisonResultsContainer.removeAll();
        final Span empty = new Span("No comparison results yet.");
        empty.getElement().setAttribute("data-testid", "comparison-stage-empty");
        comparisonResultsContainer.add(empty);
    }

    private Component buildResultGrid(final TableComparisonViewResult tableResult) {
        final List<ComparisonRowView> visibleRows = tableResult.rows().stream()
                .filter(row -> row.status() != ComparisonRowStatus.MATCH)
                .toList();

        final Grid<ComparisonRowView> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "comparison-grid-" + selectorToken(tableResult.table()));
        final ListDataProvider<ComparisonRowView> provider = new ListDataProvider<>(visibleRows);
        grid.setDataProvider(provider);
        grid.setAllRowsVisible(true);
        grid.setWidth("max-content");
        grid.getStyle().set("min-width", "100%");
        grid.addThemeNames("column-borders", "compact");

        final var keyColumn = grid.addColumn(row -> row.key().display())
                .setHeader("Business Key")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(row -> row.key().display(), String.CASE_INSENSITIVE_ORDER));

        final var statusColumn = grid.addColumn(row -> row.status().name())
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0)
                .setSortable(true)
                .setComparator(Comparator.comparing(row -> row.status().name(), String.CASE_INSENSITIVE_ORDER));

        final Map<ColumnRef, Grid.Column<ComparisonRowView>> comparedColumns = new LinkedHashMap<>();
        for (ColumnRef column : tableResult.comparedColumns()) {
            final String columnName = column.name();
            final Grid.Column<ComparisonRowView> gridColumn = grid.addColumn(new ComponentRenderer<>(row -> valueCell(row, column)))
                    .setHeader(columnName)
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.START)
                    .setSortable(true)
                    .setComparator(Comparator.comparing(row -> compactValue(
                                    row.status(),
                                    row.leftValues().getOrDefault(column, ""),
                                    row.rightValues().getOrDefault(column, "")),
                            String.CASE_INSENSITIVE_ORDER));
            comparedColumns.put(column, gridColumn);
        }

        final AtomicReference<String> keyFilter = new AtomicReference<>("");
        final AtomicReference<String> statusFilter = new AtomicReference<>("");
        final Map<ColumnRef, AtomicReference<String>> valueFilters = new LinkedHashMap<>();

        final var headerRow = grid.appendHeaderRow();
        headerRow.getCell(keyColumn).setComponent(filterField("Key", "comparison-grid-filter-key", value -> {
            keyFilter.set(value);
            applyResultGridFilters(provider, keyFilter.get(), statusFilter.get(), valueFilters);
        }));
        headerRow.getCell(statusColumn).setComponent(filterField("Status", "comparison-grid-filter-status", value -> {
            statusFilter.set(value);
            applyResultGridFilters(provider, keyFilter.get(), statusFilter.get(), valueFilters);
        }));

        for (Map.Entry<ColumnRef, Grid.Column<ComparisonRowView>> entry : comparedColumns.entrySet()) {
            final AtomicReference<String> ref = new AtomicReference<>("");
            valueFilters.put(entry.getKey(), ref);
            headerRow.getCell(entry.getValue()).setComponent(filterField("Filter", "comparison-grid-filter-" + selectorToken(tableResult.table()) + "-" + entry.getKey().name(), value -> {
                ref.set(value);
                applyResultGridFilters(provider, keyFilter.get(), statusFilter.get(), valueFilters);
            }));
        }

        final Div scrollContainer = new Div(grid);
        scrollContainer.addClassName("cmp-grid-scroll-container");
        scrollContainer.getElement().setAttribute("data-testid", "comparison-grid-scroll-container-" + selectorToken(tableResult.table()));
        return scrollContainer;
    }

    private Component valueCell(final ComparisonRowView row, final ColumnRef column) {
        final String left = row.leftValues().getOrDefault(column, "");
        final String right = row.rightValues().getOrDefault(column, "");

        if (row.status() == ComparisonRowStatus.DIFFERENT
                && !left.equals(right)
                && !left.isBlank()
                && !right.isBlank()) {
            final Div top = new Div(new Span(left));
            top.addClassNames("cmp-cell-diff", "cmp-cell-left-diff");
            top.getStyle().set("padding", "0.1rem 0.35rem");

            final Div bottom = new Div(new Span(right));
            bottom.addClassNames("cmp-cell-diff", "cmp-cell-right-diff");
            bottom.getStyle()
//                    .set("padding", "0.1rem 0.35rem")
                    .set("border-top", "1px solid var(--lumo-contrast-20pct)");

            final Div stacked = new Div(top, bottom);
            stacked.getStyle()
                    .set("display", "flex")
                    .set("flex-direction", "column")
                    .set("min-height", "2.2rem")
                    .set("line-height", "1.2");
            return stacked;
        }

        final Span value = new Span(compactValue(row.status(), left, right));
        if (row.status() == ComparisonRowStatus.ONLY_IN_LEFT) {
            value.addClassName("cmp-cell-left-only");
        } else if (row.status() == ComparisonRowStatus.ONLY_IN_RIGHT) {
            value.addClassName("cmp-cell-right-only");
        }
        return value;
    }

    private String compactValue(final ComparisonRowStatus status, final String left, final String right) {
        if (left.equals(right)) {
            return left;
        }
        if (status == ComparisonRowStatus.ONLY_IN_LEFT) {
            return left;
        }
        if (status == ComparisonRowStatus.ONLY_IN_RIGHT) {
            return right;
        }
        if (left.isBlank()) {
            return right.isBlank() ? "" : "R: " + right;
        }
        if (right.isBlank()) {
            return "L: " + left;
        }
        return "L: " + left + " | R: " + right;
    }

    private Component statusBadge(final ComparisonRowView row) {
        final Span badge = new Span(row.status().name());
        badge.getStyle()
//                .set("padding", "0.15rem 0.45rem")
                .set("border-radius", "999px")
                .set("font-size", "var(--lumo-font-size-xs)")
                .set("font-weight", "600")
                .set("display", "inline-block")
                .set("border", "1px solid transparent");

        if (row.status() == ComparisonRowStatus.DIFFERENT) {
            badge.getStyle().set("background", "#fde68a").set("border-color", "#f59e0b").set("color", "#78350f");
        } else if (row.status() == ComparisonRowStatus.ONLY_IN_LEFT) {
            badge.getStyle().set("background", "#dbeafe").set("border-color", "#3b82f6").set("color", "#1e3a8a");
        } else if (row.status() == ComparisonRowStatus.ONLY_IN_RIGHT) {
            badge.getStyle().set("background", "#dcfce7").set("border-color", "#22c55e").set("color", "#14532d");
        } else {
            badge.getStyle().set("background", "#f3f4f6").set("border-color", "#d1d5db").set("color", "#374151");
        }
        return badge;
    }

    private String safeError(final RuntimeException ex) {
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Comparison execution failed.";
        }
        return ex.getMessage();
    }

    private String selectorToken(final TableRef tableRef) {
        return (tableRef.schemaName() + "-" + tableRef.tableName()).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }

    private String safe(final String value) {
        return value == null || value.isBlank() ? "not configured" : value;
    }

    private String selectionRowPartName(final TableCatalogEntry entry) {
        final String basePart = entry.eligible() ? "eligible-table-row" : "ineligible-table-row";
        if (completedTablesInActiveRun.contains(entry.table())) {
            return basePart + " comparison-completed-row";
        }
        return basePart;
    }

    private void showComparisonProgress(final String message, final String styleClass) {
        comparisonProgressSummary.setText(message == null ? "" : message);
        applyComparisonProgressStyle(styleClass);
    }

    private void clearComparisonProgressStatus() {
        comparisonProgressSummary.setText("");
        applyComparisonProgressStyle(null);
        clearRunVisualProgress();
    }

    private void clearRunVisualProgress() {
        completedTablesInActiveRun.clear();
        compareProgressTotal = 0;
        compareProgressCounter.setText("");
        compareProgressCounter.setVisible(false);
        if (tableSelectionGrid != null) {
            tableSelectionGrid.getDataProvider().refreshAll();
        }
    }

    private void applyComparisonProgressStyle(final String styleClass) {
        comparisonProgressSummary.removeClassNames(PROGRESS_STYLE_NEUTRAL, PROGRESS_STYLE_SUCCESS, PROGRESS_STYLE_FAILURE);
        if (styleClass != null && !styleClass.isBlank()) {
            comparisonProgressSummary.addClassName(styleClass);
        }
    }

    private void configureLoginDialog() {
        loginDialog.setModal(true);
        loginDialog.setCloseOnEsc(false);
        loginDialog.setCloseOnOutsideClick(false);
        loginDialog.setDraggable(false);
        loginDialog.setResizable(false);
        loginDialog.setWidth("35rem");
        loginDialog.getElement().setAttribute("data-testid", "login-modal");
    }

    private void rebuildLoginDialogForm() {
        loginDialog.removeAll();
        loginDialog.add(new LoginForm(authenticationService, this::onAuthenticationSuccess));
    }

    private void onAuthenticationSuccess() {
        reloadTableCatalog();
        refreshConnectionFooter();
        refreshAuthUiState();
        loginDialog.close();
        focusCommandSelectionGrid();
    }

    private void handleLogout() {
        authenticationService.logout();
        latestOutcome = null;
        comparisonError.setText("");
        clearComparisonProgressStatus();
        resultActions.setVisible(false);
        renderEmptyComparisonState();
        clearTableCatalog();
        refreshConnectionFooter();
        refreshAuthUiState();
    }

    private void focusCommandSelectionGrid() {
        if (commandSelectionGrid == null || !authenticatedContextHolder.isAuthenticated()) {
            return;
        }
        focusedCommandInteractionId = commandCatalogEntries.stream()
                .findFirst()
                .map(CommandCatalogEntry::interactionId)
                .orElse(null);
        commandSelectionGrid.focus();
        commandSelectionGrid.getElement().setAttribute("data-focused", "true");
        commandSelectionGrid.getElement().executeJs("this.focus()");
    }

    private List<CommandCatalogEntry> sortCommandCatalog(final List<CommandCatalogEntry> entries) {
        return entries.stream()
                .sorted(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private String replayStateAbbreviation(final String replayState) {
        if ("OK".equalsIgnoreCase(replayState)) {
            return "K";
        }
        if ("PENDING".equalsIgnoreCase(replayState)) {
            return "P";
        }
        if ("FAILED".equalsIgnoreCase(replayState)) {
            return "F";
        }
        return replayState == null || replayState.isBlank() ? "?" : replayState.substring(0, 1).toUpperCase(Locale.ROOT);
    }

    private void reloadTableCatalog() {
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog();
        final List<CommandCatalogEntry> commandCatalog = sortCommandCatalog(commandCatalogService.discoverCommandCatalog());

        selectionState = new ManualTableSelectionState(tableCatalog);
        commandSelectionState = new CommandSelectionState(commandCatalog);
        commandRangeAnchorInteractionId = null;
        skipNextCommandCheckboxClientEventInteractionId = null;
        applyCommandDrivenSelection();

        tableCatalogEntries.clear();
        tableCatalogEntries.addAll(tableCatalog);
        commandCatalogEntries.clear();
        commandCatalogEntries.addAll(commandCatalog);

        selectionDataProvider.refreshAll();
        applyCommandFilters();
        refreshActionButtons();
    }

    private void clearTableCatalog() {
        selectionState = new ManualTableSelectionState(List.of());
        commandSelectionState = new CommandSelectionState(List.of());
        commandRangeAnchorInteractionId = null;
        skipNextCommandCheckboxClientEventInteractionId = null;
        applyCommandDrivenSelection();
        tableCatalogEntries.clear();
        commandCatalogEntries.clear();
        selectionDataProvider.refreshAll();
        applyCommandFilters();
        refreshActionButtons();
    }

    private void executeComparisonSynchronously(final List<TableRef> selectedTables) {
        try {
            latestOutcome = comparisonExecutionService.compare(
                    MultiTableComparisonRequest.forTables(selectedTables),
                    this::onComparisonProgress);
            showComparisonProgress("Comparison complete.", PROGRESS_STYLE_SUCCESS);
            downloadFormatSelect.setValue(DownloadFormat.JSON);
            resultActions.setVisible(true);
            refreshDownloadLinks();
            renderComparisonTabs();
        } catch (RuntimeException ex) {
            latestOutcome = null;
            resultActions.setVisible(false);
            comparisonResultsContainer.removeAll();
            comparisonError.setText(safeError(ex));
            showComparisonProgress("Comparison failed.", PROGRESS_STYLE_FAILURE);
            renderEmptyComparisonState();
        } finally {
            compareButton.setEnabled(selectionState.isCompareEnabled() && authenticatedContextHolder.isAuthenticated());
        }
    }

    private void onComparisonProgress(final ComparisonProgressEvent event) {
        if (event.totalTables() > 0) {
            compareProgressTotal = event.totalTables();
        }
        if (event.phase() == ComparisonProgressPhase.TABLE_STARTED) {
            showComparisonProgress(
                    "Comparing %s (%d/%d)".formatted(
                            event.table().displayName(),
                            event.completedTables() + 1,
                            event.totalTables()),
                    PROGRESS_STYLE_NEUTRAL);
            compareProgressCounter.setVisible(compareProgressTotal > 0);
            if (compareProgressTotal > 0) {
                compareProgressCounter.setText(event.completedTables() + " of " + compareProgressTotal);
            }
            return;
        }
        if (event.phase() == ComparisonProgressPhase.TABLE_COMPLETED) {
            completedTablesInActiveRun.add(event.table());
            if (tableSelectionGrid != null) {
                tableSelectionGrid.getDataProvider().refreshAll();
            }
            compareProgressCounter.setVisible(compareProgressTotal > 0);
            if (compareProgressTotal > 0) {
                compareProgressCounter.setText(event.completedTables() + " of " + compareProgressTotal);
            }
            showComparisonProgress(
                    "Compared %s (%d/%d)".formatted(
                            event.table().displayName(),
                            event.completedTables(),
                            event.totalTables()),
                    PROGRESS_STYLE_NEUTRAL);
            return;
        }
        completedTablesInActiveRun.add(event.table());
        if (tableSelectionGrid != null) {
            tableSelectionGrid.getDataProvider().refreshAll();
        }
        compareProgressCounter.setVisible(compareProgressTotal > 0);
        if (compareProgressTotal > 0) {
            compareProgressCounter.setText(event.completedTables() + " of " + compareProgressTotal);
        }
        showComparisonProgress("Comparison failed on %s".formatted(event.table().displayName()), PROGRESS_STYLE_FAILURE);
    }

    private void refreshConnectionFooter() {
        final AuthenticatedConnectionContext context = authenticatedContextHolder.current().orElseGet(() -> {
            final ConnectionLoginRequest defaults = authenticationService.loginDefaults();
            return new AuthenticatedConnectionContext(
                    defaults.jdbcUrl(),
                    defaults.jdbcDriver(),
                    defaults.username(),
                    defaults.password(),
                    defaults.leftDatabase(),
                    defaults.rightDatabase());
        });

        connectionDatabases.setText(safe(context.leftDatabase()) + " ↔ " + safe(context.rightDatabase()));
    }

    private void refreshAuthUiState() {
        final boolean authenticated = authenticatedContextHolder.isAuthenticated();
        accountMenu.setVisible(authenticated);
        if (authenticated) {
            accountMenu.removeAll();
            final String username = authenticatedContextHolder.current()
                    .map(AuthenticatedConnectionContext::username)
                    .filter(value -> value != null && !value.isBlank())
                    .orElse("Account");
            final MenuItem accountItem = accountMenu.addItem(username);
            accountItem.getElement().setAttribute("data-testid", "account-menu-label");
            final MenuItem logoutItem = accountItem.getSubMenu().addItem("Logout", event -> handleLogout());
            logoutItem.getElement().setAttribute("data-testid", "logout-menu-item");
        }
        refreshActionButtons();
        if (!authenticated) {
            rebuildLoginDialogForm();
            if (getUI().isPresent()) {
                loginDialog.open();
            }
        } else if (getUI().isPresent()) {
            loginDialog.close();
        }
    }

    private enum DownloadFormat {
        JSON("json"),
        YAML("yaml"),
        EXCEL("excel");

        private final String label;

        DownloadFormat(final String label) {
            this.label = label;
        }

        String label() {
            return label;
        }
    }
}
