package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackContributionQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackContributionQuestionDetails}.
 */
public class FeedbackContributionQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
        assertFalse(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }

}
