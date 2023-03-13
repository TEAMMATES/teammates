package teammates.storage.sqlapi;

import java.util.UUID;

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
