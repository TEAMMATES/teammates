package teammates.common.datatransfer;

/**
 * Represents the different status of an account request.
 */
public enum AccountRequestStatus {

    /**
     * Request has been submitted and is pending process.
     */
    SUBMITTED,

    /**
     * Request has been approved.
     */
    APPROVED,

    /**
     * Request has been rejected.
     */
    REJECTED,

    /**
     * Instructor has joined TEAMMATES and an account has been created from the request.
     */
    REGISTERED
}
