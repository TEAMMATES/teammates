package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Account;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 */
public final class AccountsDb extends EntitiesDb {

    private static final AccountsDb instance = new AccountsDb();

    private AccountsDb() {
        // prevent initialization
    }

    public static AccountsDb inst() {
        return instance;
    }

    /**
     * Returns an Account with the {@code id} or null if it does not exist.
     */
    public Account getAccount(UUID id) {
        assert id != null;

        return HibernateUtil.get(Account.class, id);
    }

    /**
     * Returns an Account with the {@code googleId} or null if it does not exist.
     */
    public Account getAccountByGoogleId(String googleId) {
        assert googleId != null;

        return HibernateUtil.getBySimpleNaturalId(Account.class, googleId);
    }

    /**
     * Gets accounts based on email.
     */
    public List<Account> getAccountsByEmail(String email) {
        assert email != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Account> cr = cb.createQuery(Account.class);
        Root<Account> accountRoot = cr.from(Account.class);

        cr.select(accountRoot).where(cb.equal(accountRoot.get("email"), email));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Creates an Account.
     */
    public Account createAccount(Account account) throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;

        if (!account.isValid()) {
            throw new InvalidParametersException(account.getInvalidityInfo());
        }

        if (getAccountByGoogleId(account.getGoogleId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, account.toString()));
        }

        persist(account);
        return account;
    }

    /**
     * Saves an updated {@code Account} to the db.
     */
    public Account updateAccount(Account account) throws InvalidParametersException, EntityDoesNotExistException {
        assert account != null;

        if (!account.isValid()) {
            throw new InvalidParametersException(account.getInvalidityInfo());
        }

        if (getAccount(account.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + account.toString());
        }

        return merge(account);
    }

    /**
     * Deletes an Account.
     */
    public void deleteAccount(Account account) {
        if (account != null) {
            delete(account);
        }
    }

}
