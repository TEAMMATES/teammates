package teammates.common.datatransfer.questions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * A class holding the details for a specific question type.
 * This abstract class is inherited by concrete Feedback*QuestionDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information/forms depending on the
 * question type
 */
public abstract class FeedbackQuestionDetails {
    private FeedbackQuestionType questionType;
    private String questionText;

    protected FeedbackQuestionDetails(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    protected FeedbackQuestionDetails(FeedbackQuestionType questionType, String questionText) {
        this.questionType = questionType;
        this.questionText = questionText;
    }

    public abstract String getQuestionTypeDisplayName();

    @Deprecated
    public abstract String getQuestionWithExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
            int totalNumRecipients, FeedbackResponseDetails existingResponseDetails, StudentAttributes student);

    @Deprecated
    public abstract String getQuestionWithoutExistingResponseSubmissionFormHtml(
                                boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
                                int totalNumRecipients, StudentAttributes student);

    @Deprecated
    public abstract String getQuestionSpecificEditFormHtml(int questionNumber);

    @Deprecated
    public abstract String getNewQuestionSpecificEditFormHtml();

    @Deprecated
    public abstract String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
                                                           FeedbackQuestionAttributes question,
                                                           String studentEmail,
                                                           FeedbackSessionResultsBundle bundle,
                                                           String view);

    public abstract String getQuestionResultStatisticsJson(
            List<FeedbackResponseAttributes> responses, FeedbackQuestionAttributes question,
            String userEmail, FeedbackSessionResultsBundle bundle, boolean isStudent);

    public abstract String getQuestionResultStatisticsCsv(List<FeedbackResponseAttributes> responses,
                                                          FeedbackQuestionAttributes question,
                                                          FeedbackSessionResultsBundle bundle);

    public abstract boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails);

    public abstract String getCsvHeader();

    /** Gets the header for detailed responses in csv format. Override in child classes if necessary. */
    public String getCsvDetailedResponsesHeader(int noOfInstructorComments) {
        StringBuilder header = new StringBuilder(1000);
        String headerString = "Team" + "," + "Giver's Full Name" + ","
                + "Giver's Last Name" + "," + "Giver's Email" + ","
                + "Recipient's Team" + "," + "Recipient's Full Name" + ","
                + "Recipient's Last Name" + "," + "Recipient's Email" + ","
                + getCsvHeader();
        header.append(headerString);

        if (isFeedbackParticipantCommentsOnResponsesAllowed()) {
            headerString = ',' + "Giver's Comments";
            header.append(headerString);
        }
        header.append(getCsvDetailedInstructorsCommentsHeader(noOfInstructorComments)).append(System.lineSeparator());
        return header.toString();
    }

    public String getCsvDetailedResponsesRow(FeedbackSessionResultsBundle fsrBundle,
                                             FeedbackResponseAttributes feedbackResponseAttributes,
                                             FeedbackQuestionAttributes question) {
        // Retrieve giver details
        String giverLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.giver);
        String giverFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.giver);
        String giverTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.giver);
        String giverEmail = fsrBundle.getDisplayableEmailGiver(feedbackResponseAttributes);

        // Retrieve recipient details
        String recipientLastName = fsrBundle.getLastNameForEmail(feedbackResponseAttributes.recipient);
        String recipientFullName = fsrBundle.getNameForEmail(feedbackResponseAttributes.recipient);
        String recipientTeamName = fsrBundle.getTeamNameForEmail(feedbackResponseAttributes.recipient);
        String recipientEmail = fsrBundle.getDisplayableEmailRecipient(feedbackResponseAttributes);

        StringBuilder detailedResponseRow = new StringBuilder(1000);
        String detailedResponseRowString = SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverTeamName))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverFullName))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverLastName))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(giverEmail))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientTeamName))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientFullName))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientLastName))
                + "," + SanitizationHelper.sanitizeForCsv(StringHelper.removeExtraSpace(recipientEmail))
                + "," + fsrBundle.getResponseAnswerCsv(feedbackResponseAttributes, question);
        detailedResponseRow.append(detailedResponseRowString);
        // Append feedback participant comments if allowed
        if (isFeedbackParticipantCommentsOnResponsesAllowed()) {
            String feedbackParticipantComment =
                    fsrBundle.getCsvDetailedFeedbackParticipantCommentOnResponse(feedbackResponseAttributes);
            detailedResponseRow.append(',').append(feedbackParticipantComment);
        }
        // Append instructor comments if allowed
        if (isInstructorCommentsOnResponsesAllowed()) {
            String instructorComments =
                    fsrBundle.getCsvDetailedInstructorFeedbackResponseComments(feedbackResponseAttributes);
            detailedResponseRow.append(instructorComments);
        }
        return detailedResponseRow.append(System.lineSeparator()).toString();
    }

    public String getQuestionText() {
        return questionText;
    }

    /**
     * Returns a list of strings where each string is an instruction to answer the question.
     *
     * @return List of strings containing instructions.
     */
    public abstract List<String> getInstructions();

    /**
     * Returns a HTML option for selecting question type.
     * Used in instructorFeedbackEdit.jsp for selecting the question type for a new question.
     */
    @Deprecated
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
     * Validates the question details.
     * @param courseId courseId of the question
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an
     *         empty list if question details are valid.
     */
    public abstract List<String> validateQuestionDetails(String courseId);

    /**
     * Validates {@code List<FeedbackResponseAttributes>} for the question
     * based on the current {@code Feedback*QuestionDetails}.
     *
     * @param responses - The {@code List<FeedbackResponseAttributes>} for the question to be validated
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an
     *         empty list if question responses are valid.
     */
    public abstract List<String> validateResponseAttributes(List<FeedbackResponseAttributes> responses, int numRecipients);

    /**
     * Validates if giverType and recipientType are valid for the question type.
     * Validates visibility options as well.
     *
     * <p>Override in Feedback*QuestionDetails if necessary.
     * @return error message detailing the error, or an empty string if valid.
     */
    public abstract String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes);

    /**
     * Extract question details and sets details accordingly.
     *
     * @return true to indicate success in extracting the details, false otherwise.
     */
    public abstract boolean extractQuestionDetails(Map<String, String[]> requestParameters,
                                                   FeedbackQuestionType questionType);

    // The following function handle the display of rows between possible givers
    // and recipients who did not respond to a question in feedback sessions

    /**
     * Returns true if 'No Response' is to be displayed in the Response rows.
     */
    public boolean shouldShowNoResponseText(FeedbackQuestionAttributes question) {
        // we do not show all possible responses
        return question.recipientType != FeedbackParticipantType.STUDENTS
            && question.recipientType != FeedbackParticipantType.TEAMS;
    }

    public String getNoResponseTextInCsv(String giverEmail, String recipientEmail,
                                         FeedbackSessionResultsBundle bundle,
                                         FeedbackQuestionAttributes question) {
        return SanitizationHelper.sanitizeForCsv(getNoResponseText(giverEmail, recipientEmail, bundle, question));
    }

    /**
     * Returns text to indicate that there is no response between the giver and recipient.
     *
     * <p>Used in instructorFeedbackResultsPage to show possible givers and recipients who did
     * not respond to the question in the feedback session.
     */
    public String getNoResponseText(String giverEmail, String recipientEmail,
                                    FeedbackSessionResultsBundle bundle,
                                    FeedbackQuestionAttributes question) {
        return Const.INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE;
    }

    public String getJsonString() {
        Assumption.assertNotNull(questionType);
        return JsonUtils.toJson(this, questionType.getQuestionDetailsClass());
    }

    public FeedbackQuestionDetails getDeepCopy() {
        Assumption.assertNotNull(questionType);
        String serializedDetails = getJsonString();
        return JsonUtils.fromJson(serializedDetails, questionType.getQuestionDetailsClass());
    }

    /** Checks if the question has been skipped. */
    public boolean isQuestionSkipped(String[] answers) {
        if (answers == null) {
            return true;
        }
        return Arrays.stream(answers).noneMatch(answer -> answer != null && !answer.trim().isEmpty());
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isInstructorCommentsOnResponsesAllowed() {
        return true;
    }

    public abstract boolean isFeedbackParticipantCommentsOnResponsesAllowed();

    public String getCsvDetailedInstructorsCommentsHeader(int noOfComments) {
        StringBuilder commentsHeader = new StringBuilder(200);

        for (int i = noOfComments; i > 0; i--) {
            commentsHeader.append("," + "Comment From" + "," + "Comment");
        }

        return commentsHeader.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        // Json string contains all attributes of a `FeedbackQuestionDetails` object,
        // so it is sufficient to use it to compare two `FeedbackQuestionDetails` objects.
        FeedbackQuestionDetails other = (FeedbackQuestionDetails) obj;
        return this.getJsonString().equals(other.getJsonString());
    }

    @Override
    public int hashCode() {
        return this.getJsonString().hashCode();
    }
}
