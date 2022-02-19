package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionDetails}.
 */
public class FeedbackQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testEquals() {

        ______TS("Same object with different references, should be same");
        FeedbackTextQuestionDetails ftqd1 = new FeedbackTextQuestionDetails("text question");
        FeedbackQuestionDetails ftqd2 = ftqd1;
        assertEquals(ftqd1, ftqd2);

        ______TS("One input is null, should be different");
        assertNotEquals(ftqd1, null);

        ______TS("Different classes, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("text question");
        ftqd2 = new FeedbackMcqQuestionDetails();
        assertNotEquals(ftqd1, ftqd2);

        ______TS("Some attributes are different, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("first question");
        ftqd2 = new FeedbackTextQuestionDetails("second question");
        assertNotEquals(ftqd1, ftqd2);

        ftqd2 = new FeedbackTextQuestionDetails("first question");
        ftqd1.setRecommendedLength(50);
        assertNotEquals(ftqd1, ftqd2);

        ______TS("All attributes are same, should be same");
        ((FeedbackTextQuestionDetails) ftqd2).setRecommendedLength(50);
        assertEquals(ftqd1, ftqd2);

    }
}
