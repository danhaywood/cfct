INSERT INTO dbo.flyway_schema_history (
    installed_rank,
    version,
    description,
    type,
    script,
    checksum,
    installed_by,
    installed_on,
    execution_time,
    success,
    [version_marker]
) VALUES (
    1,
    N'1',
    N'create baseline schema',
    N'SQL',
    N'V1__create_baseline_schema.sql',
    1001,
    N'fixture',
    SYSDATETIME(),
    11,
    1,
    SYSDATETIME()
);
GO
