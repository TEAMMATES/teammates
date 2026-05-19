package teammates.common.datatransfer.questions;

import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals(FeedbackQuestionType.RUBRIC, rubricDetails.getQuestionType());
        Assertions.assertFalse(rubricDetails.isHasAssignedWeights());
        Assertions.assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_emptyRubricChoices_shouldReturnTrue() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        FeedbackRubricQuestionDetails newRubricDetails = new FeedbackRubricQuestionDetails();

        Assertions.assertTrue(rubricDetails.shouldChangesRequireResponseDeletion(newRubricDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_equalSizeSubQuestions_shouldReturnFalse() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        rubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4"));

        FeedbackRubricQuestionDetails newRubricDetails = new FeedbackRubricQuestionDetails();
        newRubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        newRubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4"));

        Assertions.assertEquals(rubricDetails.getRubricChoices(), newRubricDetails.getRubricChoices());
        Assertions.assertEquals(rubricDetails.getRubricSubQuestions().size(), newRubricDetails.getRubricSubQuestions().size());
        Assertions.assertTrue(rubricDetails.getRubricSubQuestions().containsAll(newRubricDetails.getRubricSubQuestions()));
        Assertions.assertTrue(newRubricDetails.getRubricSubQuestions().containsAll(rubricDetails.getRubricSubQuestions()));

        Assertions.assertFalse(rubricDetails.shouldChangesRequireResponseDeletion(newRubricDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentSizeSubQuestions_shouldReturnTrue() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        rubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4", "Q5"));

        FeedbackRubricQuestionDetails newRubricDetails = new FeedbackRubricQuestionDetails();
        newRubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        newRubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4"));

        Assertions.assertEquals(rubricDetails.getRubricChoices(), newRubricDetails.getRubricChoices());
        Assertions.assertNotEquals(rubricDetails.getRubricSubQuestions().size(), newRubricDetails.getRubricSubQuestions().size());

        Assertions.assertTrue(rubricDetails.shouldChangesRequireResponseDeletion(newRubricDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentListSubQuestions1_shouldReturnTrue() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        rubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4"));

        FeedbackRubricQuestionDetails newRubricDetails = new FeedbackRubricQuestionDetails();
        newRubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        newRubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4", "Q5"));

        Assertions.assertEquals(rubricDetails.getRubricChoices(), newRubricDetails.getRubricChoices());
        Assertions.assertNotEquals(rubricDetails.getRubricSubQuestions().size(), newRubricDetails.getRubricSubQuestions().size());

        Assertions.assertTrue(rubricDetails.shouldChangesRequireResponseDeletion(newRubricDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentListSubQuestions2_shouldReturnTrue() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        rubricDetails.setRubricSubQuestions(Arrays.asList("Q5", "Q2", "Q3", "Q4"));

        FeedbackRubricQuestionDetails newRubricDetails = new FeedbackRubricQuestionDetails();
        newRubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        newRubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4"));

        Assertions.assertEquals(rubricDetails.getRubricChoices(), newRubricDetails.getRubricChoices());
        Assertions.assertEquals(rubricDetails.getRubricSubQuestions().size(), newRubricDetails.getRubricSubQuestions().size());
        Assertions.assertFalse(rubricDetails.getRubricSubQuestions().containsAll(newRubricDetails.getRubricSubQuestions()));
        Assertions.assertFalse(newRubricDetails.getRubricSubQuestions().containsAll(rubricDetails.getRubricSubQuestions()));

        Assertions.assertTrue(rubricDetails.shouldChangesRequireResponseDeletion(newRubricDetails));
    }

    @Test
    public void testShouldChangesRequireResponseDeletion_differentListSubQuestions3_shouldReturnTrue() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        rubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        rubricDetails.setRubricSubQuestions(Arrays.asList("Q5", "Q2", "Q3", "Q4", "Q6", "Q9"));

        FeedbackRubricQuestionDetails newRubricDetails = new FeedbackRubricQuestionDetails();
        newRubricDetails.setRubricChoices(Arrays.asList("test-1", "test-2"));
        newRubricDetails.setRubricSubQuestions(Arrays.asList("Q1", "Q2", "Q3", "Q4"));

        Assertions.assertEquals(rubricDetails.getRubricChoices(), newRubricDetails.getRubricChoices());
        Assertions.assertNotEquals(rubricDetails.getRubricSubQuestions().size(), newRubricDetails.getRubricSubQuestions().size());
        Assertions.assertFalse(rubricDetails.getRubricSubQuestions().containsAll(newRubricDetails.getRubricSubQuestions()));
        Assertions.assertFalse(newRubricDetails.getRubricSubQuestions().containsAll(rubricDetails.getRubricSubQuestions()));

        Assertions.assertTrue(rubricDetails.shouldChangesRequireResponseDeletion(newRubricDetails));
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
        Assertions.assertEquals(1, errors.size());
        Assertions.assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_INVALID_WEIGHT, errors.get(0));
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
        Assertions.assertEquals(0, errors.size());
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
        Assertions.assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_DESC_INVALID_SIZE, errors.get(0));
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
        Assertions.assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_NOT_ENOUGH_CHOICES
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
        Assertions.assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS
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
        Assertions.assertEquals(FeedbackRubricQuestionDetails.RUBRIC_ERROR_EMPTY_SUB_QUESTION, errors.get(0));
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
        Assertions.assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN, 0));
        Assertions.assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 0));
        Assertions.assertTrue(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());
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
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0));
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN,
                FeedbackRubricQuestionDetails.RUBRIC_ANSWER_NOT_CHOSEN));
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, -2));
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(2, 1));
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, 1, 0));
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());

        responseDetails.setAnswer(Arrays.asList(0, null, 0));
        Assertions.assertFalse(rubricQuestionDetails.validateResponsesDetails(Collections.singletonList(responseDetails), 0).isEmpty());
    }
}
