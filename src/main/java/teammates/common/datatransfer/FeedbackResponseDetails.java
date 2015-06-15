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
    
    public FeedbackResponseDetails(FeedbackQuestionType questionType) {
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
    public String getAnswerHtml(FeedbackResponseAttributes response, FeedbackQuestionAttributes question, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
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
    public String getAnswerCsv(FeedbackResponseAttributes response, FeedbackQuestionAttributes question, 
                                    FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getAnswerCsv(question.getQuestionDetails());
    }
    
    public static FeedbackResponseDetails createResponseDetails(
            String[] answer, FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails,
            Map<String, String[]> requestParameters, int questionIndx, int responseIndx) {
                                
        FeedbackResponseDetails responseDetails = questionType.getFeedbackResponseDetailsInstance(
                                                                   questionDetails, answer, requestParameters, questionIndx, responseIndx);                              
        return responseDetails;
    }
}
