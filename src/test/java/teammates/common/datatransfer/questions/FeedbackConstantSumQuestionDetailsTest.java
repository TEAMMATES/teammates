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
    public void testShouldChangesRequireResponseDeletion_differentConstSumOptions_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setConstSumOptions(Arrays.asList("a", "b"));

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
        assertTrue(newFeedbackQuestionDetails.shouldChangesRequireResponseDeletion(feedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameConstantSumOptionsDifferentOrder_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setConstSumOptions(Arrays.asList("c", "b", "a"));

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentDistributeToRecipients_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributeToRecipients(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributeToRecipients(false);

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameDistributeToRecipients_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributeToRecipients(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributeToRecipients(true);

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentPoints_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPoints(100);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPoints(50);

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_samePoints_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPoints(100);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPoints(100);

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentPointsPerOption_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPointsPerOption(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPointsPerOption(false);

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_samePointsPerOption_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPointsPerOption(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPointsPerOption(true);

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentForceUnevenDistribution_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setForceUnevenDistribution(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setForceUnevenDistribution(false);

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameForceUnevenDistribution_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setForceUnevenDistribution(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setForceUnevenDistribution(true);

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentMaxPoint_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMaxPoint(50);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMaxPoint(60);

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameMaxPoint_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMaxPoint(50);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMaxPoint(50);

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentMinPoint_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMinPoint(10);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMinPoint(20);

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameMinPoint_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMinPoint(10);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMinPoint(10);

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentDistributePointsFor_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameDistributePointsFor_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
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
