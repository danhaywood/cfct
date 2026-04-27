package com.danhaywood.sqlcomparer.webapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "sqlcomparer.webapp.comparison.validation.enabled=false")
class SqlComparerWebApplicationTest {

    @Test
    void contextLoads() {
        // verifies scaffolded Spring Boot + Vaadin app starts.
    }
}
