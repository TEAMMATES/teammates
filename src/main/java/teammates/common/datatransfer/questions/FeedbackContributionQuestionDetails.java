package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Contains specific structure and processing logic for contribution feedback questions.
 */
public class FeedbackContributionQuestionDetails extends FeedbackQuestionDetails {

    static final String QUESTION_TYPE_NAME = "Team contribution question";
    static final String CONTRIB_ERROR_INVALID_OPTION =
            "Invalid option for the " + QUESTION_TYPE_NAME + ".";
    static final String CONTRIB_ERROR_INVALID_FEEDBACK_PATH =
            QUESTION_TYPE_NAME + " must have "
                    + "\"Students in this course\" and \"Giver's team members and Giver\" "
                    + "as the feedback giver and recipient respectively. "
                    + "These values will be used instead.";
    static final String CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS =
            QUESTION_TYPE_NAME + " must use one of the common visibility options. The "
                    + "\"Shown anonymously to recipient and team members, visible to instructors\" "
                    + "option will be used instead.";

    private static final Logger log = Logger.getLogger();

    private boolean isZeroSum;
    private boolean isNotSureAllowed;

    public FeedbackContributionQuestionDetails() {
        this(null);
    }

    public FeedbackContributionQuestionDetails(String questionText) {
        super(FeedbackQuestionType.CONTRIB, questionText);
        isZeroSum = true;
        isNotSureAllowed = false;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        FeedbackContributionQuestionDetails newContribDetails = (FeedbackContributionQuestionDetails) newDetails;
        return newContribDetails.isZeroSum != this.isZeroSum
                || newContribDetails.isNotSureAllowed != this.isNotSureAllowed;
    }

    @Override
    public boolean isIndividualResponsesShownToStudents() {
        return false;
    }

    @Override
    public List<String> validateQuestionDetails() {
        List<String> errors = new ArrayList<>();

        if (isZeroSum && isNotSureAllowed) {
            errors.add(CONTRIB_ERROR_INVALID_OPTION);
        }

        return errors;
    }

    @Override
    public List<String> validateResponsesDetails(List<FeedbackResponseDetails> responses, int numRecipients) {
        List<String> errors = new ArrayList<>();

        // Nothing to validate if there are no responses
        boolean isAllNotSubmitted = responses
                .stream()
                .allMatch(r -> ((FeedbackContributionResponseDetails) r).getAnswer() == Const.POINTS_NOT_SUBMITTED);
        if (isAllNotSubmitted) {
            return errors;
        }

        int actualTotal = 0;
        for (FeedbackResponseDetails response : responses) {
            FeedbackContributionResponseDetails details = (FeedbackContributionResponseDetails) response;
            boolean validAnswer = false;

            // Valid answers: 0, 5, 10, 15, .... 190, 195, 200
            boolean isValidRange = details.getAnswer() >= 0 && details.getAnswer() <= 200;
            boolean isMultipleOf5 = details.getAnswer() % 5 == 0;
            if (isValidRange && isMultipleOf5) {
                validAnswer = true;
            }

            boolean isValidNotSure = details.getAnswer() == Const.POINTS_NOT_SURE && isNotSureAllowed;
            boolean isValidNotSubmitted = details.getAnswer() == Const.POINTS_NOT_SUBMITTED && !isZeroSum;
            if (isValidNotSure || isValidNotSubmitted) {
                validAnswer = true;
            }

            if (!validAnswer) {
                errors.add(CONTRIB_ERROR_INVALID_OPTION);
            }

            actualTotal += details.getAnswer();
        }

        int expectedTotal = numRecipients * 100;
        if (actualTotal != expectedTotal && isZeroSum) {
            errors.add(CONTRIB_ERROR_INVALID_OPTION);
        }

        return errors;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestion feedbackQuestion) {
        String errorMsg = "";

        // giver type can only be STUDENTS
        if (feedbackQuestion.getGiverType() != QuestionGiverType.STUDENTS) {
            log.severe("Unexpected giverType for contribution question: " + feedbackQuestion.getGiverType()
                       + " (forced to :" + QuestionGiverType.STUDENTS + ")");
            feedbackQuestion.setGiverType(QuestionGiverType.STUDENTS);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // recipient type can only be OWN_TEAM_MEMBERS_INCLUDING_SELF
        if (feedbackQuestion.getRecipientType() != QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF) {
            log.severe("Unexpected recipientType for contribution question: "
                       + feedbackQuestion.getRecipientType()
                       + " (forced to :" + QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF + ")");
            feedbackQuestion.setRecipientType(QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF);
            errorMsg = CONTRIB_ERROR_INVALID_FEEDBACK_PATH;
        }

        // restrictions on visibility options: RECIPIENT and GIVER_TEAM_MEMBERS must appear together
        if (feedbackQuestion.getShowResponsesTo().contains(FeedbackVisibilityType.RECIPIENT)
                != feedbackQuestion.getShowResponsesTo().contains(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
            log.severe("Unexpected showResponsesTo for contribution question: "
                       + feedbackQuestion.getShowResponsesTo() + " (forced to :"
                       + "Shown anonymously to recipient and team members, visible to instructors"
                       + ")");
            feedbackQuestion.setShowResponsesTo(Arrays.asList(FeedbackVisibilityType.RECIPIENT,
                                                               FeedbackVisibilityType.GIVER_TEAM_MEMBERS,
                                                               FeedbackVisibilityType.INSTRUCTORS));
            errorMsg = CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS;
        }

        return errorMsg;
    }

    @Override
    public boolean isInstructorCommentsOnResponsesAllowed() {
        return false;
    }

    public boolean isZeroSum() {
        return isZeroSum;
    }

    public boolean isNotSureAllowed() {
        return isNotSureAllowed;
    }

    public void setZeroSum(boolean zeroSum) {
        isZeroSum = zeroSum;
    }

    public void setNotSureAllowed(boolean notSureAllowed) {
        isNotSureAllowed = notSureAllowed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FeedbackContributionQuestionDetails other)) {
            return false;
        }
        return getQuestionType() == other.getQuestionType()
                && Objects.equals(getQuestionText(), other.getQuestionText())
                && isZeroSum == other.isZeroSum
                && isNotSureAllowed == other.isNotSureAllowed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionType(), getQuestionText(), isZeroSum, isNotSureAllowed);
    }
}
