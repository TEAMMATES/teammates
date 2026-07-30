package teammates.logic.email.model;

/**
 * Email context for a magic-link sign-in email.
 *
 * @param recipientEmailAddress the recipient email address
 * @param magicLinkUrl the confirmation-page URL containing the auth state and one-time token
 */
public record MagicLinkEmailContext(String recipientEmailAddress, String magicLinkUrl) {
}
