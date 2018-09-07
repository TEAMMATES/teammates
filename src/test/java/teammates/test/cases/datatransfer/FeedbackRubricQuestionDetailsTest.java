package teammates.test.cases.datatransfer;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackRubricQuestionDetails}.
 */
public class FeedbackRubricQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();

        assertEquals(FeedbackQuestionType.RUBRIC, rubricDetails.getQuestionType());
        assertTrue(rubricDetails instanceof FeedbackRubricQuestionDetails);
        assertFalse(rubricDetails.hasAssignedWeights());
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testGetRubricWeightsForEachCell_allChoicesNull_weightsListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        assertFalse(rubricDetails.getRubricSubQuestions().isEmpty());
        assertTrue(rubricDetails.getRubricChoices().isEmpty());
        // getRubricWeightForEachCell() returns empty list as there are no choices set.
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testGetRubricWeightsForEachCell_allSubQuestionsNull_weightsListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {"Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        assertTrue(rubricDetails.getRubricSubQuestions().isEmpty());
        assertFalse(rubricDetails.getRubricChoices().isEmpty());
        // getRubricWeightForEachCell() returns empty list as there are no sub questions set.
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testGetRubricWeightsForEachCell_emptySubQuestion_weightForInValidSubQestionShouldNotBeParsed() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"       "});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        // As SubQn-0 is empty, no weights for SubQn-0 should be parsed,
        // So, weight list will contain 1 value (weights for SubQn-1).
        assertEquals(1, rubricDetails.getRubricWeights().size());
        // Check weights for SubQn-1 (for cell-1-0, cell 1-1).
        assertEquals(1.00, rubricDetails.getRubricWeights().get(0).get(0));
        assertEquals(2.00, rubricDetails.getRubricWeights().get(0).get(1));
    }

    @Test
    public void testGetRubricWeightsForEachCell_allWeightsNull_weightListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        // As no weights have been passed, the weight list will be empty
        assertEquals(0, rubricDetails.getRubricWeights().size());
    }

    @Test
    public void testGetRubricWeightsForEachCell_invalidWeightPassed_invalidWeightShouldNotBeParsed() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] {"Invalid"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        assertEquals(2, rubricDetails.getRubricWeights().size());
        // As invalid weight is passed for weight-0-0, there will be only one weight for SubQn-0.
        assertEquals(1, rubricDetails.getRubricWeights().get(0).size());
        // Check weights for SubQn-0 (for cell-0-1)
        assertEquals(2.50, rubricDetails.getRubricWeights().get(0).get(0));
        // Check weights for SubQn-1 (for cell-1-0, cell 1-1).
        assertEquals(2, rubricDetails.getRubricWeights().get(1).size());
        assertEquals(1.00, rubricDetails.getRubricWeights().get(1).get(0));
        assertEquals(2.00, rubricDetails.getRubricWeights().get(1).get(1));
    }

    @Test
    public void testGetRubricWeightsForEachCell_weightsDisabledValidWeights_weightListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertFalse(rubricDetails.hasAssignedWeights());
        // As weights are disabled, weight list should be empty
        assertTrue(rubricDetails.getRubricWeights().isEmpty());
    }

    @Test
    public void testGetRubricWeightsForEachCell_weightsEnabledValidWeights_weightsListShouldHaveCorrectWeights() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        // Check for the size of the weight list.
        assertEquals(2, rubricDetails.getRubricWeights().size());
        assertEquals(2, rubricDetails.getRubricWeights().get(0).size());
        assertEquals(2, rubricDetails.getRubricWeights().get(1).size());
        // Check weight list for correct values.
        assertEquals(1.50, rubricDetails.getRubricWeights().get(0).get(0));
        assertEquals(2.50, rubricDetails.getRubricWeights().get(0).get(1));
        assertEquals(1.00, rubricDetails.getRubricWeights().get(1).get(0));
        assertEquals(2.00, rubricDetails.getRubricWeights().get(1).get(1));
    }

    @Test
    public void testValidateQuestionDetails_invalidWeightListSize_errorReturned() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        List<String> errors = rubricDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_validWeightListSize_errorListShouldBeEmpty() {
        FeedbackRubricQuestionDetails rubricDetails = new FeedbackRubricQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "RUBRIC" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "Rubric question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", new String[] {"SubQn-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", new String[] {"SubQn-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", new String[] {" Choice-1"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", new String[] {"Choice-2"});
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0-1", new String[] { "2.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-0", new String[] { "1.00" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1-1", new String[] { "2.00" });

        assertTrue(rubricDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.RUBRIC));
        assertTrue(rubricDetails.hasAssignedWeights());
        List<String> errors = rubricDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(0, errors.size());
    }
}
