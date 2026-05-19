package teammates.common.datatransfer.questions;

import org.junit.jupiter.api.Assertions;
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

        Assertions.assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, Const.POINTS_NO_VALUE);
        feedbackRankQuestionDetails.setMinOptionsToBeRanked(testValue);
        Assertions.assertEquals(feedbackRankQuestionDetails.minOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateSetMaxOptionsToBeRanked_sameValidValues_shouldReturnTrue() {
        int testValue = 100;
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        Assertions.assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, Const.POINTS_NO_VALUE);
        feedbackRankQuestionDetails.setMaxOptionsToBeRanked(testValue);
        Assertions.assertEquals(feedbackRankQuestionDetails.maxOptionsToBeRanked, testValue);
    }

    @Test
    public void testValidateSetDuplicatesAllowed_validValues_shouldReturnTrue() {
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();

        Assertions.assertFalse(feedbackRankQuestionDetails.areDuplicatesAllowed);
        feedbackRankQuestionDetails.setAreDuplicatesAllowed(true);
        Assertions.assertTrue(feedbackRankQuestionDetails.areDuplicatesAllowed);
    }

    @Test
    public void testValidateDefaultValue_sameValues_shouldReturnTrue() {
        FeedbackRankQuestionDetails feedbackRankQuestionDetails = new FeedbackRankOptionsQuestionDetails();
        Assertions.assertEquals(feedbackRankQuestionDetails.getMaxOptionsToBeRanked(), Const.POINTS_NO_VALUE);
        Assertions.assertEquals(feedbackRankQuestionDetails.getMinOptionsToBeRanked(), Const.POINTS_NO_VALUE);
    }
}
