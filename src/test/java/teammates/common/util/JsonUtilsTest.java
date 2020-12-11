package teammates.common.util;

import java.util.ArrayList;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link JsonUtils}.
 */
public class JsonUtilsTest extends BaseTestCase {

    @Test
    public void testFeedbackQuestionDetailsAdaptor_withComposedQuestionDetails_shouldSerializeToConcreteClass() {
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");

        ArrayList<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);

        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.builder()
                .withCourseId("testingCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.SELF)
                .withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .withQuestionNumber(1)
                .withQuestionDetails(questionDetails)
                .withShowGiverNameTo(participants)
                .withShowRecipientNameTo(participants)
                .withShowResponsesTo(participants)
                .build();

        assertEquals("{\n"
                + "  \"feedbackSessionName\": \"testFeedbackSession\",\n"
                + "  \"courseId\": \"testingCourse\",\n"
                + "  \"questionDetails\": {\n"
                + "    \"shouldAllowRichText\": true,\n"
                + "    \"questionType\": \"TEXT\",\n"
                + "    \"questionText\": \"Question text.\"\n"
                + "  },\n"
                + "  \"questionNumber\": 1,\n"
                + "  \"giverType\": \"INSTRUCTORS\",\n"
                + "  \"recipientType\": \"SELF\",\n"
                + "  \"numberOfEntitiesToGiveFeedbackTo\": -100,\n"
                + "  \"showResponsesTo\": [\n"
                + "    \"RECEIVER\"\n"
                + "  ],\n"
                + "  \"showGiverNameTo\": [\n"
                + "    \"RECEIVER\"\n"
                + "  ],\n"
                + "  \"showRecipientNameTo\": [\n"
                + "    \"RECEIVER\"\n"
                + "  ]\n"
                + "}", JsonUtils.toJson(fqa));

        assertEquals("{\"feedbackSessionName\":\"testFeedbackSession\","
                + "\"courseId\":\"testingCourse\",\"questionDetails\":{\"shouldAllowRichText\":true,\"questionType\":"
                + "\"TEXT\","
                + "\"questionText\":\"Question text.\"},\"questionNumber\":1,"
                + "\"giverType\":\"INSTRUCTORS\",\"recipientType\":\"SELF\",\"numberOfEntitiesToGiveFeedbackTo\":-100,"
                + "\"showResponsesTo\":[\"RECEIVER\"],\"showGiverNameTo\":[\"RECEIVER\"],"
                + "\"showRecipientNameTo\":[\"RECEIVER\"]}",
                JsonUtils.toCompactJson(fqa));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        FeedbackResponseAttributes fra =
                FeedbackResponseAttributes.builder(
                        "questionId", "giver@email.com", "recipient@email.com")
                .withFeedbackSessionName("Session1")
                .withCourseId("CS3281")
                .withGiverSection("giverSection")
                .withRecipientSection("recipientSection")
                .withResponseDetails(new FeedbackTextResponseDetails("My answer"))
                .build();

        assertEquals("{\n"
                + "  \"feedbackQuestionId\": \"questionId\",\n"
                + "  \"giver\": \"giver@email.com\",\n"
                + "  \"recipient\": \"recipient@email.com\",\n"
                + "  \"feedbackSessionName\": \"Session1\",\n"
                + "  \"courseId\": \"CS3281\",\n"
                + "  \"responseDetails\": {\n"
                + "    \"answer\": \"My answer\",\n"
                + "    \"questionType\": \"TEXT\"\n"
                + "  },\n"
                + "  \"giverSection\": \"giverSection\",\n"
                + "  \"recipientSection\": \"recipientSection\",\n"
                + "  \"feedbackResponseId\": \"questionId%giver@email.com%recipient@email.com\"\n"
                + "}", JsonUtils.toJson(fra));

        assertEquals("{\"feedbackQuestionId\":\"questionId\",\"giver\":\"giver@email.com\","
                + "\"recipient\":\"recipient@email.com\",\"feedbackSessionName\":\"Session1\","
                + "\"courseId\":\"CS3281\",\"responseDetails\":{\"answer\":\"My answer\","
                + "\"questionType\":\"TEXT\"},\"giverSection\":\"giverSection\","
                + "\"recipientSection\":\"recipientSection\","
                + "\"feedbackResponseId\":\"questionId%giver@email.com%recipient@email.com\"}",
                JsonUtils.toCompactJson(fra));
    }
}
