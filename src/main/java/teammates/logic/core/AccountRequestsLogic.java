package teammates.logic.core;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountRequestsDb;

/**
 * Handles the logic related to account requests.
 */
public final class AccountRequestsLogic {

    private static final AccountRequestsLogic instance = new AccountRequestsLogic();

    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();

    private AccountRequestsLogic() {
        // prevent initialization
    }

    public static AccountRequestsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // No dependency to other logic class
    }

    /**
     * Creates or updates an account request.
     *
     * @return the created account request
     * @throws InvalidParametersException if the account request is not valid
     */
    public AccountRequestAttributes createOrUpdateAccountRequest(AccountRequestAttributes accountRequestToAdd)
            throws InvalidParametersException {
        return accountRequestsDb.createOrUpdateAccountRequest(accountRequestToAdd);
    }

    /**
     * Deletes the account request associated with the email address and institute.
     *
     * <p>Fails silently if the account request doesn't exist.</p>
     */
    public void deleteAccountRequest(String email, String institute) {
        accountRequestsDb.deleteAccountRequest(email, institute);
    }

    /**
     * Gets an account request by email address and institute.
     *
     * @return the account request or null if no match found
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        return accountRequestsDb.getAccountRequest(email, institute);
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * @return the account request
     * @throws EntityDoesNotExistException if account request does not exist
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey)
            throws EntityDoesNotExistException {
        AccountRequestAttributes accountRequest = accountRequestsDb
                .getAccountRequestForRegistrationKey(registrationKey);

        if (accountRequest == null) {
            throw new EntityDoesNotExistException(
                    "Account request with registration key " + registrationKey + " does not exist");
        }

        return accountRequest;
    }

}
