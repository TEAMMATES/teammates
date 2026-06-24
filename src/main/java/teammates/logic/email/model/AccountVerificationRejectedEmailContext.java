package teammates.logic.email.model;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountVerificationRequestRejectionType;

/**
 * Email context for the rejection email sent after an account verification
 * request is rejected.
 *
 * @param recipientEmailAddress the rejection recipient
 * @param instituteName the name of the institute the request was for
 * @param rejectionType the structured reason for rejection
 * @param additionalComments optional additional comments from the admin
 */
public record AccountVerificationRejectedEmailContext(
        String recipientEmailAddress,
        String instituteName,
        AccountVerificationRequestRejectionType rejectionType,
        @Nullable String additionalComments) {
}
