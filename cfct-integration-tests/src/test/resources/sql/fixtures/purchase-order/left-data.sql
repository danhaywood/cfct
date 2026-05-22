INSERT INTO dbo.Supplier (
    id,
    reference,
    name,
    status,
    country_code,
    [version]
) VALUES
    (301, N'SUP-ACME', N'Acme Supplies', N'ACTIVE', 'GB', '2026-04-01T09:00:00.000'),
    (302, N'SUP-BETA', N'Beta Traders', N'ACTIVE', 'US', '2026-04-01T09:05:00.000'),
    (303, N'SUP-GAMMA', N'Gamma Wholesale', N'ONBOARDING', 'DE', '2026-04-01T09:10:00.000');
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
    (701, N'SKU-701', N'Widget 701', N'Hardware', 12.50, N'ACTIVE', '2026-04-02T10:00:00.000'),
    (702, N'SKU-702', N'Widget 702', N'Hardware', 19.99, N'ACTIVE', '2026-04-02T10:05:00.000'),
    (703, N'SKU-703', N'Widget 703', N'Service', 3.99, N'RETIRED', '2026-04-02T10:10:00.000');
GO

INSERT INTO dbo.ProductInventory (
    id,
    sku,
    quantity_on_hand,
    warehouse_code,
    [version]
) VALUES
    (801, N'SKU-701', 120, N'WH-A', '2026-04-02T11:00:00.000'),
    (802, N'SKU-702', 45, N'WH-A', '2026-04-02T11:05:00.000'),
    (803, N'SKU-703', 0, N'WH-B', '2026-04-02T11:10:00.000');
GO

INSERT INTO dbo.Customer (
    id,
    customer_number,
    name,
    tier,
    [version]
) VALUES
    (501, N'CUST-501', N'Example Retail Ltd', N'GOLD', '2026-04-03T08:00:00.000'),
    (502, N'CUST-502', N'Corner Shop Ltd', N'SILVER', '2026-04-03T08:05:00.000');
GO

SET IDENTITY_INSERT dbo.PurchaseOrder ON;
GO
INSERT INTO dbo.PurchaseOrder (
    id,
    reference,
    order_date,
    status,
    supplier_reference,
    currency_code,
    net_amount,
    tax_amount,
    gross_amount,
    [version]
) VALUES
    (101, N'PO-001', '2026-01-01', N'APPROVED', N'SUP-ACME', 'GBP', 100.00, 20.00, 120.00, '2026-01-01T09:00:00.000'),
    (102, N'PO-002', '2026-01-02', N'DRAFT', N'SUP-BETA', 'GBP', 250.00, 50.00, 300.00, '2026-01-02T09:00:00.000'),
    (103, N'PO-003', '2026-01-03', N'APPROVED', N'SUP-GAMMA', 'EUR', 300.00, 60.00, 360.00, '2026-01-03T09:00:00.000'),
    (105, N'PO-005', '2026-01-05', N'APPROVED', N'SUP-EPSILON', 'USD', 100.00, 0.00, 100.00, '2026-01-05T09:00:00.000'),
    (106, N'PO-006', '2026-01-06', N'APPROVED', N'SUP-ZETA', 'GBP', 123.45, 24.69, 148.14, '2026-01-06T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.PurchaseOrder OFF;
GO

INSERT INTO dbo.PurchaseOrderLine (
    id,
    line_reference,
    purchase_order_reference,
    sku,
    quantity,
    unit_price,
    [version]
) VALUES
    (901, N'POL-001', N'PO-001', N'SKU-701', 5, 12.50, '2026-01-01T10:00:00.000'),
    (902, N'POL-002', N'PO-002', N'SKU-702', 10, 19.99, '2026-01-02T10:00:00.000'),
    (903, N'POL-003', N'PO-003', N'SKU-703', 1, 3.99, '2026-01-03T10:00:00.000');
GO

SET IDENTITY_INSERT dbo.PurchaseOrderTimeline ON;
GO
INSERT INTO dbo.PurchaseOrderTimeline (
    id,
    reference,
    audit_message,
    [version]
) VALUES
    (401, N'POT-001', N'2026-04-20T14:08:09.050 - VT - [RENT, RENT_FIXED] - 2026-06-01 - 2026-06-01/2026-07-01', '2026-01-01T09:00:00.000'),
    (402, N'POT-002', N'2026-04-20T14:08:09.050 - VT - [RENT, RENT_FIXED] - 2026-06-01 - 2026-06-01/2026-07-01', '2026-01-02T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.PurchaseOrderTimeline OFF;
GO

INSERT INTO causewayExtCommandLog.CommandLogEntry (
    interactionId,
    executeIn,
    logicalMemberIdentifier,
    [timestamp],
    completedAt,
    target,
    replayState
) VALUES
    ('11111111-1111-1111-1111-111111111111', 'FOREGROUND', 'supplier.Supplier#registerProduct', '2026-04-05T10:00:00.000', null, 'supplier.Supplier:301', 'EXPORTED'),
    ('44444444-4444-4444-4444-444444444444', 'FOREGROUND', 'supplier.Supplier#updateContact', '2026-04-05T10:10:00.000', null, 'supplier.Supplier:302', 'EXPORTED'),
    ('55555555-5555-5555-5555-555555555555', 'FOREGROUND', 'purchaseorder.PurchaseOrder#approve', '2026-04-05T10:20:00.000', null, 'purchaseorder.PurchaseOrder:101', 'EXPORTED'),
    ('66666666-6666-6666-6666-666666666666', 'BACKGROUND', 'customer.Customer#onboard', '2026-04-05T10:30:00.000', null, 'customer.Customer:501', 'UNDEFINED'),
    ('77777777-7777-7777-7777-777777777777', 'BACKGROUND', 'product.Product#adjustInventory', '2026-04-05T10:40:00.000', '2026-04-05T10:41:00.000', 'product.Product:702', 'UNDEFINED');
GO

INSERT INTO causewayExtAuditTrail.AuditTrailEntry (
    interactionId,
    sequence,
    target,
    propertyId
) VALUES
    ('11111111-1111-1111-1111-111111111111', 1, 'product.Product:701', 'sku'),
    ('11111111-1111-1111-1111-111111111111', 2, 'product.Product:701', 'name'),
    ('11111111-1111-1111-1111-111111111111', 3, 'product.Product:701', 'status'),
    ('44444444-4444-4444-4444-444444444444', 1, 'supplier.Supplier:302', 'name'),
    ('44444444-4444-4444-4444-444444444444', 2, 'supplier.Supplier:302', 'country_code'),
    ('55555555-5555-5555-5555-555555555555', 1, 'purchaseorder.PurchaseOrder:101', 'status'),
    ('55555555-5555-5555-5555-555555555555', 2, 'purchaseorder.PurchaseOrderLine:901', 'quantity'),
    ('66666666-6666-6666-6666-666666666666', 1, 'customer.Customer:501', 'tier'),
    ('66666666-6666-6666-6666-666666666666', 2, 'customer.CustomerAddress:501', 'line1'),
    ('77777777-7777-7777-7777-777777777777', 1, 'product.Product:702', 'status');
GO

DELETE FROM util.LogicalTypeTableMapping
WHERE logicalTypeName IN ('supplier.Supplier', 'product.Product', 'customer.Customer', 'customer.CustomerAddress', 'purchaseorder.PurchaseOrder', 'purchaseorder.PurchaseOrderLine');
GO

INSERT INTO util.LogicalTypeTableMapping (
    logicalTypeName,
    qualifiedName
) VALUES
    ('supplier.Supplier', 'dbo.Supplier'),
    ('product.Product', 'dbo.Product'),
    ('product.Product', 'dbo.ProductInventory'),
    ('customer.Customer', 'dbo.Customer'),
    ('customer.CustomerAddress', 'dbo.CustomerAddress'),
    ('purchaseorder.PurchaseOrder', 'dbo.PurchaseOrder'),
    ('purchaseorder.PurchaseOrderLine', 'dbo.PurchaseOrderLine');
GO
