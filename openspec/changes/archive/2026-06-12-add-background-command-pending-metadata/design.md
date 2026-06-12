## Context

The automation REST endpoint now returns comparison JSON with selected command metadata in a top-level `command` object.
However, a foreground command can enqueue one or more background commands, and those background commands may still be pending when automation downloads `comparison.json`.
CFCT does not currently model causal relationships between foreground commands and background commands in a way that would let it block or precisely coordinate automation output.
The command catalog does expose `executeIn` and `replayState`, which are sufficient to provide an advisory pending count.

## Goals / Non-Goals

**Goals:**

- Add a top-level `backgroundCommands` object to successful automation JSON responses.
- Populate `backgroundCommands.pending` with the number of visible command catalog rows whose execution mode is background and whose replay state is pending.
- Preserve existing comparison result content and the existing `command` metadata object.
- Keep the signal advisory so automation clients can decide whether to wait and retry.

**Non-Goals:**

- Do not block the automation endpoint while background commands are pending.
- Do not infer which pending background commands were caused by the selected newest successful command.
- Do not add client-supplied wait, timeout, or consistency parameters.
- Do not change Vaadin command-grid behavior, CLI output, endpoint authentication, or error response payloads.

## Decisions

- Compute the pending count from the same command catalog snapshot used for newest successful command selection.
  This gives the response a self-consistent view of both the selected command and the background-command signal.
  The alternative was to run a second query later, but that could report a count from a different point in time than the selected command metadata.

- Treat a pending background command as a command catalog entry with `executeIn` equal to `BACKGROUND` and `replayState` equal to `PENDING`, using case-insensitive comparisons.
  This mirrors the command audit fields that CFCT already displays and avoids introducing database-specific status translation.
  The alternative was to count every non-`OK` background command, but failed or cancelled background commands are not pending work and would make the signal misleading.

- Extend the automation metadata injection path to add both `command` and `backgroundCommands` metadata in one pass.
  Keeping enrichment in the automation path avoids changing core comparison renderers and keeps CLI output unchanged.
  The `backgroundCommands` object should contain `pending` as a number, not a string, so automation clients can compare it directly.

- Continue returning successful comparison JSON even when `backgroundCommands.pending` is greater than zero.
  The field is advisory, allowing consumers to detect potential incompleteness without changing endpoint availability.

## Risks / Trade-offs

- [Risk] The count is global to the visible command catalog snapshot, not causally scoped to the selected command.
  Mitigation: Document the field as an indication of pending background commands rather than a guarantee about the selected command.

- [Risk] Future replay states may represent pending work with names other than `PENDING`.
  Mitigation: Centralize the predicate in service code so it can be adjusted if Causeway command states evolve.

- [Risk] JSON field ordering may shift as more metadata is added.
  Mitigation: Use deterministic object construction and semantic JSON assertions in tests.

## Migration Plan

No data migration is required.
Deploying the change adds a top-level `backgroundCommands` JSON object to successful automation responses.
Existing clients that ignore unknown JSON fields continue to work, while automation clients can start using `backgroundCommands.pending` as a retry signal.
Rollback removes the advisory field and returns to the previous response shape.

## Open Questions

None.
