package teammates.common.datatransfer.questions;

import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Test
    public void testGetJsonString() {
        FeedbackTextResponseDetails frd = new FeedbackTextResponseDetails("I liked this class");
        FeedbackResponseDetails frdDeep = frd.getDeepCopy();
        frdDeep.setQuestionType(FeedbackQuestionType.TEXT);
        assertEquals(frdDeep.getQuestionType(), FeedbackQuestionType.TEXT); 
        assertEquals(frdDeep.getJsonString(), "I liked this class");
        frdDeep.setQuestionType(FeedbackQuestionType.MCQ);
        assertNotEquals(frdDeep.getQuestionType(), FeedbackQuestionType.TEXT);

        FeedbackMcqResponseDetails mcqDetails = new FeedbackMcqResponseDetails();
        mcqDetails.setAnswer("I love it!");
        FeedbackResponseDetails frdDeepV2 = mcqDetails.getDeepCopy();
        frdDeepV2.setQuestionType(FeedbackQuestionType.MCQ);
        // These should not be equal because getJsonString returns Json object
        assertNotEquals(frdDeepV2.getJsonString(), "I love it!");
    }
}
