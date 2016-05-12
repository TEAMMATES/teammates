package teammates.common.datatransfer;

import java.util.Map;


/** A class holding the details for the response of a specific question type.
 * This abstract class is inherited by concrete Feedback*ResponseDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information depending on the 
 * question type.
 */
public abstract class FeedbackResponseDetails {
    public FeedbackQuestionType questionType;
    
    public FeedbackResponseDetails(final FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }
    
    /**
     * Extract response details and sets details accordingly.
     * 
     * @param questionType
     * @param questionDetails
     * @param answer
     */
    public abstract void extractResponseDetails(
            FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails,
            String[] answer);
    
    public abstract String getAnswerString();
    
    public abstract String getAnswerHtml(FeedbackQuestionDetails questionDetails);
    
    public abstract String getAnswerCsv(FeedbackQuestionDetails questionDetails);
    
    /**
     * getAnswerHtml with an additional parameter (FeedbackSessionResultsBundle)
     * 
     * default action is to call getAnswerHtml(FeedbackQuestionDetails questionDetails)
     * override in child class if necessary.
     * 
     * @param questionDetails
     * @param feedbackSessionResultsBundle
     * @return
     */
    public String getAnswerHtml(final FeedbackResponseAttributes response, final FeedbackQuestionAttributes question, final FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getAnswerHtml(question.getQuestionDetails());
    }
    
    /**
     * getAnswerCsv with an additional parameter (FeedbackSessionResultsBundle)
     * 
     * default action is to call getAnswerCsv(FeedbackQuestionDetails questionDetails)
     * override in child class if necessary.
     * 
     * @param questionDetails
     * @param feedbackSessionResultsBundle
     * @return
     */
    public String getAnswerCsv(final FeedbackResponseAttributes response, final FeedbackQuestionAttributes question, 
                                    final FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getAnswerCsv(question.getQuestionDetails());
    }
    
    public static FeedbackResponseDetails createResponseDetails(
            final String[] answer, final FeedbackQuestionType questionType,
            final FeedbackQuestionDetails questionDetails,
            final Map<String, String[]> requestParameters, final int questionIndx, final int responseIndx) {
                                
        FeedbackResponseDetails responseDetails = questionType.getFeedbackResponseDetailsInstance(
                                                                   questionDetails, answer, requestParameters, questionIndx, responseIndx);                              
        return responseDetails;
    }
}
