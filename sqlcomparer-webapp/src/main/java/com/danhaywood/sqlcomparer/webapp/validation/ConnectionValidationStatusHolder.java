package com.danhaywood.sqlcomparer.webapp.validation;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.concurrent.atomic.AtomicReference;

@Component
@SessionScope
public class ConnectionValidationStatusHolder {

    private final AtomicReference<ConnectionValidationStatus> latest =
            new AtomicReference<>(ConnectionValidationStatus.ok("Login required."));

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
