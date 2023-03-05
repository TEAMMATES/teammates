package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * Handles CRUD operations for feedback questions.
 *
 * @see FeedbackQuestion
 */
public final class FeedbackQuestionsDb extends EntitiesDb<FeedbackQuestion> {

    private static final FeedbackQuestionsDb instance = new FeedbackQuestionsDb();

    private FeedbackQuestionsDb() {
        // prevent initialization
    }

    public static FeedbackQuestionsDb inst() {
        return instance;
    }

    /**
     * Gets a feedback question.
     *
     * @return null if not found
     */
    public FeedbackQuestion getFeedbackQuestion(UUID fqId) {
        assert fqId != null;

        return HibernateUtil.get(FeedbackQuestion.class, fqId);
    }

    /**
     * Saves an updated {@code FeedbackQuestion} to the db.
     *
     * @return updated feedback question
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestion updateFeedbackQuestion(FeedbackQuestion feedbackQuestion)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert feedbackQuestion != null;

        if (!feedbackQuestion.isValid()) {
            throw new InvalidParametersException(feedbackQuestion.getInvalidityInfo());
        }

        if (getFeedbackQuestion(feedbackQuestion.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(feedbackQuestion);
    }

    /**
     * Deletes a feedback question.
     */
    public void deleteFeedbackQuestion(UUID fqId) {
        assert fqId != null;

        FeedbackQuestion fq = getFeedbackQuestion(fqId);
        if (fq != null) {
            delete(fq);
        }
    }
}
