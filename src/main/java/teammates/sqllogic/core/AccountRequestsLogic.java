package teammates.sqllogic.core;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
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
    public AccountRequest createAccountRequest(AccountRequest accountRequest) throws InvalidParametersException {
        return accountRequestDb.createAccountRequest(accountRequest);
    }

    /**
     * Creates an account request.
     */
    public AccountRequest createAccountRequest(String name, String email, String institute, AccountRequestStatus status,
            String comments) throws InvalidParametersException {
        AccountRequest toCreate = new AccountRequest(email, name, institute, status, comments);

        return accountRequestDb.createAccountRequest(toCreate);
    }

    /**
     * Gets the account request associated with the {@code id}.
     */
    public AccountRequest getAccountRequest(UUID id) {
        return accountRequestDb.getAccountRequest(id);
    }

    /**
     * Gets the account request associated with the {@code id}.
     */
    public AccountRequest getAccountRequestWithTransaction(UUID id) {
        HibernateUtil.beginTransaction();
        AccountRequest request = accountRequestDb.getAccountRequest(id);
        HibernateUtil.commitTransaction();
        return request;
    }

    /**
     * Updates an account request.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountRequestDb.updateAccountRequest(accountRequest);
    }

    /**
     * Updates an account request.
     */
    @SuppressWarnings("PMD")
    public AccountRequest updateAccountRequestWithTransaction(AccountRequest accountRequest)
            throws InvalidParametersException, EntityDoesNotExistException {

        HibernateUtil.beginTransaction();
        AccountRequest updatedRequest;

        try {
            updatedRequest = accountRequestDb.updateAccountRequest(accountRequest);
            HibernateUtil.commitTransaction();
        } catch (InvalidParametersException ipe) {
            HibernateUtil.rollbackTransaction();
            throw new InvalidParametersException(ipe.getMessage());
        }

        return updatedRequest;
    }

    /**
     * Gets account request associated with the {@code regkey}.
     */
    public AccountRequest getAccountRequestByRegistrationKey(String regkey) {
        return accountRequestDb.getAccountRequestByRegistrationKey(regkey);
    }

    /**
     * Gets all pending account requests.
     */
    public List<AccountRequest> getPendingAccountRequests() {
        return accountRequestDb.getPendingAccountRequests();
    }

    /**
     * Gets all account requests.
     */
    public List<AccountRequest> getAllAccountRequests() {
        return accountRequestDb.getAllAccountRequests();
    }

    /**
     * Get a list of account requests associated with email provided.
     */
    public List<AccountRequest> getApprovedAccountRequestsForEmailWithTransaction(String email) {
        HibernateUtil.beginTransaction();
        List<AccountRequest> accountRequests = accountRequestDb.getApprovedAccountRequestsForEmail(email);
        HibernateUtil.commitTransaction();
        return accountRequests;
    }

    /**
     * Creates/resets the account request with the given id such that it is not registered.
     */
    public AccountRequest resetAccountRequest(UUID id)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = accountRequestDb.getAccountRequest(id);

        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Failed to reset since AccountRequest with "
                + "the given id cannot be found.");
        }
        accountRequest.setRegisteredAt(null);

        return accountRequestDb.updateAccountRequest(accountRequest);
    }

    /**
     * Deletes account request associated with the {@code id}.
     *
     * <p>Fails silently if no account requests with the given id to delete can be found.</p>
     *
     */
    public void deleteAccountRequest(UUID id) {
        AccountRequest toDelete = accountRequestDb.getAccountRequest(id);

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
    public AccountRequest createOrGetAccountRequestWithTransaction(String name, String email, String institute,
            AccountRequestStatus status, String comments)
            throws InvalidParametersException {
        AccountRequest toCreate = new AccountRequest(email, name, institute, status, comments);
        HibernateUtil.beginTransaction();
        AccountRequest accountRequest;
        try {
            accountRequest = accountRequestDb.createAccountRequest(toCreate);
            HibernateUtil.commitTransaction();
        } catch (InvalidParametersException ipe) {
            HibernateUtil.rollbackTransaction();
            throw new InvalidParametersException(ipe);
        }
        return accountRequest;
    }
}
