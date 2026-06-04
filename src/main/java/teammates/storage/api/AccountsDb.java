package teammates.storage.api;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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
     * Gets accounts based on email.
     */
    public List<Account> getAccountsByEmail(String email) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Account> cr = cb.createQuery(Account.class);
        Root<Account> accountRoot = cr.from(Account.class);

        cr.select(accountRoot).where(cb.equal(accountRoot.get("email"), email));

        return HibernateUtil.createQuery(cr).getResultList();
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
