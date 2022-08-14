package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
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
     * Creates an account request and approves it instantly.
     *
     * @return the created account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityAlreadyExistsException if the account request to create already exists
     */
    public AccountRequestAttributes createAndApproveAccountRequest(AccountRequestAttributes accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        accountRequest.setCreatedAt(Instant.now());
        accountRequest.update(AccountRequestAttributes
                .updateOptionsBuilder(accountRequest.getEmail(), accountRequest.getInstitute())
                .withStatus(AccountRequestStatus.APPROVED)
                .withLastProcessedAt(accountRequest.getCreatedAt())
                .build());

        return accountRequestsDb.createEntity(accountRequest);
    }

    /**
     * Updates an account request.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the new account request is not valid
     * @throws EntityDoesNotExistException if the account request to update does not exist
     * @throws EntityAlreadyExistsException if the account request cannot be updated because of an existing account request
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        return accountRequestsDb.updateAccountRequest(updateOptions);
    }

    /**
     * Approves the account request associated with the email address and institute.
     *
     * @throws EntityDoesNotExistException if the account request to approve does not exist
     */
    public AccountRequestAttributes approveAccountRequest(String email, String institute)
            throws EntityDoesNotExistException {
        try {
            return updateAccountRequest(AccountRequestAttributes.updateOptionsBuilder(email, institute)
                    .withStatus(AccountRequestStatus.APPROVED)
                    .withLastProcessedAt(Instant.now())
                    .build());
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            throw new AssertionError("Approving an account request should not cause " + e.getClass().getSimpleName()
                    + ". Error details: " + e.getMessage(), e);
        }
    }

    /**
     * Rejects the account request associated with the email address and institute.
     *
     * @throws EntityDoesNotExistException if the account request to reject does not exist
     */
    public AccountRequestAttributes rejectAccountRequest(String email, String institute)
            throws EntityDoesNotExistException {
        try {
            return updateAccountRequest(AccountRequestAttributes.updateOptionsBuilder(email, institute)
                    .withStatus(AccountRequestStatus.REJECTED)
                    .withLastProcessedAt(Instant.now())
                    .build());
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            throw new AssertionError("Rejecting an account request should not cause " + e.getClass().getSimpleName()
                    + ". Error details: " + e.getMessage(), e);
        }
    }

    /**
     * Resets the status of the account request associated with the email address and institute back to SUBMITTED.
     *
     * @throws EntityDoesNotExistException if the account request to reset does not exist
     */
    public AccountRequestAttributes resetAccountRequest(String email, String institute)
            throws EntityDoesNotExistException {
        try {
            return updateAccountRequest(AccountRequestAttributes.updateOptionsBuilder(email, institute)
                    .withStatus(AccountRequestStatus.SUBMITTED)
                    .withLastProcessedAt(Instant.now())
                    .withRegisteredAt(null)
                    .build());
        } catch (InvalidParametersException | EntityAlreadyExistsException e) {
            throw new AssertionError("Resetting an account request should not cause " + e.getClass().getSimpleName()
                    + ". Error details: " + e.getMessage(), e);
        }
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
     * Gets all account requests pending processing.
     *
     * @return the list of all account requests pending processing or an empty list if not found.
     */
    public List<AccountRequestAttributes> getAccountRequestsPendingProcessing() {
        return accountRequestsDb.getAccountRequestsWithStatusSubmitted();
    }

    /**
     * Gets all account requests submitted between {@code startTime} and {@code endTime}.
     *
     * @return the list of all account requests submitted within the period or an empty list if not found.
     */
    public List<AccountRequestAttributes> getAccountRequestsSubmittedWithinPeriod(Instant startTime, Instant endTime) {
        return accountRequestsDb.getAccountRequestsSubmittedWithinPeriod(startTime, endTime);
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        return accountRequestsDb.getAccountRequestForRegistrationKey(registrationKey);
    }

    /**
     * Creates or updates search document for the given account request.
     *
     * @param accountRequest the account request to be put into documents
     */
    public void putDocument(AccountRequestAttributes accountRequest) throws SearchServiceException {
        accountRequestsDb.putDocument(accountRequest);
    }

    /**
     * Searches for account requests in the whole system.
     *
     * @return A list of {@link AccountRequestAttributes} or {@code null} if no match found.
     */
    public List<AccountRequestAttributes> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {
        return accountRequestsDb.searchAccountRequestsInWholeSystem(queryString);
    }

    /**
     * Gets the number of account requests created within a specified time range.
     */
    int getNumAccountRequestsByTimeRange(Instant startTime, Instant endTime) {
        return accountRequestsDb.getNumAccountRequestsByTimeRange(startTime, endTime);
    }

}
