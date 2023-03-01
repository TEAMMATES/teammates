package teammates.sqllogic.core;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidOperationException;
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
            throws EntityDoesNotExistException, InvalidOperationException, InvalidParametersException {
        AccountRequest accountRequest = accountRequestDb.getAccountRequest(email, institute);

        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Account request for instructor with email: " + email
            + " and institute: " + institute + " does not exist.");
        }

        if (accountRequest.getRegisteredAt() == null) {
            throw new InvalidOperationException("Unable to reset account request as instructor is still unregistered.");
        }

        accountRequest.setRegisteredAt(null);

        return accountRequestDb.updateAccountRequest(accountRequest);
    }

    /**
     * Deletes account request associated with the {@code email} and {@code institute}.
     *
     * Fails silently if no account requests with the given email and institute to delete can be found.
     *
     * @throws InvalidOperationException if the account request to delete has already been registered as an account.
     */
    public void deleteAccountRequest(String email, String institute) throws InvalidOperationException {
        AccountRequest toDelete = accountRequestDb.getAccountRequest(email, institute);

        if (toDelete != null && toDelete.getRegisteredAt() != null) {
            // instructor is already registered and cannot be deleted
            throw new InvalidOperationException("Account request of a registered instructor cannot be deleted.");
        }

        accountRequestDb.deleteAccountRequest(toDelete);
    }
}
