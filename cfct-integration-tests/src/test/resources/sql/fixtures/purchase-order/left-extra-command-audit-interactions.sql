INSERT INTO causewayExtCommandLog.CommandLogEntry (
    interactionId,
    executeIn,
    logicalMemberIdentifier,
    [timestamp],
    target,
    replayState
) VALUES
    ('22222222-2222-2222-2222-222222222222', 'FOREGROUND', 'supplier.Supplier#updateName', '2026-04-05T10:30:00.000', 'supplier.Supplier:302', 'EXPORTED'),
    ('33333333-3333-3333-3333-333333333333', 'FOREGROUND', 'product.Product#changeStatus', '2026-04-05T11:00:00.000', 'product.Product:702', 'EXPORTED');
GO

INSERT INTO causewayExtAuditTrail.AuditTrailEntry (
    interactionId,
    sequence,
    target,
    propertyId
) VALUES
    ('22222222-2222-2222-2222-222222222222', 1, 'supplier.Supplier:302', 'name'),
    ('22222222-2222-2222-2222-222222222222', 2, 'malformedTargetWithoutSeparator', 'ignored'),
    ('22222222-2222-2222-2222-222222222222', 3, 'unknown.Type:1', 'ignored'),
    ('33333333-3333-3333-3333-333333333333', 1, 'product.Product:702', 'status');
GO
