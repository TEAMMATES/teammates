package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRankQuestionDetails}.
 */
public class FeedbackRankQuestionDetailsTest extends BaseTestCase {
    @Test
    public void testValidateSetMinOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, Integer.MIN_VALUE);
        feedbackRankQuestionDetails.setMinOptionsToBeRanked(testValue);
        assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateSetMaxOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, Integer.MIN_VALUE);
        feedbackRankQuestionDetails.setMaxOptionsToBeRanked(testValue);
        assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateSetDuplicatesAllowed_validValues_shouldReturnTrue() {
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        assertEquals(feedbackRankQuestionDetails.areDuplicatesAllowed, false);
        feedbackRankQuestionDetails.setAreDuplicatesAllowed(true);
        assertEquals(feedbackRankQuestionDetails.areDuplicatesAllowed, true);
    }

    @Test
    public void testValidateDefaultValue_sameValues_shouldReturnTrue() {
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        assertEquals(feedbackRankQuestionDetails.getMaxOptionsToBeRanked(), Integer.MIN_VALUE);
        assertEquals(feedbackRankQuestionDetails.getMinOptionsToBeRanked(), Integer.MIN_VALUE);
    }
}
