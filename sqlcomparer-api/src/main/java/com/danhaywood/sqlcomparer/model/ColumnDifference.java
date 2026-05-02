package com.danhaywood.cfct.model;

public record ColumnDifference(ColumnRef column, String leftValue, String rightValue) {
}
