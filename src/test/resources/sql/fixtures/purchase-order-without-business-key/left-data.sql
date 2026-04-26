SET IDENTITY_INSERT dbo.PurchaseOrderWithoutBusinessKey ON;
GO
INSERT INTO dbo.PurchaseOrderWithoutBusinessKey (
    id,
    reference,
    status,
    [version]
) VALUES
    (1001, N'PO-NOBK-LEFT', N'APPROVED', '2026-02-01T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.PurchaseOrderWithoutBusinessKey OFF;
GO
