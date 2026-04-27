package com.danhaywood.sqlcomparer.webapp.validation;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class ConnectionValidationStatusHolder {

    private final AtomicReference<ConnectionValidationStatus> latest =
            new AtomicReference<>(ConnectionValidationStatus.ok("Connectivity validation not yet executed."));

    public ConnectionValidationStatus current() {
        return latest.get();
    }

    public void markOk(final String summary) {
        latest.set(ConnectionValidationStatus.ok(summary));
    }

    public void markFailed(final String summary) {
        latest.set(ConnectionValidationStatus.failed(summary));
    }
}
