package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Generates CRUD operations for AccountRequest.
 *
 * @see AccountRequest
 */
public final class AccountRequestsDb extends EntitiesDb<AccountRequest> {
    private static final AccountRequestsDb instance = new AccountRequestsDb();

    private AccountRequestsDb() {
        // prevent instantiation
    }

    public static AccountRequestsDb inst() {
        return instance;
    }

    /**
     * Creates an AccountRequest in the database.
     */
    public AccountRequest createAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }

        // don't need to check registrationKey for uniqueness since it is generated using email + institute
        if (getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()) != null) {
            throw new EntityAlreadyExistsException(
                String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, accountRequest.toString()));
        }

        persist(accountRequest);
        return accountRequest;
    }

    /**
     * Get AccountRequest by {@code email} and {@code institute} from database.
     */
    public AccountRequest getAccountRequest(String email, String institute) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.equal(
                root.get("email"), email), cb.equal(root.get("institute"), institute)));

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Get AccountRequest by {@code registrationKey} from database.
     */
    public AccountRequest getAccountRequest(String registrationKey) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.equal(root.get("registrationKey"), registrationKey));

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Get AccountRequest with {@code createdTime} within the times {@code startTime} and {@code endTime}.
     */
    public List<AccountRequest> getAccountRequests(Instant startTime, Instant endTime) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThanOrEqualTo(root.get("createdAt"), endTime)));

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Updates or creates (if does not exist) the AccountRequest in the database.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert accountRequest != null;

        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }

        if (getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()) == null) {
            throw new EntityDoesNotExistException(
                String.format(ERROR_UPDATE_NON_EXISTENT, accountRequest.toString()));
        }

        merge(accountRequest);
        return accountRequest;
    }

    /**
     * Deletes an AccountRequest.
     */
    public void deleteAccountRequest(AccountRequest accountRequest) {
        if (accountRequest != null) {
            delete(accountRequest);
        }
    }
}
