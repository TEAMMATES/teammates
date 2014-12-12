package teammates.common.datatransfer;


/** A class holding the details for the response of a specific question type.
 * This abstract class is inherited by concrete Feedback*ResponseDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information depending on the 
 * question type.
 */
public abstract class FeedbackAbstractResponseDetails {
    public FeedbackQuestionType questionType;
    
    public FeedbackAbstractResponseDetails(FeedbackQuestionType questionType){
        this.questionType = questionType;
    }
    
    /**
     * Extract response details and sets details accordingly.
     * 
     * @param questionType
     * @param questionDetails
     * @param answer
     * @return true to indicate success in extracting the details, false otherwise.
     */
    public abstract boolean extractResponseDetails(
            FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails,
            String[] answer);
    
    public abstract String getAnswerString();
    
    public abstract String getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails);
    
    public abstract String getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails);
    
    /**
     * getAnswerHtml with an additional parameter (FeedbackSessionResultsBundle)
     * 
     * default action is to call getAnswerHtml(FeedbackAbstractQuestionDetails questionDetails)
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
     * default action is to call getAnswerCsv(FeedbackAbstractQuestionDetails questionDetails)
     * override in child class if necessary.
     * 
     * @param questionDetails
     * @param feedbackSessionResultsBundle
     * @return
     */
    public String getAnswerCsv(FeedbackResponseAttributes response, FeedbackQuestionAttributes question, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getAnswerCsv(question.getQuestionDetails());
    }
    
    
    
    public static FeedbackAbstractResponseDetails createResponseDetails(
            String[] answer, FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails) {
        
        FeedbackAbstractResponseDetails responseDetails = questionType.getFeedbackResponseDetailsInstance();
        
        if (!responseDetails.extractResponseDetails(questionType, questionDetails, answer)) {
            // Set response details to null if extracting response details failed.
            responseDetails = null;
        }
        
        return responseDetails;
    }
}
