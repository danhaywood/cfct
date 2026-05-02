package com.danhaywood.cfct.spi;

import com.danhaywood.cfct.model.ColumnMetadata;

public interface IgnoreColumnAdvisor {

    boolean shouldIgnore(ColumnMetadata columnMetadata);
}
