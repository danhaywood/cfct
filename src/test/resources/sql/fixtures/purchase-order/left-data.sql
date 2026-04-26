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
