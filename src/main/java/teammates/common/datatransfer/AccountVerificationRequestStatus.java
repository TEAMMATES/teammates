package teammates.common.datatransfer;

/**
 * The status of an account request.
 */
public enum AccountVerificationRequestStatus {

    /**
     * The account request has not yet been processed by the admin.
     */
    PENDING,

    /**
     * The account request has been rejected by the admin.
     */
    REJECTED,

    /**
     * The account request has been approved by the admin but the instructor has not created an account yet.
     */
    APPROVED,

}
