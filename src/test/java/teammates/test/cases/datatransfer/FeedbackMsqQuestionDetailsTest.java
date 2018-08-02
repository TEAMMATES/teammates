package teammates.test.cases.datatransfer;

import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackMsqQuestionDetails}.
 */
public class FeedbackMsqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testConstructor_defaultConstructor_fieldsShouldHaveCorrectDefaultValues() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();

        assertEquals(FeedbackQuestionType.MSQ, msqDetails.getQuestionType());
        assertTrue(msqDetails instanceof FeedbackMsqQuestionDetails);
        assertFalse(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getMsqWeights().isEmpty());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testGetMsqWeights_allChoicesNull_weightsListShouldBeEmpty() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "2.50" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertEquals(msqDetails.getQuestionType(), FeedbackQuestionType.MSQ);
        assertTrue(msqDetails instanceof FeedbackMsqQuestionDetails);
        assertTrue(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getMsqChoices().isEmpty());
        // getMsqWeight() returns empty list as there are no msq choices set.
        assertTrue(msqDetails.getMsqWeights().isEmpty());
        assertFalse(msqDetails.getOtherEnabled());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testGetMsqWeights_emptyChoice_weightForInValidChoiceShouldNotBeParsed() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "        " });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        List<Double> weights = msqDetails.getMsqWeights();
        // As one weight can not be parsed, there will be only one weight in the list
        assertEquals(1, weights.size());
        assertEquals(1.55, weights.get(0));
    }

    @Test
    public void testGetMsqWeights_allWeightsNull_weightsListShouldBeEmpty() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        assertEquals(2, msqDetails.getMsqChoices().size());
        assertEquals(0, msqDetails.getMsqWeights().size());
    }

    @Test
    public void testGetMsqWeights_invalidWeights_weightNotParsed() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "Invalid Weight" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        List<Double> weights = msqDetails.getMsqWeights();
        // As one weight can not be parsed, there will be only one weight in the list
        assertEquals(1, weights.size());
        assertEquals(1.55, weights.get(0));
    }

    @Test
    public void testGetMsqWeights_weightsDisabledValidWeights_weightListShouldBeEmpty() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.25" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertFalse(msqDetails.hasAssignedWeights());
        // As weights are disabled, getMsqWeights should return an empty list.
        assertTrue(msqDetails.getMsqWeights().isEmpty());
    }

    @Test
    public void testGetMsqWeights_weightsEnabledValidChoicesAndWeights_weightsShouldHaveCorrectValues() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "2.50" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        assertFalse(msqDetails.getMsqChoices().isEmpty());
        assertEquals(2, msqDetails.getMsqChoices().size());
        List<Double> weights = msqDetails.getMsqWeights();
        assertEquals(2, weights.size());
        assertEquals(1.50, weights.get(0));
        assertEquals(2.50, weights.get(1));
        assertFalse(msqDetails.getOtherEnabled());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testGetMsqOtherWeight_weightsEnabledOtherDisabledValidWeights_otherWeightShouldHaveDefaultValue() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        assertFalse(msqDetails.getOtherEnabled());
        // As 'other' option is disabled, 'otherWeight' will have default value of 0.0
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testGetMsqOtherWeight_weightAndOtherEnabledValidWeights_fieldShouldHaveCorrectValue() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getOtherEnabled());
        assertEquals(3.12, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testGetMsqOtherWeight_weightsDisabledOtherEnabled_otherWeightShouldHaveDefaultValue() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertFalse(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getOtherEnabled());
        // As weights is disabled, even though other is enabled, otherWeight will have it's default value.
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testGetMsqOtherWeight_invalidOtherWeight_otherWeightNotParsed() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "aa" });

        // Other weight value before editing the question
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getOtherEnabled());
        // As 'aa' is not valid double value, other weight will keep the previous value
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testGetMsqOtherWeight_nullOtherWeight_exceptionThrown() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "on" });
        // The following line is commented out, so otherWeight parameter is missing from the requestParams.
        // requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "" });

        msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ);
    }

    @Test
    public void testValidateQuestionDetails_choicesLessThanMinRequirement_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        List<String> errors = msqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_NOT_ENOUGH_CHOICES
                + Const.FeedbackQuestion.MSQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_numberOfChoicesGreaterThanWeights_errorReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.22" });
        // The following msqWeight-1 is commented out, so the number of Weights can become 1 whereas numOfChoices is 2.
        // requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        List<String> errors = msqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_noValidationError_errorListShouldBeEmpty() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        assertTrue(msqDetails.hasAssignedWeights());
        List<String> errors = msqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateQuestionDetails_negativeWeights_errorsReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.22" });
        // Pass negative weight for choice 1 to check that negative weights are not allowed.
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "-1.55" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        List<String> errors = msqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testValidateQuestionDetails_negativeOtherWeight_errorsReturned() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.55" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "on" });
        // Pass negative weight for 'Other' option to check that negative weights are not allowed.
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "-2" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        List<String> errors = msqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, errors.get(0));
    }

    @Test
    public void testExtractQuestionDetails_weightsEnabledForGenerateOptions_weightShouldRemainDisabled() {
        FeedbackMsqQuestionDetails msqDetails = new FeedbackMsqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "msq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, new String[] { "STUDENTS" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", new String[] { "2.57" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", new String[] { "1.12" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(msqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MSQ));
        // As, weights does not support other generateOptionsFor options then 'NONE',
        // here in this case, even though we assigned weights for 'Generate Options for Student'
        // the weights will remain disabled, and the weights list will remain empty.
        assertFalse(msqDetails.hasAssignedWeights());
        assertTrue(msqDetails.getMsqWeights().isEmpty());
        assertEquals(0.0, msqDetails.getMsqOtherWeight());
    }

    @Test
    public void testCreateQuestionDetails_msqTypeQuestion_createdSuccessfully() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "MSQ question" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS,
                new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "1" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "msq option" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MSQ);

        assertEquals(feedbackQuestionDetails.getQuestionType(), FeedbackQuestionType.MSQ);
        assertTrue(feedbackQuestionDetails instanceof FeedbackMsqQuestionDetails);
        assertEquals("MSQ question", feedbackQuestionDetails.getQuestionText());
    }

    @Test
    public void testGetQuestionWithExistingResponseSubmissionFormHtml_responsePresent_htmlTagsPresent() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "MSQ" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS,
                new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "msq option 1" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", new String[] { "msq option 2" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", new String[] { "msq option 1" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MSQ);

        FeedbackResponseDetails responseDetails =
                FeedbackResponseDetails.createResponseDetails(
                        new String[] { "msq option 1" },
                        FeedbackQuestionType.MSQ,
                        feedbackQuestionDetails, requestParameters, 1, 0);

        String result = feedbackQuestionDetails.getQuestionWithExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 1, responseDetails,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());
        assertTrue(result.contains("checked")); // Checking if response is selected
        assertTrue(result.contains("msq option 1")); // Check if option is showed in HTML
    }

    @Test
    public void testGetQuestionWithoutExistingResponseSubmissionFormHtml_noResponse_htmlTagsPresent() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "MSQ" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS,
                new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "1" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "msq option" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", new String[] { "MSQ" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MSQ);

        String result = feedbackQuestionDetails.getQuestionWithoutExistingResponseSubmissionFormHtml(true, 1,
                0, "test.course", 0,
                StudentAttributes.builder("test.course", "test", "test@gmail.com").build());
        assertFalse(result.contains("checked")); // Checking if response is not present now

    }

    @Test
    public void testGetQuestionAdditionalInfo_additionalInfoPresent_htmlTagsPresent() {
        HashMap<String, String[]> requestParameters = new HashMap<>();

        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "MSQ" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MSQ" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS,
                new String[] {FeedbackParticipantType.NONE.toString()});
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "1" });
        requestParameters.put(Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", new String[] { "msq option" });

        FeedbackQuestionDetails feedbackQuestionDetails =
                FeedbackQuestionDetails.createQuestionDetails(requestParameters, FeedbackQuestionType.MSQ);

        String result = feedbackQuestionDetails.getQuestionAdditionalInfoHtml(1, "");
        assertTrue(result.contains("[more]"));
        assertTrue(result.contains("<li>msq option</li>"));
    }
}
