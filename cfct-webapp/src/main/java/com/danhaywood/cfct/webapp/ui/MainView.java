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
import com.danhaywood.cfct.webapp.validation.ConnectionValidationState;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationStatus;
import com.danhaywood.cfct.webapp.validation.ConnectionValidationStatusHolder;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.select.Select;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Route("")
@CssImport("./styles/comparison-grid.css")
public class MainView extends AppLayout implements BeforeEnterObserver {

    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

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
    private final Button clearSelectionsButton = new Button("Clear");
    private final Span comparisonError = new Span();
    private final Div comparisonResultsContainer = new Div();
    private final TextField comparedTableFilter = new TextField();
    private final Checkbox differencesOnlyFilter = new Checkbox("Diffs only");
    private final Anchor downloadAction = new Anchor();
    private final Select<DownloadFormat> downloadFormatSelect = new Select<>();
    private final HorizontalLayout resultActions = new HorizontalLayout();

    private final Span connectionServer = new Span();
    private final Span connectionDatabases = new Span();
    private final Span connectionStatusState = new Span();
    private final Span connectionStatusSummary = new Span();
    private final Span comparisonProgressSummary = new Span();
    private final MenuBar accountMenu = new MenuBar();
    private Grid<CommandCatalogEntry> commandSelectionGrid;
    private String focusedCommandInteractionId;
    private TableRef focusedBusinessTable;

    private WebappComparisonExecutionService.ComparisonExecutionOutcome latestOutcome;

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
            commandCatalogEntries.addAll(commandCatalogService.discoverCommandCatalog());
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
        differencesOnlyFilter.setValue(true);
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
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-xl)")
                //.set("padding", "var(--lumo-space-m) var(--lumo-space-l)")
                .set("background", "var(--lumo-base-color)")
                .set("border-top", "1px solid var(--lumo-contrast-10pct)")
                .set("font-size", "var(--lumo-font-size-s)");

        final Div connectionDetails = new Div();
        connectionDetails.getElement().setAttribute("data-testid", "connection-details-inline");
        connectionDetails.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)");

        connectionServer.getElement().setAttribute("data-testid", "connection-server");
        connectionDatabases.getElement().setAttribute("data-testid", "connection-database-pair");
        connectionDetails.add(connectionServer, connectionDatabases);

        final Div statusPanel = new Div();
        statusPanel.getElement().setAttribute("data-testid", "connection-status-panel");
        statusPanel.getStyle()
                .set("display", "inline-flex")
                .set("justify-content", "flex-end")
                .set("align-items", "center")
                .set("gap", "var(--lumo-space-m)")
                .set("padding-left", "var(--lumo-space-m)")
                .set("margin-left", "auto");

        connectionStatusState.getElement().setAttribute("data-testid", "connection-status-state");
        connectionStatusState.getStyle().set("font-weight", "600");
        connectionStatusSummary.getElement().setAttribute("data-testid", "connection-status-summary");
        comparisonProgressSummary.getElement().setAttribute("data-testid", "comparison-progress-summary");
        statusPanel.add(connectionStatusState, connectionStatusSummary, comparisonProgressSummary);

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
                .set("width", "32rem")
                .set("max-width", "100%")
                //.set("padding", "var(--lumo-space-m)")
                .set("box-sizing", "border-box")
                .set("height", "100%");

        final VerticalLayout layout = new VerticalLayout();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle()
                .set("gap", "var(--lumo-space-m)")
                .set("height", "100%")
                .set("overflow", "auto");

        final Div topSpacer = new Div();
        topSpacer.getElement().setAttribute("data-testid", "command-selection-spacer");
        topSpacer.getStyle()
                .set("height", "calc(var(--lumo-size-s, .3rem) + var(--lumo-space-m, .3rem))")
                .set("width", "100%");

        final TextField commandMemberFilter = new TextField();
        commandMemberFilter.setPlaceholder("Filter member id");
        commandMemberFilter.setClearButtonVisible(true);
        commandMemberFilter.setValueChangeMode(ValueChangeMode.EAGER);
        commandMemberFilter.getElement().setAttribute("data-testid", "command-filter-member-id");
        commandMemberFilter.setWidthFull();

        final TextField commandInteractionFilter = new TextField();
        commandInteractionFilter.setPlaceholder("Filter interaction id");
        commandInteractionFilter.setClearButtonVisible(true);
        commandInteractionFilter.setValueChangeMode(ValueChangeMode.EAGER);
        commandInteractionFilter.getElement().setAttribute("data-testid", "command-filter-interaction-id");
        commandInteractionFilter.setWidthFull();

        commandMemberFilter.addValueChangeListener(event ->
                applyCommandFilter(commandMemberFilter.getValue(), commandInteractionFilter.getValue()));
        commandInteractionFilter.addValueChangeListener(event ->
                applyCommandFilter(commandMemberFilter.getValue(), commandInteractionFilter.getValue()));

        final HorizontalLayout commandFilterRow = new HorizontalLayout(commandMemberFilter, commandInteractionFilter);
        commandFilterRow.getElement().setAttribute("data-testid", "command-filter-row");
        commandFilterRow.setWidthFull();
        commandFilterRow.setSpacing(true);
        commandFilterRow.setPadding(true);
        commandFilterRow.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.END);
        commandFilterRow.expand(commandMemberFilter, commandInteractionFilter);

        final Grid<CommandCatalogEntry> commandGrid = buildCommandSelectionGrid();

        final Div actionBar = new Div(compareButton);
        actionBar.getElement().setAttribute("data-testid", "navigation-compare-action-bar");
        actionBar.addClassName("navigation-compare-action-bar");
        actionBar.getStyle()
                .setDisplay(Style.Display.FLEX)
                .setJustifyContent(Style.JustifyContent.FLEX_END)
                .setWidth("100%")
                .set("position", "sticky")
                .set("bottom", "0")
                .setZIndex(2)
                .setBackground("var(--lumo-base-color)")
                .setPaddingTop("var(--lumo-space-m, .3em)")
                .set("padding-bottom", "var(--lumo-space-xs)")
                .set("margin-top", "var(--lumo-space-xs)");

        compareButton.getElement().setAttribute("data-testid", "compare-button");
        compareButton.getElement().setAttribute("data-default-action", "compare");
        compareButton.getElement().setAttribute("title", "Execute comparison for selected eligible tables.");
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

        final TextField tableFilter = filterField("Filter table", "table-filter-table", selectionDataProvider);
        tableFilter.setWidthFull();
        final Grid<TableCatalogEntry> tableGrid = buildSelectionGrid();

        final HorizontalLayout tableFilterRow = new HorizontalLayout(tableFilter);
        tableFilterRow.setPadding(true);

        layout.add(topSpacer, commandFilterRow, commandGrid, clearActionBar, tableFilterRow, tableGrid, actionBar);
        panel.add(layout);
        return panel;
    }

    private Grid<TableCatalogEntry> buildSelectionGrid() {
        final Grid<TableCatalogEntry> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "table-selection-grid");
        grid.getElement().setAttribute("tabindex", "0");
        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        grid.setPartNameGenerator(entry -> entry.eligible() ? "eligible-table-row" : "ineligible-table-row");
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
        grid.setAllRowsVisible(true);
        grid.setWidthFull();
        grid.setDataProvider(commandSelectionDataProvider);

        grid.addColumn(new ComponentRenderer<>(entry -> {
            final Checkbox checkbox = new Checkbox();
            checkbox.setEnabled(authenticatedContextHolder.isAuthenticated());
            checkbox.setValue(commandSelectionState.isSelected(entry.interactionId()));
            checkbox.getElement().setAttribute("data-testid", "command-checkbox-" + entry.interactionId().toLowerCase(Locale.ROOT));
            checkbox.addValueChangeListener(event -> {
                focusedCommandInteractionId = entry.interactionId();
                commandSelectionState.updateSelection(entry.interactionId(), event.getValue());
                applyCommandDrivenSelection();
            });
            return checkbox;
        })).setHeader("").setAutoWidth(true).setFlexGrow(0).setTextAlign(ColumnTextAlign.CENTER);

        grid.addColumn(CommandCatalogEntry::timestamp)
                .setHeader("Timestamp")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::timestamp, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("timestamp");
        grid.addColumn(CommandCatalogEntry::logicalMemberIdentifier)
                .setHeader("Member")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::logicalMemberIdentifier, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("member");
        grid.addColumn(CommandCatalogEntry::interactionId)
                .setHeader("Interaction")
                .setSortable(true)
                .setComparator(Comparator.comparing(CommandCatalogEntry::interactionId, String.CASE_INSENSITIVE_ORDER))
                .setAutoWidth(true)
                .setKey("interaction");

        grid.addItemClickListener(event -> focusedCommandInteractionId = event.getItem().interactionId());
        grid.addCellFocusListener(event -> event.getItem().ifPresent(item -> focusedCommandInteractionId = item.interactionId()));
        grid.getElement()
                .addEventListener("keydown", event -> toggleFocusedCommandSelection())
                .setFilter("event.code === 'Space' || event.key === ' '")
                .addEventData("event.code");

        return grid;
    }

    private void applyCommandFilter(final String memberIdFilter, final String interactionIdFilter) {
        commandSelectionDataProvider.clearFilters();
        commandSelectionDataProvider.addFilter(entry -> commandSelectionState.matchesFilter(entry, memberIdFilter, interactionIdFilter));
    }

    private TextField filterField(
            final String placeholder,
            final String testId,
            final ListDataProvider<TableCatalogEntry> dataProvider) {
        final TextField filter = new TextField();
        filter.setPlaceholder(placeholder);
        filter.setClearButtonVisible(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.getElement().setAttribute("data-testid", testId);
        filter.addValueChangeListener(event -> applyFilter(dataProvider, event.getValue()));
        return filter;
    }

    private void applyFilter(final ListDataProvider<TableCatalogEntry> dataProvider, final String value) {
        dataProvider.clearFilters();
        if (value != null && !value.isBlank()) {
            dataProvider.addFilter(entry -> selectionState.matchesFilter(entry, value));
        }
    }

    private void applyCommandDrivenSelection() {
        final List<String> selectedInteractionIds = commandSelectionState.selectedInteractionIds();
        final var touchedTables = commandDrivenTableSelectionService.resolveTouchedBusinessTables(selectedInteractionIds, tableCatalogEntries);
        selectionState.applyProgrammaticSelections(touchedTables);
        selectionDataProvider.refreshAll();
        refreshActionButtons();
    }

    private void clearAllSelections() {
        focusedCommandInteractionId = null;
        focusedBusinessTable = null;
        commandSelectionState.clearSelections();
        selectionState.clearSelections();
        commandSelectionDataProvider.refreshAll();
        applyCommandDrivenSelection();
    }

    private void toggleFocusedCommandSelection() {
        final String interactionId = focusedCommandInteractionId != null
                ? focusedCommandInteractionId
                : commandCatalogEntries.stream().findFirst().map(CommandCatalogEntry::interactionId).orElse(null);
        if (interactionId == null || interactionId.isBlank()) {
            return;
        }
        final boolean nextSelected = !commandSelectionState.isSelected(interactionId);
        commandSelectionState.updateSelection(interactionId, nextSelected);
        commandSelectionDataProvider.refreshAll();
        applyCommandDrivenSelection();
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
        comparisonProgressSummary.setText("Comparison running...");

        try {
            latestOutcome = comparisonExecutionService.compare(
                    MultiTableComparisonRequest.forTables(selectedTables),
                    this::onComparisonProgress);
            comparisonProgressSummary.setText("Comparison complete.");
            downloadFormatSelect.setValue(DownloadFormat.JSON);
            resultActions.setVisible(true);
            refreshDownloadLinks();
            renderComparisonTabs();
        } catch (RuntimeException ex) {
            latestOutcome = null;
            resultActions.setVisible(false);
            comparisonResultsContainer.removeAll();
            comparisonError.setText(safeError(ex));
            comparisonProgressSummary.setText("Comparison failed.");
            renderEmptyComparisonState();
        } finally {
            compareButton.setEnabled(selectionState.isCompareEnabled() && authenticatedContextHolder.isAuthenticated());
        }
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
        final Grid<ComparisonRowView> grid = new Grid<>();
        grid.getElement().setAttribute("data-testid", "comparison-grid-" + selectorToken(tableResult.table()));
        grid.setItems(tableResult.rows());
        grid.setAllRowsVisible(true);
        grid.setWidth("max-content");
        grid.getStyle().set("min-width", "100%");
        grid.addThemeNames("column-borders", "compact");

        grid.addColumn(row -> row.key().display())
                .setHeader("Business Key")
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(this::statusBadge))
                .setHeader("Status")
                .setAutoWidth(true)
                .setFlexGrow(0);

        for (ColumnRef column : tableResult.comparedColumns()) {
            final String columnName = column.name();
            grid.addColumn(new ComponentRenderer<>(row -> valueCell(row, column)))
                    .setHeader(columnName)
                    .setAutoWidth(true)
                    .setTextAlign(ColumnTextAlign.START);
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

    private void reloadTableCatalog() {
        final List<TableCatalogEntry> tableCatalog = tableCatalogService.discoverTableCatalog();
        final List<CommandCatalogEntry> commandCatalog = commandCatalogService.discoverCommandCatalog();

        selectionState = new ManualTableSelectionState(tableCatalog);
        commandSelectionState = new CommandSelectionState(commandCatalog);
        applyCommandDrivenSelection();

        tableCatalogEntries.clear();
        tableCatalogEntries.addAll(tableCatalog);
        commandCatalogEntries.clear();
        commandCatalogEntries.addAll(commandCatalog);

        selectionDataProvider.refreshAll();
        commandSelectionDataProvider.refreshAll();
        refreshActionButtons();
    }

    private void clearTableCatalog() {
        selectionState = new ManualTableSelectionState(List.of());
        commandSelectionState = new CommandSelectionState(List.of());
        applyCommandDrivenSelection();
        tableCatalogEntries.clear();
        commandCatalogEntries.clear();
        selectionDataProvider.refreshAll();
        commandSelectionDataProvider.refreshAll();
        refreshActionButtons();
    }

    private void onComparisonProgress(final ComparisonProgressEvent event) {
        if (event.phase() == ComparisonProgressPhase.TABLE_STARTED) {
            comparisonProgressSummary.setText("Comparing %s (%d/%d)".formatted(
                    event.table().displayName(),
                    event.completedTables() + 1,
                    event.totalTables()));
            return;
        }
        if (event.phase() == ComparisonProgressPhase.TABLE_COMPLETED) {
            comparisonProgressSummary.setText("Compared %s (%d/%d)".formatted(
                    event.table().displayName(),
                    event.completedTables(),
                    event.totalTables()));
            return;
        }
        comparisonProgressSummary.setText("Comparison failed on %s".formatted(event.table().displayName()));
    }

    private void refreshConnectionFooter() {
        final AuthenticatedConnectionContext context = authenticatedContextHolder.current().orElseGet(() -> {
            return new AuthenticatedConnectionContext(
                    properties.getDatasourceUrl(),
                    properties.getDatasourceDriverClassName(),
                    properties.getDatasourceUsername(),
                    properties.getDatasourcePassword(),
                    properties.getConnection().getLeftDatabase(),
                    properties.getConnection().getRightDatabase());
        });

        connectionServer.setText(safe(context.jdbcUrl()));
        connectionDatabases.setText(safe(context.leftDatabase()) + " ↔ " + safe(context.rightDatabase()));

        final ConnectionValidationStatus status = statusHolder.current();
        connectionStatusState.setText("Status: " + status.state());
        final boolean showSummary = status.state() == ConnectionValidationState.FAILED
                && status.summary() != null
                && !status.summary().isBlank();
        connectionStatusSummary.setVisible(showSummary);
        connectionStatusSummary.setText(showSummary ? status.summary() : "");
    }

    private void refreshAuthUiState() {
        final boolean authenticated = authenticatedContextHolder.isAuthenticated();
        accountMenu.setVisible(authenticated);
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
