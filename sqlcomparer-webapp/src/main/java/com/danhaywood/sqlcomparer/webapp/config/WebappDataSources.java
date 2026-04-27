package com.danhaywood.sqlcomparer.webapp.config;

import javax.sql.DataSource;

public record WebappDataSources(
        DataSource master,
        DataSource left,
        DataSource right) {
}
