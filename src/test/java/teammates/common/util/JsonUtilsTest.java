package teammates.common.util;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2026-01-07T00:00:00Z");
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);
        fs.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        fs.setName("testFeedbackSession");
        fs.setStartTime(startTime);
        fs.setEndTime(endTime);
        fs.setSessionVisibleFromTime(startTime);
        fs.setResultsVisibleFromTime(endTime);

        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
        fq.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        fq.setGiverType(FeedbackParticipantType.INSTRUCTORS);
        fq.setRecipientType(FeedbackParticipantType.SELF);
        fq.setNumOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS);
        fq.setQuestionNumber(1);
        fq.setQuestionDetails(questionDetails);
        fq.setShowGiverNameTo(participants);
        fq.setShowRecipientNameTo(participants);
        fq.setShowResponsesTo(participants);

        assertEquals("{\n"
                + "  \"questionDetails\": {\n"
                + "    \"shouldAllowRichText\": true,\n"
                + "    \"questionType\": \"TEXT\",\n"
                + "    \"questionText\": \"Question text.\"\n"
                + "  },\n"
                + "  \"id\": \"00000000-0000-0000-0000-000000000001\",\n"
                + "  \"feedbackSession\": {\n"
                + "    \"id\": \"00000000-0000-0000-0000-000000000002\",\n"
                + "    \"course\": {\n"
                + "      \"id\": \"testingCourse\",\n"
                + "      \"name\": \"course-name\",\n"
                + "      \"timeZone\": \"UTC\",\n"
                + "      \"institute\": \"teammates\"\n"
                + "    },\n"
                + "    \"name\": \"testFeedbackSession\",\n"
                + "    \"creatorEmail\": \"test@teammates.tmt\",\n"
                + "    \"instructions\": \"<p>test-instructions</p>\",\n"
                + "    \"startTime\": \"2026-01-01T00:00:00Z\",\n"
                + "    \"endTime\": \"2026-01-07T00:00:00Z\",\n"
                + "    \"sessionVisibleFromTime\": \"2026-01-01T00:00:00Z\",\n"
                + "    \"resultsVisibleFromTime\": \"2026-01-07T00:00:00Z\",\n"
                + "    \"gracePeriod\": 5,\n"
                + "    \"isOpenedEmailEnabled\": false,\n"
                + "    \"isClosingSoonEmailEnabled\": false,\n"
                + "    \"isPublishedEmailEnabled\": false,\n"
                + "    \"isOpeningSoonEmailSent\": false,\n"
                + "    \"isOpenedEmailSent\": false,\n"
                + "    \"isClosingSoonEmailSent\": false,\n"
                + "    \"isClosedEmailSent\": false,\n"
                + "    \"isPublishedEmailSent\": false\n"
                + "  },\n"
                + "  \"questionNumber\": 1,\n"
                + "  \"description\": \"test-description\",\n"
                + "  \"giverType\": \"INSTRUCTORS\",\n"
                + "  \"recipientType\": \"SELF\",\n"
                + "  \"numOfEntitiesToGiveFeedbackTo\": -100,\n"
                + "  \"showResponsesTo\": [\n"
                + "    \"OWN_TEAM_MEMBERS\",\n"
                + "    \"RECEIVER\"\n"
                + "  ],\n"
                + "  \"showGiverNameTo\": [\n"
                + "    \"OWN_TEAM_MEMBERS\",\n"
                + "    \"RECEIVER\"\n"
                + "  ],\n"
                + "  \"showRecipientNameTo\": [\n"
                + "    \"OWN_TEAM_MEMBERS\",\n"
                + "    \"RECEIVER\"\n"
                + "  ]\n"
                + "}", JsonUtils.toJson(fq));

        assertEquals("{\"questionDetails\":{\"shouldAllowRichText\":true,\"questionType\":\"TEXT\","
                + "\"questionText\":\"Question text.\"},\"id\":\"00000000-0000-0000-0000-000000000001\","
                + "\"feedbackSession\":{\"id\":\"00000000-0000-0000-0000-000000000002\","
                + "\"course\":{\"id\":\"testingCourse\",\"name\":\"course-name\",\"timeZone\":\"UTC\","
                + "\"institute\":\"teammates\"},\"name\":\"testFeedbackSession\","
                + "\"creatorEmail\":\"test@teammates.tmt\",\"instructions\":\"<p>test-instructions</p>\","
                + "\"startTime\":\"2026-01-01T00:00:00Z\",\"endTime\":\"2026-01-07T00:00:00Z\","
                + "\"sessionVisibleFromTime\":\"2026-01-01T00:00:00Z\","
                + "\"resultsVisibleFromTime\":\"2026-01-07T00:00:00Z\",\"gracePeriod\":5,"
                + "\"isOpenedEmailEnabled\":false,\"isClosingSoonEmailEnabled\":false,"
                + "\"isPublishedEmailEnabled\":false,\"isOpeningSoonEmailSent\":false,"
                + "\"isOpenedEmailSent\":false,\"isClosingSoonEmailSent\":false,"
                + "\"isClosedEmailSent\":false,\"isPublishedEmailSent\":false},"
                + "\"questionNumber\":1,\"description\":\"test-description\","
                + "\"giverType\":\"INSTRUCTORS\",\"recipientType\":\"SELF\","
                + "\"numOfEntitiesToGiveFeedbackTo\":-100,"
                + "\"showResponsesTo\":[\"OWN_TEAM_MEMBERS\",\"RECEIVER\"],"
                + "\"showGiverNameTo\":[\"OWN_TEAM_MEMBERS\",\"RECEIVER\"],"
                + "\"showRecipientNameTo\":[\"OWN_TEAM_MEMBERS\",\"RECEIVER\"]}",
                JsonUtils.toCompactJson(fq));
    }

    @Test
    public void testFeedbackResponseDetailsAdaptor_withComposedResponseDetails_shouldSerializeToConcreteClass() {
        Course course = getTypicalCourse();
        course.setId("CS3281");

        Instant startTime = Instant.parse("2026-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2026-01-07T00:00:00Z");
        FeedbackSession fs = getTypicalFeedbackSessionForCourse(course);
        fs.setId(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        fs.setName("Session1");
        fs.setStartTime(startTime);
        fs.setEndTime(endTime);
        fs.setSessionVisibleFromTime(startTime);
        fs.setResultsVisibleFromTime(endTime);

        FeedbackQuestion fq = getTypicalFeedbackQuestionForSession(fs);
        fq.setId(UUID.fromString("00000000-0000-0000-0000-000000000004"));

        Section giverSection = getTypicalSection();
        giverSection.setName("giverSection");
        giverSection.setId(UUID.fromString("00000000-0000-0000-0000-000000000006"));

        Section recipientSection = getTypicalSection();
        recipientSection.setName("recipientSection");
        recipientSection.setId(UUID.fromString("00000000-0000-0000-0000-000000000007"));

        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("My answer");
        FeedbackResponse fr = FeedbackResponse.makeResponse(fq, "giver@email.com",
                giverSection, "recipient@email.com",
                recipientSection, frd);
        fr.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));

        assertEquals("{\n"
                + "  \"answer\": {\n"
                + "    \"answer\": \"My answer\",\n"
                + "    \"questionType\": \"TEXT\"\n"
                + "  },\n"
                + "  \"id\": \"00000000-0000-0000-0000-000000000003\",\n"
                + "  \"feedbackQuestion\": {\n"
                + "    \"questionDetails\": {\n"
                + "      \"shouldAllowRichText\": true,\n"
                + "      \"questionType\": \"TEXT\",\n"
                + "      \"questionText\": \"test question text\"\n"
                + "    },\n"
                + "    \"id\": \"00000000-0000-0000-0000-000000000004\",\n"
                + "    \"feedbackSession\": {\n"
                + "      \"id\": \"00000000-0000-0000-0000-000000000005\",\n"
                + "      \"course\": {\n"
                + "        \"id\": \"CS3281\",\n"
                + "        \"name\": \"course-name\",\n"
                + "        \"timeZone\": \"UTC\",\n"
                + "        \"institute\": \"teammates\"\n"
                + "      },\n"
                + "      \"name\": \"Session1\",\n"
                + "      \"creatorEmail\": \"test@teammates.tmt\",\n"
                + "      \"instructions\": \"<p>test-instructions</p>\",\n"
                + "      \"startTime\": \"2026-01-01T00:00:00Z\",\n"
                + "      \"endTime\": \"2026-01-07T00:00:00Z\",\n"
                + "      \"sessionVisibleFromTime\": \"2026-01-01T00:00:00Z\",\n"
                + "      \"resultsVisibleFromTime\": \"2026-01-07T00:00:00Z\",\n"
                + "      \"gracePeriod\": 5,\n"
                + "      \"isOpenedEmailEnabled\": false,\n"
                + "      \"isClosingSoonEmailEnabled\": false,\n"
                + "      \"isPublishedEmailEnabled\": false,\n"
                + "      \"isOpeningSoonEmailSent\": false,\n"
                + "      \"isOpenedEmailSent\": false,\n"
                + "      \"isClosingSoonEmailSent\": false,\n"
                + "      \"isClosedEmailSent\": false,\n"
                + "      \"isPublishedEmailSent\": false\n"
                + "    },\n"
                + "    \"questionNumber\": 1,\n"
                + "    \"description\": \"test-description\",\n"
                + "    \"giverType\": \"SELF\",\n"
                + "    \"recipientType\": \"SELF\",\n"
                + "    \"numOfEntitiesToGiveFeedbackTo\": 1,\n"
                + "    \"showResponsesTo\": [],\n"
                + "    \"showGiverNameTo\": [],\n"
                + "    \"showRecipientNameTo\": []\n"
                + "  },\n"
                + "  \"giver\": \"giver@email.com\",\n"
                + "  \"giverSection\": {\n"
                + "    \"id\": \"00000000-0000-0000-0000-000000000006\",\n"
                + "    \"course\": {\n"
                + "      \"id\": \"course-id\",\n"
                + "      \"name\": \"course-name\",\n"
                + "      \"timeZone\": \"UTC\",\n"
                + "      \"institute\": \"teammates\"\n"
                + "    },\n"
                + "    \"name\": \"giverSection\"\n"
                + "  },\n"
                + "  \"recipient\": \"recipient@email.com\",\n"
                + "  \"recipientSection\": {\n"
                + "    \"id\": \"00000000-0000-0000-0000-000000000007\",\n"
                + "    \"course\": {\n"
                + "      \"id\": \"course-id\",\n"
                + "      \"name\": \"course-name\",\n"
                + "      \"timeZone\": \"UTC\",\n"
                + "      \"institute\": \"teammates\"\n"
                + "    },\n"
                + "    \"name\": \"recipientSection\"\n"
                + "  }\n"
                + "}", JsonUtils.toJson(fr));

        assertEquals("{\"answer\":{\"answer\":\"My answer\",\"questionType\":\"TEXT\"},"
                + "\"id\":\"00000000-0000-0000-0000-000000000003\","
                + "\"feedbackQuestion\":{\"questionDetails\":{\"shouldAllowRichText\":true,"
                + "\"questionType\":\"TEXT\",\"questionText\":\"test question text\"},"
                + "\"id\":\"00000000-0000-0000-0000-000000000004\","
                + "\"feedbackSession\":{\"id\":\"00000000-0000-0000-0000-000000000005\","
                + "\"course\":{\"id\":\"CS3281\",\"name\":\"course-name\",\"timeZone\":\"UTC\","
                + "\"institute\":\"teammates\"},\"name\":\"Session1\","
                + "\"creatorEmail\":\"test@teammates.tmt\",\"instructions\":\"<p>test-instructions</p>\","
                + "\"startTime\":\"2026-01-01T00:00:00Z\",\"endTime\":\"2026-01-07T00:00:00Z\","
                + "\"sessionVisibleFromTime\":\"2026-01-01T00:00:00Z\","
                + "\"resultsVisibleFromTime\":\"2026-01-07T00:00:00Z\",\"gracePeriod\":5,"
                + "\"isOpenedEmailEnabled\":false,\"isClosingSoonEmailEnabled\":false,"
                + "\"isPublishedEmailEnabled\":false,\"isOpeningSoonEmailSent\":false,"
                + "\"isOpenedEmailSent\":false,\"isClosingSoonEmailSent\":false,"
                + "\"isClosedEmailSent\":false,\"isPublishedEmailSent\":false},"
                + "\"questionNumber\":1,\"description\":\"test-description\","
                + "\"giverType\":\"SELF\",\"recipientType\":\"SELF\","
                + "\"numOfEntitiesToGiveFeedbackTo\":1,"
                + "\"showResponsesTo\":[],\"showGiverNameTo\":[],\"showRecipientNameTo\":[]},"
                + "\"giver\":\"giver@email.com\","
                + "\"giverSection\":{\"id\":\"00000000-0000-0000-0000-000000000006\","
                + "\"course\":{\"id\":\"course-id\",\"name\":\"course-name\",\"timeZone\":\"UTC\","
                + "\"institute\":\"teammates\"},\"name\":\"giverSection\"},"
                + "\"recipient\":\"recipient@email.com\","
                + "\"recipientSection\":{\"id\":\"00000000-0000-0000-0000-000000000007\","
                + "\"course\":{\"id\":\"course-id\",\"name\":\"course-name\",\"timeZone\":\"UTC\","
                + "\"institute\":\"teammates\"},\"name\":\"recipientSection\"}}",
                JsonUtils.toCompactJson(fr));
    }
}
