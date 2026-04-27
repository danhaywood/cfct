package com.danhaywood.sqlcomparer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public final class JsonComparisonRequestLoader {

    private final ObjectMapper objectMapper;

    public JsonComparisonRequestLoader(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonComparisonRequest load(final Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return load(inputStream);
        } catch (IOException ex) {
            throw new ComparisonRequestException("Failed to read comparison request JSON: %s".formatted(path), ex);
        }
    }

    public JsonComparisonRequest load(final InputStream inputStream) {
        try {
            final JsonComparisonRequest request = objectMapper.readValue(inputStream, JsonComparisonRequest.class);
            request.outputType();
            request.toMultiTableComparisonRequest();
            return request;
        } catch (ComparisonRequestException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new ComparisonRequestException("Failed to parse comparison request JSON", ex);
        }
    }
}
