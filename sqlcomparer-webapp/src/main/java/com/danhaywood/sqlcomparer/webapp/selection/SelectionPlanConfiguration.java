package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(ExplicitSelectionPlanProperties.class)
public class SelectionPlanConfiguration {

    @Bean
    public SelectionPlan selectionPlan(final ExplicitSelectionPlanProperties properties) {
        final List<String> configuredTables = properties.getTables();
        if (configuredTables == null || configuredTables.isEmpty()) {
            throw new IllegalArgumentException("sqlcomparer.webapp.selection-plan.explicit.tables requires at least one schema.table entry");
        }

        final List<TableRef> resolvedTables = new ArrayList<>();
        for (String tableToken : configuredTables) {
            resolvedTables.add(parseTableToken(tableToken));
        }
        return new ExplicitSelectionPlan(resolvedTables);
    }

    private TableRef parseTableToken(final String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Invalid explicit table reference: blank value");
        }
        final String normalized = token.trim();
        final int separator = normalized.indexOf('.');
        if (separator <= 0 || separator != normalized.lastIndexOf('.') || separator == normalized.length() - 1) {
            throw new IllegalArgumentException("Invalid explicit table reference '%s'. Expected schema.table".formatted(token));
        }
        final String schemaName = normalized.substring(0, separator).trim();
        final String tableName = normalized.substring(separator + 1).trim();
        if (schemaName.isBlank() || tableName.isBlank()) {
            throw new IllegalArgumentException("Invalid explicit table reference '%s'. Expected schema.table".formatted(token));
        }
        return new TableRef(schemaName, tableName);
    }
}
