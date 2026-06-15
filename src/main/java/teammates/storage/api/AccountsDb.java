package teammates.storage.api;

import java.util.UUID;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.Provider;
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
     * Returns an Account with the given auth identity or null if it does not exist.
     */
    public Account getAccountByAuthIdentity(Provider provider, String subject, @Nullable String tenantId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Account> cr = cb.createQuery(Account.class);
        Root<Account> root = cr.from(Account.class);

        cr.select(root).where(cb.and(
                cb.equal(root.get("provider"), provider),
                cb.equal(root.get("subject"), subject),
                tenantId == null
                        ? cb.isNull(root.get("tenantId"))
                        : cb.equal(root.get("tenantId"), tenantId)
        ));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
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
