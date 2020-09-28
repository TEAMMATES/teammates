package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;

import teammates.test.BaseTestCase;

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
