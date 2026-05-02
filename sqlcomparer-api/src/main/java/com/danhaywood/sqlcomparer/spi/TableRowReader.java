package com.danhaywood.cfct.spi;

import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.RowKey;
import com.danhaywood.cfct.model.TableMetadata;

import java.sql.Connection;
import java.util.Map;

public interface TableRowReader {

    Map<RowKey, Map<ColumnRef, String>> readRows(Connection connection, TableMetadata metadata);
}
