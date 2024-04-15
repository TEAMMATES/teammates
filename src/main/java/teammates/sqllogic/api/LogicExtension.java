package teammates.sqllogic.api;

import java.util.UUID;

import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * Holds additional methods for {@link Logic} used only in tests.
 */
public class LogicExtension extends Logic {

    /**
     * Gets the unique feedback question based on sessionId and questionNumber.
     */
    public FeedbackQuestion getFeedbackQuestionForSessionQuestionNumber(UUID sessionId, int questionNumber) {
        return feedbackQuestionsLogic.getFeedbackQuestionForSessionQuestionNumber(sessionId, questionNumber);
    }
}
