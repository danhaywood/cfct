DROP TABLE IF EXISTS dbo.flyway_schema_history;
GO
CREATE TABLE dbo.flyway_schema_history (
    installed_rank INT NOT NULL CONSTRAINT PK_flyway_schema_history PRIMARY KEY,
    version NVARCHAR(50) NULL,
    description NVARCHAR(200) NOT NULL,
    type NVARCHAR(20) NOT NULL,
    script NVARCHAR(1000) NOT NULL,
    checksum INT NULL,
    installed_by NVARCHAR(100) NOT NULL,
    installed_on DATETIME2(3) NOT NULL,
    execution_time INT NOT NULL,
    success BIT NOT NULL,
    [version_marker] DATETIME2(3) NOT NULL
);
GO
CREATE UNIQUE INDEX flyway_schema_history_PK
ON dbo.flyway_schema_history(installed_rank);
GO
EXEC sys.sp_addextendedproperty
    @name = N'cfct.ignored',
    @value = N'true',
    @level0type = N'SCHEMA',
    @level0name = N'dbo',
    @level1type = N'TABLE',
    @level1name = N'flyway_schema_history';
GO
