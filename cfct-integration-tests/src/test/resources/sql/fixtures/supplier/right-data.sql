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
    (301, N'SUP-001', N'Acme Supplies', N'ACTIVE', 'GB', '2026-03-11T09:00:00.000'),
    (302, N'SUP-002', N'Beta Components', N'SUSPENDED', 'GB', '2026-03-12T09:00:00.000'),
    (404, N'SUP-004', N'Delta Industrial', N'ACTIVE', 'FR', '2026-03-14T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.Supplier OFF;
GO
