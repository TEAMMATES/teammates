package teammates.ui.request;

/**
 * The intent of calling the account requests GET API.
 */
public enum AccountRequestsGetIntent {

    /**
     * To get all account requests pending processing.
     */
    PENDING_PROCESSING,

    /**
     * To get all account requests within some period.
     */
    WITHIN_PERIOD,
}
