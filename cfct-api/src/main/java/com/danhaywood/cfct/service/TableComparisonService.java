package com.danhaywood.cfct.service;

import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.model.TableComparisonResult;
import com.danhaywood.cfct.request.TableComparisonRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface TableComparisonService {

    TableComparisonResult compare(Connection leftConnection, Connection rightConnection, TableComparisonRequest request);

    default TableComparisonResult compare(
            final DataSource leftDataSource,
            final DataSource rightDataSource,
            final TableComparisonRequest request) {
        try (Connection leftConnection = leftDataSource.getConnection();
             Connection rightConnection = rightDataSource.getConnection()) {
            return compare(leftConnection, rightConnection, request);
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to acquire comparison database connection", ex);
        }
    }
}
