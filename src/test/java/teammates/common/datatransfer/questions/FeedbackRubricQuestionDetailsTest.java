package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRubricQuestionDetails}.
 */
public class FeedbackRubricQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();

        assertEquals(FeedbackQuestionType.RUBRIC, rubricDetails.getQuestionType());
        assertFalse(rubricDetails.isHasAssignedWeights());
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testValidateQuestionDetails_invalidWeightListSize_errorReturned() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", ""), Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(true);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(1.5, 2.5), Collections.singletonList(1.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(1, errors.size());
        assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_validWeightListSize_errorListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", ""), Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(true);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(1.5, 2.5), Arrays.asList(1.0, 2.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_invalidDescriptionSize_errorReturned() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("A", "B")));
        rubricDetails.setHasAssignedWeights(false);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1", "SubQn-2", "SubQn-3"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(3.0, 3.0)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_DESC_INVALID_SIZE, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_invalidChoicesSize_shouldBe2MinChoices() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("")));
        rubricDetails.setHasAssignedWeights(false);
        rubricDetails.setRubricSubQuestions(Arrays.asList("SubQn-1"));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(0.5, 0.5)));

        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_NOT_ENOUGH_CHOICES
                + FeedbackRubricQuestionDetails.RUBRIC_MIN_NUM_OF_CHOICES,
                errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_invalidSubQuestionSize_shouldBe2MinSubQuestion() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(false);
        rubricDetails.setRubricSubQuestions(Arrays.asList());
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(0.5, 0.5)));
        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
                + FeedbackRubricQuestionDetails.RUBRIC_MIN_NUM_OF_SUB_QUESTIONS,
                errors.get(1));
    }

    @Test
    public void testValidateQuestionDetails_invalidBeNotEmptySubQuestionSize_shouldBeNotEmptySubQuestion() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("", "")));
        rubricDetails.setHasAssignedWeights(false);
        rubricDetails.setRubricSubQuestions(Arrays.asList(" "));
        rubricDetails.setRubricChoices(Arrays.asList("Choice-1", "Choice-2"));
        rubricDetails.setRubricWeightsForEachCell(Arrays.asList(Arrays.asList(0.5, 0.5)));
        List<String> errors = rubricDetails.validateQuestionDetails();
        assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_EMPTY_SUB_QUESTION, errors.get(0));
    }

    @Test
    public void testValidateResponseDetails_validAnswer_shouldReturnEmptyErrorList() {
        FeedbackRubricQuestionDetails rubricQuestionDetails = new FeedbackRubricQuestionDetails();
        rubricQuestionDetails.setHasAssignedWeights(false);
        rubricQuestionDetails.setRubricWeightsForEachCell(new ArrayList<>());
        rubricQuestionDetails.setRubricChoices(Arrays.asList("a", "b"));
        rubricQuestionDetails.setRubricSubQuestions(Arrays.asList("q1", "q2"));
        rubricQuestionDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("d1", "d2"), Arrays.asList("d3", "d4")));

        FeedbackRubricResponseDetails responseDetails = new FeedbackRubricResponseDetails();

        responseDetails.setAnswer(Arrays.asList(1, FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN));
        assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN, 0));
        assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 0));
        assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());
    }

    @Test
    public void testValidateResponseDetails_invalidAnswer_shouldReturnNonEmptyErrorList() {
        FeedbackRubricQuestionDetails rubricQuestionDetails = new FeedbackRubricQuestionDetails();
        rubricQuestionDetails.setHasAssignedWeights(false);
        rubricQuestionDetails.setRubricWeightsForEachCell(new ArrayList<>());
        rubricQuestionDetails.setRubricChoices(Arrays.asList("a", "b"));
        rubricQuestionDetails.setRubricSubQuestions(Arrays.asList("q1", "q2"));
        rubricQuestionDetails.setRubricDescriptions(Arrays.asList(Arrays.asList("d1", "d2"), Arrays.asList("d3", "d4")));

        FeedbackRubricResponseDetails responseDetails = new FeedbackRubricResponseDetails();

        responseDetails.setAnswer(Arrays.asList());
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN,
                FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, -2));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(2, 1));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 1, 0));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, null, 0));
        assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRubricQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRubricQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }
}
