package com.danhaywood.cfct.cli;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CliImplBoundaryArchitectureTest {

    private static final List<String> FORBIDDEN_IMPORT_PREFIXES = List.of(
            "import com.danhaywood.cfct.comparison.",
            "import com.danhaywood.cfct.report.",
            "import com.danhaywood.cfct.sqlserver.",
            "import com.danhaywood.cfct.config.");

    @Test
    void mainSourcesOnlyImportImplConfigurationFromImplModule() throws IOException {
        try (Stream<Path> sources = Files.walk(Path.of("src/main/java"))) {
            final List<String> violations = sources
                    .filter(path -> path.toString().endsWith(".java"))
                    .flatMap(this::importViolations)
                    .toList();

            assertThat(violations)
                    .as("CLI main sources must not import non-configuration implementation types")
                    .isEmpty();
        }
    }

    private Stream<String> importViolations(final Path sourceFile) {
        try {
            return Files.readAllLines(sourceFile).stream()
                    .map(String::trim)
                    .filter(line -> line.startsWith("import "))
                    .filter(line -> !line.startsWith("import com.danhaywood.cfct.implspring."))
                    .filter(this::isForbidden)
                    .map(line -> sourceFile + " -> " + line);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read " + sourceFile, ex);
        }
    }

    private boolean isForbidden(final String importLine) {
        return FORBIDDEN_IMPORT_PREFIXES.stream().anyMatch(importLine::startsWith);
    }
}
