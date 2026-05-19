package teammates.common.datatransfer.questions;

import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(100));
        Assertions.assertTrue(constantSumQuestionDetails
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
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(-1));
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(100, 101));
        Assertions.assertFalse(constantSumQuestionDetails
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
        Assertions.assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(0, 100, 0));
        Assertions.assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setPointsPerOption(true);
        constantSumQuestionDetails.setPoints(100);

        constantSumResponseDetails.setAnswers(Arrays.asList(100, 100, 100));
        Assertions.assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(99, 101, 100));
        Assertions.assertTrue(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(40, 50, 210));
        Assertions.assertTrue(constantSumQuestionDetails
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
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1));
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1, -1, 99));
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumResponseDetails.setAnswers(Arrays.asList(1, 1, 99));
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        constantSumResponseDetails.setAnswers(Arrays.asList(33, 33, 33));
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());

        constantSumQuestionDetails.setForceUnevenDistribution(true);
        constantSumQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        constantSumQuestionDetails.setPoints(100);

        constantSumResponseDetails.setAnswers(Arrays.asList(33, 34, 33));
        Assertions.assertFalse(constantSumQuestionDetails
                .validateResponsesDetails(Collections.singletonList(constantSumResponseDetails), 0).isEmpty());
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentConstSumOptions_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setConstSumOptions(Arrays.asList("a", "b"));

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
        Assertions.assertTrue(newFeedbackQuestionDetails.shouldChangesRequireResponseDeletion(feedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameConstantSumOptionsDifferentOrder_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setConstSumOptions(Arrays.asList("c", "b", "a"));

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setConstSumOptions(Arrays.asList("a", "b", "c"));

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentDistributeToRecipients_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributeToRecipients(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributeToRecipients(false);

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameDistributeToRecipients_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributeToRecipients(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributeToRecipients(true);

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentPoints_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPoints(100);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPoints(50);

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_samePoints_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPoints(100);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPoints(100);

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentPointsPerOption_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPointsPerOption(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPointsPerOption(false);

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_samePointsPerOption_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPointsPerOption(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setPointsPerOption(true);

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentForceUnevenDistribution_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setForceUnevenDistribution(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setForceUnevenDistribution(false);

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameForceUnevenDistribution_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setForceUnevenDistribution(true);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setForceUnevenDistribution(true);

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentMaxPoint_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMaxPoint(50);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMaxPoint(60);

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameMaxPoint_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMaxPoint(50);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMaxPoint(50);

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentMinPoint_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMinPoint(10);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMinPoint(20);

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameMinPoint_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setMinPoint(10);

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setMinPoint(10);

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentDistributePointsFor_shouldReturnTrue() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_SOME_UNEVENLY.getDisplayedOption());

        Assertions.assertTrue(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameDistributePointsFor_shouldReturnFalse() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        FeedbackConstantSumQuestionDetails newFeedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        newFeedbackQuestionDetails.setDistributePointsFor(
                FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());

        Assertions.assertFalse(feedbackQuestionDetails.shouldChangesRequireResponseDeletion(newFeedbackQuestionDetails));
    }

    @Test
    public void testValidateQuestionDetails_invalidMaxMinOptions_errorReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails =
                new FeedbackConstantSumQuestionDetails("Is this a test?");

        feedbackQuestionDetails.setConstSumOptions(new ArrayList<>(Arrays.asList("Yes", "No")));
        feedbackQuestionDetails.setMinPoint(101);
        feedbackQuestionDetails.setMaxPoint(-1);

        List<String> errors = feedbackQuestionDetails.validateQuestionDetails();

        Assertions.assertEquals(5, errors.size());
        Assertions.assertTrue(errors.contains(FeedbackConstantSumQuestionDetails.CONST_SUM_ERROR_MIN_GREATER_THAN_MAX));
        Assertions.assertTrue(errors.contains(FeedbackConstantSumQuestionDetails.CONST_SUM_ERROR_MIN_POINT_ABOVE_UPPER_BOUND + "50"));
        Assertions.assertTrue(errors.contains(FeedbackConstantSumQuestionDetails.CONST_SUM_ERROR_MAX_POINT_BELOW_LOWER_BOUND + "50"));
        Assertions.assertTrue(errors.contains(String.format(FeedbackConstantSumQuestionDetails.CONST_SUM_TEMPLATE_NEGATIVE,
                FeedbackConstantSumQuestionDetails.MAX_POINT_STRING)));
        Assertions.assertTrue(errors.contains(String.format(FeedbackConstantSumQuestionDetails.CONST_SUM_TEMPLATE_EXCEEDS_POINTS,
                FeedbackConstantSumQuestionDetails.MIN_POINT_STRING, 100)));
    }

    @Test
    public void testValidateQuestionDetails_duplicateOptions_errorReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();

        feedbackQuestionDetails.setConstSumOptions(new ArrayList<>(Arrays.asList("foo", "No", "foo")));

        Assertions.assertTrue(feedbackQuestionDetails.validateQuestionDetails()
                .contains(FeedbackConstantSumQuestionDetails.CONST_SUM_ERROR_DUPLICATE_OPTIONS));
    }

    @Test
    public void testValidateQuestionDetails_invalidConstSumOptionSize_errorReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();

        feedbackQuestionDetails.setConstSumOptions(new ArrayList<>(Arrays.asList("foo")));

        Assertions.assertTrue(feedbackQuestionDetails.validateQuestionDetails()
                .contains(FeedbackConstantSumQuestionDetails.CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS
                        + FeedbackConstantSumQuestionDetails.CONST_SUM_MIN_NUM_OF_OPTIONS + "."));
    }

    @Test
    public void testValidateQuestionDetails_invalidPoint_errorReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
        feedbackQuestionDetails.setPoints(0);

        Assertions.assertTrue(feedbackQuestionDetails.validateQuestionDetails()
                .contains(FeedbackConstantSumQuestionDetails.CONST_SUM_ERROR_NOT_ENOUGH_POINTS
                        + FeedbackConstantSumQuestionDetails.CONST_SUM_MIN_NUM_OF_POINTS + "."));
    }

    @Test
    public void testValidateQuestionDetails_destributeToRecipients_noMinMaxOptionErrorsReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();

        feedbackQuestionDetails.setConstSumOptions(new ArrayList<>(Arrays.asList("foo", "test")));
        feedbackQuestionDetails.setMinPoint(101);
        feedbackQuestionDetails.setMaxPoint(-1);
        feedbackQuestionDetails.setDistributeToRecipients(true);

        Assertions.assertEquals(0, feedbackQuestionDetails.validateQuestionDetails().size());
    }

    @Test
    public void testValidateQuestionDetails_noMinMaxOptions_noMinMaxOptionErrorsReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();

        feedbackQuestionDetails.setConstSumOptions(new ArrayList<>(Arrays.asList("foo", "test")));

        Assertions.assertEquals(0, feedbackQuestionDetails.validateQuestionDetails().size());
    }

    @Test
    public void testValidateQuestionDetails_validMinMaxOption_noErrorsReturned() {
        FeedbackConstantSumQuestionDetails feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();

        feedbackQuestionDetails.setConstSumOptions(new ArrayList<>(Arrays.asList("foo", "test", "passed")));
        feedbackQuestionDetails.setMinPoint(10);
        feedbackQuestionDetails.setMaxPoint(100);
        feedbackQuestionDetails.setPointsPerOption(true);

        Assertions.assertEquals(0, feedbackQuestionDetails.validateQuestionDetails().size());
    }
}
