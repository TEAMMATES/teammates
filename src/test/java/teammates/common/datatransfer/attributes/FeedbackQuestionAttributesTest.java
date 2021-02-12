package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackQuestion;

/**
 * SUT: {@link FeedbackQuestionAttributes}.
 */
public class FeedbackQuestionAttributesTest extends BaseAttributesTest {

    private DataBundle typicalBundle = getTypicalDataBundle();

    @Override
    @Test
    public void testToEntity() {
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        FeedbackQuestion expectedQuestion = new FeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(),
                fqa.getSerializedQuestionDetails(), fqa.getQuestionDescription(), fqa.getQuestionNumber(),
                fqa.getQuestionType(), fqa.getGiverType(), fqa.getRecipientType(), fqa.getNumberOfEntitiesToGiveFeedbackTo(),
                fqa.getShowResponsesTo(), fqa.showGiverNameTo, fqa.showRecipientNameTo);

        FeedbackQuestion actualQuestion = fqa.toEntity();

        assertEquals(expectedQuestion.getFeedbackSessionName(), actualQuestion.getFeedbackSessionName());
        assertEquals(expectedQuestion.getCourseId(), actualQuestion.getCourseId());
        assertEquals(expectedQuestion.getQuestionDescription(), actualQuestion.getQuestionDescription());
        assertEquals(expectedQuestion.getQuestionNumber(), actualQuestion.getQuestionNumber());
        assertEquals(expectedQuestion.getQuestionType(), actualQuestion.getQuestionType());
        assertEquals(expectedQuestion.getNumberOfEntitiesToGiveFeedbackTo(),
                actualQuestion.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(expectedQuestion.getQuestionMetaData(), actualQuestion.getQuestionMetaData());
        assertEquals(expectedQuestion.getGiverType(), actualQuestion.getGiverType());
        assertEquals(expectedQuestion.getRecipientType(), actualQuestion.getRecipientType());
        assertEquals(expectedQuestion.getShowGiverNameTo(), actualQuestion.getShowGiverNameTo());
        assertEquals(expectedQuestion.getShowRecipientNameTo(), actualQuestion.getShowRecipientNameTo());
        assertEquals(expectedQuestion.getShowResponsesTo(), actualQuestion.getShowResponsesTo());
        assertNotNull(actualQuestion.getCreatedAt());
        assertNotNull(actualQuestion.getUpdatedAt());
    }

    @Test
    public void testValueOf_withAllFieldPopulatedFeedbackQuestion_shouldGenerateAttributesCorrectly() {
        FeedbackQuestion qn = new FeedbackQuestion("session", "course",
                "text", "description", 1, FeedbackQuestionType.TEXT,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS, Const.MAX_POSSIBLE_RECIPIENTS,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        qn.setFeedbackQuestionId(1L);

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.valueOf(qn);

        assertEquals(qn.getId(), feedbackQuestionAttributes.getId());
        assertEquals(qn.getFeedbackSessionName(), feedbackQuestionAttributes.getFeedbackSessionName());
        assertEquals(qn.getCourseId(), feedbackQuestionAttributes.getCourseId());
        assertEquals(qn.getQuestionMetaData(), feedbackQuestionAttributes.getQuestionDetails().getQuestionText());
        assertEquals(qn.getQuestionDescription(), feedbackQuestionAttributes.getQuestionDescription());
        assertEquals(feedbackQuestionAttributes.getQuestionNumber(), qn.getQuestionNumber());
        assertEquals(qn.getQuestionType(), feedbackQuestionAttributes.getQuestionType());
        assertEquals(qn.getGiverType(), feedbackQuestionAttributes.getGiverType());
        assertEquals(qn.getRecipientType(), feedbackQuestionAttributes.getRecipientType());
        assertEquals(qn.getNumberOfEntitiesToGiveFeedbackTo(),
                feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo());

        assertEquals(new ArrayList<>(), feedbackQuestionAttributes.getShowGiverNameTo());
        assertEquals(new ArrayList<>(), feedbackQuestionAttributes.getShowRecipientNameTo());
        assertEquals(new ArrayList<>(), feedbackQuestionAttributes.getShowResponsesTo());

        assertEquals(qn.getCreatedAt(), feedbackQuestionAttributes.getCreatedAt());
        assertEquals(qn.getUpdatedAt(), feedbackQuestionAttributes.getUpdatedAt());
    }

    @Test
    public void testValueOf_withSomeFieldsPopulatedAsNull_shouldUseDefaultValues() {
        FeedbackQuestion qn = new FeedbackQuestion("session", "course",
                "text", "description", 1, FeedbackQuestionType.TEXT,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS, Const.MAX_POSSIBLE_RECIPIENTS,
                null, null, null);
        qn.setFeedbackQuestionId(1L);
        qn.setCreatedAt(null);
        qn.setLastUpdate(null);
        qn.setShowGiverNameTo(null);
        qn.setShowRecipientNameTo(null);
        qn.setShowResponsesTo(null);
        assertNull(qn.getShowGiverNameTo());
        assertNull(qn.getShowRecipientNameTo());
        assertNull(qn.getShowResponsesTo());

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.valueOf(qn);

        assertEquals(qn.getId(), feedbackQuestionAttributes.getId());
        assertEquals(qn.getFeedbackSessionName(), feedbackQuestionAttributes.getFeedbackSessionName());
        assertEquals(qn.getCourseId(), feedbackQuestionAttributes.getCourseId());
        assertEquals(qn.getQuestionMetaData(), feedbackQuestionAttributes.getQuestionDetails().getQuestionText());
        assertEquals(qn.getQuestionDescription(), feedbackQuestionAttributes.getQuestionDescription());
        assertEquals(feedbackQuestionAttributes.getQuestionNumber(), qn.getQuestionNumber());
        assertEquals(qn.getQuestionType(), feedbackQuestionAttributes.getQuestionType());
        assertEquals(qn.getGiverType(), feedbackQuestionAttributes.getGiverType());
        assertEquals(qn.getRecipientType(), feedbackQuestionAttributes.getRecipientType());
        assertEquals(qn.getNumberOfEntitiesToGiveFeedbackTo(),
                feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo());

        assertEquals(new ArrayList<>(), feedbackQuestionAttributes.getShowGiverNameTo());
        assertEquals(new ArrayList<>(), feedbackQuestionAttributes.getShowRecipientNameTo());
        assertEquals(new ArrayList<>(), feedbackQuestionAttributes.getShowResponsesTo());

        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, feedbackQuestionAttributes.getCreatedAt());
        assertEquals(Const.TIME_REPRESENTS_DEFAULT_TIMESTAMP, feedbackQuestionAttributes.getUpdatedAt());
    }

    @Test
    public void testValueOf_modificationInAttributes_shouldNotLeakStateToEntity() {
        FeedbackQuestion qn = new FeedbackQuestion("session", "course",
                "text", "description", 1, FeedbackQuestionType.TEXT,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS, Const.MAX_POSSIBLE_RECIPIENTS,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        qn.setFeedbackQuestionId(1L);

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.valueOf(qn);

        feedbackQuestionAttributes.getShowResponsesTo().add(FeedbackParticipantType.STUDENTS);
        feedbackQuestionAttributes.getShowGiverNameTo().add(FeedbackParticipantType.STUDENTS);
        feedbackQuestionAttributes.getShowRecipientNameTo().add(FeedbackParticipantType.STUDENTS);

        assertTrue(qn.getShowResponsesTo().isEmpty());
        assertTrue(qn.getShowGiverNameTo().isEmpty());
        assertTrue(qn.getShowRecipientNameTo().isEmpty());
    }

    @Test
    public void testBuilder_withIrrelevantFeedbackParticipant_shouldGenerateAttributesCorrectly() {
        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withCourseId("testingCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.SELF)
                .withNumberOfEntitiesToGiveFeedbackTo(1)
                .withQuestionNumber(1)
                .withQuestionDetails(new FeedbackTextQuestionDetails("text"))
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponsesTo(new ArrayList<>(participants))
                .build();

        List<FeedbackParticipantType> participantTypesAfterRemovingIrrelevantVisibilitiesOptions =
                new ArrayList<>(participants);
        participantTypesAfterRemovingIrrelevantVisibilitiesOptions.remove(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participantTypesAfterRemovingIrrelevantVisibilitiesOptions.remove(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);

        // .build() will remove irrelevant visibilities options, so the lists showResponsesTo,
        // showGiverNameTo, and showRecipientNameTo are not the same as the ones in qn
        assertEquals(participantTypesAfterRemovingIrrelevantVisibilitiesOptions,
                feedbackQuestionAttributes.getShowGiverNameTo());
        assertEquals(participantTypesAfterRemovingIrrelevantVisibilitiesOptions,
                feedbackQuestionAttributes.getShowRecipientNameTo());
        assertEquals(participantTypesAfterRemovingIrrelevantVisibilitiesOptions,
                feedbackQuestionAttributes.getShowResponsesTo());
    }

    @Test
    public void testValueOf_textQuestions_shouldDeserializeCorrectly() throws InvalidParametersException {
        ______TS("legacy data: plain text: single word, should deserialize correctly");
        FeedbackQuestion qn = new FeedbackQuestion("session", "course",
                "singleWord", "description", 1, FeedbackQuestionType.TEXT,
                FeedbackParticipantType.STUDENTS, FeedbackParticipantType.STUDENTS, Const.MAX_POSSIBLE_RECIPIENTS,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        qn.setFeedbackQuestionId(1L);

        FeedbackQuestionAttributes fqa = FeedbackQuestionAttributes.valueOf(qn);
        assertEquals("singleWord", fqa.questionDetails.getQuestionText());
        assertNull(((FeedbackTextQuestionDetails) fqa.questionDetails).getRecommendedLength());

        ______TS("legacy data: plain text: multiple words, should deserialize correctly");
        qn.setQuestionText("multiple words text");

        FeedbackQuestionAttributes fqaMulti = FeedbackQuestionAttributes.valueOf(qn);
        assertEquals("multiple words text", fqaMulti.questionDetails.getQuestionText());
        assertNull(((FeedbackTextQuestionDetails) fqaMulti.questionDetails).getRecommendedLength());

        ______TS("json text: should deserialize as json");
        String jsonQuestionText = "{\n"
                + "  \"recommendedLength\": 70,\n"
                + "  \"questionType\": \"TEXT\",\n"
                + "  \"questionText\": \"normal question\"\n"
                + "}";
        qn.setQuestionText(jsonQuestionText);
        FeedbackQuestionAttributes fqaJson = FeedbackQuestionAttributes.valueOf(qn);
        assertEquals("normal question", fqaJson.questionDetails.getQuestionText());
        assertEquals(70, ((FeedbackTextQuestionDetails) fqaJson.questionDetails).getRecommendedLength().intValue());
    }

    @Test
    public void testBuilder_withNullArguments_shouldThrowException() {
        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withFeedbackSessionName(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withCourseId(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withQuestionDetails(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withGiverType(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withRecipientType(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withShowGiverNameTo(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            List<FeedbackParticipantType> participants = new ArrayList<>();
            participants.add(null);
            FeedbackQuestionAttributes.builder()
                    .withShowGiverNameTo(participants)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withShowRecipientNameTo(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            List<FeedbackParticipantType> participants = new ArrayList<>();
            participants.add(null);
            FeedbackQuestionAttributes.builder()
                    .withShowRecipientNameTo(participants)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            FeedbackQuestionAttributes.builder()
                    .withShowResponsesTo(null)
                    .build();
        });

        assertThrows(AssertionError.class, () -> {
            List<FeedbackParticipantType> participants = new ArrayList<>();
            participants.add(null);
            FeedbackQuestionAttributes.builder()
                    .withShowResponsesTo(participants)
                    .build();
        });
    }

    @Test
    public void testBuilder_withDescriptionNull_shouldBuildCorrectAttributes() {
        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withQuestionDescription(null)
                .build();

        assertNull(feedbackQuestionAttributes.getQuestionDescription());
    }

    @Test
    public void testBuilder_withTypicalData_shouldBuildCorrectAttributes() {
        String feedbackSession = "test session";
        String courseId = "some course";
        FeedbackQuestionDetails questionDetails = new FeedbackTextQuestionDetails("test qn from teams->none.");
        String questionDescription = "some description";
        int questionNumber = 1;
        int numOfEntities = 4;
        FeedbackQuestionType questionType = FeedbackQuestionType.TEXT;
        FeedbackParticipantType giverType = FeedbackParticipantType.TEAMS;
        FeedbackParticipantType recipientType = FeedbackParticipantType.TEAMS;

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.RECEIVER);

        FeedbackQuestionAttributes feedbackQuestionAttributes = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName(feedbackSession)
                .withCourseId(courseId)
                .withQuestionDetails(questionDetails)
                .withQuestionDescription(questionDescription)
                .withQuestionNumber(questionNumber)
                .withNumberOfEntitiesToGiveFeedbackTo(numOfEntities)
                .withGiverType(giverType)
                .withRecipientType(recipientType)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponsesTo(new ArrayList<>(participants))
                .build();

        assertEquals(feedbackSession, feedbackQuestionAttributes.getFeedbackSessionName());
        assertEquals(courseId, feedbackQuestionAttributes.getCourseId());
        assertEquals(JsonUtils.toJson(questionDetails), JsonUtils.toJson(feedbackQuestionAttributes.getQuestionDetails()));
        assertEquals(questionDescription, feedbackQuestionAttributes.getQuestionDescription());
        assertEquals(questionNumber, feedbackQuestionAttributes.getQuestionNumber());
        assertEquals(numOfEntities, feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(questionType, feedbackQuestionAttributes.getQuestionType());
        assertEquals(giverType, feedbackQuestionAttributes.getGiverType());
        assertEquals(recipientType, feedbackQuestionAttributes.getRecipientType());
        assertEquals(participants, feedbackQuestionAttributes.getShowGiverNameTo());
        assertEquals(participants, feedbackQuestionAttributes.getShowResponsesTo());
        assertEquals(participants, feedbackQuestionAttributes.getShowRecipientNameTo());

        assertNull(feedbackQuestionAttributes.getCreatedAt());
        assertNull(feedbackQuestionAttributes.getUpdatedAt());
    }

    @Test
    public void testBuilder_buildNothing_shouldUseDefaultValues() {
        FeedbackQuestionAttributes observedFeedbackQuestionAttributes =
                FeedbackQuestionAttributes.builder().build();

        assertEquals(0, observedFeedbackQuestionAttributes.getQuestionNumber());
        assertNull(observedFeedbackQuestionAttributes.getRecipientType());
        assertNull(observedFeedbackQuestionAttributes.getGiverType());
        assertNull(observedFeedbackQuestionAttributes.getCourseId());
        assertNull(observedFeedbackQuestionAttributes.getFeedbackSessionName());
        assertEquals(new ArrayList<>(), observedFeedbackQuestionAttributes.getShowResponsesTo());
        assertEquals(new ArrayList<>(), observedFeedbackQuestionAttributes.getShowGiverNameTo());
        assertEquals(new ArrayList<>(), observedFeedbackQuestionAttributes.getShowRecipientNameTo());
        assertNull(observedFeedbackQuestionAttributes.getQuestionDescription());
        assertNull(observedFeedbackQuestionAttributes.questionDetails);
        assertEquals(0, observedFeedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo());
        assertNull(observedFeedbackQuestionAttributes.getCreatedAt());
        assertNull(observedFeedbackQuestionAttributes.getUpdatedAt());
    }

    @Test
    public void testValidate() {

        List<FeedbackParticipantType> showGiverNameToList = new ArrayList<>();
        showGiverNameToList.add(FeedbackParticipantType.SELF);
        showGiverNameToList.add(FeedbackParticipantType.STUDENTS);

        List<FeedbackParticipantType> showRecipientNameToList = new ArrayList<>();
        showRecipientNameToList.add(FeedbackParticipantType.SELF);
        showRecipientNameToList.add(FeedbackParticipantType.STUDENTS);

        List<FeedbackParticipantType> showResponseToList = new ArrayList<>();
        showResponseToList.add(FeedbackParticipantType.NONE);
        showResponseToList.add(FeedbackParticipantType.SELF);

        FeedbackQuestionAttributes fq = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("")
                .withCourseId("")
                .withGiverType(FeedbackParticipantType.NONE)
                .withRecipientType(FeedbackParticipantType.RECEIVER)
                .withShowGiverNameTo(new ArrayList<>(showGiverNameToList))
                .withShowRecipientNameTo(new ArrayList<>(showRecipientNameToList))
                .withShowResponsesTo(new ArrayList<>(showResponseToList))
                .build();

        assertFalse(fq.isValid());

        String errorMessage = getPopulatedEmptyStringErrorMessage(
                                  FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING_FOR_SESSION_NAME,
                                  FieldValidator.FEEDBACK_SESSION_NAME_FIELD_NAME,
                                  FieldValidator.FEEDBACK_SESSION_NAME_MAX_LENGTH) + System.lineSeparator()
                              + getPopulatedEmptyStringErrorMessage(
                                    FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH)
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.giverType.toString(),
                                              FieldValidator.GIVER_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.recipientType.toString(),
                                              FieldValidator.RECIPIENT_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showGiverNameTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + "Trying to show giver name to STUDENTS without showing response first."
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showRecipientNameTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + "Trying to show recipient name to STUDENTS without showing response first."
                              + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showResponsesTo.get(0).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                              + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                              fq.showResponsesTo.get(1).toString(),
                                              FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.feedbackSessionName = "First Feedback Session";
        fq.courseId = "CS1101";
        fq.giverType = FeedbackParticipantType.TEAMS;
        fq.recipientType = FeedbackParticipantType.OWN_TEAM;

        assertFalse(fq.isValid());

        errorMessage = String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                     "Giver's team",
                                     "Teams in this course") + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show giver name to STUDENTS without showing response first." + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                       fq.showRecipientNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show recipient name to STUDENTS without showing response first."
                       + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        assertFalse(fq.isValid());

        errorMessage = String.format(FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                                     "Giver's team members",
                                     "Teams in this course") + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showGiverNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show giver name to STUDENTS without showing response first." + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE,
                                       fq.showRecipientNameTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + "Trying to show recipient name to STUDENTS without showing response first."
                       + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(0).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME) + System.lineSeparator()
                       + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, fq.showResponsesTo.get(1).toString(),
                                       FieldValidator.VIEWER_TYPE_NAME);

        assertEquals(errorMessage, StringHelper.toString(fq.getInvalidityInfo()));

        fq.recipientType = FeedbackParticipantType.TEAMS;

        fq.showGiverNameTo = new ArrayList<>();
        fq.showGiverNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showRecipientNameTo = new ArrayList<>();
        fq.showRecipientNameTo.add(FeedbackParticipantType.RECEIVER);

        fq.showResponsesTo = new ArrayList<>();
        fq.showResponsesTo.add(FeedbackParticipantType.RECEIVER);

        assertTrue(fq.isValid());
    }

    @Test
    public void testGetQuestionDetails() {

        ______TS("Text question: new Json format");

        FeedbackQuestionAttributes fq = typicalBundle.feedbackQuestions.get("qn5InSession1InCourse1");
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("New format text question");
        fq.setQuestionDetails(questionDetails);

        assertTrue(fq.isValid());
        assertEquals(fq.getQuestionDetails().getQuestionText(), "New format text question");

        ______TS("Text question: old string format");

        fq = typicalBundle.feedbackQuestions.get("qn2InSession1InCourse1");
        assertEquals(fq.getQuestionDetails().getQuestionText(), "Rate 1 other student's product");
    }

    @Test
    public void testRemoveIrrelevantVisibilityOptions() {

        ______TS("test teams->none");

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        List<FeedbackParticipantType> participantsForShowResponseTo = new ArrayList<>(participants);
        participantsForShowResponseTo.add(FeedbackParticipantType.STUDENTS);

        FeedbackQuestionAttributes question = FeedbackQuestionAttributes.builder()
                .withFeedbackSessionName("test session")
                .withCourseId("some course")
                .withQuestionDetails(new FeedbackTextQuestionDetails("test qn from teams->none."))
                .withQuestionNumber(1)
                .withGiverType(FeedbackParticipantType.TEAMS)
                .withRecipientType(FeedbackParticipantType.NONE)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponsesTo(new ArrayList<>(participantsForShowResponseTo))
                .withNumberOfEntitiesToGiveFeedbackTo(Const.MAX_POSSIBLE_RECIPIENTS)
                .build();

        assertTrue(question.showGiverNameTo.isEmpty());
        assertTrue(question.showRecipientNameTo.isEmpty());
        // check that other types are not removed
        assertTrue(question.showResponsesTo.contains(FeedbackParticipantType.STUDENTS));
        assertEquals(question.showResponsesTo.size(), 1);

        ______TS("test students->teams");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.TEAMS;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 2);
        assertEquals(question.showRecipientNameTo.size(), 3);
        assertEquals(question.showResponsesTo.size(), 3);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->team members including giver");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF;

        participants.clear();
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        question.showGiverNameTo = new ArrayList<>(participants);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 3);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF));

        ______TS("test students->instructors");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.INSTRUCTORS;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->own team");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));

        ______TS("test students->own team members");

        question.giverType = FeedbackParticipantType.STUDENTS;
        question.recipientType = FeedbackParticipantType.OWN_TEAM_MEMBERS;

        participants.clear();
        participants.add(FeedbackParticipantType.RECEIVER);
        participants.add(FeedbackParticipantType.INSTRUCTORS);
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.STUDENTS);
        question.showGiverNameTo = new ArrayList<>(participants);
        question.showRecipientNameTo = new ArrayList<>(participants);
        question.showResponsesTo = new ArrayList<>(participants);

        question.removeIrrelevantVisibilityOptions();

        assertEquals(question.showGiverNameTo.size(), 4);
        assertEquals(question.showRecipientNameTo.size(), 4);
        assertEquals(question.showResponsesTo.size(), 4);
        assertFalse(question.showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showRecipientNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
        assertFalse(question.showResponsesTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS));
    }

    @Test
    public void testGetQuestionDetails_shouldDoDeepCopy() {
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        FeedbackQuestionDetails details = fqa.getQuestionDetails();
        fqa.questionDetails.setQuestionText("updated question");

        assertEquals("Question text.", details.getQuestionText());
        assertEquals("updated question", fqa.questionDetails.getQuestionText());
    }

    @Test
    public void testSetQuestionDetails_shouldDoDeepCopy() {
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        FeedbackQuestionDetails details = new FeedbackTextQuestionDetails("my question");
        fqa.setQuestionDetails(details);
        details.setQuestionText("updated question");

        assertEquals("updated question", details.getQuestionText());
        assertEquals("my question", fqa.questionDetails.getQuestionText());
    }

    @Test
    public void testUpdateOptions_withTypicalUpdateOptions_shouldUpdateAttributeCorrectly() {
        FeedbackQuestionAttributes.UpdateOptions updateOptions =
                FeedbackQuestionAttributes.updateOptionsBuilder("questionId")
                        .withQuestionDetails(new FeedbackTextQuestionDetails("question text"))
                        .withQuestionDescription("description")
                        .withQuestionNumber(2)
                        .withGiverType(FeedbackParticipantType.STUDENTS)
                        .withRecipientType(FeedbackParticipantType.INSTRUCTORS)
                        .withNumberOfEntitiesToGiveFeedbackTo(2)
                        .withShowResponsesTo(Lists.newArrayList(
                                FeedbackParticipantType.INSTRUCTORS, FeedbackParticipantType.RECEIVER_TEAM_MEMBERS))
                        .withShowGiverNameTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .withShowRecipientNameTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .build();

        assertEquals("questionId", updateOptions.getFeedbackQuestionId());

        FeedbackQuestionAttributes questionAttributes =
                FeedbackQuestionAttributes.builder()
                        .withCourseId("courseId")
                        .withFeedbackSessionName("session")
                        .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withRecipientType(FeedbackParticipantType.SELF)
                        .withNumberOfEntitiesToGiveFeedbackTo(3)
                        .withQuestionNumber(1)
                        .withQuestionDetails(new FeedbackTextQuestionDetails("question text 2"))
                        .withShowGiverNameTo(new ArrayList<>())
                        .withShowRecipientNameTo(new ArrayList<>())
                        .withShowResponsesTo(new ArrayList<>())
                        .build();

        questionAttributes.update(updateOptions);

        assertEquals("courseId", questionAttributes.getCourseId());
        assertEquals("session", questionAttributes.getFeedbackSessionName());
        assertEquals(FeedbackQuestionType.TEXT, questionAttributes.getQuestionType());
        assertEquals("question text", questionAttributes.getQuestionDetails().getQuestionText());
        assertEquals("description", questionAttributes.getQuestionDescription());
        assertEquals(2, questionAttributes.getQuestionNumber());
        assertEquals(FeedbackParticipantType.STUDENTS, questionAttributes.getGiverType());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, questionAttributes.getRecipientType());
        assertEquals(2, questionAttributes.getNumberOfEntitiesToGiveFeedbackTo());
        // RECEIVER_TEAM_MEMBERS is removed as it is irrelevant visibility
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), questionAttributes.getShowResponsesTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), questionAttributes.getShowGiverNameTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), questionAttributes.getShowRecipientNameTo());
    }

    @Test
    public void testUpdateOptionsBuilder_withNullDescriptionInput_shouldUpdateAttributeCorrectly() {
        FeedbackQuestionAttributes.UpdateOptions updateOptions =
                FeedbackQuestionAttributes.updateOptionsBuilder("questionId")
                        .withQuestionDescription(null)
                        .build();

        FeedbackQuestionAttributes questionAttributes =
                FeedbackQuestionAttributes.builder()
                        .withCourseId("courseId")
                        .withFeedbackSessionName("session")
                        .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                        .withRecipientType(FeedbackParticipantType.SELF)
                        .withNumberOfEntitiesToGiveFeedbackTo(3)
                        .withQuestionNumber(1)
                        .withQuestionDetails(new FeedbackTextQuestionDetails("question text"))
                        .withShowGiverNameTo(new ArrayList<>())
                        .withShowRecipientNameTo(new ArrayList<>())
                        .withShowResponsesTo(new ArrayList<>())
                        .build();

        questionAttributes.update(updateOptions);

        assertNull(questionAttributes.getQuestionDescription());
        assertEquals("courseId", questionAttributes.getCourseId());
        assertEquals("session", questionAttributes.getFeedbackSessionName());
        assertEquals(FeedbackQuestionType.TEXT, questionAttributes.getQuestionType());
        assertEquals("question text", questionAttributes.getQuestionDetails().getQuestionText());
        assertEquals(1, questionAttributes.getQuestionNumber());
        assertEquals(FeedbackParticipantType.INSTRUCTORS, questionAttributes.getGiverType());
        assertEquals(FeedbackParticipantType.SELF, questionAttributes.getRecipientType());
        assertEquals(3, questionAttributes.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(Lists.newArrayList(), questionAttributes.getShowResponsesTo());
        assertEquals(Lists.newArrayList(), questionAttributes.getShowGiverNameTo());
        assertEquals(Lists.newArrayList(), questionAttributes.getShowRecipientNameTo());

    }

    @Test
    public void testUpdateOptionsBuilder_withNullInput_shouldFailWithAssertionError() {
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder(null));
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder("id")
                        .withQuestionDetails(null));
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder("id")
                        .withGiverType(null));
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder("id")
                        .withRecipientType(null));
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder("id")
                        .withShowResponsesTo(null));
        assertThrows(AssertionError.class, () -> {
            List<FeedbackParticipantType> participants = new ArrayList<>();
            participants.add(null);
            FeedbackQuestionAttributes.updateOptionsBuilder("id")
                    .withShowResponsesTo(participants)
                    .build();
        });
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder("id")
                        .withShowGiverNameTo(null));
        assertThrows(AssertionError.class, () -> {
            List<FeedbackParticipantType> participants = new ArrayList<>();
            participants.add(null);
            FeedbackQuestionAttributes.updateOptionsBuilder("id")
                    .withShowGiverNameTo(participants)
                    .build();
        });
        assertThrows(AssertionError.class, () ->
                FeedbackQuestionAttributes.updateOptionsBuilder("id")
                        .withShowRecipientNameTo(null));
        assertThrows(AssertionError.class, () -> {
            List<FeedbackParticipantType> participants = new ArrayList<>();
            participants.add(null);
            FeedbackQuestionAttributes.updateOptionsBuilder("id")
                    .withShowRecipientNameTo(participants)
                    .build();
        });
    }

    @Test
    public void testEquals() {
        FeedbackQuestionAttributes feedbackQuestion = getNewFeedbackQuestionAttributes();

        // When the two feedback questions are copies of each other
        FeedbackQuestionAttributes feedbackQuestionCopy = getNewFeedbackQuestionAttributes();

        assertTrue(feedbackQuestion.equals(feedbackQuestionCopy));

        // When the two feedback questions have same values but created at different time
        FeedbackQuestionAttributes feedbackQuestionSimilar = getNewFeedbackQuestionAttributes();

        assertTrue(feedbackQuestion.equals(feedbackQuestionSimilar));

        // When the two feedback questions are different
        FeedbackQuestionAttributes feedbackQuestionDifferent = FeedbackQuestionAttributes.builder()
                .withCourseId("differentCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .build();

        assertFalse(feedbackQuestion.equals(feedbackQuestionDifferent));

        // When the other object is of different class
        assertFalse(feedbackQuestion.equals(3));
    }

    @Test
    public void testHashCode() {
        FeedbackQuestionAttributes feedbackQuestion = getNewFeedbackQuestionAttributes();

        // When the two feedback questions are copies of each other, they should have the same hash code
        FeedbackQuestionAttributes feedbackQuestionCopy = getNewFeedbackQuestionAttributes();

        assertTrue(feedbackQuestion.hashCode() == feedbackQuestionCopy.hashCode());

        // When the two feedback questions have same values but created at different time,
        // they should still have the same hash code
        FeedbackQuestionAttributes feedbackQuestionSimilar = getNewFeedbackQuestionAttributes();

        assertTrue(feedbackQuestion.hashCode() == feedbackQuestionSimilar.hashCode());

        // When the two feedback questions are different, they should have different hash code
        FeedbackQuestionAttributes feedbackQuestionDifferent = FeedbackQuestionAttributes.builder()
                .withCourseId("differentCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .build();

        assertFalse(feedbackQuestion.hashCode() == feedbackQuestionDifferent.hashCode());
    }

    private FeedbackQuestionAttributes getNewFeedbackQuestionAttributes() {
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");

        List<FeedbackParticipantType> participants = new ArrayList<>();
        participants.add(FeedbackParticipantType.OWN_TEAM_MEMBERS);
        participants.add(FeedbackParticipantType.RECEIVER);

        return FeedbackQuestionAttributes.builder()
                .withCourseId("testingCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.SELF)
                .withNumberOfEntitiesToGiveFeedbackTo(1)
                .withQuestionNumber(1)
                .withQuestionDetails(questionDetails)
                .withShowGiverNameTo(new ArrayList<>(participants))
                .withShowRecipientNameTo(new ArrayList<>(participants))
                .withShowResponsesTo(new ArrayList<>(participants))
                .build();
    }

}
