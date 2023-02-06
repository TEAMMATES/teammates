package teammates.storage.sqlapi;

import java.time.Instant;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackSession;

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
     * Gets a feedback session that is not soft-deleted.
     *
     * @return null if not found or soft-deleted.
     */
    public FeedbackSession getFeedbackSession(Long fsId) {
        assert fsId != null;

        FeedbackSession fs = HibernateUtil.getSessionFactory().getCurrentSession().get(FeedbackSession.class, fsId);

        if (fs != null && fs.getDeletedAt() != null) {
            log.info("Trying to access soft-deleted session: " + fs.getName() + "/" + fs.getCourse().getId());
            return null;
        }

        return fs;
    }

    /**
     * Gets a soft-deleted feedback session.
     *
     * @return null if not found or not soft-deleted.
     */
    public FeedbackSession getSoftDeletedFeedbackSession(Long fsId) {
        assert fsId != null;

        FeedbackSession fs = HibernateUtil.getSessionFactory().getCurrentSession().get(FeedbackSession.class, fsId);

        if (fs != null && fs.getDeletedAt() != null) {
            log.info(fs.getName() + "/" + fs.getCourse().getId() + " is not soft-deleted");
            return null;
        }

        return fs;
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

        feedbackSession.sanitizeForSaving();
        if (!feedbackSession.isValid()) {
            throw new InvalidParametersException(feedbackSession.getInvalidityInfo());
        }

        if (getFeedbackSession(feedbackSession.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(feedbackSession);
    }

    /**
     * Soft-deletes a feedback session.
     *
     * @return Soft-deletion time of the feedback session.
     */
    public Instant softDeleteFeedbackSession(Long fsId)
            throws EntityDoesNotExistException {
        assert fsId != null;

        FeedbackSession fs = getFeedbackSession(fsId);
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        fs.setDeletedAt(Instant.now());

        return fs.getDeletedAt();
    }

    /**
     * Restores a specific soft deleted feedback session.
     */
    public void restoreDeletedFeedbackSession(Long fsId)
            throws EntityDoesNotExistException {
        assert fsId != null;

        FeedbackSession fs = getFeedbackSession(fsId);

        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        fs.setDeletedAt(null);
    }

    /**
     * Deletes a feedback session.
     */
    public void deleteFeedbackSession(Long fsId) {
        assert fsId != null;

        FeedbackSession fs = getFeedbackSession(fsId);
        if (fs != null) {
            delete(fs);
        }
    }
}
