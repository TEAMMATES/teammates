package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * Handles CRUD operations for feedback session logs.
 *
 * @see FeedbackSessionLog
 */
public final class FeedbackSessionLogsDb {

    private static final FeedbackSessionLogsDb instance = new FeedbackSessionLogsDb();

    private FeedbackSessionLogsDb() {
        // prevent initialization
    }

    public static FeedbackSessionLogsDb inst() {
        return instance;
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters ordered by
     * ascending timestamp. Logs with the same timestamp will be ordered by the
     * student's email.
     *
     * @param studentId        Can be null
     * @param feedbackSessionId Can be null
     */
    public List<FeedbackSessionLog> getOrderedFeedbackSessionLogs(String courseId, UUID studentId,
            UUID feedbackSessionId, Instant startTime, Instant endTime) {

        assert courseId != null;
        assert startTime != null;
        assert endTime != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSessionLog> cr = cb.createQuery(FeedbackSessionLog.class);
        Root<FeedbackSessionLog> root = cr.from(FeedbackSessionLog.class);
        Join<FeedbackSessionLog, FeedbackSession> feedbackSessionJoin = root.join("feedbackSession");
        Join<FeedbackSessionLog, Student> studentJoin = root.join("student");

        List<Predicate> predicates = new ArrayList<>();

        if (studentId != null) {
            predicates.add(cb.equal(studentJoin.get("id"), studentId));
        }

        if (feedbackSessionId != null) {
            predicates.add(cb.equal(feedbackSessionJoin.get("id"), feedbackSessionId));
        }

        predicates.add(cb.equal(feedbackSessionJoin.get("course").get("id"), courseId));
        predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startTime));
        predicates.add(cb.lessThan(root.get("timestamp"), endTime));

        cr.select(root).where(predicates.toArray(new Predicate[0])).orderBy(cb.asc(root.get("timestamp")),
                cb.asc(studentJoin.get("email")));
        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets the latest feedback session log for the given student, feedback session, and log type.
     */
    public FeedbackSessionLog getLatestFeedbackSessionLog(UUID studentId, UUID feedbackSessionId,
            FeedbackSessionLogType feedbackSessionLogType) {
        assert studentId != null;
        assert feedbackSessionId != null;
        assert feedbackSessionLogType != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSessionLog> cr = cb.createQuery(FeedbackSessionLog.class);
        Root<FeedbackSessionLog> root = cr.from(FeedbackSessionLog.class);
        Join<FeedbackSessionLog, FeedbackSession> feedbackSessionJoin = root.join("feedbackSession");
        Join<FeedbackSessionLog, Student> studentJoin = root.join("student");

        cr.select(root)
                .where(
                        cb.equal(studentJoin.get("id"), studentId),
                        cb.equal(feedbackSessionJoin.get("id"), feedbackSessionId),
                        cb.equal(root.get("feedbackSessionLogType"), feedbackSessionLogType)
                )
                .orderBy(cb.desc(root.get("timestamp")));

        try {
            return HibernateUtil.createQuery(cr).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Creates feedback session logs.
     */
    public FeedbackSessionLog createFeedbackSessionLog(FeedbackSessionLog log) {
        assert log != null;

        HibernateUtil.persist(log);

        return log;
    }

    /**
     * Creates a feedback session log if there is no duplicate in the same deduplication window.
     *
     * @return true if the log is inserted, false if it is filtered as a duplicate.
     */
    public boolean createFeedbackSessionLogIfNotDuplicate(FeedbackSessionLog log) {
        assert log != null;

        String sql = "INSERT INTO feedback_session_logs "
                + "(id, created_at, feedback_session_log_type, timestamp, session_id, student_id, dedup_window_bucket) "
                + "VALUES (:id, :createdAt, :feedbackSessionLogType, :timestamp, :sessionId, "
                + ":studentId, :dedupWindowBucket) "
                + "ON CONFLICT ON CONSTRAINT uq_fsl_student_session_type_bucket DO NOTHING";

        int rowsAffected = HibernateUtil.createNativeMutationQuery(sql)
                .setParameter("id", log.getId())
                .setParameter("createdAt", log.getCreatedAt() == null ? Instant.now() : log.getCreatedAt())
                .setParameter("feedbackSessionLogType", log.getFeedbackSessionLogType().name())
                .setParameter("timestamp", log.getTimestamp())
                .setParameter("sessionId", log.getFeedbackSession().getId())
                .setParameter("studentId", log.getStudent().getId())
                .setParameter("dedupWindowBucket", log.getDedupWindowBucket())
                .executeUpdate();

        return rowsAffected == 1;
    }

    /**
     * Deletes feedback session logs older than the given cutoff time.
     */
    public int deleteFeedbackSessionLogsOlderThan(Instant cutoffTime) {
        assert cutoffTime != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaDelete<FeedbackSessionLog> cd = cb.createCriteriaDelete(FeedbackSessionLog.class);
        Root<FeedbackSessionLog> root = cd.from(FeedbackSessionLog.class);
        cd.where(cb.lessThan(root.get("timestamp"), cutoffTime));

        return HibernateUtil.createMutationQuery(cd).executeUpdate();
    }
}
