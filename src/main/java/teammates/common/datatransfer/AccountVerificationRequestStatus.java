package teammates.common.datatransfer;

/**
 * The status of an account verification request.
 */
public enum AccountVerificationRequestStatus {

    /**
     * The account verification request has not yet been processed by the admin.
     */
    PENDING,

    /**
     * The account verification request has been rejected by the admin.
     */
    REJECTED,

    /**
     * The account verification request has been approved by the admin but the instructor has not created an account yet.
     */
    APPROVED,

}
