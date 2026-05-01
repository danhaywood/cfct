DROP TABLE IF EXISTS dbo.PurchaseOrder;
GO
IF SCHEMA_ID('causewayExtCommandLog') IS NULL EXEC('CREATE SCHEMA causewayExtCommandLog');
GO
IF SCHEMA_ID('causewayExtAuditTrail') IS NULL EXEC('CREATE SCHEMA causewayExtAuditTrail');
GO
IF SCHEMA_ID('util') IS NULL EXEC('CREATE SCHEMA util');
GO
DROP TABLE IF EXISTS causewayExtAuditTrail.AuditTrailEntry;
GO
DROP TABLE IF EXISTS causewayExtCommandLog.CommandLogEntry;
GO
CREATE TABLE dbo.PurchaseOrder (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PurchaseOrder PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    order_date DATE NOT NULL,
    status NVARCHAR(20) NOT NULL,
    supplier_reference NVARCHAR(40) NOT NULL,
    currency_code CHAR(3) NOT NULL,
    net_amount DECIMAL(18,2) NOT NULL,
    tax_amount DECIMAL(18,2) NOT NULL,
    gross_amount DECIMAL(18,2) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE TABLE causewayExtCommandLog.CommandLogEntry (
    interactionId UNIQUEIDENTIFIER NOT NULL,
    executeIn VARCHAR(10) NOT NULL,
    logicalMemberIdentifier VARCHAR(255) NOT NULL,
    [timestamp] DATETIME2 NOT NULL,
    target VARCHAR(1500) NOT NULL,
    replayState VARCHAR(20) NOT NULL,
    CONSTRAINT PK_CommandLogEntry PRIMARY KEY (interactionId)
);
GO
CREATE TABLE causewayExtAuditTrail.AuditTrailEntry (
    interactionId UNIQUEIDENTIFIER NOT NULL,
    sequence INT NOT NULL,
    target VARCHAR(1500) NOT NULL,
    propertyId VARCHAR(100) NOT NULL,
    CONSTRAINT PK_AuditTrailEntry PRIMARY KEY (interactionId, sequence, target, propertyId),
    CONSTRAINT FK_AuditTrailEntry_CommandLogEntry_InteractionId
        FOREIGN KEY (interactionId)
        REFERENCES causewayExtCommandLog.CommandLogEntry (interactionId)
);
GO
IF OBJECT_ID(N'util.LogicalTypeTableMapping', N'U') IS NULL
BEGIN
    CREATE TABLE util.LogicalTypeTableMapping (
        logicalTypeName NVARCHAR(255) NULL,
        qualifiedName NVARCHAR(255) NOT NULL
    );
END;
GO
CREATE UNIQUE INDEX PurchaseOrder_PK
ON dbo.PurchaseOrder(reference);
GO
