package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

/**
 * Handles CRUD operations for feedback sessions.
 *
 * @see FeedbackSession
 */
public final class FeedbackSessionsDb extends EntitiesDb {

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
    public FeedbackSession getFeedbackSession(UUID fsId) {
        assert fsId != null;

        return HibernateUtil.get(FeedbackSession.class, fsId);
    }

    /**
     * Gets a feedback session for {@code feedbackSessionName} and {@code courseId}.
     *
     * @return null if not found
     */
    public FeedbackSession getFeedbackSession(String feedbackSessionName, String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cq = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> fsRoot = cq.from(FeedbackSession.class);
        Join<FeedbackSession, Course> fsJoin = fsRoot.join("course");
        cq.select(fsRoot).where(cb.and(
                cb.equal(fsRoot.get("name"), feedbackSessionName),
                cb.equal(fsJoin.get("id"), courseId)));
        return HibernateUtil.createQuery(cq).getResultStream().findFirst().orElse(null);
    }

    /**
     * Creates a feedback session.
     */
    public FeedbackSession createFeedbackSession(FeedbackSession session)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert session != null;

        if (!session.isValid()) {
            throw new InvalidParametersException(session.getInvalidityInfo());
        }

        if (getFeedbackSession(session.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, session.toString()));
        }

        persist(session);
        return session;
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
    public void deleteFeedbackSession(FeedbackSession feedbackSession) {
        if (feedbackSession != null) {
            delete(feedbackSession);
        }
    }

    /**
     * Gets feedback sessions for a given {@code courseId}.
     */
    public List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {
        assert courseId != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root).where(cb.equal(root.get("courseId"), courseId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets feedback sessions for a given {@code courseId} that start after {@code after}.
     */
    public List<FeedbackSession> getFeedbackSessionEntitiesForCourseStartingAfter(String courseId, Instant after) {
        assert courseId != null;
        assert after != null;

        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<FeedbackSession> cr = cb.createQuery(FeedbackSession.class);
        Root<FeedbackSession> root = cr.from(FeedbackSession.class);

        cr.select(root)
                .where(cb.and(
                    cb.greaterThanOrEqualTo(root.get("startTime"), after),
                    cb.equal(root.get("courseId"), courseId)));

        return HibernateUtil.createQuery(cr).getResultList();
    }
}
