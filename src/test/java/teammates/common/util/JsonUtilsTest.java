package teammates.common.util;

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
                + "  \"shouldAllowRichText\": true,\n"
                + "  \"questionType\": \"TEXT\",\n"
                + "  \"questionText\": \"Question text.\"\n"
                + "}";

        assertEquals(expectedQuestionDetailsJson, JsonUtils.toJson(qd));

        expectedQuestionDetailsJson = "{\"shouldAllowRichText\":true,\"questionType\":\"TEXT\","
                + "\"questionText\":\"Question text.\"}";

        assertEquals(expectedQuestionDetailsJson, JsonUtils.toCompactJson(qd));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("My answer");

        String expectedFeedbackResponseDetailsJson = "{\n"
                + "  \"answer\": \"My answer\",\n"
                + "  \"questionType\": \"TEXT\"\n"
                + "}";

        assertEquals(expectedFeedbackResponseDetailsJson, JsonUtils.toJson(frd));

        expectedFeedbackResponseDetailsJson = "{\"answer\":\"My answer\",\"questionType\":\"TEXT\"}";

        assertEquals(expectedFeedbackResponseDetailsJson, JsonUtils.toCompactJson(frd));
    }
}
