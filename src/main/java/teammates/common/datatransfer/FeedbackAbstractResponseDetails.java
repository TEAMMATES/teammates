package teammates.common.datatransfer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import teammates.common.util.Assumption;

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
    
    
    public static FeedbackAbstractResponseDetails createResponseDetails(
            String[] answer, FeedbackQuestionType questionType,
            FeedbackAbstractQuestionDetails questionDetails) {
        
        FeedbackAbstractResponseDetails responseDetails = null;
        
        Class<? extends FeedbackAbstractResponseDetails> responseDetailsClass= questionType.getResponseDetailsClass();
        Constructor<? extends FeedbackAbstractResponseDetails> responseDetailsClassConstructor;
        try {
            responseDetailsClassConstructor = responseDetailsClass.getConstructor();
            responseDetails = responseDetailsClassConstructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            Assumption.fail("Failed to instantiate Feedback*ResponseDetails instance for " + questionType.toString() + " question type.");
        }
        
        //TODO: assert answer is not null, size > 0, etc.
        
        if (!responseDetails.extractResponseDetails(questionType, questionDetails, answer)) {
            // Set response details to null if extracting response details failed.
            responseDetails = null;
        }
        
        return responseDetails;
    }
}
