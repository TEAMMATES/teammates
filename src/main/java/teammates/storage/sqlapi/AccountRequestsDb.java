package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.logic.entity.AccountRequest;

/**
 * Generates CRUD operations for AccountRequest.
 *
 * @see AccountRequest
 */
public final class AccountRequestsDb {
    private static final AccountRequestsDb instance = new AccountRequestsDb();

    private AccountRequestsDb() {
        // prevent instantiation
    }

    public static AccountRequestsDb inst() {
        return instance;
    }

    /**
     * Escapes LIKE pattern metacharacters so user input is treated literally.
     */
    private static String escapeLikePattern(String pattern, char escapeChar) {
        String esc = String.valueOf(escapeChar);
        return pattern
                .replace(esc, esc + esc)
                .replace("%", esc + "%")
                .replace("_", esc + "_");
    }

    /**
     * Creates an AccountRequest in the database.
     */
    public AccountRequest createAccountRequest(AccountRequest accountRequest) {
        assert accountRequest != null;

        HibernateUtil.persist(accountRequest);
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
     * Get all Account Requests for a given {@code email} and {@code institute}.
     */
    public List<AccountRequest> getApprovedAccountRequestsForEmailAndInstitute(String email, String institute) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);
        cr.select(root).where(cb.and(
                cb.equal(root.get("email"), email),
                cb.equal(root.get("institute"), institute),
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
     * Deletes an AccountRequest.
     */
    public void deleteAccountRequest(AccountRequest accountRequest) {
        if (accountRequest != null) {
            HibernateUtil.remove(accountRequest);
        }
    }

    /**
     * Searches all account requests in the system.
     *
     * <p>This is used by admin to search account requests in the whole system.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString) {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        char escapeChar = '\\';
        String escapedQuery = escapeLikePattern(queryString.toLowerCase(), escapeChar);
        String wildcardQuery = "%" + escapedQuery + "%";

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountRequest> cr = cb.createQuery(AccountRequest.class);
        Root<AccountRequest> root = cr.from(AccountRequest.class);

        Predicate searchPredicate = cb.or(
                cb.like(cb.lower(root.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(root.get("email")), wildcardQuery, escapeChar),
                cb.like(cb.lower(root.get("institute")), wildcardQuery, escapeChar),
                cb.like(cb.lower(cb.coalesce(root.get("comments"), "")), wildcardQuery, escapeChar),
                cb.like(cb.lower(cb.coalesce(root.get("status").as(String.class), "")), wildcardQuery, escapeChar));

        cr.select(root)
                .where(searchPredicate)
                .orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<AccountRequest> query = HibernateUtil.createQuery(cr);
        query.setMaxResults(Const.SEARCH_QUERY_SIZE_LIMIT);
        return query.getResultList();
    }
}
