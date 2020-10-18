package teammates.logic.api;

import java.time.Instant;
import java.util.List;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;

/**
 * Holds additional methods for {@link Logic} used only in tests.
 */
public class LogicExtension extends Logic {

    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName, String courseId, int questionNumber) {
        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
    }

    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String recipient) {
        return feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Instant creationDate) {
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForResponse(String responseId) {
        return feedbackResponseCommentsLogic.getFeedbackResponseCommentForResponse(responseId);
    }

    public void deleteFeedbackResponseComments(AttributesDeletionQuery query) {
        feedbackResponseCommentsLogic.deleteFeedbackResponseComments(query);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        return feedbackResponsesLogic.getFeedbackResponsesForQuestion(feedbackQuestionId);
    }

}
