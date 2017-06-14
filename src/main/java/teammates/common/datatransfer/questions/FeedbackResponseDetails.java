package teammates.common.datatransfer.questions;

import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

/** A class holding the details for the response of a specific question type.
 * This abstract class is inherited by concrete Feedback*ResponseDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information depending on the
 * question type.
 */
public abstract class FeedbackResponseDetails {
    public FeedbackQuestionType questionType;

    public FeedbackResponseDetails(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    /**
     * Extract response details and sets details accordingly.
     */
    public abstract void extractResponseDetails(
            FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails,
            String[] answer);

    public abstract String getAnswerString();

    public abstract String getAnswerHtmlInstructorView(FeedbackQuestionDetails questionDetails);

    public String getAnswerHtmlStudentView(FeedbackQuestionDetails questionDetails) {
        return getAnswerHtmlInstructorView(questionDetails);
    }

    public abstract String getAnswerCsv(FeedbackQuestionDetails questionDetails);

    /**
     * getAnswerHtml with an additional parameter (FeedbackSessionResultsBundle)
     *
     * <p>default action is to call getAnswerHtml(FeedbackQuestionDetails questionDetails).
     * override in child class if necessary.
     */
    public String getAnswerHtml(FeedbackResponseAttributes response, FeedbackQuestionAttributes question,
                                FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getAnswerHtmlInstructorView(question.getQuestionDetails());
    }

    /**
     * getAnswerCsv with an additional parameter (FeedbackSessionResultsBundle)
     *
     * <p>default action is to call getAnswerCsv(FeedbackQuestionDetails questionDetails).
     * override in child class if necessary.
     */
    public String getAnswerCsv(FeedbackResponseAttributes response, FeedbackQuestionAttributes question,
                                    FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getAnswerCsv(question.getQuestionDetails());
    }

    public static FeedbackResponseDetails createResponseDetails(
            String[] answer, FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails,
            Map<String, String[]> requestParameters, int questionIndx, int responseIndx) {

        return questionType.getFeedbackResponseDetailsInstance(questionDetails, answer, requestParameters,
                                                               questionIndx, responseIndx);
    }
}
