package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRankQuestionDetails}.
 */
public class FeedbackRankQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testValidateMinOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackRankQuestionDetails.setMinOptionsToBeRanked(testValue);
        assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateMinOptionsToBeRanked_differentValidValues_shouldReturnTrue() {
        int testValue = 100;
        int testDiffValue = 101;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackRankQuestionDetails.setMinOptionsToBeRanked(testValue);
        assertNotEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, testDiffValue);
    }

    @Test
    public void testValidateMaxOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackRankQuestionDetails.setMaxOptionsToBeRanked(testValue);
        assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateMaxOptionsToBeRanked_differentValidValues_shouldReturnTrue() {
        int testValue = 100;
        int testDiffValue = 101;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        feedbackRankQuestionDetails.setMaxOptionsToBeRanked(testValue);
        assertNotEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, testDiffValue);
    }
}
