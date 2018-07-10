package teammates.test.cases.datatransfer;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackMcqQuestionDetails}.
 */
public class FeedbackMcqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();

        assertEquals(FeedbackQuestionType.MCQ, mcqDetails.getQuestionType());
        assertTrue(mcqDetails instanceof FeedbackMcqQuestionDetails);
        assertFalse(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testGetMcqWeights_allChoicesNull_weightsListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "2.50" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertEquals(mcqDetails.getQuestionType(), FeedbackQuestionType.MCQ);
        assertTrue(mcqDetails instanceof FeedbackMcqQuestionDetails);
        assertTrue(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getMcqChoices().isEmpty());
        // getMcqWeight() returns empty list as there are no mcq choices set.
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
        assertFalse(mcqDetails.getOtherEnabled());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testGetMcqWeights_emptyChoice_weightForInValidChoiceShouldNotBeParsed() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "        " });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        List<Double> weights = mcqDetails.getMcqWeights();
        // As one weight can not be parsed, there will be only one weight in the list
        assertEquals(1, weights.size());
        assertEquals(1.55, weights.get(0));
    }

    @Test
    public void testGetMcqWeights_allWeightsNull_weightsListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertEquals(2, mcqDetails.getMcqChoices().size());
        assertEquals(0, mcqDetails.getMcqWeights().size());
    }

    @Test
    public void testGetMcqWeights_invalidWeights_weightNotParsed() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "Invalid Weight" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        List<Double> weights = mcqDetails.getMcqWeights();
        // As one weight can not be parsed, there will be only one weight in the list
        assertEquals(1, weights.size());
        assertEquals(1.55, weights.get(0));
    }

    @Test
    public void testGetMcqWeights_weightsDisabledValidWeights_weightListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "2.55" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertFalse(mcqDetails.hasAssignedWeights());
        // As weights are disabled, getMcqWeights should return an empty list.
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
    }

    @Test
    public void testGetMcqWeights_weightsEnabledValidChoicesAndWeights_weightsShouldHaveCorrectValues() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "2.50" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertFalse(mcqDetails.getMcqChoices().isEmpty());
        assertEquals(2, mcqDetails.getMcqChoices().size());
        List<Double> weights = mcqDetails.getMcqWeights();
        assertEquals(2, weights.size());
        assertEquals(1.50, weights.get(0));
        assertEquals(2.50, weights.get(1));
        assertFalse(mcqDetails.getOtherEnabled());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testGetMcqOtherWeight_weightsEnabledOtherDisabledValidWeights_otherWeightShouldHaveDefaultValue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertFalse(mcqDetails.getOtherEnabled());
        // As 'other' option is disabled, 'otherWeight' will have default value of 0.0
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testGetMcqOtherWeight_weightAndOtherEnabledValidWeights_fieldShouldHaveCorrectValue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getOtherEnabled());
        assertEquals(3.12, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testGetMcqOtherWeight_weightsDisabledOtherEnabled_otherWeightShouldHaveDefaultValue() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertFalse(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getOtherEnabled());
        // As weights is disabled, even though other is enabled, otherWeight will have it's default value.
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testGetMcqOtherWeight_invalidOtherWeight_otherWeightNotParsed() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "aa" });

        // Other weight value before editing the question
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getOtherEnabled());
        // As 'aa' is not valid double value, other weight will keep the previous value
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testGetMcqOtherWeight_nullOtherWeight_exceptionThrown() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        // Removed to send null as otherWeight parameter
        // requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "" });

        mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ);
    }

    @Test
    public void testValidateQuestionDetails_choicesLessThanMinRequirement_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        List<String> errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_numberOfChoicesGreaterThanWeights_errorReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        // Remove this weight to make number of choices greater than number of weights
        // requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        List<String> errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        List<String> errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_negativeWeights_errorsReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        // Pass negative weight for choice 1 to check that negative weights are not allowed.
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "-1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        List<String> errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorsReturned() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        // Pass negative weight for 'Other' option to check that negative weights are not allowed.
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "-2" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        List<String> errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testExtractQuestionDetails_weightsEnabledForGenerateOptions_weightShouldRemainDisabled() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "STUDENTS" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        // As, weights does not support other generateOptionsFor options then 'NONE',
        // here in this case, even though we assigned weights for 'Generate Options for Student'
        // the weights will remain disabled, and the weights list will remain empty.
        assertFalse(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
    }

    @Test
    public void testCreateQuestionDetails_mcqTypeQuestion_createdSuccessfully() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put("questiontext", new String[] { "MCQ" });
        requestParameters.put("mcqGeneratedOptions", new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put("noofchoicecreated", new String[] { "1" });
        requestParameters.put("mcqOption-0", new String[] { "mcq option" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MCQ);

        assertEquals(feedbackQuestionDetails.getQuestionType(), FeedbackQuestionType.MCQ);
        assertTrue(feedbackQuestionDetails instanceof FeedbackMcqQuestionDetails);
        assertEquals(feedbackQuestionDetails.getQuestionText(), "MCQ");
    }

    @Test
    public void testGetQuestionWithExistingResponseSubmissionFormHtml_responsePresent_htmlTagsPresent() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put("questiontext", new String[] { "MCQ" });
        requestParameters.put("mcqGeneratedOptions", new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put("noofchoicecreated", new String[] { "1" });
        requestParameters.put("mcqOption-0", new String[] { "mcq option" });
        requestParameters.put("questiontype-1", new String[] { "MCQ" });
        requestParameters.put("responsetext-1-0", new String[] { "mcq option" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MCQ);

        FeedbackResponseDetails responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "mcq option" },
                        FeedbackQuestionType.MCQ,
                        feedbackQuestionDetails, requestParameters, 1, 0);

        String result = feedbackQuestionDetails.getQuestionWithExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 1, responseDetails,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());
        assertTrue(result.contains("checked")); // Checking if response is selected
        assertTrue(result.contains("mcq option")); // Check if option is showed in HTML
    }

    @Test
    public void testGetQuestionWithoutExistingResponseSubmissionFormHtml_noResponse_htmlTagsPresent() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put("questiontext", new String[] { "MCQ" });
        requestParameters.put("mcqGeneratedOptions", new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put("noofchoicecreated", new String[] { "1" });
        requestParameters.put("mcqOption-0", new String[] { "mcq option" });
        requestParameters.put("questiontype-1", new String[] { "MCQ" });
        requestParameters.put("responsetext-1-0", new String[] { "mcq option" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MCQ);

        String result = feedbackQuestionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 0,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());
        assertFalse(result.contains("checked")); // Checking if response is not present now

    }

    @Test
    public void testGetQuestionAdditionalInfo_additionalInfoPresent_htmlTagsPresent() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put("questiontext", new String[] { "MCQ" });
        requestParameters.put("mcqGeneratedOptions", new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put("noofchoicecreated", new String[] { "1" });
        requestParameters.put("mcqOption-0", new String[] { "mcq option" });
        requestParameters.put("questiontype-1", new String[] { "MCQ" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MCQ);

        String result = feedbackQuestionDetails.getQuestionAdditionalInfoHtml(1, "");
        assertTrue(result.contains("[more]"));
        assertTrue(result.contains("<li>mcq option</li>"));
    }
}
