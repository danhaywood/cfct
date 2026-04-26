package com.danhaywood.sqlcomparer.core;

public record ColumnDifference(ColumnRef column, String leftValue, String rightValue) {
}
