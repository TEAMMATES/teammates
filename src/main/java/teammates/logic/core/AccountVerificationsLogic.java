package teammates.logic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.InvalidVerificationRequestStateException;
import teammates.common.util.Config;
import teammates.common.util.LinksUtil;
import teammates.logic.email.AccountVerificationEmailsLogic;
import teammates.logic.email.model.AccountVerificationCreatedAcknowledgementEmailContext;
import teammates.logic.email.model.AccountVerificationCreatedAdminAlertEmailContext;
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
    private AccountVerificationEmailsLogic accountVerificationEmailsLogic;

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
            AccountsLogic accountsLogic, InstitutesLogic institutesLogic,
            AccountVerificationEmailsLogic accountVerificationEmailsLogic) {
        this.accountVerificationRequestDb = accountVerificationRequestDb;
        this.accountsLogic = accountsLogic;
        this.institutesLogic = institutesLogic;
        this.accountVerificationEmailsLogic = accountVerificationEmailsLogic;
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
        AccountVerificationRequest createdRequest = createAccountVerificationRequest(
                name, email, instituteName, country, AccountVerificationRequestStatus.PENDING, comments, accountId);
        enqueueCreatedEmails(createdRequest);
        return createdRequest;
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

    private void enqueueCreatedEmails(AccountVerificationRequest request) {
        accountVerificationEmailsLogic.enqueueCreatedAdminAlertEmail(
                new AccountVerificationCreatedAdminAlertEmailContext(
                        Config.SUPPORT_EMAIL,
                        request.getName(),
                        request.getInstitute().getName(),
                        request.getEmail(),
                        request.getComments(),
                        LinksUtil.getAdminHomePageUrl()));
        accountVerificationEmailsLogic.enqueueCreatedAcknowledgementEmail(
                new AccountVerificationCreatedAcknowledgementEmailContext(
                        request.getEmail(),
                        request.getName(),
                        request.getInstitute().getName(),
                        request.getEmail(),
                        request.getComments()));
    }

    /**
     * Gets the account verification request associated with the {@code id}.
     */
    public AccountVerificationRequest getAccountVerificationRequest(UUID id) {
        return accountVerificationRequestDb.getAccountVerificationRequest(id);
    }

    /**
     * Updates the details (name, email, institute, comments) of the account verification request with the given
     * {@code id}. Status is not changed by this method.
     *
     * @throws EntityDoesNotExistException if no request with the given id exists.
     * @throws InvalidParametersException if the updated details are invalid.
     */
    public AccountVerificationRequest updateAccountVerificationRequestDetails(
            UUID id, String name, String email, String instituteName, String country, String comments)
            throws EntityDoesNotExistException, InvalidParametersException {
        AccountVerificationRequest request = accountVerificationRequestDb.getAccountVerificationRequest(id);
        if (request == null) {
            throw new EntityDoesNotExistException(
                    "Account verification request with id = " + id + " not found");
        }
        Institute institute = institutesLogic.getOrCreateInstitute(instituteName, country);
        request.setName(name);
        request.setEmail(email);
        request.setInstitute(institute);
        request.setComments(comments);
        validateAccountVerificationRequest(request);
        return request;
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
