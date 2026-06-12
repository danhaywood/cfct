DROP TABLE IF EXISTS dbo.ApplicationUser;

CREATE TABLE dbo.ApplicationUser (
    id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT ApplicationUser_PK PRIMARY KEY,
    accountType VARCHAR(255) NOT NULL,
    username VARCHAR(120) NOT NULL CONSTRAINT ApplicationUser__username__UNQ UNIQUE,
    status VARCHAR(255) NOT NULL,
    version BIGINT NOT NULL
);
