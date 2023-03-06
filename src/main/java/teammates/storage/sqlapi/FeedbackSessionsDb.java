package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Session;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 */
public final class FeedbackSessionsDb extends EntitiesDb<FeedbackSession> {

    private static final FeedbackSessionsDb instance = new FeedbackSessionsDb();

    private FeedbackSessionsDb() {
        // prevent initialization
    }

    public static FeedbackSessionsDb inst() {
        return instance;
    }

    /**
     * Gets a feedback session.
     *
     * @return null if not found
     */
    public FeedbackSession getFeedbackSession(Integer fsId) {
        assert fsId != null;

        return HibernateUtil.get(FeedbackSession.class, fsId);
    }

    /**
     * Saves an updated {@code FeedbackSession} to the db.
     *
     * @return updated feedback session
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession updateFeedbackSession(FeedbackSession feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert feedbackSession != null;

        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        if (getFeedbackSession(feedbackSession.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(feedbackSession);
    }

    /**
     * Deletes a feedback session.
     */
    public void deleteFeedbackSession(Integer fsId) {
        assert fsId != null;

        FeedbackSession fs = getFeedbackSession(fsId);
        if (fs != null) {
            delete(fs);
        }
    }

    /**
     * Gets a list of all sessions for the given course except those are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        return getFeedbackSessionEntitiesForCourse(courseId).stream()
                .filter(fs -> fs.getDeletedAt() == null)
                .collect(Collectors.toList());    
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        assert courseId != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return session.createQuery(cr).getResultList();
    }

    /**
     * Gets a list of all sessions starting from some date for the given course except those are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourseStartingAfter(String courseId, Instant after) {
        return getFeedbackSessionEntitiesForCourseStartingAfter(courseId, after).stream()
                .filter(session -> session.getDeletedAt() == null)
                .collect(Collectors.toList());
    }

    private List<FeedbackSession> getFeedbackSessionEntitiesForCourseStartingAfter(String courseId, Instant after) {
        assert courseId != null;
        assert after != null;

        Session session = HibernateUtil.getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), after), 
                    cb.equal(root.get("courseId"), courseId)));

        return session.createQuery(cr).getResultList();
    }
}
