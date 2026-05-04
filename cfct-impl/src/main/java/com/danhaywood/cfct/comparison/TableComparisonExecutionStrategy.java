package com.danhaywood.cfct.comparison;

import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.model.TableMetadata;

import java.sql.Connection;

interface TableComparisonExecutionStrategy {

    boolean supports(Connection leftConnection, Connection rightConnection, TableMetadata metadata);

    TableComparisonResult compare(Connection leftConnection, Connection rightConnection, TableMetadata metadata);
}
