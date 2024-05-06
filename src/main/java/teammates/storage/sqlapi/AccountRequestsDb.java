package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlsearch.AccountRequestSearchManager;
import teammates.storage.sqlsearch.SearchManagerFactory;

/**
 * Generates CRUD operations for AccountRequest.
 *
 * @see AccountRequest
 */
public final class AccountRequestsDb extends EntitiesDb {
    private static final AccountRequestsDb instance = new AccountRequestsDb();

    private AccountRequestsDb() {
        // prevent instantiation
    }

    public static AccountRequestsDb inst() {
        return instance;
    }

    public AccountRequestSearchManager getSearchManager() {
        return SearchManagerFactory.getAccountRequestSearchManager();
    }

    /**
     * Creates an AccountRequest in the database.
     */
    public AccountRequest createAccountRequest(AccountRequest accountRequest) throws InvalidParametersException {
        assert accountRequest != null;

        if (!accountRequest.isValid()) {
            throw new InvalidParametersException(accountRequest.getInvalidityInfo());
        }
        persist(accountRequest);
        return accountRequest;
    }

    /**
     * Get AccountRequest by {@code id} from the database.
     */
    public AccountRequest getAccountRequest(UUID id) {
        assert id != null;
        return HibernateUtil.get(AccountRequest.class, id);
    }

    /**
     * Get all Account Requests with {@code status} of 'pending'.
     */
    public List<AccountRequest> getPendingAccountRequests() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root)
                .where(cb.equal(root.get("status"), AccountRequestStatus.PENDING))
                .orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Get all Account Requests.
     */
    public List<AccountRequest> getAllAccountRequests() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root);

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Get all Account Requests for a given {@code email}.
     */
    public List<AccountRequest> getApprovedAccountRequestsForEmail(String email) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(cb.equal(root.get("email"), email),
                cb.equal(root.get("status"), AccountRequestStatus.APPROVED)));

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Get AccountRequest by {@code registrationKey} from database.
     */
    public AccountRequest getAccountRequestByRegistrationKey(String registrationKey) {
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

        if (getAccountRequest(accountRequest.getId()) == null) {
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
            deleteDocumentByAccountRequestId(accountRequest.getId());
        }
    }

    /**
     * Removes search document for the given account request.
     */
    public void deleteDocumentByAccountRequestId(UUID accountRequestId) {
        if (getSearchManager() != null) {
            getSearchManager().deleteDocuments(
                    Collections.singletonList(accountRequestId.toString()));
        }
    }

    /**
     * Searches all account requests in the system.
     *
     * <p>This is used by admin to search account requests in the whole system.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return getSearchManager().searchAccountRequests(queryString);
    }
}
