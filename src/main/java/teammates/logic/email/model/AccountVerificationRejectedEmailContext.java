package teammates.logic.email.model;

/**
 * Email context for the rejection email sent after an account verification
 * request is rejected.
 *
 * @param recipientEmailAddress the rejection recipient
 * @param reasonTitle the rejection subject line suffix
 * @param reasonBodyHtml the sanitized rich-text rejection body
 */
public record AccountVerificationRejectedEmailContext(
        String recipientEmailAddress,
        String reasonTitle,
        String reasonBodyHtml) {
}
