package teammates.common.util;

import static teammates.test.AssertHelper.assertJsonEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link JsonUtils}.
 */
public class JsonUtilsTest extends BaseTestCase {

    @Test
    public void testFeedbackQuestionDetailsAdaptor_withComposedQuestionDetails_shouldSerializeToConcreteClass() {
        FeedbackTextQuestionDetails qd = new FeedbackTextQuestionDetails("Question text.");

        String expectedQuestionDetailsJson = "{\n"
                + "  \"questionType\": \"TEXT\",\n"
                + "  \"questionText\": \"Question text.\",\n"
                + "  \"shouldAllowRichText\": true\n"
                + "}";

        assertJsonEquals(expectedQuestionDetailsJson, JsonUtils.toJson(qd));

        expectedQuestionDetailsJson = "{\"questionType\":\"TEXT\",\"questionText\":\"Question text.\","
                + "\"shouldAllowRichText\":true}";

        assertEquals(expectedQuestionDetailsJson, JsonUtils.toCompactJson(qd));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("My answer");

        String expectedFeedbackResponseDetailsJson = "{\n"
                + "  \"questionType\": \"TEXT\",\n"
                + "  \"answer\": \"My answer\"\n"
                + "}";

        assertJsonEquals(expectedFeedbackResponseDetailsJson, JsonUtils.toJson(frd));

        expectedFeedbackResponseDetailsJson = "{\"questionType\":\"TEXT\",\"answer\":\"My answer\"}";

        assertEquals(expectedFeedbackResponseDetailsJson, JsonUtils.toCompactJson(frd));
    }
}
