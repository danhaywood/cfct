SET IDENTITY_INSERT dbo.CustomerAddress ON;
GO
INSERT INTO dbo.CustomerAddress (
    id,
    reference,
    customer_reference,
    line1,
    city,
    postcode,
    [version]
) VALUES
    (501, N'ADDR-001', N'CUS-001', N'10 High Street', N'Bristol', N'BS1 1AA', '2026-03-01T09:00:00.000'),
    (502, N'ADDR-002', N'CUS-002', N'22 River Road', N'Bath', N'BA1 2BB', '2026-03-02T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.CustomerAddress OFF;
GO
