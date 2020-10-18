package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackConstantSumQuestionDetails}.
 */
public class FeedbackConstantSumQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testValidateResponseDetails_amongRecipientsValidAnswer_shouldReturnEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(true);
        constantSumQuestionDetails.setConstSumOptions(new ArrayList<>());
        constantSumQuestionDetails.setPoints(100);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(Arrays.asList(0));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(100));
        assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_amongRecipientsInvalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(true);
        constantSumQuestionDetails.setConstSumOptions(new ArrayList<>());
        constantSumQuestionDetails.setPoints(100);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(Arrays.asList());
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(-1));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(100, 101));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_amongOptionsValidAnswer_shouldReturnEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(false);
        constantSumQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));
        constantSumQuestionDetails.setPoints(100);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(Arrays.asList(1, 99, 0));
        assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(0, 100, 0));
        assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setPointsPerOption(true);
        constantSumQuestionDetails.setPoints(100);

        constantSumResponseDetails.setAnswers(Arrays.asList(100, 100, 100));
        assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(99, 101, 100));
        assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(40, 50, 210));
        assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_amongOptionsInvalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackConstantSumQuestionDetails constantSumQuestionDetails = new FeedbackConstantSumQuestionDetails();
        constantSumQuestionDetails.setDistributeToRecipients(false);
        constantSumQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));
        constantSumQuestionDetails.setPointsPerOption(false);
        constantSumQuestionDetails.setPoints(99);
        constantSumQuestionDetails.setForceUnevenDistribution(false);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption());

        FeedbackConstantSumResponseDetails constantSumResponseDetails = new FeedbackConstantSumResponseDetails();

        constantSumResponseDetails.setAnswers(new ArrayList<>());
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1, -1, 99));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1, 1, 99));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(33, 33, 33));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        constantSumQuestionDetails.setPoints(100);

        constantSumResponseDetails.setAnswers(Arrays.asList(33, 34, 33));
        assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());
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
