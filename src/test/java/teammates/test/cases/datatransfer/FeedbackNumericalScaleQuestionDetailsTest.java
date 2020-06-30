package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackNumericalScaleQuestionDetails}.
 */
public class FeedbackNumericalScaleQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }

}
