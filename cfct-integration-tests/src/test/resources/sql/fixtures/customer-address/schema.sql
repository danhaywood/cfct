DROP TABLE IF EXISTS dbo.CustomerAddress;
GO
CREATE TABLE dbo.CustomerAddress (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_CustomerAddress PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    customer_reference NVARCHAR(40) NOT NULL,
    line1 NVARCHAR(120) NOT NULL,
    city NVARCHAR(80) NOT NULL,
    postcode NVARCHAR(20) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE UNIQUE INDEX CustomerAddress_PK
ON dbo.CustomerAddress(reference);
GO
