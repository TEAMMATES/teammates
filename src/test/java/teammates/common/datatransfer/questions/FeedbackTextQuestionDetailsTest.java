package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;
import teammates.common.util.SanitizationHelper;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackTextQuestionDetails}.
 */
public class FeedbackTextQuestionDetailsTest extends BaseTestCase {

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

    @Test
    public void testValidateQuestionArgumentConstructor_sameValues_shouldReturnTrue() {
        String testQuestion = "What are your thoughts about this class?";
        FeedbackTextQuestionDetails FeedbackTextQuestionDetails = new FeedbackTextQuestionDetails(testQuestion);
        assertEquals(FeedbackTextQuestionDetails.getQuestionText(), "What are your thoughts about this class?");
    }

     @Test
    public void testValidateAttributesOfQuestion_nullValue_shouldReturnNull() {
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails(null);
        assertNull(feedbackTextQuestionDetails.getQuestionText());
    }

     @Test
    public void testValidateSanitizedQuestionString_nullValues_shouldReturnTrue() {
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails(null);
        assertEquals(feedbackTextQuestionDetails.getQuestionText(),
                SanitizationHelper.sanitizeForRichText(feedbackTextQuestionDetails.getQuestionText()));
    }

     @Test
    public void testValidateSanitizedQuestionString_validValues_shouldReturnTrue() {
        String testQuestion = "Why do you enjoy this class?";
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails(testQuestion);
        assertEquals(feedbackTextQuestionDetails.getQuestionText(),
                SanitizationHelper.sanitizeForRichText(feedbackTextQuestionDetails.getQuestionText()));
    }

    @Test
    public void testValidateSetQuestion_sameValues_shouldReturnTrue() {
        String testQuestion = "Why do you like this class?";
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails();
        assertNotEquals(feedbackTextQuestionDetails.getQuestionText(),
                SanitizationHelper.sanitizeForRichText(testQuestion));

        feedbackTextQuestionDetails.setQuestionText(testQuestion);

        assertEquals(feedbackTextQuestionDetails.getQuestionText(),
                SanitizationHelper.sanitizeForRichText(testQuestion));
    }

    @Test
    public void testValidateSetQuestionType_sameValues_shouldReturnTrue() {
        String testQuestion = "Why do you like this class?";
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails();
        feedbackTextQuestionDetails.setQuestionType(FeedbackQuestionType.TEXT);

        FeedbackTextQuestionDetails feedbackTextQuestionDetails2 = new FeedbackTextQuestionDetails();
        feedbackTextQuestionDetails2.setQuestionType(FeedbackQuestionType.MCQ);

        assertNotEquals(feedbackTextQuestionDetails.getQuestionType(),
                feedbackTextQuestionDetails2.getQuestionType());

        feedbackTextQuestionDetails2.setQuestionType(FeedbackQuestionType.TEXT);

        assertEquals(feedbackTextQuestionDetails.getQuestionType(),
                feedbackTextQuestionDetails2.getQuestionType());
    }

    @Test
    public void testValidateQuestionDeepCopy_sameValues_shouldReturnTrue() {
        String testQuestion = "Why do you like this class?";
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails();
        feedbackTextQuestionDetails.setQuestionType(FeedbackQuestionType.TEXT);
        feedbackTextQuestionDetails.setQuestionText(testQuestion); 
        FeedbackQuestionDetails feedbackQuestionDetailsV2 = feedbackTextQuestionDetails.getDeepCopy();
        assertEquals(feedbackTextQuestionDetails.getQuestionType(),
                feedbackQuestionDetailsV2.getQuestionType());
        assertEquals(feedbackTextQuestionDetails.getQuestionText(),
                feedbackQuestionDetailsV2.getQuestionText());
        feedbackQuestionDetailsV2.setQuestionText("What challenges did you face?");
        feedbackQuestionDetailsV2.setQuestionType(FeedbackQuestionType.MCQ);

        assertNotEquals(feedbackTextQuestionDetails.getQuestionType(),
                feedbackQuestionDetailsV2.getQuestionType());

        assertNotEquals(feedbackTextQuestionDetails.getQuestionText(),
                feedbackQuestionDetailsV2.getQuestionText());
    }

    @Test
    public void testValidateQuestionMethods_sameValues_shouldReturnTrue() {
        String testQuestion = "Why do you like this class?";
        FeedbackTextQuestionDetails feedbackTextQuestionDetails = new FeedbackTextQuestionDetails();
        feedbackTextQuestionDetails.setRecommendedLength(100);
        feedbackTextQuestionDetails.setShouldAllowRichText(false);

        assertEquals(feedbackTextQuestionDetails.getRecommendedLength(),Integer.valueOf(100));
        assertEquals(feedbackTextQuestionDetails.getShouldAllowRichText(),false);

        feedbackTextQuestionDetails.setRecommendedLength(20);
        feedbackTextQuestionDetails.setShouldAllowRichText(true);
        assertNotEquals(feedbackTextQuestionDetails.getRecommendedLength(),Integer.valueOf(100));
        assertNotEquals(feedbackTextQuestionDetails.getShouldAllowRichText(),false);
    }
}



