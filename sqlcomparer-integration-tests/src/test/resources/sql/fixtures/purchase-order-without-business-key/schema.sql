DROP TABLE IF EXISTS dbo.PurchaseOrderWithoutBusinessKey;
GO
CREATE TABLE dbo.PurchaseOrderWithoutBusinessKey (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PurchaseOrderWithoutBusinessKey PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    status NVARCHAR(20) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
