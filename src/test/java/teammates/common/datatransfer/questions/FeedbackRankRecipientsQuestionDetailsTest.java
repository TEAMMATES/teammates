package teammates.common.datatransfer.questions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    public void tesValidateResponsesDetails() {
        FeedbackRankRecipientsQuestionDetails feedbackRankRecipientsQuestionDetails =
                new FeedbackRankRecipientsQuestionDetails();
        String duplicateErrorStr = "Duplicate rank %d in question";
        String invalidErrorStr = "Invalid rank %d in question";
        String maxOptionErrorStr = "You can rank at most %d options.";
        String minOptionErrorStr = "You must rank at least %d options.";

        ______TS("Failure: Duplicate ranks");
        FeedbackRankRecipientsResponseDetails response1 = new FeedbackRankRecipientsResponseDetails();
        FeedbackRankRecipientsResponseDetails response2 = new FeedbackRankRecipientsResponseDetails();
        response1.setAnswer(1);
        response2.setAnswer(1);
        List<String> actualErrors = feedbackRankRecipientsQuestionDetails.validateResponsesDetails(
                Arrays.asList(response1, response2), 2);
        List<String> expectedErrors = Arrays.asList(String.format(duplicateErrorStr, response1.getAnswer()));
        assertEquals(expectedErrors, actualErrors);

        ______TS("Failure: Invalid ranks");
        response1.setAnswer(3);
        response2.setAnswer(-1);
        actualErrors = feedbackRankRecipientsQuestionDetails.validateResponsesDetails(
                Arrays.asList(response1, response2), 2);
        expectedErrors = Arrays.asList(String.format(invalidErrorStr, response1.getAnswer()),
                String.format(invalidErrorStr, response2.getAnswer()));
        assertEquals(expectedErrors, actualErrors);

        ______TS("Failure: Violate max and min options ranked");
        feedbackRankRecipientsQuestionDetails.setMinOptionsToBeRanked(2);
        response1.setAnswer(3);
        response2.setAnswer(1);
        actualErrors = feedbackRankRecipientsQuestionDetails.validateResponsesDetails(
                Arrays.asList(response1), 3);
        expectedErrors = Arrays.asList(
                String.format(minOptionErrorStr, feedbackRankRecipientsQuestionDetails.minOptionsToBeRanked));
        assertEquals(expectedErrors, actualErrors);

        feedbackRankRecipientsQuestionDetails.setMaxOptionsToBeRanked(1);
        actualErrors = feedbackRankRecipientsQuestionDetails.validateResponsesDetails(
                Arrays.asList(response1, response2), 3);
        expectedErrors = Arrays.asList(
                String.format(maxOptionErrorStr, feedbackRankRecipientsQuestionDetails.maxOptionsToBeRanked));
        assertEquals(expectedErrors, actualErrors);

        ______TS("Success: valid responses");
        feedbackRankRecipientsQuestionDetails.setMinOptionsToBeRanked(2);
        feedbackRankRecipientsQuestionDetails.setMaxOptionsToBeRanked(2);
        response1.setAnswer(1);
        response2.setAnswer(2);
        actualErrors = feedbackRankRecipientsQuestionDetails.validateResponsesDetails(
                Arrays.asList(response1, response2), 2);
        expectedErrors = Collections.emptyList();
        assertEquals(expectedErrors, actualErrors);
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
