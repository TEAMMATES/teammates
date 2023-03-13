package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackResponseComment;

/**
 * Handles CRUD operations for feedbackResponseComments.
 *
 * @see FeedbackResponseComment
 */
public final class FeedbackResponseCommentsDb extends EntitiesDb {

    private static final FeedbackResponseCommentsDb instance = new FeedbackResponseCommentsDb();

    private FeedbackResponseCommentsDb() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsDb inst() {
        return instance;
    }

    /**
     * Gets a feedbackResponseComment or null if it does not exist.
     */
    public FeedbackResponseComment getFeedbackResponseComment(UUID frId) {
        assert frId != null;

        return HibernateUtil.get(FeedbackResponseComment.class, frId);
    }

    /**
     * Creates a feedbackResponseComment.
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponseComment != null;

        if (!feedbackResponseComment.isValid()) {
            throw new InvalidParametersException(feedbackResponseComment.getInvalidityInfo());
        }

        if (getFeedbackResponseComment(feedbackResponseComment.getId()) != null) {
            throw new EntityAlreadyExistsException(
                    String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, feedbackResponseComment.toString()));
        }

        persist(feedbackResponseComment);
        return feedbackResponseComment;
    }

    /**
     * Deletes a feedbackResponseComment.
     */
    public void deleteFeedbackResponseComment(FeedbackResponseComment feedbackResponseComment) {
        if (feedbackResponseComment != null) {
            delete(feedbackResponseComment);
        }
    }

}
