package com.danhaywood.cfct.webapp.selection;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "cfct.webapp.selection-plan.explicit")
public class ExplicitSelectionPlanProperties {

    private List<String> tables = new ArrayList<>();

    public List<String> getTables() {
        return tables;
    }

    public void setTables(final List<String> tables) {
        this.tables = tables;
    }
}
