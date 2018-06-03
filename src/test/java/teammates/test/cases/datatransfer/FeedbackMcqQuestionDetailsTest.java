package teammates.test.cases.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackMcqQuestionDetails}.
 */
public class FeedbackMcqQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testAddMcqWeights() {
        List<Double> weights = new ArrayList<>();
        ______TS("Success: weights disabled, 'other' disabled");
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();

        assertEquals(FeedbackQuestionType.MCQ, mcqDetails.getQuestionType());
        assertTrue(mcqDetails instanceof FeedbackMcqQuestionDetails);
        assertFalse(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getMcqWeights().isEmpty());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());

        ______TS("Failure: Weights enabled, null choices, custom weights, other disabled");
        mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
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
        requestParams.clear();

        ______TS("Failure: weights enabled, two choices, null weights, other disabled");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertFalse(mcqDetails.getMcqChoices().isEmpty());
        assertEquals(2, mcqDetails.getMcqChoices().size());
        assertEquals(0, mcqDetails.getMcqWeights().size());
        requestParams.clear();

        ______TS("Success: Weights enalbed, two choices, custom weights, other disabled");
        mcqDetails = new FeedbackMcqQuestionDetails();
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.50" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "2.50" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertFalse(mcqDetails.getMcqChoices().isEmpty());
        assertEquals(2, mcqDetails.getMcqChoices().size());
        weights = mcqDetails.getMcqWeights();
        assertEquals(2, weights.size());
        assertEquals(1.50, weights.get(0));
        assertEquals(2.50, weights.get(1));
        assertFalse(mcqDetails.getOtherEnabled());
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        requestParams.clear();

        ______TS("Failure: weights enabled, other disabled, custom other weight");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertFalse(mcqDetails.getOtherEnabled());
        // As 'other' option is disabled, 'otherWeight' will have default value of 0.0
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        requestParams.clear();

        ______TS("Failure: weights enabled, other enabled, custom other weight");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getOtherEnabled());
        // If no other option has weights attached, 'other' option should not have a weight either.
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        requestParams.clear();

        ______TS("Failure weights disabled, other enabled, custom other weight");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "off" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "3.12" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertFalse(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getOtherEnabled());
        // As weights is disabled, even though other is enabled, otherWeight will have it's default value.
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        requestParams.clear();

        ______TS("Failure: other weight not parsed");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, new String[] { "aa" });

        // Other weight value before editing the question
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        assertTrue(mcqDetails.getOtherEnabled());
        // As 'aa' is not valid double value, other weight will keep the previous value
        assertEquals(0.0, mcqDetails.getMcqOtherWeight());
        requestParams.clear();

        ______TS("Failure: Weight not parsed, not valid double");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "not parsed" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        weights = mcqDetails.getMcqWeights();
        // As one weight can not be parsed, there will be only one weight in the list
        assertEquals(1, weights.size());
        assertEquals(1.55, weights.get(0));
        requestParams.clear();

        ______TS("Failure: weight not parsed as choice is only spaces");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "        " });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        weights = mcqDetails.getMcqWeights();
        // As one weight can not be parsed, there will be only one weight in the list
        assertEquals(1, weights.size());
        assertEquals(1.55, weights.get(0));
        requestParams.clear();
    }

    @Test
    public void testValidate() {
        FeedbackMcqQuestionDetails mcqDetails = new FeedbackMcqQuestionDetails();
        HashMap<String, String[]> requestParams = new HashMap<>();
        List<String> errors = new ArrayList<>();

        ______TS("Number of choices is less than number of minimum choices");
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_NOT_ENOUGH_CHOICES
                + Const.FeedbackQuestion.MCQ_MIN_NUM_OF_CHOICES + ".", errors.get(0));
        requestParams.clear();

        ______TS("weight assigned, number of choices is greater than number of weights");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        // Remove this weight to make number of choices greater than number of weights
        // requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT, errors.get(0));
        requestParams.clear();

        ______TS("Success case: No validation errors");
        mcqDetails = new FeedbackMcqQuestionDetails();

        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TYPE, new String[] { "MCQ" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_TEXT, new String[] { "mcq question text" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, new String[] { "NONE" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, new String[] { "2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", new String[] { "Choice 1" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", new String[] { "Choice 2" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHTS_ASSIGNED, new String[] { "on" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", new String[] { "1.22" });
        requestParams.put(Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", new String[] { "1.55" });

        assertTrue(mcqDetails.extractQuestionDetails(requestParams, FeedbackQuestionType.MCQ));
        assertTrue(mcqDetails.hasAssignedWeights());
        errors = mcqDetails.validateQuestionDetails(dummySessionToken);
        assertEquals(0, errors.size());
        requestParams.clear();
    }
}
