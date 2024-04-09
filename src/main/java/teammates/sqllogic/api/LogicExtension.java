package teammates.sqllogic.api;

import java.util.UUID;

import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;

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

    /**
     * Gets a single question corresponding to question-giver-receiver.
     */
    public FeedbackResponse getFeedbackResponseForQuestionGiverRecipient(
        UUID feedbackQuestionId,
        String giver,
        String recipient) {
        return feedbackResponsesLogic.getFeedbackResponseForQuestionGiverRecipient(feedbackQuestionId, giver, recipient);
    }    
}