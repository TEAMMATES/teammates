package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackQuestionDetails}.
 */
public class FeedbackQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testEquals() {

        ______TS("Same object with different references, should be same");
        FeedbackQuestionDetails ftqd1 = new FeedbackTextQuestionDetails("text question");
        FeedbackQuestionDetails ftqd2 = ftqd1;
        assertTrue(ftqd1.equals(ftqd2));

        ______TS("One input is null, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("text question");
        ftqd2 = null;
        assertFalse(ftqd1.equals(ftqd2));

        ______TS("Different classes, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("text question");
        ftqd2 = new FeedbackMcqQuestionDetails();
        assertFalse(ftqd1.equals(ftqd2));

        ______TS("Some attributes are different, should be different");
        ftqd1 = new FeedbackTextQuestionDetails("first question");
        ftqd2 = new FeedbackTextQuestionDetails("second question");
        assertFalse(ftqd1.equals(ftqd2));

        ftqd2 = new FeedbackTextQuestionDetails("first question");
        ((FeedbackTextQuestionDetails) ftqd1).setRecommendedLength(50);
        assertFalse(ftqd1.equals(ftqd2));

        ______TS("All attributes are same, should be same");
        ((FeedbackTextQuestionDetails) ftqd2).setRecommendedLength(50);
        assertTrue(ftqd1.equals(ftqd2));

    }
}
