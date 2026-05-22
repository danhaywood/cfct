## Context

The command-selection grid already exposes a row context menu for baseline actions.
Users often need to share command diagnostics externally, especially logical member and interaction ID.
A direct copy action in the same menu improves usability and reduces transcription mistakes.

## Goals / Non-Goals

**Goals:**
- Add a context-menu command to copy selected row details to clipboard.
- Ensure copied payload includes member and command GUID with predictable formatting.
- Keep existing context-menu behaviors and command selection semantics unchanged.

**Non-Goals:**
- Adding bulk-copy across multiple rows.
- Introducing server-side persistence or audit for clipboard actions.
- Changing command-grid filtering or ordering behavior.

## Decisions

Add a new context-menu item on command rows labeled for copy behavior.
Format clipboard content as a short single-line payload including `member=<value>` and `interactionId=<value>`.
Use browser clipboard APIs from the Vaadin UI event path with fallback user notification if clipboard write fails.
Retain existing baseline action and ordering, placing copy action near existing row actions.
Add tests to assert action presence and copied payload generation path.

## Risks / Trade-offs

[Risk] Clipboard API restrictions can vary by browser context. → Mitigation: keep action user-triggered and provide graceful fallback messaging.
[Risk] Users may expect richer payload fields later. → Mitigation: keep format deterministic and extensible.
[Risk] Context-menu growth may impact discoverability. → Mitigation: keep labels concise and action grouping clear.
