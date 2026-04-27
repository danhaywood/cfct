package com.danhaywood.sqlcomparer.architecture;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ImplementationNamingConventionTest {

    private static final Pattern IMPLEMENTS_PATTERN = Pattern.compile("\\bclass\\s+([A-Za-z0-9_]+)\\s+implements\\s+([A-Za-z0-9_]+)");

    @Test
    void implementationsUseInterfaceFirstNaming() throws IOException {
        try (Stream<Path> sources = Files.walk(Path.of("src/main/java"))) {
            final List<String> violations = sources
                    .filter(path -> path.toString().endsWith(".java"))
                    .flatMap(this::namingViolations)
                    .toList();

            assertThat(violations)
                    .as("Implementation class names should start with their interface names")
                    .isEmpty();
        }
    }

    private Stream<String> namingViolations(final Path sourceFile) {
        try {
            return Files.readAllLines(sourceFile).stream()
                    .map(IMPLEMENTS_PATTERN::matcher)
                    .filter(Matcher::find)
                    .map(matcher -> {
                        final String className = matcher.group(1);
                        final String interfaceName = matcher.group(2);
                        final boolean valid = className.startsWith(interfaceName);
                        return valid ? null : sourceFile + " -> " + className + " implements " + interfaceName;
                    })
                    .filter(java.util.Objects::nonNull);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read " + sourceFile, ex);
        }
    }
}
