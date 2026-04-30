package teammates.common.datatransfer.questions;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.FeedbackQuestion;

/**
 * A class holding the details for a specific question type.
 * This abstract class is inherited by concrete Feedback*QuestionDetails
 * classes which provides the implementation for the various abstract methods
 * such that pages can render the correct information/forms depending on the
 * question type
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "questionType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FeedbackTextQuestionDetails.class, name = "TEXT"),
        @JsonSubTypes.Type(value = FeedbackMcqQuestionDetails.class, name = "MCQ"),
        @JsonSubTypes.Type(value = FeedbackMsqQuestionDetails.class, name = "MSQ"),
        @JsonSubTypes.Type(value = FeedbackNumericalScaleQuestionDetails.class, name = "NUMSCALE"),
        @JsonSubTypes.Type(
                value = FeedbackConstantSumQuestionDetails.class,
                names = {"CONSTSUM", "CONSTSUM_OPTIONS", "CONSTSUM_RECIPIENTS"}),
        @JsonSubTypes.Type(value = FeedbackContributionQuestionDetails.class, name = "CONTRIB"),
        @JsonSubTypes.Type(value = FeedbackRubricQuestionDetails.class, name = "RUBRIC"),
        @JsonSubTypes.Type(value = FeedbackRankOptionsQuestionDetails.class, name = "RANK_OPTIONS"),
        @JsonSubTypes.Type(value = FeedbackRankRecipientsQuestionDetails.class, name = "RANK_RECIPIENTS")
})
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

    /**
    * Get question result statistics as JSON string.
    */
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public String getQuestionResultStatisticsJson(
            FeedbackQuestion question, String studentEmail, SessionResultsBundle bundle) {
        // Statistics are calculated in the front-end as it is dependent on the responses being filtered.
        // The only exception is contribution question, where there is only one statistics for the entire question.
        // It is also necessary to calculate contribution question statistics here
        // to be displayed in student result page as students are not supposed to be able to see the exact responses.
        return "";
    }

    /**
     * Checks whether the changes to the question details require deletion of corresponding responses.
     */
    public abstract boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails);

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
     * Validates the list of response details.
     * @return A {@code List<String>} of error messages (to show as status message to user) if any, or an
     *         empty list if all response details are valid.
     */
    public abstract List<String> validateResponsesDetails(List<FeedbackResponseDetails> responseDetails, int numRecipients);

    /**
     * Validates if giverType and recipientType are valid for the question type.
     * Validates visibility options as well.
     *
     * <p>Override in Feedback*QuestionDetails if necessary.
     * @return error message detailing the error, or an empty string if valid.
     */
    public abstract String validateGiverRecipientVisibility(FeedbackQuestion feedbackQuestion);

    /**
     * Checks whether instructor comments are allowed for the question.
     */
    public boolean isInstructorCommentsOnResponsesAllowed() {
        return true;
    }

    /**
     * Checks whether missing responses should be generated.
     */
    public boolean shouldGenerateMissingResponses(FeedbackQuestion question) {
        // generate combinations against all students/teams are meaningless
        return question.getRecipientType() != FeedbackParticipantType.STUDENTS
                && question.getRecipientType() != FeedbackParticipantType.STUDENTS_EXCLUDING_SELF
                && question.getRecipientType() != FeedbackParticipantType.TEAMS
                && question.getRecipientType() != FeedbackParticipantType.TEAMS_EXCLUDING_SELF;
    }

    /**
     * Returns a JSON string representation of the question details.
     */
    public String getJsonString() {
        assert questionType != null;
        return JsonUtils.toJson(this, questionType.getQuestionDetailsClass());
    }

    /**
     * Returns a deep copy of the question details.
     */
    public FeedbackQuestionDetails getDeepCopy() {
        assert questionType != null;
        String serializedDetails = getJsonString();
        return JsonUtils.fromJson(serializedDetails, questionType.getQuestionDetailsClass());
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
}
