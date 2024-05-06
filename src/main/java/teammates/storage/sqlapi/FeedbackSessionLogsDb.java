package teammates.storage.sqlapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Student;

/**
 * Handles CRUD operations for feedback session logs.
 *
 * @see FeedbackSessionLog
 */
public final class FeedbackSessionLogsDb extends EntitiesDb {

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
     * Creates feedback session logs.
     */
    public FeedbackSessionLog createFeedbackSessionLog(FeedbackSessionLog log) {
        assert log != null;

        persist(log);

        return log;
    }
}
