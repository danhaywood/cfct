package com.danhaywood.sqlcomparer.webapp.auth;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
@SessionScope
public class AuthenticatedConnectionContextHolder {

    private final AtomicReference<AuthenticatedConnectionContext> currentContext = new AtomicReference<>();

    public Optional<AuthenticatedConnectionContext> current() {
        return Optional.ofNullable(currentContext.get());
    }

    public boolean isAuthenticated() {
        return currentContext.get() != null;
    }

    public AuthenticatedConnectionContext required() {
        final AuthenticatedConnectionContext context = currentContext.get();
        if (context == null) {
            throw new IllegalStateException("User is not authenticated.");
        }
        return context;
    }

    public void set(final AuthenticatedConnectionContext context) {
        currentContext.set(context);
    }

    public void clear() {
        currentContext.set(null);
    }
}
