package com.danhaywood.sqlcomparer.service;

import com.danhaywood.sqlcomparer.model.MultiTableComparisonResult;

public interface MultiTableComparisonReportFormatter {

    String renderText(MultiTableComparisonResult result);

    String renderJson(MultiTableComparisonResult result);

    byte[] renderExcel(MultiTableComparisonResult result);
}
