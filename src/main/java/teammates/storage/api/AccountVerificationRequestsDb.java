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

import teammates.common.datatransfer.AccountVerificationRequestQuery;
import teammates.common.datatransfer.AccountVerificationRequestStatus;
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
    public AccountVerificationRequest persistAccountVerificationRequest(
            AccountVerificationRequest accountVerificationRequest) {
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
     * Gets account verification requests matching the supplied query.
     */
    public List<AccountVerificationRequest> getAccountVerificationRequests(AccountVerificationRequestQuery queryObject) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<AccountVerificationRequest> cr = cb.createQuery(AccountVerificationRequest.class);
        Root<AccountVerificationRequest> root = cr.from(AccountVerificationRequest.class);
        List<Predicate> predicates = new ArrayList<>();

        if (queryObject.status() != null) {
            predicates.add(cb.equal(root.get("status"), queryObject.status()));
        }
        if (queryObject.accountId() != null) {
            predicates.add(cb.equal(root.get("accountId"), queryObject.accountId()));
        }
        if (queryObject.instituteId() != null) {
            predicates.add(cb.equal(root.get("instituteId"), queryObject.instituteId()));
        }

        String searchKey = queryObject.searchKey();
        if (searchKey != null && !searchKey.trim().isEmpty()) {
            char escapeChar = '\\';
            String escapedQuery = escapeLikePattern(searchKey.toLowerCase(), escapeChar);
            String wildcardQuery = "%" + escapedQuery + "%";
            Join<AccountVerificationRequest, Institute> instituteJoin = root.join("institute");

            predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), wildcardQuery, escapeChar),
                    cb.like(cb.lower(root.get("email")), wildcardQuery, escapeChar),
                    cb.like(cb.lower(instituteJoin.get("name")), wildcardQuery, escapeChar),
                    cb.like(cb.lower(cb.coalesce(root.get("comments"), "")), wildcardQuery, escapeChar)));
        }

        cr.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(root.get("createdAt")));

        TypedQuery<AccountVerificationRequest> query = HibernateUtil.createQuery(cr);
        if (queryObject.limit() != null) {
            query.setMaxResults(queryObject.limit());
        }
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
     * Gets all approved account verification requests for the given account.
     */
    public List<AccountVerificationRequest> getApprovedRequestsByAccountId(UUID accountId) {
        String jpql = "SELECT r FROM AccountVerificationRequest r"
                + " WHERE r.accountId = :accountId AND r.status = :status";
        TypedQuery<AccountVerificationRequest> query = HibernateUtil.createQuery(jpql, AccountVerificationRequest.class);
        query.setParameter("accountId", accountId);
        query.setParameter("status", AccountVerificationRequestStatus.APPROVED);
        return query.getResultList();
    }

    /**
     * Gets the approved AccountVerificationRequest for the given account and institute, or null if none exists.
     */
    public AccountVerificationRequest getApprovedAccountVerificationRequest(UUID accountId, UUID instituteId) {
        String jpql = "SELECT r FROM AccountVerificationRequest r"
                + " WHERE r.accountId = :accountId AND r.instituteId = :instituteId AND r.status = :status";
        TypedQuery<AccountVerificationRequest> query = HibernateUtil.createQuery(jpql, AccountVerificationRequest.class);
        query.setParameter("accountId", accountId);
        query.setParameter("instituteId", instituteId);
        query.setParameter("status", AccountVerificationRequestStatus.APPROVED);
        return query.getResultStream().findFirst().orElse(null);
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
