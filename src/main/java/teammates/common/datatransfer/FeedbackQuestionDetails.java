package teammates.common.datatransfer;

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
public abstract class FeedbackQuestionDetails {
    public FeedbackQuestionType questionType;
    public String questionText;
    
    protected FeedbackQuestionDetails(FeedbackQuestionType questionType){
        this.questionType = questionType;
    }
    
    protected FeedbackQuestionDetails(FeedbackQuestionType questionType,
            String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }
        
    public abstract String getQuestionTypeDisplayName();
    
    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
            int qnIdx, int responseIdx, String courseId,
            FeedbackResponseDetails existingResponseDetails);
    
    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(boolean sessionIsOpen,
            int qnIdx, int responseIdx, String courseId);
    
    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);
    
    public abstract String getNewQuestionSpecificEditFormHtml();
    
    public abstract String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId);
    
    public abstract String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            AccountAttributes currentUser,
            FeedbackSessionResultsBundle bundle,
            String view);
    
    public abstract String getQuestionResultStatisticsCsv(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle);
    
    public abstract boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails);
    public abstract String getCsvHeader();

    /**
     * Returns a HTML option for selecting question type.
     * Used in instructorFeedbackEdit.jsp for selecting the question type for a new question.
     */
    public abstract String getQuestionTypeChoiceOption();
    
    
    /**
     * Individual responses are shown by default.
     * Override for specific question types if necessary.
     * @return boolean indicating if individual responses are to be shown to students.
     */
    public boolean isIndividualResponsesShownToStudents() {
       return true;
    }
    
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
     * Validates if giverType and recipientType are valid for the question type.
     * Validates visibility options as well.
     * 
     * Override in Feedback*QuestionDetails if necessary.
     * @param giverType
     * @param recipientType
     * @return error message detailing the error, or an empty string if valid.
     */
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        // All giver/recipient types and visibility options are valid by default, so return ""
        return "";
    }
    
    /**
     * Extract question details and sets details accordingly
     * @param requestParameters
     * @param questionType
     * @return true to indicate success in extracting the details, false otherwise.
     */
    public abstract boolean extractQuestionDetails(Map<String, String[]> requestParameters, FeedbackQuestionType questionType);
    
    public static FeedbackQuestionDetails createQuestionDetails(Map<String, String[]> requestParameters, FeedbackQuestionType questionType) {
        String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters, Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        Assumption.assertNotNull("Null question text", questionText);
        Assumption.assertNotEmpty("Empty question text", questionText);
        
        FeedbackQuestionDetails questionDetails = questionType.getFeedbackQuestionDetailsInstance(questionText, requestParameters);
        
        return questionDetails;
    }
    
    // The following function handle the display of rows between possible givers 
    // and recipients who did not respond to a question in feedback sessions
    
    public String getNoResponseTextInHtml(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
        return "<span class=\"color_neutral\"><i>" + 
               getNoResponseText(giverEmail, recipientEmail, bundle, question) + 
               "</i></span>";
    }
    
    public boolean shouldShowNoResponseText(String giverEmail, String recipientEmail, FeedbackQuestionAttributes question) {
        // we do not show all possible responses 
        if (question.recipientType == FeedbackParticipantType.STUDENTS || question.recipientType == FeedbackParticipantType.TEAMS) {
            return false;
        }
        return true;
    }
    
    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
       return getNoResponseText(giverEmail, recipientEmail, bundle, question);
    }
    
    /**
     * Returns text to indicate that there is no response between the giver and recipient.
     * 
     * Used in instructorFeedbackResultsPage to show possible givers and recipients who did 
     * not respond to the question in the feedback session.
     * @param giverEmail
     * @param recipientEmail
     * @param bundle
     * @param question
     * @return
     */
    public String getNoResponseText(String giverEmail, String recipientEmail,
            FeedbackSessionResultsBundle bundle,
            FeedbackQuestionAttributes question) {
        return Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
    }
    
    
}