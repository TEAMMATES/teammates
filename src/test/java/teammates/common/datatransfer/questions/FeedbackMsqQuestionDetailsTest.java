package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;
import teammates.test.AssertHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackMsqQuestionDetails}.
 */
public class FeedbackMsqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        assertEquals(FeedbackQuestionType.MSQ, msqDetails.getQuestionType());
        assertFalse(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getMsqWeights().isEmpty());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testValidateQuestionDetails_choicesLessThanMinRequirement_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        msqDetails.setMsqChoices(Collections.singletonList("Choice 1"));

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackMsqQuestionDetails.MSQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_numberOfChoicesGreaterThanWeights_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setMsqWeights(Collections.singletonList(1.22));
        msqDetails.setHasAssignedWeights(true);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setMsqWeights(Arrays.asList(1.22, 1.55));
        msqDetails.setHasAssignedWeights(true);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_negativeWeights_errorsReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setHasAssignedWeights(true);
        msqDetails.setMsqWeights(Arrays.asList(1.22, -1.55));

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorsReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("Choice 1", "Choice 2"));
        msqDetails.setMsqWeights(Arrays.asList(1.22, 1.55));
        msqDetails.setOtherEnabled(true);
        msqDetails.setHasAssignedWeights(true);
        msqDetails.setMsqOtherWeight(-2);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_duplicateMsqOptions_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("choice 1", "choice 1"));

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_DUPLICATE_MSQ_OPTION, errors.get(0));

        //duplicate cases that has trailing and leading spaces
        msqDetails.setMsqChoices(Arrays.asList("choice 1", " choice 1 "));
        errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_DUPLICATE_MSQ_OPTION, errors.get(0));

    }

    @Test
    public void testValidateQuestionDetails_maxSelectableChoicesMoreThanTotalNumberOfChoice_shouldReturnError() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("a", "b"));
        // 'other' is NOT one of the choices
        msqDetails.setOtherEnabled(false);
        msqDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);
        msqDetails.setHasAssignedWeights(false);
        msqDetails.setMsqOtherWeight(0);
        msqDetails.setMsqWeights(new ArrayList<>());
        msqDetails.setMaxSelectableChoices(3);
        msqDetails.setMinSelectableChoices(Const.POINTS_NO_VALUE);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        AssertHelper.assertContains(FeedbackMsqQuestionDetails.MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_maxSelectableChoicesEqualTotalNumberOfChoice_shouldNotReturnError() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        msqDetails.setMsqChoices(Arrays.asList("a", "b"));
        // 'other' is one of the choices
        msqDetails.setOtherEnabled(true);
        msqDetails.setGenerateOptionsFor(FeedbackParticipantType.NONE);
        msqDetails.setHasAssignedWeights(false);
        msqDetails.setMsqOtherWeight(0);
        msqDetails.setMsqWeights(new ArrayList<>());
        msqDetails.setMaxSelectableChoices(3);
        msqDetails.setMinSelectableChoices(Const.POINTS_NO_VALUE);

        List<String> errors = msqDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateResponseDetails_otherAnswerNotChosenButOtherFieldIsNotEmpty_shouldTriggerError() {
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        msqQuestionDetails.setMsqChoices(Arrays.asList("choiceA", "choiceB"));
        msqQuestionDetails.setOtherEnabled(true);
        msqQuestionDetails.setHasAssignedWeights(false);

        FeedbackMsqResponseDetails feedbackMsqResponseDetails = new FeedbackMsqResponseDetails();
        feedbackMsqResponseDetails.setOther(false);
        feedbackMsqResponseDetails.setOtherFieldContent("NonEmpty");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("choiceA"));

        List<String> errors = msqQuestionDetails.validateResponsesDetails(
                Collections.singletonList(feedbackMsqResponseDetails), 0);
        assertEquals(1, errors.size());
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_INVALID_OPTION, errors.get(0));

        // now set other field to empty
        feedbackMsqResponseDetails.setOtherFieldContent("");
        errors = msqQuestionDetails.validateResponsesDetails(Collections.singletonList(feedbackMsqResponseDetails), 0);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateResponseDetails_choiceNotInValidChoices_shouldTriggerError() {
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        msqQuestionDetails.setMsqChoices(Arrays.asList("choiceA", "choiceB"));
        msqQuestionDetails.setOtherEnabled(true);
        msqQuestionDetails.setHasAssignedWeights(false);

        // typical case: answers not in valid choices
        FeedbackMsqResponseDetails feedbackMsqResponseDetails = new FeedbackMsqResponseDetails();
        feedbackMsqResponseDetails.setOther(false);
        feedbackMsqResponseDetails.setOtherFieldContent("");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("choiceC"));
        List<String> errors = msqQuestionDetails.validateResponsesDetails(
                Collections.singletonList(feedbackMsqResponseDetails), 0);
        assertEquals(1, errors.size());
        assertEquals(feedbackMsqResponseDetails.getAnswerString()
                + " " + FeedbackMsqQuestionDetails.MSQ_ERROR_INVALID_OPTION, errors.get(0));

        // now set choice to be within the valid choices
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("choiceA"));
        errors = msqQuestionDetails.validateResponsesDetails(Collections.singletonList(feedbackMsqResponseDetails), 0);
        assertEquals(0, errors.size());

        // when other field is enabled, the other field content will become a valid choice
        feedbackMsqResponseDetails.setOther(true);
        feedbackMsqResponseDetails.setOtherFieldContent("Other");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("Other1"));
        errors = msqQuestionDetails.validateResponsesDetails(Collections.singletonList(feedbackMsqResponseDetails), 0);
        assertEquals(2, errors.size());
        assertEquals(feedbackMsqResponseDetails.getAnswerString()
                + " " + FeedbackMsqQuestionDetails.MSQ_ERROR_INVALID_OPTION, errors.get(0));
        assertEquals(FeedbackMsqQuestionDetails.MSQ_ERROR_OTHER_CONTENT_NOT_PROVIDED, errors.get(1));

        // make answer list and other field content consistent
        feedbackMsqResponseDetails.setOther(true);
        feedbackMsqResponseDetails.setOtherFieldContent("Other");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("Other"));
        errors = msqQuestionDetails.validateResponsesDetails(Collections.singletonList(feedbackMsqResponseDetails), 0);
        assertEquals(0, errors.size());
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackMsqQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackMsqQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }
}
