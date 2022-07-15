package teammates.logic.core;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Logger;
import teammates.storage.api.AccountRequestsDb;

/**
 * Handles the logic related to account requests.
 */
public final class AccountRequestsLogic {

    private static final Logger log = Logger.getLogger();

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
     * Updates an account request.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityDoesNotExistException if the account request to update does not exist
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountRequestsDb.updateAccountRequest(updateOptions);
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
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        AccountRequestAttributes accountRequestAttributes = accountRequestsDb.createEntity(accountRequest);
        try {
            accountRequestAttributes = accountRequestsDb.updateAccountRequest(AccountRequestAttributes
                    .updateOptionsBuilder(accountRequestAttributes.getEmail(), accountRequestAttributes.getInstitute())
                    .withStatus(AccountRequestStatus.APPROVED)
                    .withLastProcessedAt(accountRequestAttributes.getCreatedAt())
                    .build());
        } catch (EntityDoesNotExistException ednee) {
            log.severe("Encountered exception when creating account request: "
                    + "The newly created account request disappeared before it could be approved.", ednee);
            throw ednee;
        }
        return accountRequestAttributes;
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
