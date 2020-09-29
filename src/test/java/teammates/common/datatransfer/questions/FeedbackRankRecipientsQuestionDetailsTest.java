package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRankRecipientsQuestionDetails}.
 */
public class FeedbackRankRecipientsQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRankRecipientsQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRankRecipientsQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }
}
