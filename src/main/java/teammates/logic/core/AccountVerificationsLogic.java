package teammates.logic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.InvalidVerificationRequestStateException;
import teammates.storage.api.AccountVerificationRequestsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;

/**
 * Handles operations related to account verifications, including account
 * verification requests and determining if an account is verified for a particular institute.
 *
 * @see AccountVerificationRequest
 * @see AccountVerificationRequestsDb
 */
public final class AccountVerificationsLogic {

    private static final AccountVerificationsLogic instance = new AccountVerificationsLogic();

    private AccountVerificationRequestsDb accountVerificationRequestDb;
    private AccountsLogic accountsLogic;
    private InstitutesLogic institutesLogic;

    private AccountVerificationsLogic() {
        // prevent notification
    }

    public static AccountVerificationsLogic inst() {
        return instance;
    }

    /**
     * Initialise dependencies for {@code AccountVerificationRequestLogic} object.
     */
    public void initLogicDependencies(AccountVerificationRequestsDb accountVerificationRequestDb,
            AccountsLogic accountsLogic, InstitutesLogic institutesLogic) {
        this.accountVerificationRequestDb = accountVerificationRequestDb;
        this.accountsLogic = accountsLogic;
        this.institutesLogic = institutesLogic;
    }

    /**
     * Creates an account verification request.
     */
    public AccountVerificationRequest createAccountVerificationRequest(
            AccountVerificationRequest accountVerificationRequest) throws InvalidParametersException {
        validateAccountVerificationRequest(accountVerificationRequest);
        return accountVerificationRequestDb.persistAccountVerificationRequest(accountVerificationRequest);
    }

    /**
     * Creates a new pending account verification request, resolving (or creating) the shared institute for the given
     * {@code instituteName} and {@code country}, and associating it with the given {@code accountId}.
     */
    public AccountVerificationRequest createAccountVerificationRequest(
            String name, String email, String instituteName, String country,
            String comments, UUID accountId) throws InvalidParametersException {
        return createAccountVerificationRequest(
                name, email, instituteName, country, AccountVerificationRequestStatus.PENDING, comments, accountId);
    }

    /**
     * Creates an account verification request with an explicit status, resolving (or creating) the shared institute
     * for the given {@code instituteName} and {@code country}, and associating it with the given {@code accountId}.
     */
    public AccountVerificationRequest createAccountVerificationRequest(
            String name, String email, String instituteName, String country,
            AccountVerificationRequestStatus status, String comments, UUID accountId) throws InvalidParametersException {
        Institute institute = institutesLogic.getOrCreateInstitute(instituteName, country);
        Account account = accountsLogic.getAccount(accountId);
        AccountVerificationRequest toCreate = new AccountVerificationRequest(email, name, status, comments);
        institute.addAccountVerificationRequest(toCreate);
        account.addAccountVerificationRequest(toCreate);

        return createAccountVerificationRequest(toCreate);
    }

    /**
     * Gets the account verification request associated with the {@code id}.
     */
    public AccountVerificationRequest getAccountVerificationRequest(UUID id) {
        return accountVerificationRequestDb.getAccountVerificationRequest(id);
    }

    /**
     * Updates an account verification request.
     */
    public AccountVerificationRequest updateAccountVerificationRequest(AccountVerificationRequest accountVerificationRequest)
            throws InvalidParametersException {
        validateAccountVerificationRequest(accountVerificationRequest);
        return accountVerificationRequest;
    }

    /**
     * Approves the account verification request with the given {@code id}.
     *
     * @throws EntityDoesNotExistException if no request with the given id exists.
     * @throws InvalidVerificationRequestStateException if the request is already approved.
     * @throws InvalidParametersException if the request is invalid.
     */
    public AccountVerificationRequest approveAccountVerificationRequest(UUID id)
            throws EntityDoesNotExistException, InvalidVerificationRequestStateException, InvalidParametersException {
        AccountVerificationRequest request = accountVerificationRequestDb.getAccountVerificationRequest(id);
        if (request == null) {
            throw new EntityDoesNotExistException(
                    "Account verification request with id = " + id + " not found");
        }
        if (request.getStatus() == AccountVerificationRequestStatus.APPROVED) {
            throw new InvalidVerificationRequestStateException(
                    "Account verification request with id " + id + " is already approved.");
        }
        request.setStatus(AccountVerificationRequestStatus.APPROVED);
        validateAccountVerificationRequest(request);
        return request;
    }

    /**
     * Rejects the account verification request with the given {@code id}.
     *
     * @throws EntityDoesNotExistException if no request with the given id exists.
     * @throws InvalidVerificationRequestStateException if the request is not in pending state.
     * @throws InvalidParametersException if the request is invalid.
     */
    public AccountVerificationRequest rejectAccountVerificationRequest(UUID id)
            throws EntityDoesNotExistException, InvalidVerificationRequestStateException, InvalidParametersException {
        AccountVerificationRequest request = accountVerificationRequestDb.getAccountVerificationRequest(id);
        if (request == null) {
            throw new EntityDoesNotExistException(
                    "Account verification request with id = " + id + " not found");
        }
        if (request.getStatus() != AccountVerificationRequestStatus.PENDING) {
            throw new InvalidVerificationRequestStateException(
                    "Account verification request with id " + id + " is not in pending state and cannot be rejected.");
        }
        request.setStatus(AccountVerificationRequestStatus.REJECTED);
        validateAccountVerificationRequest(request);
        return request;
    }

    /**
     * Gets all pending account verification requests.
     */
    public List<AccountVerificationRequest> getPendingAccountVerificationRequests() {
        return accountVerificationRequestDb.getPendingAccountVerificationRequests();
    }

    /**
     * Deletes account verification request associated with the {@code id}.
     *
     * <p>
     * Fails silently if no account verification requests with the given id to delete can be
     * found.
     * </p>
     */
    public void deleteAccountVerificationRequest(UUID id) {
        AccountVerificationRequest toDelete = accountVerificationRequestDb.getAccountVerificationRequest(id);

        accountVerificationRequestDb.removeAccountVerificationRequest(toDelete);
    }

    /**
     * Searches for account verification requests in the whole system.
     *
     * @return A list of {@link AccountVerificationRequest}, or an empty list if no match is found.
     */
    public List<AccountVerificationRequest> searchAccountVerificationRequestsInWholeSystem(String queryString) {
        return accountVerificationRequestDb.searchAccountVerificationRequestsInWholeSystem(queryString);
    }

    private void validateAccountVerificationRequest(
            AccountVerificationRequest accountVerificationRequest) throws InvalidParametersException {
        if (!accountVerificationRequest.isValid()) {
            throw new InvalidParametersException(accountVerificationRequest.getInvalidityInfo());
        }
    }

    /**
     * Returns true if the given account has an approved account verification request for the given institute.
     */
    public boolean isAccountVerifiedForInstitute(UUID accountId, UUID instituteId) {
        return accountVerificationRequestDb.hasApprovedRequestForAccountAndInstitute(accountId, instituteId);
    }

    /**
     * Returns true if the given account has at least one approved account verification request.
     */
    public boolean hasAnyApprovedVerificationRequest(UUID accountId) {
        return !accountVerificationRequestDb.getApprovedRequestsByAccountId(accountId).isEmpty();
    }

    /**
     * Returns the institutes for which the given account has an approved account verification request.
     */
    public List<Institute> getApprovedInstitutesForAccount(UUID accountId) {
        return accountVerificationRequestDb.getApprovedRequestsByAccountId(accountId)
                .stream()
                .map(AccountVerificationRequest::getInstitute)
                .distinct()
                .toList();
    }

    /**
     * Gets createdAt timestamps of account verification requests created within the given time range.
     */
    public List<Instant> getAccountVerificationRequestCreatedAtTimestampsForTimeRange(Instant startTime, Instant endTime) {
        return accountVerificationRequestDb.getCreatedAtTimestampsForTimeRange(startTime, endTime);
    }
}
