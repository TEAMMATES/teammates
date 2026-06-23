package teammates.common.datatransfer;

/**
 * Represents the reason an account verification request was rejected.
 */
public enum AccountVerificationRequestRejectionType {
    ALREADY_VERIFIED,
    CANNOT_VERIFY_IDENTITY,
    NOT_OFFICIAL_EMAIL,
    NOT_INSTRUCTOR_ACCOUNT,
    OTHERS,
}
