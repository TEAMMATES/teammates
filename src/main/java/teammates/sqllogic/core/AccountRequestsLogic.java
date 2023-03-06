package teammates.sqllogic.core;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Handles operations related to account requests.
 *
 * @see AccountRequest
 * @see AccountRequestsDb
 */
public final class AccountRequestsLogic {

    private static final AccountRequestsLogic instance = new AccountRequestsLogic();

    private AccountRequestsDb accountRequestDb;

    private AccountRequestsLogic() {
        // prevent notification
    }

    public static AccountRequestsLogic inst() {
        return instance;
    }

    /**
     * Initialise dependencies for {@code AccountRequestLogic} object.
     */
    public void initLogicDependencies(AccountRequestsDb accountRequestDb) {
        this.accountRequestDb = accountRequestDb;
    }

    /**
     * Creates an account request.
     */
    public AccountRequest createAccountRequest(String name, String email, String institute)
            throws InvalidParametersException, EntityAlreadyExistsException {
        AccountRequest toCreate = new AccountRequest(email, name, institute);

        return accountRequestDb.createAccountRequest(toCreate);
    }

    /**
     * Gets account request associated with the {@code }.
     */
    public AccountRequest getAccountRequest(String email, String institute) {

        return accountRequestDb.getAccountRequest(email, institute);
    }

    /**
     * Creates/resets the account request with the given email and institute such that it is not registered.
     */
    public AccountRequest resetAccountRequest(String email, String institute)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = accountRequestDb.getAccountRequest(email, institute);

        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Failed to reset since AccountRequest with " +
                "the given email and institute cannot be found.");
        }
        accountRequest.setRegisteredAt(null);

        return accountRequestDb.updateAccountRequest(accountRequest);
    }

    /**
     * Deletes account request associated with the {@code email} and {@code institute}.
     *
     * <p>Fails silently if no account requests with the given email and institute to delete can be found.</p>
     *
     */
    public void deleteAccountRequest(String email, String institute) {
        AccountRequest toDelete = accountRequestDb.getAccountRequest(email, institute);

        accountRequestDb.deleteAccountRequest(toDelete);
    }
}
