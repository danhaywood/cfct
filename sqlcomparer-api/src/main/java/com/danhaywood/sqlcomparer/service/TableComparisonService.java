package com.danhaywood.sqlcomparer.service;

import com.danhaywood.sqlcomparer.model.TableComparisonResult;
import com.danhaywood.sqlcomparer.request.TableComparisonRequest;

import java.sql.Connection;

public interface TableComparisonService {

    TableComparisonResult compare(Connection leftConnection, Connection rightConnection, TableComparisonRequest request);
}
