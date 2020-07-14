package teammates.test.cases.datatransfer;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqResponseDetails;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackMsqResponseDetails}.
 */
public class FeedbackMsqResponseDetailsTest extends BaseTestCase {

    @Test
    public void testValidateResponseDetails_otherAnswerNotChosenButOtherFieldIsNotEmpty_shouldTriggerError() {
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        msqQuestionDetails.setMsqChoices(Arrays.asList("choiceA", "choiceB"));
        msqQuestionDetails.setOtherEnabled(true);
        msqQuestionDetails.setHasAssignedWeights(false);

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionDetails(msqQuestionDetails)
                .build();

        FeedbackMsqResponseDetails feedbackMsqResponseDetails = new FeedbackMsqResponseDetails();
        feedbackMsqResponseDetails.setOther(false);
        feedbackMsqResponseDetails.setOtherFieldContent("NonEmpty");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("choiceA"));

        List<String> errors = feedbackMsqResponseDetails.validateResponseDetails(feedbackQuestionAttributes);
        assertEquals(1, errors.size());
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION, errors.get(0));

        // now set other field to empty
        feedbackMsqResponseDetails.setOtherFieldContent("");
        errors = feedbackMsqResponseDetails.validateResponseDetails(feedbackQuestionAttributes);
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidateResponseDetails_choiceNotInValidChoices_shouldTriggerError() {
        FeedbackMsqQuestionDetails msqQuestionDetails = new FeedbackMsqQuestionDetails();
        msqQuestionDetails.setMsqChoices(Arrays.asList("choiceA", "choiceB"));
        msqQuestionDetails.setOtherEnabled(true);
        msqQuestionDetails.setHasAssignedWeights(false);
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionDetails(msqQuestionDetails)
                .build();

        // typical case: answers not in valid choices
        FeedbackMsqResponseDetails feedbackMsqResponseDetails = new FeedbackMsqResponseDetails();
        feedbackMsqResponseDetails.setOther(false);
        feedbackMsqResponseDetails.setOtherFieldContent("");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("choiceC"));
        List<String> errors = feedbackMsqResponseDetails.validateResponseDetails(feedbackQuestionAttributes);
        assertEquals(1, errors.size());
        assertEquals(feedbackMsqResponseDetails.getAnswerString()
                + " " + Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION, errors.get(0));

        // now set choice to be within the valid choices
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("choiceA"));
        errors = feedbackMsqResponseDetails.validateResponseDetails(feedbackQuestionAttributes);
        assertEquals(0, errors.size());

        // when other field is enabled, the other field content will become a valid choice
        feedbackMsqResponseDetails.setOther(true);
        feedbackMsqResponseDetails.setOtherFieldContent("Other");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("Other1"));
        errors = feedbackMsqResponseDetails.validateResponseDetails(feedbackQuestionAttributes);
        assertEquals(2, errors.size());
        assertEquals(feedbackMsqResponseDetails.getAnswerString()
                + " " + Const.FeedbackQuestion.MSQ_ERROR_INVALID_OPTION, errors.get(0));
        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_OTHER_CONTENT_NOT_PROVIDED, errors.get(1));

        // make answer list and other field content consistent
        feedbackMsqResponseDetails.setOther(true);
        feedbackMsqResponseDetails.setOtherFieldContent("Other");
        feedbackMsqResponseDetails.setAnswers(Arrays.asList("Other"));
        errors = feedbackMsqResponseDetails.validateResponseDetails(feedbackQuestionAttributes);
        assertEquals(0, errors.size());
    }
}
