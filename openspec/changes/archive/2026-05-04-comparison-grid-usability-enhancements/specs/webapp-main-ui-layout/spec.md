## ADDED Requirements

### Requirement: Main shell supports bounded resizable navigation drawer behavior
The AppLayout-based main shell SHALL support end-user resizing of the expanded navigation drawer width.
The shell SHALL keep drawer resizing within configured minimum and maximum bounds.
The shell SHALL preserve responsive behavior for the right-side comparison content while drawer width changes.

#### Scenario: Drawer resize updates shell layout
- **WHEN** the user resizes the expanded navigation drawer
- **THEN** the shell recomputes layout so drawer and comparison content remain visible without overlap

#### Scenario: Drawer resize honors responsive constraints
- **WHEN** the viewport is reduced while a custom drawer width is active
- **THEN** the shell applies responsive constraints so content remains usable within the available viewport