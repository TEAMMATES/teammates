package teammates.logic.email.model;

/**
 * Email context for the acknowledgement sent after a new account verification
 * request is created.
 *
 * @param recipientEmailAddress the acknowledgement recipient
 * @param recipientName the acknowledgement recipient name
 * @param instituteName the submitted institute name
 * @param comments the submitted comments, if any
 */
public record AccountVerificationCreatedAcknowledgementEmailContext(
        String recipientEmailAddress,
        String recipientName,
        String instituteName,
        String comments) {
}
