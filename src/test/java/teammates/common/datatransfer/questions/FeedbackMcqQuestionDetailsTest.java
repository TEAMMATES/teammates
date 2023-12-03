package teammates.common.datatransfer.questions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackMcqQuestionDetails}.
 */
public class FeedbackMcqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();

        assertEquals(FeedbackQuestionType.MCQ, mcqDetails.getQuestionType());
        assertFalse(mcqDetails.isHasAssignedWeights());
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        assertEquals(FeedbackParticipantType.NONE, mcqDetails.getGenerateOptionsFor());
    }

    @Test
    public void testSetter_generateOptionsFor_correctValue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS);

        assertEquals(FeedbackParticipantType.STUDENTS, mcqDetails.getGenerateOptionsFor());
    }

    @Test
    public void testValidateQuestionDetails_choicesLessThanMinRequirement_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Collections.singletonList("Choice 2"));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMcqQuestionDetails.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_numberOfChoicesGreaterThanWeights_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setMcqWeights(Collections.singletonList(1.22));
        mcqDetails.setHasAssignedWeights(true);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setMcqWeights(Arrays.asList(1.22, 1.55));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_negativeWeights_errorsReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setMcqWeights(Arrays.asList(1.22, -1.55));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorsReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Arrays.asList("Choice 1", "Choice 2"));
        mcqDetails.setMcqWeights(Arrays.asList(1.22, 1.55));
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setMcqOtherWeight(-2);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_duplicateMcqOptions_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();

        mcqDetails.setMcqChoices(Arrays.asList("choice 1", "choice 1"));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_DUPLICATE_MCQ_OPTION, errors.get(0));

        // duplicate cases that has trailing and leading spaces
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", " choice 1 "));
        errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_DUPLICATE_MCQ_OPTION, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_emptyMcqOption_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", ""));

        List<String> errors = mcqDetails.validateQuestionDetails();

        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_EMPTY_MCQ_OPTION, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", "choice 2"));
        mcqDetails.setMcqWeights(List.of(1.22, 1.33));
        mcqDetails.setMcqOtherWeight(-1.22);
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setOtherEnabled(true);

        List<String> errors = mcqDetails.validateQuestionDetails();

        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackMcqQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentMqcChoices_shouldReturnTrue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(List.of("choice1", "choice2"));

        FeedbackMcqQuestionDetails newMcqDetails = new FeedbackMcqQuestionDetails();
        newMcqDetails.setMcqChoices(List.of("choice1", "choice3"));

        assertTrue(mcqDetails.shouldChangesRequireResponseDeletion(newMcqDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentGenerateOptionsFor_shouldReturnTrue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS);

        FeedbackMcqQuestionDetails newMcqDetails = new FeedbackMcqQuestionDetails();
        newMcqDetails.setGenerateOptionsFor(FeedbackParticipantType.INSTRUCTORS);

        assertTrue(mcqDetails.shouldChangesRequireResponseDeletion(newMcqDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentOtherEnabled_shouldReturnTrue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setOtherEnabled(true);

        FeedbackMcqQuestionDetails newMcqDetails = new FeedbackMcqQuestionDetails();
        newMcqDetails.setOtherEnabled(false);

        assertTrue(mcqDetails.shouldChangesRequireResponseDeletion(newMcqDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_sameQuestionsDifferentOrder_shouldReturnFalse() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(List.of("choice1", "choice2", "choice3"));
        mcqDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS);
        mcqDetails.setOtherEnabled(false);

        FeedbackMcqQuestionDetails newMcqDetails = new FeedbackMcqQuestionDetails();
        newMcqDetails.setMcqChoices(List.of("choice2", "choice3", "choice1"));
        newMcqDetails.setGenerateOptionsFor(FeedbackParticipantType.STUDENTS);
        newMcqDetails.setOtherEnabled(false);

        assertFalse(mcqDetails.shouldChangesRequireResponseDeletion(newMcqDetails));
    }

    @Test
    public void testValidateResponsesDetails_answerNotPartOfMcq_shouldReturnError() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(List.of("choice1", "choice2"));

        FeedbackMcqResponseDetails response = new FeedbackMcqResponseDetails();
        response.setAnswer("choice3");
        response.setOther(false);
        List<FeedbackResponseDetails> responses = List.of(response);

        List<String> errors = mcqDetails.validateResponsesDetails(responses, 1);

        assertEquals(1, errors.size());
        assertEquals(response.getAnswerString() + " " + FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_OPTION,
                errors.get(0));
    }

    @Test
    public void testValidateResponsesDetails_otherOptionNoText_shouldReturnError() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(List.of("choice1", "choice2"));

        FeedbackMcqResponseDetails response = new FeedbackMcqResponseDetails();
        response.setAnswer("");
        response.setOther(true);
        List<FeedbackResponseDetails> responses = List.of(response);

        List<String> errors = mcqDetails.validateResponsesDetails(responses, 1);

        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_OTHER_CONTENT_NOT_PROVIDED, errors.get(0));
    }

    @Test
    public void testValidateResponsesDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqChoices(List.of("choice1", "choice2"));

        FeedbackMcqResponseDetails response = new FeedbackMcqResponseDetails();
        response.setAnswer("choice1");
        response.setOther(false);
        List<FeedbackResponseDetails> responses = List.of(response);

        List<String> errors = mcqDetails.validateResponsesDetails(responses, 1);

        assertEquals(0, errors.size());
    }

    @Test
    public void testIsQuestionDropdownEnabled_shouldReturnTrue() {
        FeedbackMcqQuestionDetails feedbackQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackQuestionDetails.setQuestionDropdownEnabled(true);
        assertTrue(feedbackQuestionDetails.isQuestionDropdownEnabled());
    }

    @Test
    public void testSetQuestionDropdownEnabled_shouldReturnFalse() {
        FeedbackMcqQuestionDetails feedbackQuestionDetails = new FeedbackMcqQuestionDetails();
        feedbackQuestionDetails.setQuestionDropdownEnabled(false);
        assertFalse(feedbackQuestionDetails.isQuestionDropdownEnabled());
    }

    @Test
    public void testValidateQuestionDetails_weightsNotEnabledButWeightListNotEmpty_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqWeights(Arrays.asList(1.22, -1.55));
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", "choice 2"));
        mcqDetails.setHasAssignedWeights(false);
        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_weightsNotEnabledButOtherWeightNotZero_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setMcqOtherWeight(0.5);
        mcqDetails.setHasAssignedWeights(false);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(FeedbackParticipantType.NONE, mcqDetails.getGenerateOptionsFor());
        assertEquals(2, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMcqQuestionDetails.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(1));
    }

    @Test
    public void testValidateQuestionDetails_hasAssignedWeightsOtherEnabledNonZeroWeight_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setOtherEnabled(true);
        mcqDetails.setMcqOtherWeight(1.5);
        mcqDetails.setMcqChoices(Arrays.asList("choice 1", "choice 2"));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_hasAssignedWeightsOtherEnabledNegativeWeight_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setOtherEnabled(true);
        mcqDetails.setMcqOtherWeight(-1.5);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(2, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMcqQuestionDetails.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(1));
    }

    @Test
    public void testValidateQuestionDetails_hasAssignedWeightsNonEmptyWeights_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setHasAssignedWeights(true);
        mcqDetails.setMcqWeights(Arrays.asList(1.5));

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(2, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMcqQuestionDetails.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_INVALID_WEIGHT, errors.get(1));
    }

    @Test
    public void testValidateQuestionDetails_generateOptionsForNone_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        mcqDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);

        List<String> errors = mcqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMcqQuestionDetails.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMcqQuestionDetails.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }
}
