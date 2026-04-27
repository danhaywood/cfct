DROP TABLE IF EXISTS dbo.AmbiguousBusinessKey;
GO
CREATE TABLE dbo.AmbiguousBusinessKey (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_AmbiguousBusinessKey PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    external_reference NVARCHAR(40) NOT NULL,
    payload NVARCHAR(40) NOT NULL
);
GO
CREATE UNIQUE INDEX AmbiguousBusinessKey_PK
ON dbo.AmbiguousBusinessKey(reference);
GO
CREATE UNIQUE INDEX AmbiguousBusinessKeyExternal_PK
ON dbo.AmbiguousBusinessKey(external_reference);
GO
