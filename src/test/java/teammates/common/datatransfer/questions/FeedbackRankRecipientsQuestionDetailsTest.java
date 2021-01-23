package teammates.common.datatransfer.questions;

import java.util.Arrays;

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

    @Test
    public void testValidateResponsesDetails_duplicateRank_shouldReturnDuplicateRankError() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRankRecipientsQuestionDetails();

        int answerDup = 1;
        FeedbackRankRecipientsResponseDetails details = new FeedbackRankRecipientsResponseDetails();
        details.setAnswer(answerDup);
        FeedbackRankRecipientsResponseDetails detailsValid = new FeedbackRankRecipientsResponseDetails();
        detailsValid.setAnswer(2);
        FeedbackRankRecipientsResponseDetails detailsDup = new FeedbackRankRecipientsResponseDetails();
        detailsDup.setAnswer(answerDup);
        FeedbackRankRecipientsResponseDetails detailsDup2 = new FeedbackRankRecipientsResponseDetails();
        detailsDup2.setAnswer(answerDup);

        String errorString = "Duplicate rank " + answerDup + " in question";
        assertEquals(
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(
                        details, detailsValid, detailsDup, detailsDup2), 4),
                Arrays.asList(errorString, errorString)
        );
    }

    @Test
    public void testValidateResponsesDetails_invalidRank_shouldReturnInvalidRankError() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackRankRecipientsQuestionDetails();

        int numberOfRecipients = 5;
        int answerBeyondBound = numberOfRecipients + 1;
        FeedbackRankRecipientsResponseDetails details = new FeedbackRankRecipientsResponseDetails();
        details.setAnswer(numberOfRecipients - 1);
        FeedbackRankRecipientsResponseDetails detailsInvalid = new FeedbackRankRecipientsResponseDetails();
        detailsInvalid.setAnswer(answerBeyondBound);

        String errorString = "Invalid rank " + answerBeyondBound + " in question";
        assertEquals(
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(
                        details, detailsInvalid), numberOfRecipients),
                Arrays.asList(errorString)
        );

        int answerInvalid = -1;
        details = new FeedbackRankRecipientsResponseDetails();
        details.setAnswer(1);
        detailsInvalid = new FeedbackRankRecipientsResponseDetails();
        detailsInvalid.setAnswer(answerInvalid);

        errorString = "Invalid rank " + answerInvalid + " in question";
        assertEquals(
                feedbackQuestionDetails.validateResponsesDetails(Arrays.asList(
                        details, detailsInvalid), numberOfRecipients),
                Arrays.asList(errorString)
        );
    }
}
