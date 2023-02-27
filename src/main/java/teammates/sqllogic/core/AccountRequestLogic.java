package teammates.sqllogic.core;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidOperationException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.AccountRequestDb;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.webapi.EntityNotFoundException;

public class AccountRequestLogic {

    private static final AccountRequestLogic instance = new AccountRequestLogic();

    private AccountRequestDb accountRequestDb;

    private AccountRequestLogic() {
        // prevent notification
    }

    public static AccountRequestLogic inst() {
        return instance;
    }

    /**
     * Initialise dependencies for {@code AccountRequestLogic} object.
     */
    public void initLogicDependencies(AccountRequestDb accountRequestDb) {
        this.accountRequestDb = accountRequestDb;
    }

    /**
     * Creates an account request.
     * @return
     */
    public AccountRequest createAccountRequest(String name, String email, String institute)
        throws InvalidParametersException, EntityAlreadyExistsException {
        AccountRequest toCreate = new AccountRequest(email, name, institute);

        return accountRequestDb.createAccountRequest(toCreate);
    }

    /**
     * Gets account request associated with the {@code }.
     */
    public AccountRequest getAccountRequest(String email, String institute) throws EntityNotFoundException {

        AccountRequest accountRequest = accountRequestDb.getAccountRequest(email, institute);

        return accountRequest;
    }

    /**
     * Updates/Creates the account request using {@Link AccountRequest}.
     */
    // public AccountRequest updateAccountRequest() {}

    /**
     * Deletes account request associated with the {@code email} and {@code institute}.
     *
     * <p>Fails silently if the account request doesn't exist.</p>
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
