package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackRankQuestionDetails}.
 */
public class FeedbackRankQuestionDetailsTest extends BaseTestCase {
    @Test
    public void testValidateSetMinOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, Const.POINTS_NO_VALUE);
        feedbackRankQuestionDetails.setMinOptionsToBeRanked(testValue);
        assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateSetMaxOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, Const.POINTS_NO_VALUE);
        feedbackRankQuestionDetails.setMaxOptionsToBeRanked(testValue);
        assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateSetDuplicatesAllowed_validValues_shouldReturnTrue() {
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        assertFalse(feedbackRankQuestionDetails.areDuplicatesAllowed);
        feedbackRankQuestionDetails.setAreDuplicatesAllowed(true);
        assertTrue(feedbackRankQuestionDetails.areDuplicatesAllowed);
    }

    @Test
    public void testValidateDefaultValue_sameValues_shouldReturnTrue() {
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        assertEquals(feedbackRankQuestionDetails.getMaxOptionsToBeRanked(), Const.POINTS_NO_VALUE);
        assertEquals(feedbackRankQuestionDetails.getMinOptionsToBeRanked(), Const.POINTS_NO_VALUE);
    }
}
