package com.danhaywood.sqlcomparer.core;

import java.sql.Connection;
import java.util.Map;

public interface TableRowReader {

    Map<RowKey, Map<ColumnRef, String>> readRows(Connection connection, TableMetadata metadata);
}
