package com.danhaywood.cfct.service;

import com.danhaywood.cfct.exception.ComparisonException;
import com.danhaywood.cfct.model.MultiTableComparisonResult;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface MultiTableComparisonService {

    MultiTableComparisonResult compare(Connection leftConnection, Connection rightConnection, MultiTableComparisonRequest request);

    default MultiTableComparisonResult compare(
            final DataSource leftDataSource,
            final DataSource rightDataSource,
            final MultiTableComparisonRequest request) {
        try (Connection leftConnection = leftDataSource.getConnection();
             Connection rightConnection = rightDataSource.getConnection()) {
            return compare(leftConnection, rightConnection, request);
        } catch (SQLException ex) {
            throw new ComparisonException("Failed to acquire comparison database connection", ex);
        }
    }
}
