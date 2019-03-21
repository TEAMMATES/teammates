package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Account;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 * @see AccountAttributes
 */
public class AccountsDb extends EntitiesDb<Account, AccountAttributes> {

    /**
     * Gets the data transfer version of the account.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return Null if not found.
     */
    public AccountAttributes getAccount(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
        return googleId.isEmpty() ? null : makeAttributesOrNull(getAccountEntity(googleId));
    }

    /**
     * Returns {@link AccountAttributes} objects for all accounts with instructor privileges.
     *         Returns an empty list if no such accounts are found.
     */
    public List<AccountAttributes> getInstructorAccounts() {
        return makeAttributes(
                load().filter("isInstructor =", true).list());
    }

    /**
     * Updates an account with {@link AccountAttributes.UpdateOptions}.
     *
     * <br/> Preconditions: <br/>
     * * {@code accountToAdd} is not null and has valid data.
     *
     * @return updated account
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if account cannot be found
     */
    public AccountAttributes updateAccount(AccountAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updateOptions);

        Account account = getAccountEntity(updateOptions.getGoogleId());
        if (account == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + updateOptions);
        }

        AccountAttributes newAttributes = makeAttributes(account);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        account.setIsInstructor(newAttributes.isInstructor);

        saveEntity(account, newAttributes);

        return makeAttributes(account);
    }

    /**
     * Deletes an account.
     *
     * <p>Fails silently if there is no such account.
     */
    public void deleteAccount(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

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
    protected LoadType<Account> load() {
        return ofy().load().type(Account.class);
    }

    @Override
    protected Account getEntity(AccountAttributes entity) {
        return getAccountEntity(entity.googleId);
    }

    @Override
    protected boolean hasExistingEntities(AccountAttributes entityToCreate) {
        Key<Account> keyToFind = Key.create(Account.class, entityToCreate.getGoogleId());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    @Override
    protected AccountAttributes makeAttributes(Account entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return AccountAttributes.valueOf(entity);
    }
}
