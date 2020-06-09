package teammates.common.datatransfer.questions;

import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
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

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public String getQuestionResultStatisticsJson(
            FeedbackQuestionAttributes question, String studentEmail, SessionResultsBundle bundle) {
        // Statistics are calculated in the front-end as it is dependent on the responses being filtered.
        // The only exception is contribution question, where there is only one statistics for the entire question.
        // It is also necessary to calculate contribution question statistics here
        // to be displayed in student result page as students are not supposed to be able to see the exact responses.
        return "";
    }

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
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an
     *         empty list if question details are valid.
     */
    public abstract List<String> validateQuestionDetails();

    /**
     * Validates if giverType and recipientType are valid for the question type.
     * Validates visibility options as well.
     *
     * <p>Override in Feedback*QuestionDetails if necessary.
     * @return error message detailing the error, or an empty string if valid.
     */
    public abstract String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes);

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

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }
}
