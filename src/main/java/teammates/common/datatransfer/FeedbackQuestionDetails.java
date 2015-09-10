package teammates.common.datatransfer;

import java.util.List;
import java.util.Map;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

/**
 * A class holding the details for a specific question type.
 * This abstract class is inherited by concrete Feedback*QuestionDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information/forms depending on the
 * question type
 */
public abstract class FeedbackQuestionDetails {
    public FeedbackQuestionType questionType;
    public String questionText;

    protected FeedbackQuestionDetails(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    protected FeedbackQuestionDetails(FeedbackQuestionType questionType, String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }

    public abstract String getQuestionTypeDisplayName();

    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(
                                boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                                FeedbackResponseDetails existingResponseDetails);

    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(
                                boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId);

    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);

    public abstract String getNewQuestionSpecificEditFormHtml();

    public abstract String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId);

    public abstract String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
                                                           FeedbackQuestionAttributes question,
                                                           String studentEmail,
                                                           FeedbackSessionResultsBundle bundle,
                                                           String view);

    public abstract String getQuestionResultStatisticsCsv(List<FeedbackResponseAttributes> responses,
                                                          FeedbackQuestionAttributes question,
                                                          FeedbackSessionResultsBundle bundle);

    public abstract boolean isChangesRequiresResponseDeletion(FeedbackQuestionDetails newDetails);

    public abstract String getCsvHeader();

    /** Gets the header for detailed responses in csv format. Override in child classes if necessary. */
    public String getCsvDetailedResponsesHeader() {
        return "Team" + "," + "Giver's Full Name" + ","
               + "Giver's Last Name" + "," +"Giver's Email" + "," 
               + "Recipient's Team" + "," + "Recipient's Full Name" + ","
               + "Recipient's Last Name" + "," + "Recipient's Email" + "," 
               + this.getCsvHeader() + Const.EOL;
    }

    public String getCsvDetailedResponsesRow(FeedbackSessionResultsBundle fsrBundle,
                                             FeedbackResponseAttributes feedbackResponseAttributes,
                                             FeedbackQuestionAttributes question) {
        // Retrieve giver details
        String giverLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.giverEmail);
        String giverFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.giverEmail);
        String giverTeamName =fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.giverEmail);
        String giverEmail = fsrBundle.getDisplayableEmailGiver(feedbackResponseAttributes);

        // Retrieve recipient details
        String recipientLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.recipientEmail);
        String recipientFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.recipientEmail);
        String recipientTeamName =fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.recipientEmail);
        String recipientEmail = fsrBundle.getDisplayableEmailRecipient(feedbackResponseAttributes);

        String detailedResponsesRow = Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverTeamName))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverFullName))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientTeamName))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientFullName))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientLastName))
                                      + "," + Sanitizer.sanitizeForCsv(StringHelper.removeExtraSpace(recipientEmail))
                                      + "," + fsrBundle.getResponseAnswerCsv(feedbackResponseAttributes, question)
                                      + Const.EOL;

        return detailedResponsesRow;
    }
    
    public String getQuestionText() {
        return questionText;
    }

    /**
     * Returns a HTML option for selecting question type.
     * Used in instructorFeedbackEdit.jsp for selecting the question type for a new question.
     */
    public abstract String getQuestionTypeChoiceOption();

    /**
     * Individual responses are shown by default.
     * Override for specific question types if necessary.
     *
     * @return boolean indicating if individual responses are to be shown to students.
     */
    public boolean isIndividualResponsesShownToStudents() {
       return true;
    }

    /**
     * Validates the question details
     *
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an 
     * empty list if question details are valid.
     */
    public abstract List<String> validateQuestionDetails();

    /**
     * Validates {@code List<FeedbackResponseAttributes>} for the question based on the current {@code Feedback*QuestionDetails}.
     *
     * @param responses - The {@code List<FeedbackResponseAttributes>} for the question to be validated
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an 
     * empty list if question responses are valid.
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
     *
     * @param requestParameters
     * @param questionType
     * @return true to indicate success in extracting the details, false otherwise.
     */
    public abstract boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                                   FeedbackQuestionType questionType);

    public static FeedbackQuestionDetails createQuestionDetails(Map<String, String[]> requestParameters,
                                                                FeedbackQuestionType questionType) {
        String questionText = HttpRequestHelper.getValueFromParamMap(requestParameters,
                                                                     Const.ParamsNames.FEEDBACK_QUESTION_TEXT);
        Assumption.assertNotNull("Null question text", questionText);
        Assumption.assertNotEmpty("Empty question text", questionText);

        FeedbackQuestionDetails questionDetails = questionType.getFeedbackQuestionDetailsInstance(questionText,
                                                                                                  requestParameters);

        return questionDetails;
    }

    // The following function handle the display of rows between possible givers
    // and recipients who did not respond to a question in feedback sessions

    public String getNoResponseTextInHtml(String giverEmail, String recipientEmail,
                                          FeedbackSessionResultsBundle bundle,
                                          FeedbackQuestionAttributes question) {
        return "<i>"
               + Sanitizer.sanitizeForHtml(getNoResponseText(giverEmail, recipientEmail, bundle, question))
               + "</i>";
    }

    public boolean shouldShowNoResponseText(String giverEmail, String recipientEmail,
                                            FeedbackQuestionAttributes question) {
        // we do not show all possible responses
        if (question.recipientType == FeedbackParticipantType.STUDENTS
            || question.recipientType == FeedbackParticipantType.TEAMS) {
            return false;
        }

        return true;
    }

    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
                                         FeedbackSessionResultsBundle bundle,
                                         FeedbackQuestionAttributes question) {
       return Sanitizer.sanitizeForCsv(getNoResponseText(giverEmail, recipientEmail, bundle, question));
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
     */
    public String getNoResponseText(String giverEmail, String recipientEmail,
                                    FeedbackSessionResultsBundle bundle,
                                    FeedbackQuestionAttributes question) {
        return Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
    }

    /** Checks if the question has been skipped. */
    public boolean isQuestionSkipped(String[] answer) {
        if (answer == null) { return true; }

        boolean allAnswersEmpty = true;

        for (int i = 0; i < answer.length; i++) {
            if (answer[i] != null && !answer[i].trim().isEmpty()) {
                allAnswersEmpty = false;
                break;
            }
        }

        return allAnswersEmpty;
    }
}
