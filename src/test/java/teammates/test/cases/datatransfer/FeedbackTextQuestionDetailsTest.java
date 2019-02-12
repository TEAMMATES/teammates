package teammates.test.cases.datatransfer;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.test.cases.BaseTestCase;

/**
 * SUT: {@link FeedbackTextQuestionDetails}.
 */
public class FeedbackTextQuestionDetailsTest extends BaseTestCase {

    @Test
    public void testEquals() {
        ______TS("No recommended length, compare based on text");

        FeedbackTextQuestionDetails ftqd1 = new FeedbackTextQuestionDetails("first question");
        FeedbackTextQuestionDetails ftqd2 = new FeedbackTextQuestionDetails("first question");
        assertTrue(ftqd1.equals(ftqd2));

        ftqd1.setQuestionText("updated first question");
        assertFalse(ftqd1.equals(ftqd2));

        ______TS("With recommended length, compare based on text and recommended length");
        ftqd1 = new FeedbackTextQuestionDetails("second question");
        ftqd2 = new FeedbackTextQuestionDetails("second question");

        ftqd1.setRecommendedLength(50);
        assertFalse(ftqd1.equals(ftqd2));

        ftqd2.setRecommendedLength(50);
        assertTrue(ftqd1.equals(ftqd2));

        ftqd1.setRecommendedLength(500);
        assertFalse(ftqd1.equals(ftqd2));

        ______TS("Extreme case: same json string but different recommended length. Should be different");
        ftqd1 = new FeedbackTextQuestionDetails("extreme question");
        ftqd1.setRecommendedLength(50);
        ftqd2 = new FeedbackTextQuestionDetails("{\n"
                + "  \"recommendedLength\": 50,\n"
                + "  \"questionType\": \"TEXT\",\n"
                + "  \"questionText\": \"extreme question\"\n"
                + "}");

        assertEquals(ftqd1.getJsonString(), ftqd2.getJsonString());
        assertFalse(ftqd1.equals(ftqd2));
    }
}
