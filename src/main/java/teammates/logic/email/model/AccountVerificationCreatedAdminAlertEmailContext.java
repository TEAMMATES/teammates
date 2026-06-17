package teammates.logic.email.model;

/**
 * Email context for the admin alert sent after a new account verification
 * request is created.
 *
 * @param recipientEmailAddress the admin alert recipient
 * @param instructorName the submitted instructor name
 * @param instituteName the submitted institute name
 * @param instructorEmailAddress the submitted instructor email address
 * @param comments the submitted comments, if any
 * @param adminAccountVerificationRequestsPageUrl the admin review page URL
 */
public record AccountVerificationCreatedAdminAlertEmailContext(
        String recipientEmailAddress,
        String instructorName,
        String instituteName,
        String instructorEmailAddress,
        String comments,
        String adminAccountVerificationRequestsPageUrl) {
}
