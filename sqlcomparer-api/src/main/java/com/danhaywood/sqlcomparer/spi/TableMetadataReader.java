package com.danhaywood.sqlcomparer.spi;

import com.danhaywood.sqlcomparer.model.TableMetadata;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;

import java.sql.Connection;

public interface TableMetadataReader {

    TableMetadata read(Connection connection, TableComparisonRequest request);
}
