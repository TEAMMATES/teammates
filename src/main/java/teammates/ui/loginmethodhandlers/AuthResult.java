package teammates.ui.loginmethodhandlers;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.Provider;

/**
 * Represents the result of an authentication attempt.
 *
 * @param provider the authentication provider (e.g., Google)
 * @param subject the unique identifier for the user from the provider
 * @param tenantId the tenant ID for multi-tenant providers
 * @param email the email of the authenticated user
 */
public record AuthResult(
        Provider provider,
        String subject,
        @Nullable String tenantId,
        String email) {
}
