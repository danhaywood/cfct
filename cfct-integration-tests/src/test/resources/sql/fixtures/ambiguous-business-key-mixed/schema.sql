DROP TABLE IF EXISTS dbo.AmbiguousBusinessKeyMixed;
GO
CREATE TABLE dbo.AmbiguousBusinessKeyMixed (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_AmbiguousBusinessKeyMixed PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    external_reference NVARCHAR(40) NOT NULL,
    payload NVARCHAR(40) NOT NULL,
    CONSTRAINT AmbiguousBusinessKeyMixed__external_reference__PK UNIQUE(external_reference)
);
GO
CREATE UNIQUE INDEX AmbiguousBusinessKeyMixed__reference__PK
ON dbo.AmbiguousBusinessKeyMixed(reference);
GO
