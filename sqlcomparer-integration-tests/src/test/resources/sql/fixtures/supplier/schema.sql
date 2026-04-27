DROP TABLE IF EXISTS dbo.Supplier;
GO
CREATE TABLE dbo.Supplier (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Supplier PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status NVARCHAR(20) NOT NULL,
    country_code CHAR(2) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE UNIQUE INDEX Supplier_BK
ON dbo.Supplier(reference);
GO
