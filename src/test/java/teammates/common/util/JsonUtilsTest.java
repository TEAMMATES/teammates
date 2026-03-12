package teammates.common.util;

import org.testng.annotations.Test;

import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Section;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link JsonUtils}.
 */
public class JsonUtilsTest extends BaseTestCase {

    @Test
    public void testFeedbackQuestionDetailsAdaptor_withComposedQuestionDetails_shouldSerializeToConcreteClass() {
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");
        Course course = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
        fq.setQuestionDetails(questionDetails);

        String expectedQuestionDetailsJson = "\"questionDetails\": {\n"
                + "    \"shouldAllowRichText\": true,\n"
                + "    \"questionType\": \"TEXT\",\n"
                + "    \"questionText\": \"Question text.\"\n"
                + "  }";

        assertTrue(JsonUtils.toJson(fq).contains(expectedQuestionDetailsJson));

        expectedQuestionDetailsJson = "\"questionDetails\":{\"shouldAllowRichText\":true,\"questionType\":\"TEXT\","
                + "\"questionText\":\"Question text.\"}";

        assertTrue(JsonUtils.toCompactJson(fq).contains(expectedQuestionDetailsJson));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        Course course = getTypicalCourse();
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);
        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
        Section giverSection = getTypicalSection();
        Section recipientSection = getTypicalSection();

        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("My answer");
        FeedbackResponse fr = FeedbackResponse.makeResponse(fq, "giver@email.com",
                giverSection, "recipient@email.com",
                recipientSection, frd);

        String expectedFeedbackResponseDetailsJson = "\"answer\": {\n"
                + "    \"answer\": \"My answer\",\n"
                + "    \"questionType\": \"TEXT\"\n"
                + "  }";

        assertTrue(JsonUtils.toJson(fr).contains(expectedFeedbackResponseDetailsJson));

        expectedFeedbackResponseDetailsJson = "\"answer\":{\"answer\":\"My answer\",\"questionType\":\"TEXT\"}";

        assertTrue(JsonUtils.toCompactJson(fr).contains(expectedFeedbackResponseDetailsJson));
    }
}
