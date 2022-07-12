package teammates.ui.request;

/**
 * The intent of calling the account request POST API.
 */
public enum AccountRequestCreateIntent {

    /**
     * To create the account request as a public user.
     */
    PUBLIC_CREATE,

    /**
     * To create the account request as an administrator.
     */
    ADMIN_CREATE,
}
