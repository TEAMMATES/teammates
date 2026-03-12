package teammates.common.util;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
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

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);

        Course course = getTypicalCourse();
        course.setId("testingCourse");

        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);
        fs.setName("testFeedbackSession");

        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
        fq.setGiverType(FeedbackParticipantType.INSTRUCTORS);
        fq.setRecipientType(FeedbackParticipantType.SELF);
        fq.setNumOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS);
        fq.setQuestionNumber(1);
        fq.setQuestionDetails(questionDetails);
        fq.setShowGiverNameTo(participants);
        fq.setShowRecipientNameTo(participants);
        fq.setShowResponsesTo(participants);

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
                + "}", JsonUtils.toJson(fq));

        assertEquals("{\"feedbackSessionName\":\"testFeedbackSession\","
                + "\"courseId\":\"testingCourse\",\"questionDetails\":{\"shouldAllowRichText\":true,\"questionType\":"
                + "\"TEXT\","
                + "\"questionText\":\"Question text.\"},\"questionNumber\":1,"
                + "\"giverType\":\"INSTRUCTORS\",\"recipientType\":\"SELF\",\"numberOfEntitiesToGiveFeedbackTo\":-100,"
                + "\"showResponsesTo\":[\"RECEIVER\"],\"showGiverNameTo\":[\"RECEIVER\"],"
                + "\"showRecipientNameTo\":[\"RECEIVER\"]}",
                JsonUtils.toCompactJson(fq));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        Course course = getTypicalCourse();
        course.setId("CS3281");

        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);
        fs.setName("Session1");

        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);

        Section giverSection = getTypicalSection();
        giverSection.setName("giverSection");

        Section recipientSection = getTypicalSection();
        recipientSection.setName("recipientSection");

        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("My answer");
        FeedbackResponse fr = FeedbackResponse.makeResponse(fq, "giver@email.com",
                giverSection, "recipient@email.com",
                recipientSection, frd);

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
                + "  \"recipientSection\": \"recipientSection\"\n"
                + "}", JsonUtils.toJson(fr));

        assertEquals("{\"feedbackQuestionId\":\"questionId\",\"giver\":\"giver@email.com\","
                + "\"recipient\":\"recipient@email.com\",\"feedbackSessionName\":\"Session1\","
                + "\"courseId\":\"CS3281\",\"responseDetails\":{\"answer\":\"My answer\","
                + "\"questionType\":\"TEXT\"},\"giverSection\":\"giverSection\","
                + "\"recipientSection\":\"recipientSection\"}",
                JsonUtils.toCompactJson(fr));
    }
}
