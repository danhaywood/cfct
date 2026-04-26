SET IDENTITY_INSERT dbo.Supplier ON;
GO
INSERT INTO dbo.Supplier (
    id,
    reference,
    name,
    status,
    country_code,
    [version]
) VALUES
    (301, N'SUP-001', N'Acme Supplies', N'ACTIVE', 'GB', '2026-03-01T09:00:00.000'),
    (302, N'SUP-002', N'Beta Components', N'ACTIVE', 'GB', '2026-03-02T09:00:00.000'),
    (303, N'SUP-003', N'Gamma Logistics', N'ACTIVE', 'DE', '2026-03-03T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.Supplier OFF;
GO
