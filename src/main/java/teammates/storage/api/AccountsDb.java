package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.storage.entity.Account;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 * @see AccountAttributes
 */
public class AccountsDb extends EntitiesDb<Account, AccountAttributes> {
    /**
     * Preconditions:
     * <br> * {@code accountToAdd} is not null and has valid data.
     */
    public void createAccount(AccountAttributes accountToAdd)
            throws InvalidParametersException {
        // TODO: use createEntity once there is a proper way to add instructor accounts.
        try {
            createEntity(accountToAdd);
        } catch (EntityAlreadyExistsException e) {
            // We update the account instead if it already exists. This is due to how
            // adding of instructor accounts work.
            try {
                updateAccount(accountToAdd);
            } catch (EntityDoesNotExistException edne) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Account found to be already existing and not existing simultaneously");
            }
        }
    }

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
     * Preconditions:
     * <br> * {@code accountToAdd} is not null and has valid data.
     */
    public void updateAccount(AccountAttributes a)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, a);

        if (!a.isValid()) {
            throw new InvalidParametersException(a.getInvalidityInfo());
        }

        Account accountToUpdate = getAccountEntity(a.googleId);

        if (accountToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + a.googleId
                + ThreadHelper.getCurrentThreadStack());
        }

        a.sanitizeForSaving();
        accountToUpdate.setName(a.name);
        accountToUpdate.setEmail(a.email);
        accountToUpdate.setIsInstructor(a.isInstructor);
        accountToUpdate.setInstitute(a.institute);
        saveEntity(accountToUpdate, a);
    }

    /**
     * Note: This is a non-cascade delete. <br>
     *   <br> Fails silently if there is no such account.
     * <br> Preconditions:
     * <br> * {@code googleId} is not null.
     */
    public void deleteAccount(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        Account accountToDelete = getAccountEntity(googleId);

        if (accountToDelete == null) {
            return;
        }

        deleteEntityDirect(accountToDelete);
    }

    public void deleteAccounts(Collection<AccountAttributes> accounts) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, accounts);

        for (AccountAttributes accountToDelete : accounts) {
            deleteAccount(accountToDelete.googleId);
        }
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
    protected QueryKeys<Account> getEntityQueryKeys(AccountAttributes attributes) {
        Key<Account> keyToFind = Key.create(Account.class, attributes.googleId);
        return load().filterKey(keyToFind).keys();
    }

    @Override
    protected AccountAttributes makeAttributes(Account entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return AccountAttributes.valueOf(entity);
    }
}
