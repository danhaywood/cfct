IF OBJECT_ID('dbo.sample_items', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.sample_items (
        id INT PRIMARY KEY,
        payload NVARCHAR(255) NOT NULL
    )
END
GO
MERGE dbo.sample_items AS target
USING (SELECT 1 AS id, N'right payload' AS payload) AS source
ON target.id = source.id
WHEN MATCHED THEN
    UPDATE SET payload = source.payload
WHEN NOT MATCHED THEN
    INSERT (id, payload) VALUES (source.id, source.payload);
GO
