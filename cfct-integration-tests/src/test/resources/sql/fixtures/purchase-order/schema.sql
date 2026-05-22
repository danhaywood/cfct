DROP TABLE IF EXISTS dbo.PurchaseOrderLine;
GO
DROP TABLE IF EXISTS dbo.ProductInventory;
GO
DROP TABLE IF EXISTS dbo.Product;
GO
DROP TABLE IF EXISTS dbo.Supplier;
GO
DROP TABLE IF EXISTS dbo.Customer;
GO
DROP TABLE IF EXISTS dbo.PurchaseOrder;
GO
DROP TABLE IF EXISTS dbo.PurchaseOrderTimeline;
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
CREATE TABLE dbo.Supplier (
    id INT NOT NULL CONSTRAINT PK_Supplier PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    status NVARCHAR(20) NOT NULL,
    country_code CHAR(2) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE TABLE dbo.Product (
    id INT NOT NULL CONSTRAINT PK_Product PRIMARY KEY,
    sku NVARCHAR(40) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    category NVARCHAR(40) NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    status NVARCHAR(20) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE TABLE dbo.ProductInventory (
    id INT NOT NULL CONSTRAINT PK_ProductInventory PRIMARY KEY,
    sku NVARCHAR(40) NOT NULL,
    quantity_on_hand INT NOT NULL,
    warehouse_code NVARCHAR(20) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE TABLE dbo.Customer (
    id INT NOT NULL CONSTRAINT PK_Customer PRIMARY KEY,
    customer_number NVARCHAR(40) NOT NULL,
    name NVARCHAR(100) NOT NULL,
    tier NVARCHAR(20) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
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
CREATE TABLE dbo.PurchaseOrderLine (
    id INT NOT NULL CONSTRAINT PK_PurchaseOrderLine PRIMARY KEY,
    line_reference NVARCHAR(40) NOT NULL,
    purchase_order_reference NVARCHAR(40) NOT NULL,
    sku NVARCHAR(40) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(18,2) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE TABLE dbo.PurchaseOrderTimeline (
    id INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PurchaseOrderTimeline PRIMARY KEY,
    reference NVARCHAR(40) NOT NULL,
    audit_message NVARCHAR(400) NOT NULL,
    [version] DATETIME2(3) NOT NULL
);
GO
CREATE TABLE causewayExtCommandLog.CommandLogEntry (
    interactionId UNIQUEIDENTIFIER NOT NULL,
    executeIn VARCHAR(10) NOT NULL,
    logicalMemberIdentifier VARCHAR(255) NOT NULL,
    [timestamp] DATETIME2 NOT NULL,
    completedAt DATETIME2 NULL,
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
CREATE UNIQUE INDEX Supplier_PK ON dbo.Supplier(reference);
GO
CREATE UNIQUE INDEX Product_PK ON dbo.Product(sku);
GO
CREATE UNIQUE INDEX ProductInventory_PK ON dbo.ProductInventory(sku);
GO
CREATE UNIQUE INDEX Customer_PK ON dbo.Customer(customer_number);
GO
CREATE UNIQUE INDEX PurchaseOrder_PK ON dbo.PurchaseOrder(reference);
GO
CREATE UNIQUE INDEX PurchaseOrderLine_PK ON dbo.PurchaseOrderLine(line_reference);
GO
CREATE UNIQUE INDEX PurchaseOrderTimeline_PK ON dbo.PurchaseOrderTimeline(reference);
GO
EXEC sys.sp_addextendedproperty
    @name = N'cfct.normalizeMask',
    @value = N'yyyy-MM-ddThh:MM.ss.SSS',
    @level0type = N'SCHEMA', @level0name = N'dbo',
    @level1type = N'TABLE',  @level1name = N'PurchaseOrderTimeline',
    @level2type = N'COLUMN', @level2name = N'audit_message';
GO
