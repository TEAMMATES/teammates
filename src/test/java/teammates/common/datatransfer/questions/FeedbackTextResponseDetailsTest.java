package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.common.util.SanitizationHelper;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackTextResponseDetails}.
 */
public class FeedbackTextResponseDetailsTest extends BaseTestCase {
    @Test
    public void testValidateArgumentConstructor_sameValues_shouldReturnTrue() {
        String testAnswer = "Hello World";
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(testAnswer);
        assertEquals(feedbackTextResponseDetails.getAnswer(), "Hello World");
    }

    @Test
    public void testValidateAttributesOfAnswer_nullValue_shouldReturnNull() {
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(null);
        assertNull(feedbackTextResponseDetails.getAnswer());
    }

    @Test
    public void testValidateSanitizedAnswerString_nullValues_shouldReturnTrue() {
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(null);
        assertEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(feedbackTextResponseDetails.getAnswer()));
    }

    @Test
    public void testValidateSanitizedAnswerString_validValues_shouldReturnTrue() {
        String testValue = "Hello World";
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails(testValue);
        assertEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(feedbackTextResponseDetails.getAnswer()));
    }

    @Test
    public void testValidateSetAnswer_sameValues_shouldReturnTrue() {
        String testValue = "Hello World";
        FeedbackTextResponseDetails feedbackTextResponseDetails = new FeedbackTextResponseDetails();
        assertNotEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(testValue));

        feedbackTextResponseDetails.setAnswer(testValue);

        assertEquals(feedbackTextResponseDetails.getAnswerString(),
                SanitizationHelper.sanitizeForRichText(testValue));
    }
}
