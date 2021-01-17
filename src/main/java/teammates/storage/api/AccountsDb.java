package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.storage.entity.Account;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 * @see AccountAttributes
 */
public class AccountsDb extends EntitiesDb<Account, AccountAttributes> {

    /**
     * Gets an account.
     */
    public AccountAttributes getAccount(String googleId) {
        Assumption.assertNotNull(googleId);

        return googleId.isEmpty() ? null : makeAttributesOrNull(getAccountEntity(googleId));
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
        Assumption.assertNotNull(updateOptions);

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
        boolean hasSameAttributes = this.<Boolean>hasSameValue(account.isInstructor(), newAttributes.isInstructor());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Account.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        account.setIsInstructor(newAttributes.isInstructor);

        saveEntity(account);

        return makeAttributes(account);
    }

    /**
     * Deletes an account.
     *
     * <p>Fails silently if there is no such account.
     */
    public void deleteAccount(String googleId) {
        Assumption.assertNotNull(googleId);

        deleteEntity(Key.create(Account.class, googleId));
    }

    private Account getAccountEntity(String googleId) {
        Account account = load().id(googleId).now();
        if (account == null) {
            return null;
        }

        return account;
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
        Assumption.assertNotNull(entity);

        return AccountAttributes.valueOf(entity);
    }
}
