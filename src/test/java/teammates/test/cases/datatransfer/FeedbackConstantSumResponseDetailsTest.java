package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumDistributePointsType;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackConstantSumResponseDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackConstantSumQuestionDetails}.
 */
public class FeedbackConstantSumResponseDetailsTest extends BaseTestCase {

    @Test
    public void testValidateResponseDetails_amongRecipientsValidAnswer_shouldReturnEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(true);
        constantSumQuestionDetails.setConstSumOptions(new ArrayList<>());
        constantSumQuestionDetails.setNumOfConstSumOptions(0);
        constantSumQuestionDetails.setPoints(100);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(Arrays.asList(0));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(100));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_amongRecipientsInvalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(true);
        constantSumQuestionDetails.setConstSumOptions(new ArrayList<>());
        constantSumQuestionDetails.setNumOfConstSumOptions(0);
        constantSumQuestionDetails.setPoints(100);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(Arrays.asList());
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(-1));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(100, 101));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_amongOptionsValidAnswer_shouldReturnEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(false);
        constantSumQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));
        constantSumQuestionDetails.setNumOfConstSumOptions(3);
        constantSumQuestionDetails.setPoints(100);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(Arrays.asList(1, 99, 0));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(0, 100, 0));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumQuestionDetails.setPointsPerOption(true);
        constantSumQuestionDetails.setPoints(100);

        constantSumResponseDetails.setAnswers(Arrays.asList(100, 100, 100));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(99, 101, 100));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(40, 50, 210));
        assertTrue(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_amongOptionsInvalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(false);
        constantSumQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));
        constantSumQuestionDetails.setNumOfConstSumOptions(3);
        constantSumQuestionDetails.setPointsPerOption(false);
        constantSumQuestionDetails.setPoints(99);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(new ArrayList<>());
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1, -1, 99));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1, 1, 99));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(33, 33, 33));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        constantSumQuestionDetails.setPoints(100);

        constantSumResponseDetails.setAnswers(Arrays.asList(33, 34, 33));
        assertFalse(constantSumResponseDetails
                .validateResponseDetails(getConstSumQuestion(constantSumQuestionDetails)).isEmpty());
    }

    private FeedbackQuestionAttributes getConstSumQuestion(FeedbackConstantSumQuestionDetails constantSumQuestionDetails) {
        return FeedbackQuestionAttributes.builder()
                .withCourseId("testCourse")
                .withFeedbackSessionName("testSession")
                .withQuestionDescription("testDescription")
                .withQuestionDetails(constantSumQuestionDetails)
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.STUDENTS)
                .withRecipientType(FeedbackParticipantType.STUDENTS)
                .withNumberOfEntitiesToGiveFeedbackTo(5)
                .withShowGiverNameTo(new ArrayList<>())
                .withShowResponsesTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .build();
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }
}
