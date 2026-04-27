package com.danhaywood.sqlcomparer.model;

public record ColumnDifference(ColumnRef column, String leftValue, String rightValue) {
}
