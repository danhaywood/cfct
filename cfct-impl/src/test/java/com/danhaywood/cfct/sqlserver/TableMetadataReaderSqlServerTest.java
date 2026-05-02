package com.danhaywood.cfct.sqlserver;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TableMetadataReaderSqlServerTest {

    @Test
    void matchesBusinessKeySuffixCaseInsensitively() {
        assertThat(TableMetadataReaderSqlServer.hasBusinessKeySuffix("PurchaseOrder_pk", "_PK")).isTrue();
    }

    @Test
    void matchesBusinessKeySuffixForCompoundNames() {
        assertThat(TableMetadataReaderSqlServer.hasBusinessKeySuffix("PurchaseOrder__reference__PK", "_PK")).isTrue();
    }

    @Test
    void doesNotMatchWhenSuffixDiffers() {
        assertThat(TableMetadataReaderSqlServer.hasBusinessKeySuffix("PurchaseOrder__reference__UK", "_PK")).isFalse();
    }
}
