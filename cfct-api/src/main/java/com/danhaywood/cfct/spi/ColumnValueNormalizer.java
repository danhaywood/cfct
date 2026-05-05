package com.danhaywood.cfct.spi;

import com.danhaywood.cfct.model.ColumnMetadata;

public interface ColumnValueNormalizer {

    NormalizedValues normalize(ColumnMetadata columnMetadata, String leftValue, String rightValue);

    record NormalizedValues(String leftValue, String rightValue) {
    }
}
