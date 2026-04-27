package com.danhaywood.sqlcomparer.spi;

import com.danhaywood.sqlcomparer.model.ColumnRef;
import com.danhaywood.sqlcomparer.model.RowKey;
import com.danhaywood.sqlcomparer.model.TableMetadata;

import java.sql.Connection;
import java.util.Map;

public interface TableRowReader {

    Map<RowKey, Map<ColumnRef, String>> readRows(Connection connection, TableMetadata metadata);
}
