package com.danhaywood.sqlcomparer.core;

import java.util.List;
import java.util.stream.Collectors;

public record RowKey(List<String> values) implements Comparable<RowKey> {

    public RowKey {
        values = List.copyOf(values);
        if (values.isEmpty()) {
            throw new IllegalArgumentException("values are required");
        }
    }

    public String display() {
        return String.join(", ", values);
    }

    @Override
    public int compareTo(final RowKey other) {
        return display().compareTo(other.display());
    }

    @Override
    public String toString() {
        return values.stream().collect(Collectors.joining(", "));
    }
}
