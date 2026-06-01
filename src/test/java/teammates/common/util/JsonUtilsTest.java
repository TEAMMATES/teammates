package teammates.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static teammates.test.AssertHelper.assertJsonEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
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

    @Test
    public void testFeedbackQuestionDetailsAdaptor_withConstSumConcreteTypes_shouldDeserialize() {
        String optionsQuestionJson = "{\"questionType\":\"CONSTSUM_OPTIONS\",\"questionText\":\"Q\","
                + "\"distributeToRecipients\":false,\"pointsPerOption\":false,\"points\":100,\"constSumOptions\":[\"A\",\"B\"]}";
        FeedbackQuestionDetails optionsDetails = JsonUtils.fromJson(optionsQuestionJson, FeedbackQuestionDetails.class);
        assertEquals(FeedbackQuestionType.CONSTSUM_OPTIONS, optionsDetails.getQuestionType());

        String recipientsQuestionJson = "{\"questionType\":\"CONSTSUM_RECIPIENTS\",\"questionText\":\"Q\","
                + "\"distributeToRecipients\":true,\"pointsPerOption\":true,\"points\":100,\"constSumOptions\":[]}";
        FeedbackQuestionDetails recipientsDetails = JsonUtils.fromJson(recipientsQuestionJson, FeedbackQuestionDetails.class);
        assertEquals(FeedbackQuestionType.CONSTSUM_RECIPIENTS, recipientsDetails.getQuestionType());
    }
}
