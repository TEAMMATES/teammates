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
     * Gets an account request.
     */
    public AccountRequestAttributes getAccountRequest(String email) {
        assert email != null;

        return makeAttributesOrNull(getAccountRequestEntity(email));
    }

    private AccountRequest getAccountRequestEntity(String email) {
        return load().id(email).now();
    }

    /**
     * Deletes an accountRequest.
     */
    public void deleteAccountRequest(String email) {
        assert email != null;

        deleteEntity(Key.create(AccountRequest.class, email));
    }

    @Override
    LoadType<AccountRequest> load() {
        return ofy().load().type(AccountRequest.class);
    }

    @Override
    boolean hasExistingEntities(AccountRequestAttributes entityToCreate) {
        Key<AccountRequest> keyToFind = Key.create(AccountRequest.class, entityToCreate.getEmail());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    AccountRequestAttributes makeAttributes(AccountRequest entity) {
        assert entity != null;

        return AccountRequestAttributes.valueOf(entity);
    }

    /**
     * Creates or updates the account request using {@link AccountRequestAttributes.UpdateOptions}.
     *
     * @return updated account request
     * @throws InvalidParametersException if attributes to update are not valid
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

        return accountRequestToAdd;
    }

    /**
     * Gets an account request by unique constraint registrationKey.
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        return makeAttributesOrNull(getAccountRequestEntityForRegistrationKey(registrationKey.trim()));
    }

    private AccountRequest getAccountRequestEntityForRegistrationKey(String key) {
        List<AccountRequest> accountRequestList = load().filter("registrationKey =", key).list();

        // If registration key detected is not unique, something is wrong
        if (accountRequestList.size() > 1) {
            log.severe("Duplicate registration keys detected for: "
                    + accountRequestList.stream().map(i -> i.getEmail()).collect(Collectors.joining(", ")));
        }

        if (accountRequestList.isEmpty()) {
            return null;
        }

        return accountRequestList.get(0);
    }

}
