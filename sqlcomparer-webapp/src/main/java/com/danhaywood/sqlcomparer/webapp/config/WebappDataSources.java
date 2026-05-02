package com.danhaywood.cfct.webapp.config;

import javax.sql.DataSource;

public record WebappDataSources(
        DataSource master,
        DataSource left,
        DataSource right) {
}
