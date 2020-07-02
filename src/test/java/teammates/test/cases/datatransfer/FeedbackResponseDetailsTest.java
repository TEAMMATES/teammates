package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackResponseDetails}.
 */
public class FeedbackResponseDetailsTest extends BaseTestCase {

    @Test
    public void testGetDeepCopy() {
        FeedbackTextResponseDetails frd = new FeedbackTextResponseDetails("original");
        FeedbackResponseDetails frdDeep = frd.getDeepCopy();
        frd.setAnswer("updated");

        assertEquals("updated", frd.getAnswerString());
        assertEquals("original", frdDeep.getAnswerString());
    }
}
