package teammates.storage.api;

import java.util.UUID;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Account;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 */
public final class AccountsDb {

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
        return HibernateUtil.get(Account.class, id);
    }

    /**
     * Returns an Account with the {@code googleId} or null if it does not exist.
     */
    public Account getAccountByGoogleId(String googleId) {
        return HibernateUtil.getBySimpleNaturalId(Account.class, googleId);
    }

    /**
     * Persists an Account.
     */
    public Account persistAccount(Account account) {
        HibernateUtil.persist(account);
        return account;
    }

    /**
     * Removes an Account.
     */
    public void removeAccount(Account account) {
        HibernateUtil.remove(account);
    }

}
