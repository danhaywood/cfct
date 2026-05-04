package com.danhaywood.cfct.sqlserver;

import com.danhaywood.cfct.model.BusinessKey;
import com.danhaywood.cfct.model.ColumnMetadata;
import com.danhaywood.cfct.model.ColumnRef;
import com.danhaywood.cfct.model.TableMetadata;
import com.danhaywood.cfct.model.TableRef;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SqlServerTableDiffQueryBuilderTest {

    private final SqlServerTableDiffQueryBuilder builder = new SqlServerTableDiffQueryBuilder();

    @Test
    void buildsCompatibilityLevel100SafeQueryWithSetBranches() {
        final TableMetadata metadata = new TableMetadata(
                new TableRef("dbo", "PurchaseOrder"),
                new BusinessKey("PurchaseOrder_PK", List.of(new ColumnRef("reference"))),
                List.of(
                        new ColumnMetadata(new ColumnRef("reference"), false, "nvarchar"),
                        new ColumnMetadata(new ColumnRef("status"), false, "nvarchar"),
                        new ColumnMetadata(new ColumnRef("net_amount"), false, "decimal")),
                List.of(new ColumnRef("reference")),
                List.of(),
                List.of(new ColumnRef("status"), new ColumnRef("net_amount")));

        final String sql = builder.buildQuery(metadata, "cfct_left", "cfct_right");

        assertThat(sql).contains("EXCEPT");
        assertThat(sql).contains("UNION ALL");
        assertThat(sql).contains("'ONLY_IN_LEFT' AS diff_kind");
        assertThat(sql).contains("'ONLY_IN_RIGHT' AS diff_kind");
        assertThat(sql).contains("'DIFFERENT' AS diff_kind");
        assertThat(sql).contains("[cfct_left].[dbo].[PurchaseOrder]");
        assertThat(sql).contains("[cfct_right].[dbo].[PurchaseOrder]");
        assertThat(sql).doesNotContainIgnoringCase(" OFFSET ");
        assertThat(sql).doesNotContainIgnoringCase(" FETCH ");
        assertThat(sql).doesNotContainIgnoringCase(" IIF(");
        assertThat(sql).doesNotContainIgnoringCase(" TRY_CONVERT(");
        assertThat(sql).doesNotContainIgnoringCase(" CONCAT(");
        assertThat(sql).doesNotContainIgnoringCase(" STRING_AGG(");
    }
}
