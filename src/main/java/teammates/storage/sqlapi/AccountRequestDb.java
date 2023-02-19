package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.List;

import org.hibernate.Session;

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
public final class AccountRequestDb extends EntitiesDb<AccountRequest> {
    private static final AccountRequestDb instance = new AccountRequestDb();

    private AccountRequestDb() {
        // prevent instantiation
    }

    public static AccountRequestDb inst() {
        return instance;
    }

    /**
     * Creates an AccountRequest in the database.
     */
    public AccountRequest createAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        accountRequest.sanitizeForSaving();
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
     * Get AccountRequest by {@code accountRequestId} from database.
     */
    public AccountRequest getAccountRequest(int accountRequestId) {
        return HibernateUtil.getSessionFactory().getCurrentSession().get(AccountRequest.class, accountRequestId);
    }

    /**
     * Get AccountRequest by {@code email} and {@code institute} from database.
     */
    public AccountRequest getAccountRequest(String email, String institute) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.equal(
                root.get("email"), email), cb.equal(root.get("institute"), institute)));

        TypedQuery<AccountRequest> query = currentSession.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Get AccountRequest by {@code registrationKey} from database.
     */
    public AccountRequest getAccountRequest(String registrationKey) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.equal(root.get("registrationKey"), registrationKey));

        TypedQuery<AccountRequest> query = currentSession.createQuery(cr);
        return query.getResultStream().findFirst().orElse(null);
    }

    /**
     * Get AccountRequest with {@code createdTime} within the times {@code startTime} and {@code endTime}.
     */
    public List<AccountRequest> getAccountRequests(Instant startTime, Instant endTime) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThanOrEqualTo(root.get("createdAt"), endTime)));

        TypedQuery<AccountRequest> query = currentSession.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Updates or creates (if does not exist) the AccountRequest in the database.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert accountRequest != null;

        accountRequest.sanitizeForSaving();
        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }

        if (getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute()) == null) {
            throw new EntityDoesNotExistException(
                String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, accountRequest.toString()));
        }

        merge(accountRequest);
        return accountRequest;
    }

    /**
     * Delete the AccountRequest with the given email and institute from the database.
     */
    public void deleteAccountRequestByEmailAndInstitute(String email, String institute) {
        assert email != null && institute != null;

        AccountRequest accountRequestToDelete = getAccountRequest(email, institute);
        if (accountRequestToDelete != null) {
            delete(accountRequestToDelete);
        }
    }
}
