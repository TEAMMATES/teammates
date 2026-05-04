package teammates.logic.core;

import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.entity.AccountRequest;
import teammates.storage.api.AccountRequestsDb;

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
    public AccountRequest createAccountRequest(AccountRequest accountRequest) throws InvalidParametersException {
        validateAccountRequest(accountRequest);
        return accountRequestDb.createAccountRequest(accountRequest);
    }

    /**
     * Creates an account request.
     */
    public AccountRequest createAccountRequest(String name, String email, String institute, AccountRequestStatus status,
            String comments) throws InvalidParametersException {
        AccountRequest toCreate = new AccountRequest(email, name, institute, status, comments);

        return createAccountRequest(toCreate);
    }

    /**
     * Gets the account request associated with the {@code id}.
     */
    public AccountRequest getAccountRequest(UUID id) {
        return accountRequestDb.getAccountRequest(id);
    }

    /**
     * Updates an account request.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException {
        validateAccountRequest(accountRequest);
        return accountRequest;
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
     * Get a list of approved account requests associated with email and institute provided.
     */
    public List<AccountRequest> getApprovedAccountRequestsForEmailAndInstitute(String email, String institute) {
        return accountRequestDb.getApprovedAccountRequestsForEmailAndInstitute(email, institute);
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
        validateAccountRequest(accountRequest);

        return accountRequest;
    }

    /**
     * Deletes account request associated with the {@code id}.
     *
     * <p>
     * Fails silently if no account requests with the given id to delete can be
     * found.
     * </p>
     *
     */
    public void deleteAccountRequest(UUID id) {
        AccountRequest toDelete = accountRequestDb.getAccountRequest(id);

        accountRequestDb.deleteAccountRequest(toDelete);
    }

    /**
     * Searches for account requests in the whole system.
     *
     * @return A list of {@link AccountRequest}, or an empty list if no match is found.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString) {
        return accountRequestDb.searchAccountRequestsInWholeSystem(queryString);
    }

    private void validateAccountRequest(AccountRequest accountRequest) throws InvalidParametersException {
        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }
    }
}
