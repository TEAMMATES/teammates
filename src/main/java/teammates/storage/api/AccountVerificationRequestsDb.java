package teammates.storage.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Institute;

/**
 * Generates CRUD operations for AccountVerificationRequest.
 *
 * @see AccountVerificationRequest
 */
public final class AccountVerificationRequestsDb {
    private static final AccountVerificationRequestsDb instance = new AccountVerificationRequestsDb();

    private AccountVerificationRequestsDb() {
        // prevent instantiation
    }

    public static AccountVerificationRequestsDb inst() {
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
     * Persists an AccountVerificationRequest in the database.
     */
    public AccountVerificationRequest persistAccountVerificationRequest(AccountVerificationRequest accountVerificationRequest) {
        HibernateUtil.persist(accountVerificationRequest);
        return accountVerificationRequest;
    }

    /**
     * Get AccountVerificationRequest by {@code id} from the database.
     */
    public AccountVerificationRequest getAccountVerificationRequest(UUID id) {
        return HibernateUtil.get(AccountVerificationRequest.class, id);
    }

    /**
     * Get all Account Verification Requests with {@code status} of 'pending'.
     */
    public List<AccountVerificationRequest> getPendingAccountVerificationRequests() {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountVerificationRequest> cr = cb.createQuery(AccountVerificationRequest.class);
        Root<AccountVerificationRequest> root = cr.from(AccountVerificationRequest.class);
        cr.select(root)
                .where(cb.equal(root.get("status"), AccountVerificationRequestStatus.PENDING))
                .orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<AccountVerificationRequest> query = HibernateUtil.createQuery(cr);
        return query.getResultList();
    }

    /**
     * Removes an AccountVerificationRequest.
     */
    public void removeAccountVerificationRequest(AccountVerificationRequest accountVerificationRequest) {
        if (accountVerificationRequest != null) {
            HibernateUtil.remove(accountVerificationRequest);
        }
    }

    /**
     * Searches all account verification requests in the system.
     *
     * <p>This is used by admin to search account verification requests in the whole system.
     */
    public List<AccountVerificationRequest> searchAccountVerificationRequestsInWholeSystem(String queryString) {

        if (queryString.trim().isEmpty()) {
            return new ArrayList<>();
        }

        char escapeChar = '\\';
        String escapedQuery = escapeLikePattern(queryString.toLowerCase(), escapeChar);
        String wildcardQuery = "%" + escapedQuery + "%";

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountVerificationRequest> cr = cb.createQuery(AccountVerificationRequest.class);
        Root<AccountVerificationRequest> root = cr.from(AccountVerificationRequest.class);
        Join<AccountVerificationRequest, Institute> instituteJoin = root.join("institute");

        Predicate searchPredicate = cb.or(
                cb.like(cb.lower(root.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(root.get("email")), wildcardQuery, escapeChar),
                cb.like(cb.lower(instituteJoin.get("name")), wildcardQuery, escapeChar),
                cb.like(cb.lower(cb.coalesce(root.get("comments"), "")), wildcardQuery, escapeChar),
                cb.like(cb.lower(cb.coalesce(root.get("status").as(String.class), "")), wildcardQuery, escapeChar));

        cr.select(root)
                .where(searchPredicate)
                .orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<AccountVerificationRequest> query = HibernateUtil.createQuery(cr);
        query.setMaxResults(Const.SEARCH_QUERY_SIZE_LIMIT);
        return query.getResultList();
    }

    /**
     * Gets createdAt timestamps of account verification requests created within the given time range.
     */
    public List<Instant> getCreatedAtTimestampsForTimeRange(Instant startTime, Instant endTime) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<Instant> cr = cb.createQuery(Instant.class);
        Root<AccountVerificationRequest> root = cr.from(AccountVerificationRequest.class);
        cr.select(root.get("createdAt")).where(cb.and(
                cb.greaterThanOrEqualTo(root.get("createdAt"), startTime),
                cb.lessThan(root.get("createdAt"), endTime)));
        return HibernateUtil.createQuery(cr).getResultList();
    }
}
