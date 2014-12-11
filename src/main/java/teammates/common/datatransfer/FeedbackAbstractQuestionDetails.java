package teammates.common.datatransfer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;

/** A class holding the details for a specific question type.
+ * This abstract class is inherited by concrete Feedback*QuestionDetails
+ * classes which provides the implementation for the various abstract methods
+ * such that pages can render the correct information/forms depending on the 
+ * question type
+ */
public abstract class FeedbackAbstractQuestionDetails {
    public FeedbackQuestionType questionType;
    public String questionText;
    
    protected FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType){
        this.questionType = questionType;
    }
    
    protected FeedbackAbstractQuestionDetails(FeedbackQuestionType questionType,
            String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
        
    public abstract String getQuestionTypeDisplayName();
    
    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
            int qnIdx, int responseIdx, String courseId,
            FeedbackAbstractResponseDetails existingResponseDetails);
    
    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
            int qnIdx, int responseIdx, String courseId);
    
    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);
    
    public abstract String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId);
    
    public abstract String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view);
    
    public abstract String getQuestionResultStatisticsCsv(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle);
    
    public abstract boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails);
    
    public abstract String getCsvHeader();
    
    /**
     * Validates the question details
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an empty list if question details are valid.
     */
    public abstract List<String> validateQuestionDetails();
    
    /**
     * Validates {@code List<FeedbackResponseAttributes>} for the question based on the current {@code Feedback*QuestionDetails}.
     * @param responses - The {@code List<FeedbackResponseAttributes>} for the question to be validated
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an empty list if question responses are valid.
     */
    public abstract List<String> validateResponseAttributes(List<FeedbackResponseAttributes> responses, int numRecipients);
    
    /**
     * Extract question details and sets details accordingly
     * @param requestParameters
     * @param questionType
     * @return true to indicate success in extracting the details, false otherwise.
     */
    public abstract boolean extractQuestionDetails(Map<String, String[]> requestParameters, FeedbackQuestionType questionType);
    
    public static FeedbackAbstractQuestionDetails createQuestionDetails(Map<String, String[]> requestParameters, FeedbackQuestionType questionType) {
        String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        Assumption.assertNotNull("Null question text", questionText);
        Assumption.assertNotEmpty("Empty question text", questionText);
        
        FeedbackAbstractQuestionDetails questionDetails = null;
        
        Class<? extends FeedbackAbstractQuestionDetails> questionDetailsClass= questionType.getQuestionDetailsClass();
        Constructor<? extends FeedbackAbstractQuestionDetails> questionDetailsClassConstructor;
        try {
            questionDetailsClassConstructor = questionDetailsClass.getConstructor();
            questionDetails = questionDetailsClassConstructor.newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
            Assumption.fail("Failed to instantiate Feedback*QuestionDetails instance for " + questionType.toString() + " question type.");
        }
        
        questionDetails.questionText = questionText;
        questionDetails.extractQuestionDetails(requestParameters, questionType);
        
        return questionDetails;
    }
}