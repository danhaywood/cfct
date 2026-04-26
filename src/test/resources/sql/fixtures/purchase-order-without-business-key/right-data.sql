SET IDENTITY_INSERT dbo.PurchaseOrderWithoutBusinessKey ON;
GO
INSERT INTO dbo.PurchaseOrderWithoutBusinessKey (
    id,
    reference,
    status,
    [version]
) VALUES
    (2001, N'PO-NOBK-RIGHT', N'APPROVED', '2026-02-11T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.PurchaseOrderWithoutBusinessKey OFF;
GO
