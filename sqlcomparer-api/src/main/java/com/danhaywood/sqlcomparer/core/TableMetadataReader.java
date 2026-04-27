package com.danhaywood.sqlcomparer.core;

import java.sql.Connection;

public interface TableMetadataReader {

    TableMetadata read(Connection connection, TableComparisonRequest request);
}
