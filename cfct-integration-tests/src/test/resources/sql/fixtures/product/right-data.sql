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
    (501, N'SKU-001', N'Widget', N'Hardware', 10.00, N'ACTIVE', '2026-04-11T09:00:00.000'),
    (502, N'SKU-002', N'Gadget', N'Hardware', 10.50, N'ACTIVE', '2026-04-12T09:00:00.000'),
    (604, N'SKU-004', N'Right Only Part', N'Spares', 30.00, N'ACTIVE', '2026-04-14T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.Product OFF;
GO
