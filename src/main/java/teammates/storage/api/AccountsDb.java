package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Account;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 * @see AccountAttributes
 */
public final class AccountsDb extends EntitiesDb<Account, AccountAttributes> {

    private static final AccountsDb instance = new AccountsDb();

    private AccountsDb() {
        // prevent initialization
    }

    public static AccountsDb inst() {
        return instance;
    }

    /**
     * Gets an account.
     */
    public AccountAttributes getAccount(String googleId) {
        assert googleId != null;

        return googleId.isEmpty() ? null : makeAttributesOrNull(getAccountEntity(googleId));
    }

    /**
     * Returns a list of accounts with email matching {@code email}.
     */
    public List<AccountAttributes> getAccountsForEmail(String email) {
        assert email != null;

        List<Account> accounts = load().filter("email =", email).list();

        return makeAttributes(accounts);
    }

    /**
     * Updates an account with {@link AccountAttributes.UpdateOptions}.
     *
     * @return updated account
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if account cannot be found
     */
    public AccountAttributes updateAccount(AccountAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        Account account = getAccountEntity(updateOptions.getGoogleId());
        if (account == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        AccountAttributes newAttributes = makeAttributes(account);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<Map<String, Instant>>hasSameValue(account.getReadNotifications(), newAttributes.getReadNotifications())
                && this.hasSameValue(account.isMigrated(), newAttributes.isMigrated());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Account.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        account.setReadNotifications(newAttributes.getReadNotifications());
        account.setMigrated(newAttributes.isMigrated());

        saveEntity(account);

        return makeAttributes(account);
    }

    /**
     * Deletes an account.
     *
     * <p>Fails silently if there is no such account.
     */
    public void deleteAccount(String googleId) {
        assert googleId != null;

        deleteEntity(Key.create(Account.class, googleId));
    }

    private Account getAccountEntity(String googleId) {
        return load().id(googleId).now();
    }

    @Override
    LoadType<Account> load() {
        return ofy().load().type(Account.class);
    }

    @Override
    boolean hasExistingEntities(AccountAttributes entityToCreate) {
        Key<Account> keyToFind = Key.create(Account.class, entityToCreate.getGoogleId());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    AccountAttributes makeAttributes(Account entity) {
        assert entity != null;

        return AccountAttributes.valueOf(entity);
    }
}
