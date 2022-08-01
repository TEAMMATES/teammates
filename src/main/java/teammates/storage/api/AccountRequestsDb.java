package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.storage.entity.AccountRequest;
import teammates.storage.search.AccountRequestSearchManager;
import teammates.storage.search.SearchManagerFactory;

/**
 * Handles CRUD operations for account requests.
 *
 * @see AccountRequest
 * @see AccountRequestAttributes
 */
public final class AccountRequestsDb extends EntitiesDb<AccountRequest, AccountRequestAttributes> {

    private static final AccountRequestsDb instance = new AccountRequestsDb();

    private AccountRequestsDb() {
        // prevent initialization
    }

    public static AccountRequestsDb inst() {
        return instance;
    }

    private AccountRequestSearchManager getSearchManager() {
        return SearchManagerFactory.getAccountRequestSearchManager();
    }

    /**
     * Creates or updates search document for the given account request.
     */
    public void putDocument(AccountRequestAttributes accountRequest) throws SearchServiceException {
        getSearchManager().putDocument(accountRequest);
    }

    /**
     * Searches all account requests in the system.
     *
     * <p>This is used by admin to search account requests in the whole system.
     */
    public List<AccountRequestAttributes> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchAccountRequests(queryString);
    }

    /**
     * Gets an account request by email and institute.
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        return makeAttributesOrNull(getAccountRequestEntity(email, institute));
    }

    /**
     * Gets all account requests with status {@code AccountRequestStatus.SUBMITTED}.
     */
    public List<AccountRequestAttributes> getAccountRequestsWithStatusSubmitted() {
        return makeAttributes(getAccountRequestEntitiesWithStatusSubmitted());
    }

    /**
     * Gets all account requests with {@code createdAt} timestamp between {@code startTime} and {@code endTime}.
     */
    public List<AccountRequestAttributes> getAccountRequestsSubmittedWithinPeriod(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;

        return makeAttributes(getAccountRequestEntitiesSubmittedWithinPeriod(startTime, endTime));
    }

    /**
     * Gets all account requests. This method is only used in testing.
     */
    public List<AccountRequestAttributes> getAllAccountRequests() {
        return makeAttributes(getAllAccountRequestEntities());
    }

    /**
     * Updates an account request.
     *
     * <p>If the email or institute of the account request is changed, it will be re-created.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the new account request is not valid
     * @throws EntityDoesNotExistException if the account request to update cannot be found
     * @throws EntityAlreadyExistsException if the account request cannot be updated by re-creation because
     *                                      of an existing account request
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        assert updateOptions != null;

        AccountRequest accountRequest = getAccountRequestEntity(updateOptions.getEmail(), updateOptions.getInstitute());
        if (accountRequest == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        AccountRequestAttributes newAccountRequestAttributes = makeAttributes(accountRequest);
        newAccountRequestAttributes.update(updateOptions);

        newAccountRequestAttributes.sanitizeForSaving();
        if (!newAccountRequestAttributes.isValid()) {
            throw new InvalidParametersException(newAccountRequestAttributes.getInvalidityInfo());
        }

        boolean isEmailOrInstituteChanged = !accountRequest.getEmail().equals(newAccountRequestAttributes.getEmail())
                || !accountRequest.getInstitute().equals(newAccountRequestAttributes.getInstitute());

        if (isEmailOrInstituteChanged) {
            // re-create the updated account request
            newAccountRequestAttributes = createEntity(newAccountRequestAttributes);
            // delete the old account request
            deleteAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
        } else {
            // update only if change
            boolean hasSameAttributes = hasSameValue(accountRequest.getName(), newAccountRequestAttributes.getName())
                    && hasSameValue(accountRequest.getHomePageUrl(), newAccountRequestAttributes.getHomePageUrl())
                    && hasSameValue(accountRequest.getComments(), newAccountRequestAttributes.getComments())
                    && hasSameValue(accountRequest.getStatus(), newAccountRequestAttributes.getStatus())
                    && hasSameValue(accountRequest.getLastProcessedAt(), newAccountRequestAttributes.getLastProcessedAt())
                    && hasSameValue(accountRequest.getRegisteredAt(), newAccountRequestAttributes.getRegisteredAt());
            if (hasSameAttributes) {
                // reset to the exact old account request
                newAccountRequestAttributes = makeAttributes(accountRequest);
                log.info(String.format(
                        OPTIMIZED_SAVING_POLICY_APPLIED, AccountRequest.class.getSimpleName(), updateOptions));
            } else {
                saveEntity(newAccountRequestAttributes.toEntity());
            }
        }

        return newAccountRequestAttributes;
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * @return the account request or null if no match found
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        List<AccountRequest> accountRequestList = load().filter("registrationKey =", registrationKey).list();

        if (accountRequestList.size() > 1) {
            log.severe("Duplicate registration keys detected for: "
                    + accountRequestList.stream().map(i -> i.getId()).collect(Collectors.joining(", ")));
        }

        if (accountRequestList.isEmpty()) {
            return null;
        }

        return makeAttributes(accountRequestList.get(0));
    }

    private AccountRequest getAccountRequestEntity(String id) {
        return load().id(id).now();
    }

    private AccountRequest getAccountRequestEntity(String email, String institute) {
        return getAccountRequestEntity(AccountRequest.generateId(email, institute));
    }

    private List<AccountRequest> getAccountRequestEntitiesWithStatusSubmitted() {
        return load()
                .filter("status", AccountRequestStatus.SUBMITTED)
                .list();
    }

    private List<AccountRequest> getAccountRequestEntitiesSubmittedWithinPeriod(Instant startTime, Instant endTime) {
        return load()
                .filter("createdAt >=", startTime)
                .filter("createdAt <", endTime)
                .list();
    }

    private List<AccountRequest> getAllAccountRequestEntities() {
        return load().list();
    }

    /**
     * Deletes an accountRequest.
     */
    public void deleteAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        deleteDocumentByAccountRequestId(AccountRequest.generateId(email, institute));
        deleteEntity(Key.create(AccountRequest.class, AccountRequest.generateId(email, institute)));
    }

    /**
     * Removes search document for the given account request by using {@code accountRequestUniqueId}.
     */
    public void deleteDocumentByAccountRequestId(String accountRequestUniqueId) {
        getSearchManager().deleteDocuments(Collections.singletonList(accountRequestUniqueId));
    }

    @Override
    LoadType<AccountRequest> load() {
        return ofy().load().type(AccountRequest.class);
    }

    @Override
    boolean hasExistingEntities(AccountRequestAttributes entityToCreate) {
        Key<AccountRequest> keyToFind = Key.create(AccountRequest.class,
                AccountRequest.generateId(entityToCreate.getEmail(), entityToCreate.getInstitute()));
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    AccountRequestAttributes makeAttributes(AccountRequest entity) {
        assert entity != null;

        return AccountRequestAttributes.valueOf(entity);
    }

    /**
     * Gets the number of account requests created within a specified time range.
     */
    public int getNumAccountRequestsByTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;

        return load()
                .filter("createdAt >=", startTime)
                .filter("createdAt <", endTime)
                .count();
    }

}
