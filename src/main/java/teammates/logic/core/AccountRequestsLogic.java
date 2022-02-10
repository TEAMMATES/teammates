package teammates.logic.core;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
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
     * Updates an account request.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityDoesNotExistException if the account request to create does not exist
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountRequestsDb.updateAccountRequest(updateOptions);
    }

    /**
     * Creates an account request.
     *
     * @return the created account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityAlreadyExistsException if the account request to create already exists
     */
    public AccountRequestAttributes createAccountRequest(AccountRequestAttributes accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountRequestsDb.createEntity(accountRequest);
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
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        return accountRequestsDb.getAccountRequest(email, institute);
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        return accountRequestsDb.getAccountRequestForRegistrationKey(registrationKey);
    }

}
