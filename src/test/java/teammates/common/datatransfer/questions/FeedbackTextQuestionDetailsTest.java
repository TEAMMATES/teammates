package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.test.BaseTestCase;

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
        expectedResult = new ArrayList<>();
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

        assertTrue(feedbackTextQuestionDetails.validateResponsesDetails(responses, 0).isEmpty());

        responses.add(new FeedbackTextResponseDetails());
        assertTrue(feedbackTextQuestionDetails.validateResponsesDetails(responses, 10).isEmpty());

        assertTrue(feedbackTextQuestionDetails.validateResponsesDetails(responses, -100).isEmpty());
    }

    @Test
    public void testValidateGiverRecipientVisibility_shouldReturnEmptyString() {
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder().build();
        assertEquals("", feedbackTextQuestionDetails.validateGiverRecipientVisibility(feedbackQuestionAttributes));
    }

    @Test
    public void testIsRichTextOptionTrueByDefault_shouldReturnTrue() {
        FeedbackTextQuestionDetails feedbackQuestionDetails = new FeedbackTextQuestionDetails();
        assertTrue(feedbackQuestionDetails.getShouldAllowRichText());
    }
}
