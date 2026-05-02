package com.danhaywood.cfct.service;

import com.danhaywood.cfct.model.MultiTableComparisonResult;

public interface MultiTableComparisonReportFormatter {

    String renderText(MultiTableComparisonResult result);

    String renderJson(MultiTableComparisonResult result);

    String renderYaml(MultiTableComparisonResult result);

    byte[] renderExcel(MultiTableComparisonResult result);
}
