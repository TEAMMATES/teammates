package teammates.ui.loginmethodhandlers;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.Provider;

/**
 * Represents the result of an authentication attempt.
 */
public final class AuthResult {
    private final Provider provider;
    private final String subject;
    private final String tenantId;
    private final String email;

    /**
     * Creates an AuthResult with the given parameters.
     */
    public AuthResult(Provider provider, String subject, @Nullable String tenantId, String email) {
        this.provider = provider;
        this.subject = subject;
        this.tenantId = tenantId;
        this.email = email;
    }

    /**
     * Checks if the authentication result is valid (i.e., has all required fields).
     */
    public boolean isValid() {
        boolean hasProvider = provider != null;
        boolean hasEmail = email != null;
        boolean hasSubject = subject != null;
        return hasProvider && hasEmail && hasSubject;
    }

    public Provider getProvider() {
        return provider;
    }

    public String getSubject() {
        return subject;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getEmail() {
        return email;
    }

}
