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
     * Returns an Account with the given auth identity or null if it does not exist.
     */
    public Account getAccountByAuthIdentity(Provider provider, String subject, @Nullable String tenantId) {
        String normalizedTenantId = Account.normalizeTenantId(tenantId);
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Account> cr = cb.createQuery(Account.class);
        Root<Account> root = cr.from(Account.class);

        cr.select(root).where(cb.and(
                cb.equal(root.get("provider"), provider),
                cb.equal(root.get("subject"), subject),
                cb.equal(root.get("tenantId"), normalizedTenantId)
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
     * Atomically creates or gets an Account by auth identity and returns the persisted row.
     */
    public Account upsertAccount(Account account) {
        String sql = """
                INSERT INTO accounts (id, created_at, email, name, provider, subject, tenant_id, updated_at)
                VALUES (:id, CURRENT_TIMESTAMP, :email, :name, :provider, :subject, :tenantId,
                        CURRENT_TIMESTAMP)
                ON CONFLICT (provider, subject, tenant_id)
                DO UPDATE SET updated_at = accounts.updated_at
                RETURNING *
                """;

        return HibernateUtil.createNativeQuery(sql, Account.class)
                .setParameter("id", account.getId())
                .setParameter("email", account.getEmail())
                .setParameter("name", account.getName())
                .setParameter("provider", account.getProvider().name())
                .setParameter("subject", account.getSubject())
                .setParameter("tenantId", account.getTenantId())
                .getSingleResult();
    }

    /**
     * Removes an Account.
     */
    public void removeAccount(Account account) {
        HibernateUtil.remove(account);
    }

}
