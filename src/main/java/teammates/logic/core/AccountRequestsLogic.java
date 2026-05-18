package teammates.logic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountRequestsDb;
import teammates.storage.entity.AccountRequest;

/**
 * Handles operations related to account requests.
 *
 * @see AccountRequest
 * @see AccountRequestsDb
 */
public final class AccountRequestsLogic {

    private static final AccountRequestsLogic instance = new AccountRequestsLogic();

    private AccountRequestsDb accountRequestDb;

    private UsersLogic usersLogic;

    private AccountRequestsLogic() {
        // prevent notification
    }

    public static AccountRequestsLogic inst() {
        return instance;
    }

    /**
     * Initialise dependencies for {@code AccountRequestLogic} object.
     */
    public void initLogicDependencies(AccountRequestsDb accountRequestDb, UsersLogic usersLogic) {
        this.accountRequestDb = accountRequestDb;
        this.usersLogic = usersLogic;
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
     * Approves an account request.
     *
     * <p>Validates that the request is not already approved/registered, that no duplicate
     * approved request exists for the same email/institute, and that no instructor with
     * the same email/institute already exists.
     *
     * @throws EntityDoesNotExistException if the account request does not exist.
     * @throws InvalidParametersException if the approval is not permitted due to existing state.
     */
    public AccountRequest approveAccountRequest(UUID id)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = getAccountRequest(id);
        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Account request with id = " + id + " not found");
        }
        if (accountRequest.getStatus() == AccountRequestStatus.APPROVED
                || accountRequest.getStatus() == AccountRequestStatus.REGISTERED) {
            throw new InvalidParametersException(
                    "Account request with id " + id + " is already approved or registered.");
        }
        if (!getApprovedAccountRequestsForEmailAndInstitute(
                accountRequest.getEmail(), accountRequest.getInstitute()).isEmpty()) {
            throw new InvalidParametersException(String.format(
                    "An account request with email %s and institute %s has already been approved. "
                    + "Please reject or delete the account request instead.",
                    accountRequest.getEmail(), accountRequest.getInstitute()));
        }
        if (usersLogic.getInstructorForEmailAndInstitute(
                accountRequest.getEmail(), accountRequest.getInstitute()) != null) {
            throw new InvalidParametersException(String.format(
                    "An instructor with email %s and institute %s already exists. "
                    + "Please reject or delete the account request instead.",
                    accountRequest.getEmail(), accountRequest.getInstitute()));
        }
        accountRequest.setStatus(AccountRequestStatus.APPROVED);
        return accountRequest;
    }

    /**
     * Rejects an account request.
     *
     * <p>Validates that the request is currently in PENDING state.
     *
     * @throws EntityDoesNotExistException if the account request does not exist.
     * @throws InvalidParametersException if the request is not in PENDING state.
     */
    public AccountRequest rejectAccountRequest(UUID id)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = getAccountRequest(id);
        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Account request with id = " + id + " not found");
        }
        if (accountRequest.getStatus() != AccountRequestStatus.PENDING) {
            throw new InvalidParametersException(
                    "Account request with id " + id + " is not in pending state and cannot be rejected.");
        }
        accountRequest.setStatus(AccountRequestStatus.REJECTED);
        return accountRequest;
    }

    /**
     * Updates the editable details of an account request.
     *
     * @throws EntityDoesNotExistException if the account request does not exist.
     * @throws InvalidParametersException if the updated data is invalid.
     */
    public AccountRequest updateAccountRequestDetails(UUID id, String name, String email,
            String institute, String comments)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = getAccountRequest(id);
        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Account request with id = " + id + " not found");
        }
        accountRequest.setName(name);
        accountRequest.setEmail(email);
        accountRequest.setInstitute(institute);
        accountRequest.setComments(comments);
        validateAccountRequest(accountRequest);
        return accountRequest;
    }

    /**
     * Marks an account request as registered (sets status to REGISTERED and records the timestamp).
     *
     * @throws EntityDoesNotExistException if the account request does not exist.
     * @throws InvalidParametersException if the resulting state is invalid.
     */
    public AccountRequest markAccountRequestAsRegistered(UUID id)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = getAccountRequest(id);
        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Account request with id = " + id + " not found");
        }
        accountRequest.setStatus(AccountRequestStatus.REGISTERED);
        accountRequest.setRegisteredAt(Instant.now());
        validateAccountRequest(accountRequest);
        return accountRequest;
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
     *
     * @throws EntityDoesNotExistException if the account request does not exist.
     * @throws InvalidParametersException if the instructor has not registered yet (nothing to reset).
     */
    public AccountRequest resetAccountRequest(UUID id)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountRequest accountRequest = accountRequestDb.getAccountRequest(id);

        if (accountRequest == null) {
            throw new EntityDoesNotExistException("Failed to reset since AccountRequest with "
                    + "the given id cannot be found.");
        }
        if (accountRequest.getRegisteredAt() == null) {
            throw new InvalidParametersException(
                    "Unable to reset account request as instructor is still unregistered.");
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
