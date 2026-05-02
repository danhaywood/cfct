package com.danhaywood.cfct.spi;

import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.request.TableComparisonRequest;

import java.sql.Connection;

public interface TableMetadataReader {

    TableMetadata read(Connection connection, TableComparisonRequest request);
}
