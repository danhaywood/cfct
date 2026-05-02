DROP TABLE IF EXISTS dbo.BusinessKeyConstraintSuffix;
GO
CREATE TABLE dbo.BusinessKeyConstraintSuffix (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_BusinessKeyConstraintSuffix PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    payload NVARCHAR(40) NOT NULL,
    CONSTRAINT BusinessKeyConstraintSuffix__reference__PK UNIQUE(reference)
);
GO
