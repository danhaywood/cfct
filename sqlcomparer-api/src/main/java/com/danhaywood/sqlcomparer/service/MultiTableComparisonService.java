package com.danhaywood.sqlcomparer.service;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;
import com.danhaywood.sqlcomparer.request.MultiTableComparisonRequest;

import java.sql.Connection;

public interface MultiTableComparisonService {

    MultiTableComparisonResult compare(Connection leftConnection, Connection rightConnection, MultiTableComparisonRequest request);
}
