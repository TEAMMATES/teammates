package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
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

        return makeAttributesOrNull(getAccountRequestEntity(AccountRequest.generateId(email, institute)));
    }

    /**
     * Updates an account request.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityDoesNotExistException if the account request cannot be found
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        AccountRequestAttributes accountRequest = getAccountRequest(updateOptions.getEmail(), updateOptions.getInstitute());
        if (accountRequest == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        accountRequest.update(updateOptions);
        accountRequest.sanitizeForSaving();

        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }

        saveEntity(accountRequest.toEntity());

        return accountRequest;
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
        return load()
                .filter("createdAt >=", startTime)
                .filter("createdAt <", endTime)
                .count();
    }

}
