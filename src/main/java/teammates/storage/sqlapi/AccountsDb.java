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
import teammates.storage.sqlentity.AccountIdentity;

/**
 * Handles CRUD operations for accounts and account identities.
 *
 * @see Account
 * @see AccountIdentity
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
        assert id != null;

        return HibernateUtil.get(Account.class, id);
    }

    /**
     * Returns the {@link AccountIdentity} for the given OIDC issuer and subject, or null.
     */
    public AccountIdentity getAccountIdentityByIssuerAndSubject(String issuer, String subject) {
        assert issuer != null;
        assert subject != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountIdentity> cr = cb.createQuery(AccountIdentity.class);
        Root<AccountIdentity> root = cr.from(AccountIdentity.class);

        cr.select(root).where(cb.and(
                cb.equal(root.get("issuer"), issuer),
                cb.equal(root.get("subject"), subject)));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Returns the first {@link AccountIdentity} for the given account ID, or null if none exists.
     * For accounts with a single identity (the common case) this is unambiguous.
     */
    public AccountIdentity getFirstAccountIdentityByAccountId(UUID accountId) {
        assert accountId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountIdentity> cr = cb.createQuery(AccountIdentity.class);
        Root<AccountIdentity> root = cr.from(AccountIdentity.class);

        cr.select(root)
                .where(cb.equal(root.get("account").get("id"), accountId))
                .orderBy(cb.asc(root.get("createdAt")));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
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
     * Creates an Account (profile row only). Identities are persisted separately.
     */
    public Account createAccount(Account account) throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;

        if (!account.isValid()) {
            throw new InvalidParametersException(account.getInvalidityInfo());
        }

        if (getAccount(account.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, account.toString()));
        }

        HibernateUtil.persist(account);
        return account;
    }

    /**
     * Persists a new {@link AccountIdentity}.
     */
    public AccountIdentity createAccountIdentity(AccountIdentity identity)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert identity != null;

        if (!identity.isValid()) {
            throw new InvalidParametersException(identity.getInvalidityInfo());
        }

        if (getAccountIdentityByIssuerAndSubject(identity.getIssuer(), identity.getSubject()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, identity));
        }

        HibernateUtil.persist(identity);
        return identity;
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

        return HibernateUtil.merge(account);
    }

    /**
     * Deletes an Account.
     */
    public void deleteAccount(Account account) {
        if (account != null) {
            HibernateUtil.remove(account);
        }
    }
}
