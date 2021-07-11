package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRankRecipientsQuestionDetails}.
 */
public class FeedbackRankRecipientsQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testShouldChangesRequireResponseDeletion_shouldReturnFalse() {
        FeedbackRankRecipientsQuestionDetails feedbackRankRecipientsQuestionDetails =
                new FeedbackRankRecipientsQuestionDetails();
        FeedbackQuestionDetails newDetails = new FeedbackRankRecipientsQuestionDetails();
        assertFalse(feedbackRankRecipientsQuestionDetails.shouldChangesRequireResponseDeletion(newDetails));
    }

    @Test
    public void testValidateQuestionDetails_shouldReturnEmptyList() {
        FeedbackRankRecipientsQuestionDetails feedbackRankRecipientsQuestionDetails =
                new FeedbackRankRecipientsQuestionDetails();
        assertTrue(feedbackRankRecipientsQuestionDetails.validateQuestionDetails().isEmpty());
    }

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

    @Test
    public void testValidateGiverRecipientVisibility() {
        FeedbackRankRecipientsQuestionDetails feedbackRankRecipientsQuestionDetails =
                new FeedbackRankRecipientsQuestionDetails();
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder().build();
        assertEquals("", feedbackRankRecipientsQuestionDetails.validateGiverRecipientVisibility(feedbackQuestionAttributes));
    }
}
