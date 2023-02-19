package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.List;

import org.hibernate.Session;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;

/**
 * Generates CRUD operations for AccountRequest
 *
 * @see AccountRequest
 */
public final class AccountRequestDb extends EntitiesDb<AccountRequest> {
    private static final AccountRequestDb instance = new AccountRequestDb();

    private AccountRequestDb() {
        // prevent instantiation
    }

    public static AccountRequestDb getInstance() {
        return instance;
    }

    /**
     * Creates an AccountRequest in the database.
     */
    public AccountRequest createAccountRequest(AccountRequest accountRequest) throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        accountRequest.sanitizeForSaving();
        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }

        if (getAccountRequest(accountRequest.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, accountRequest.toString()));
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

    public AccountRequest getAccountRequest(String email, String institute) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.equal(root.get("email"), email), cb.equal(root.get("institute"), institute)));

        TypedQuery<AccountRequest> query = currentSession.createQuery(cr);
        return query.getSingleResult();
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
        return query.getSingleResult();
    }

    /**
     * Get AccountRequest with {@code createdTime} within the times {@code startTime} and {@code endTime}.
     */
    public List<AccountRequest> getAccountRequests(Instant startTime, Instant endTime) {
        Session currentSession = HibernateUtil.getSessionFactory().getCurrentSession();
        CriteriaBuilder cb = currentSession.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.greaterThanOrEqualTo(root.get("createdTime"), startTime), cb.lessThanOrEqualTo(root.get("createdTime"), endTime)));

        TypedQuery<AccountRequest> query = currentSession.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Updates the AccountRequest in the database.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest) throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        accountRequest.sanitizeForSaving();
        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }

        if (getAccountRequest(accountRequest.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, accountRequest.toString()));
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
