package teammates.ui.request;

/**
 * The intent of calling the account request status PUT API.
 */
public enum AccountRequestStatusUpdateIntent {

    /**
     * To approve the account request.
     */
    TO_APPROVE,

    /**
     * To reject the account request.
     */
    TO_REJECT,

    /**
     * To reset the account request.
     */
    TO_RESET,
}
