SET IDENTITY_INSERT dbo.NormalizedTimestampNoise ON;
GO
INSERT INTO dbo.NormalizedTimestampNoise (
    id,
    reference,
    payload,
    [version]
) VALUES
    (701, N'NTN-001', N'2026-05-21T15:09:10.111 - VT - [RENT, RENT_FIXED] - 2026-06-01 - 2026-06-01/2026-07-01', '2026-03-01T09:00:00.000'),
    (702, N'NTN-002', N'2027-06-22T16:10:11.222 - VT - [RENT, RENT_VARIABLE] - 2026-06-01 - 2026-06-01/2026-07-01', '2026-03-02T09:00:00.000');
GO
SET IDENTITY_INSERT dbo.NormalizedTimestampNoise OFF;
GO

EXEC sys.sp_addextendedproperty
    @name = N'cfct.normalizeMask',
    @value = N'yyyy-MM-ddThh:MM.ss.SSS',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE',  @level1name = N'NormalizedTimestampNoise',
    @level2type = N'COLUMN', @level2name = N'payload';
GO
