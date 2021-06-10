package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.test.BaseTestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * SUT: {@link FeedbackTextQuestionDetails}.
 */
public class FeedbackTextQuestionDetailsTest extends BaseTestCase {

    FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails();

    @Test
    public void testShouldChangesRequireResponseDeletion() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
        FeedbackTextQuestionDetails updatedFeedbackTextQuestionDetails = new FeedbackTextQuestionDetails();

        ______TS("Updated question is not a feedback text question; throws AssertionError");
        assertThrows(AssertionError.class,
                () -> feedbackTextQuestionDetails.shouldChangesRequireResponseDeletion(feedbackQuestionDetails));

        ______TS("Updated question allows rich text and original question allows rich text; return false");
        feedbackTextQuestionDetails.setShouldAllowRichText(true);
        updatedFeedbackTextQuestionDetails.setShouldAllowRichText(true);
        assertFalse(feedbackTextQuestionDetails.shouldChangesRequireResponseDeletion(updatedFeedbackTextQuestionDetails));

        ______TS("Updated question allows rich text and original question does not allow rich text; return true");
        feedbackTextQuestionDetails.setShouldAllowRichText(true);
        updatedFeedbackTextQuestionDetails.setShouldAllowRichText(false);
        assertTrue(feedbackTextQuestionDetails.shouldChangesRequireResponseDeletion(updatedFeedbackTextQuestionDetails));

        ______TS("Updated question does not allow rich text and original question does not allow rich text; return false");
        feedbackTextQuestionDetails.setShouldAllowRichText(false);
        updatedFeedbackTextQuestionDetails.setShouldAllowRichText(false);
        assertFalse(feedbackTextQuestionDetails.shouldChangesRequireResponseDeletion(updatedFeedbackTextQuestionDetails));

        ______TS("Updated question does not allow rich text and original question allows rich text; return false");
        feedbackTextQuestionDetails.setShouldAllowRichText(false);
        updatedFeedbackTextQuestionDetails.setShouldAllowRichText(true);
        assertFalse(feedbackTextQuestionDetails.shouldChangesRequireResponseDeletion(updatedFeedbackTextQuestionDetails));
    }

    @Test
    public void testValidateQuestionDetails() {
        ______TS("Recommended length is null; returns an empty list");
        feedbackTextQuestionDetails.setRecommendedLength(null);
        assertTrue(feedbackTextQuestionDetails.validateQuestionDetails().isEmpty());

        ______TS("Recommended length is not null and is less than 1; returns a non-empty list");
        List<String> expectedResult = new ArrayList<>();
        expectedResult.add(FeedbackTextQuestionDetails.TEXT_ERROR_INVALID_RECOMMENDED_LENGTH);
        feedbackTextQuestionDetails.setRecommendedLength(0);
        assertEquals(expectedResult, feedbackTextQuestionDetails.validateQuestionDetails());

        feedbackTextQuestionDetails.setRecommendedLength(-1);
        assertEquals(expectedResult, feedbackTextQuestionDetails.validateQuestionDetails());

        feedbackTextQuestionDetails.setRecommendedLength(-100);
        assertEquals(expectedResult, feedbackTextQuestionDetails.validateQuestionDetails());

        ______TS("Recommended length is not null and is greater than or equal to 1; returns an empty list");
        expectedResult.clear();
        feedbackTextQuestionDetails.setRecommendedLength(1);
        assertEquals(expectedResult, feedbackTextQuestionDetails.validateQuestionDetails());

        feedbackTextQuestionDetails.setRecommendedLength(2);
        assertEquals(expectedResult, feedbackTextQuestionDetails.validateQuestionDetails());

        feedbackTextQuestionDetails.setRecommendedLength(100);
        assertEquals(expectedResult, feedbackTextQuestionDetails.validateQuestionDetails());
    }

    @Test
    public void testValidateResponsesDetails_shouldReturnEmptyList() {
        List<FeedbackResponseDetails> responses = new ArrayList<>();
        int numRecipients = 0;

        assertTrue(feedbackTextQuestionDetails.validateResponsesDetails(responses, numRecipients).isEmpty());

        for(int i = 0; i < 10; i++) {
            responses.add(new FeedbackTextResponseDetails());
        }
        numRecipients = 10;
        assertTrue(feedbackTextQuestionDetails.validateResponsesDetails(responses, numRecipients).isEmpty());

        numRecipients = -100;
        assertTrue(feedbackTextQuestionDetails.validateResponsesDetails(responses, numRecipients).isEmpty());
    }

    @Test
    public void testValidateGiverRecipientVisibility_shouldReturnEmptyString() {
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder().build();
        assertEquals("", feedbackTextQuestionDetails.validateGiverRecipientVisibility(feedbackQuestionAttributes));
    }

    @Test
    public void testIsInstructorCommentsOnResponsesAllowed_shouldReturnTrue() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackTextQuestionDetails();
        assertTrue(feedbackQuestionDetails.isInstructorCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsFeedbackParticipantCommentsOnResponsesAllowed_shouldReturnFalse() {
        FeedbackQuestionDetails feedbackQuestionDetails = new FeedbackTextQuestionDetails();
        assertFalse(feedbackQuestionDetails.isFeedbackParticipantCommentsOnResponsesAllowed());
    }

    @Test
    public void testIsRichTextOptionTrueByDefault_shouldReturnTrue() {
        FeedbackTextQuestionDetails feedbackQuestionDetails = new FeedbackTextQuestionDetails();
        assertTrue(feedbackQuestionDetails.getShouldAllowRichText());
    }
}
