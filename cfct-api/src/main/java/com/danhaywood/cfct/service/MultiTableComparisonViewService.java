package com.danhaywood.cfct.service;

import com.danhaywood.cfct.model.MultiTableComparisonViewResult;
import com.danhaywood.cfct.request.MultiTableComparisonRequest;

import javax.sql.DataSource;
import java.sql.Connection;

public interface MultiTableComparisonViewService {

    MultiTableComparisonViewResult compare(Connection leftConnection, Connection rightConnection, MultiTableComparisonRequest request);

    default MultiTableComparisonViewResult compare(
            final DataSource leftDataSource,
            final DataSource rightDataSource,
            final MultiTableComparisonRequest request) {
        try (Connection leftConnection = leftDataSource.getConnection();
             Connection rightConnection = rightDataSource.getConnection()) {
            return compare(leftConnection, rightConnection, request);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to compare selected tables using DataSources.", ex);
        }
    }
}
