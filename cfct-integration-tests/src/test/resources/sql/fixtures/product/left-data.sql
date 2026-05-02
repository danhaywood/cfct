SET IDENTITY_INSERT dbo.Product ON;
GO
INSERT INTO dbo.Product (
    id,
    sku,
    name,
    category,
    unit_price,
    status,
    [version]
) VALUES
    (501, N'SKU-001', N'Widget', N'Hardware', 10.00, N'ACTIVE', '2026-04-01T09:00:00.000'),
    (502, N'SKU-002', N'Gadget', N'Hardware', 10.00, N'ACTIVE', '2026-04-02T09:00:00.000'),
    (503, N'SKU-003', N'Left Only Part', N'Spares', 25.00, N'ACTIVE', '2026-04-03T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.Product OFF;
GO
