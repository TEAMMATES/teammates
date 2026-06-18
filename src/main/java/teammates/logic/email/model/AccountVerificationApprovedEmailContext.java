package teammates.logic.email.model;

/**
 * Email context for the approval email sent after an account verification
 * request is approved.
 *
 * @param recipientEmailAddress the approval recipient
 * @param recipientName the approval recipient name
 * @param instructorWelcomeUrl the instructor welcome URL
 */
public record AccountVerificationApprovedEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String instructorWelcomeUrl) {
}
