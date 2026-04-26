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
    (201, N'PO-001', '2026-01-01', N'APPROVED', N'SUP-ACME', 'GBP', 100.00, 20.00, 120.00, '2026-01-11T09:00:00.000'),
    (202, N'PO-002', '2026-01-02', N'APPROVED', N'SUP-BETA', 'GBP', 250.00, 50.00, 300.00, '2026-01-12T09:00:00.000'),
    (204, N'PO-004', '2026-01-04', N'APPROVED', N'SUP-DELTA', 'EUR', 400.00, 80.00, 480.00, '2026-01-14T09:00:00.000'),
    (205, N'PO-005', '2026-01-05', N'APPROVED', N'SUP-EPSILON', 'USD', 100.01, 0.00, 100.01, '2026-01-15T09:00:00.000'),
    (9006, N'PO-006', '2026-01-06', N'APPROVED', N'SUP-ZETA', 'GBP', 123.45, 24.69, 148.14, '2026-01-16T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.PurchaseOrder OFF;
GO
