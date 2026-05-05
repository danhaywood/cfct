DROP TABLE IF EXISTS dbo.NormalizedTimestampNoise;
GO
CREATE TABLE dbo.NormalizedTimestampNoise (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_NormalizedTimestampNoise PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    payload NVARCHAR(400) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE UNIQUE INDEX NormalizedTimestampNoise_PK
ON dbo.NormalizedTimestampNoise(reference);
GO
