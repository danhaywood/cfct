INSERT INTO dbo.Supplier (
    id,
    reference,
    name,
    status,
    country_code,
    [version]
) VALUES
    (301, N'SUP-ACME', N'Acme Supplies', N'ACTIVE', 'GB', '2026-04-11T09:00:00.000'),
    (302, N'SUP-BETA', N'Beta Trading Co', N'ACTIVE', 'US', '2026-04-11T09:05:00.000'),
    (304, N'SUP-DELTA', N'Delta Services', N'ACTIVE', 'FR', '2026-04-11T09:10:00.000');
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
    (701, N'SKU-701', N'Widget 701', N'Hardware', 12.50, N'ACTIVE', '2026-04-12T10:00:00.000'),
    (702, N'SKU-702', N'Widget 702', N'Hardware', 18.49, N'ACTIVE', '2026-04-12T10:05:00.000'),
    (704, N'SKU-704', N'Widget 704', N'Service', 4.99, N'ACTIVE', '2026-04-12T10:10:00.000');
GO

INSERT INTO dbo.ProductInventory (
    id,
    sku,
    quantity_on_hand,
    warehouse_code,
    [version]
) VALUES
    (801, N'SKU-701', 115, N'WH-A', '2026-04-12T11:00:00.000'),
    (802, N'SKU-702', 35, N'WH-A', '2026-04-12T11:05:00.000'),
    (804, N'SKU-704', 20, N'WH-C', '2026-04-12T11:10:00.000');
GO

INSERT INTO dbo.Customer (
    id,
    customer_number,
    name,
    tier,
    [version]
) VALUES
    (501, N'CUST-501', N'Example Retail Ltd', N'SILVER', '2026-04-13T08:00:00.000'),
    (503, N'CUST-503', N'Northwind Foods', N'GOLD', '2026-04-13T08:05:00.000');
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
    (101, N'PO-001', '2026-01-01', N'APPROVED', N'SUP-ACME', 'GBP', 100.00, 20.00, 120.00, '2026-01-11T09:00:00.000'),
    (202, N'PO-002', '2026-01-02', N'APPROVED', N'SUP-BETA', 'GBP', 250.00, 50.00, 300.00, '2026-01-12T09:00:00.000'),
    (204, N'PO-004', '2026-01-04', N'APPROVED', N'SUP-DELTA', 'EUR', 400.00, 80.00, 480.00, '2026-01-14T09:00:00.000'),
    (205, N'PO-005', '2026-01-05', N'APPROVED', N'SUP-EPSILON', 'USD', 100.01, 0.00, 100.01, '2026-01-15T09:00:00.000'),
    (9006, N'PO-006', '2026-01-06', N'APPROVED', N'SUP-ZETA', 'GBP', 123.45, 24.69, 148.14, '2026-01-06T09:00:00.000');
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
    (901, N'POL-001', N'PO-001', N'SKU-701', 5, 12.50, '2026-01-11T10:00:00.000'),
    (904, N'POL-004', N'PO-004', N'SKU-704', 2, 4.99, '2026-01-14T10:00:00.000');
GO

INSERT INTO causewayExtCommandLog.CommandLogEntry (
    interactionId,
    executeIn,
    logicalMemberIdentifier,
    [timestamp],
    target,
    replayState
) VALUES
    ('11111111-1111-1111-1111-111111111111', 'FOREGROUND', 'supplier.Supplier#registerProduct', '2026-04-05T10:00:00.000', 'supplier.Supplier:301', 'PENDING'),
    ('44444444-4444-4444-4444-444444444444', 'FOREGROUND', 'supplier.Supplier#updateContact', '2026-04-05T10:10:00.000', 'supplier.Supplier:302', 'PENDING'),
    ('55555555-5555-5555-5555-555555555555', 'FOREGROUND', 'purchaseorder.PurchaseOrder#approve', '2026-04-05T10:20:00.000', 'purchaseorder.PurchaseOrder:101', 'PENDING'),
    ('66666666-6666-6666-6666-666666666666', 'FOREGROUND', 'customer.Customer#onboard', '2026-04-05T10:30:00.000', 'customer.Customer:501', 'PENDING'),
    ('77777777-7777-7777-7777-777777777777', 'BACKGROUND', 'product.Product#adjustInventory', '2026-04-05T10:40:00.000', 'product.Product:702', 'PENDING');
GO

INSERT INTO causewayExtAuditTrail.AuditTrailEntry (
    interactionId,
    sequence,
    target,
    propertyId
) VALUES
    ('44444444-4444-4444-4444-444444444444', 1, 'supplier.Supplier:302', 'country_code'),
    ('55555555-5555-5555-5555-555555555555', 1, 'purchaseorder.PurchaseOrder:101', 'status'),
    ('55555555-5555-5555-5555-555555555555', 2, 'purchaseorder.PurchaseOrderLine:901', 'quantity'),
    ('66666666-6666-6666-6666-666666666666', 1, 'customer.Customer:501', 'tier'),
    ('77777777-7777-7777-7777-777777777777', 1, 'product.Product:702', 'status');
GO

DELETE FROM util.LogicalTypeTableMapping
WHERE logicalTypeName IN ('supplier.Supplier', 'product.Product', 'customer.Customer', 'purchaseorder.PurchaseOrder', 'purchaseorder.PurchaseOrderLine');
GO

INSERT INTO util.LogicalTypeTableMapping (
    logicalTypeName,
    qualifiedName
) VALUES
    ('supplier.Supplier', 'dbo.Supplier'),
    ('product.Product', 'dbo.Product'),
    ('product.Product', 'dbo.ProductInventory'),
    ('customer.Customer', 'dbo.Customer'),
    ('purchaseorder.PurchaseOrder', 'dbo.PurchaseOrder'),
    ('purchaseorder.PurchaseOrderLine', 'dbo.PurchaseOrderLine');
GO
