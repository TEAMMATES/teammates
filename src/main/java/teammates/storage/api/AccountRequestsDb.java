package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;
import java.util.stream.Collectors;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.AccountRequest;

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

    /**
     * Gets an account request by email and institute.
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        return makeAttributesOrNull(getAccountRequestEntity(AccountRequest.generateId(email, institute)));
    }

    /**
     * Creates or updates an account request.
     *
     * @return the created account request
     * @throws InvalidParametersException if the account request is not valid
     */
    public AccountRequestAttributes createOrUpdateAccountRequest(AccountRequestAttributes accountRequestToAdd)
            throws InvalidParametersException {
        assert accountRequestToAdd != null;
        accountRequestToAdd.sanitizeForSaving();

        if (!accountRequestToAdd.isValid()) {
            throw new InvalidParametersException(accountRequestToAdd.getInvalidityInfo());
        }

        AccountRequest accountRequest = accountRequestToAdd.toEntity();
        saveEntity(accountRequest);

        return makeAttributes(accountRequest);
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

        deleteEntity(Key.create(AccountRequest.class, AccountRequest.generateId(email, institute)));
    }

    @Override
    LoadType<AccountRequest> load() {
        return ofy().load().type(AccountRequest.class);
    }

    @Override
    boolean hasExistingEntities(AccountRequestAttributes entityToCreate) {
        Key<AccountRequest> keyToFind = Key.create(AccountRequest.class, entityToCreate.getRegistrationKey());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    AccountRequestAttributes makeAttributes(AccountRequest entity) {
        assert entity != null;

        return AccountRequestAttributes.valueOf(entity);
    }

}
