# Implementation Notes

Comparison results now hide MATCH rows by default and expose a `Show MATCH rows` toggle in the result actions row.
Comparison result columns are sortable and include per-column value filter inputs in the grid header.
The Compare button now uses primary-action styling and remains visible above selection content in constrained drawer heights.
The navigation drawer supports end-user width resizing via a right-edge resize handle with min/max bounds and in-session persistence.
Relevant verification was run with `MainViewTest` and `HomePageConnectionStatusPlaywrightSuccessTest` in `cfct-webapp`.