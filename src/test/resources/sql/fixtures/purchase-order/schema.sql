DROP TABLE IF EXISTS dbo.PurchaseOrder;
GO
CREATE TABLE dbo.PurchaseOrder (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PurchaseOrder PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    order_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL,
    supplier_reference NVARCHAR(40) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    net_amount DECIMAL(18,2) NOT NULL,
    tax_amount DECIMAL(18,2) NOT NULL,
    gross_amount DECIMAL(18,2) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE UNIQUE INDEX PurchaseOrder_BK
ON dbo.PurchaseOrder(reference);
GO
