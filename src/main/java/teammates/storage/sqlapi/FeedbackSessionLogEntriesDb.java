package teammates.storage.sqlapi;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSessionLogEntry;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for feedback session logs.
 *
 * @see FeedbackSessionLogEntry
 */
public final class FeedbackSessionLogEntriesDb extends EntitiesDb<FeedbackSessionLogEntry> {

    private static final FeedbackSessionLogEntriesDb instance = new FeedbackSessionLogEntriesDb();

    private FeedbackSessionLogEntriesDb() {
        // prevent initialization
    }

    public static FeedbackSessionLogEntriesDb inst() {
        return instance;
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters.
     */
    public List<FeedbackSessionLogEntry> getFeedbackSessionLogs(
            String courseId, String email, long startTime, long endTime, String fsName) {

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<FeedbackSessionLogEntry> cr = cb.createQuery(FeedbackSessionLogEntry.class);
        Root<FeedbackSessionLogEntry> root = cr.from(FeedbackSessionLogEntry.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startTime));
        predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), endTime));
        predicates.add(cb.equal(root.get("courseId"), courseId));

        if (email != null) {
            predicates.add(cb.equal(root.get("studentEmail"), email));
        }

        if (fsName != null) {
            predicates.add(cb.equal(root.get("feedbackSessionName"), fsName));
        }

        cr.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        return session.createQuery(cr).getResultList();
    }

    /**
     * Creates feedback session logs.
     */
    public List<FeedbackSessionLogEntry> createFeedbackSessionLogs(
            List<FeedbackSessionLogEntry> entries) throws InvalidParametersException {
        assert entries != null;

        for (FeedbackSessionLogEntry entry : entries) {
            persist(entry);
        }

        return entries;
    }

}
