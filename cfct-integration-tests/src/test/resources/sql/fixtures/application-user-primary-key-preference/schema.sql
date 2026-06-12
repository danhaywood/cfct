DROP TABLE IF EXISTS dbo.ApplicationUserPrimaryKeyPreference;

CREATE TABLE dbo.ApplicationUserPrimaryKeyPreference (
    id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT ApplicationUserPrimaryKeyPreference_PK PRIMARY KEY,
    username VARCHAR(120) NOT NULL CONSTRAINT ApplicationUserPrimaryKeyPreference__username__PK UNIQUE,
    status VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL
);
