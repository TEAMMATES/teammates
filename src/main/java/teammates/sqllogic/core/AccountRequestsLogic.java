package teammates.sqllogic.core;

import java.util.List;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlsearch.AccountRequestSearchManager;

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

    private AccountRequestSearchManager getSearchManager() {
        return accountRequestDb.getSearchManager();
    }

    /**
     * Creates or updates search document for the given account request.
     */
    public void putDocument(AccountRequest accountRequest) throws SearchServiceException {
        getSearchManager().putDocument(accountRequest);
    }

    /**
     * Creates an account request.
     */
    public AccountRequest createAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountRequestDb.createAccountRequest(accountRequest);
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
     * Gets account request associated with the {@code email} and {@code institute}.
     */
    public AccountRequest getAccountRequest(String email, String institute) {

        return accountRequestDb.getAccountRequest(email, institute);
    }

    /**
     * Updates an account request.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountRequestDb.updateAccountRequest(accountRequest);
    }

    /**
     * Gets account request associated with the {@code regkey}.
     */
    public AccountRequest getAccountRequestByRegistrationKey(String regkey) {
        return accountRequestDb.getAccountRequestByRegistrationKey(regkey);
    }

    /**
     * Creates/resets the account request with the given email and institute such that it is not registered.
     */
    public AccountRequest resetAccountRequest(String email, String institute)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = accountRequestDb.getAccountRequest(email, institute);

        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Failed to reset since AccountRequest with "
                + "the given email and institute cannot be found.");
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

    /**
     * Searches for account requests in the whole system.
     *
     * @return A list of {@link AccountRequest} or {@code null} if no match found.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {
        return accountRequestDb.searchAccountRequestsInWholeSystem(queryString);
    }

    /**
     * Creates an or gets an account request.
     */
    public AccountRequest createOrGetAccountRequestWithTransaction(String name, String email, String institute)
            throws InvalidParametersException {
        AccountRequest toCreate = new AccountRequest(email, name, institute);
        HibernateUtil.beginTransaction();
        AccountRequest accountRequest;
        try {
            accountRequest = accountRequestDb.createAccountRequest(toCreate);
            HibernateUtil.commitTransaction();
        } catch (InvalidParametersException ipe) {
            HibernateUtil.rollbackTransaction();
            throw new InvalidParametersException(ipe);
        } catch (EntityAlreadyExistsException eaee) {
            // Use existing account request
            accountRequest = getAccountRequest(email, institute);
            HibernateUtil.commitTransaction();
        }
        return accountRequest;
    }
}
