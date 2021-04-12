package teammates.storage.api;

import static teammates.common.util.FieldValidator.PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;
import org.testng.collections.Lists;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.ThreadHelper;
import teammates.test.AssertHelper;
import teammates.test.BaseComponentTestCase;

/**
 * SUT: {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbTest extends BaseComponentTestCase {
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();

    @Test
    public void testTimestamp() throws Exception {

        ______TS("success : created");

        FeedbackQuestionAttributes fq = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        deleteFeedbackQuestion(fq);

        fqDb.createEntity(fq);
        verifyPresentInDatastore(fq);

        String feedbackSessionName = fq.feedbackSessionName;
        String courseId = fq.courseId;
        int questionNumber = fq.questionNumber;

        FeedbackQuestionAttributes feedbackQuestion =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);

        // Assert dates are now.
        AssertHelper.assertInstantIsNow(feedbackQuestion.getCreatedAt());
        AssertHelper.assertInstantIsNow(feedbackQuestion.getUpdatedAt());

        ______TS("success : update lastUpdated");

        // wait for very briefly so that the update timestamp is guaranteed to change
        ThreadHelper.waitFor(5);

        feedbackQuestion.questionNumber++;
        fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(feedbackQuestion.getId())
                        .withQuestionNumber(feedbackQuestion.questionNumber)
                        .build());

        FeedbackQuestionAttributes updatedFq =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);

        // Assert lastUpdate has changed, and is now.
        assertFalse(feedbackQuestion.getUpdatedAt().equals(updatedFq.getUpdatedAt()));
        AssertHelper.assertInstantIsNow(updatedFq.getUpdatedAt());
    }

    @Test
    public void testDeleteFeedbackQuestion() throws Exception {

        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        FeedbackQuestionAttributes oldFqa =
                fqDb.getFeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(), fqa.getQuestionNumber());
        if (oldFqa != null) {
            fqDb.deleteFeedbackQuestion(oldFqa.getId());
        }
        fqDb.createEntity(fqa);
        fqa = fqDb.getFeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(), fqa.getQuestionNumber());

        ______TS("delete non-existent question");

        // should pass silently
        fqDb.deleteFeedbackQuestion("123");

        ______TS("standard success case");

        assertNotNull(fqDb.getFeedbackQuestion(fqa.getId()));

        fqDb.deleteFeedbackQuestion(fqa.getId());

        assertNull(fqDb.getFeedbackQuestion(fqa.getId()));

        ______TS("delete question again");

        // should pass silently
        fqDb.deleteFeedbackQuestion(fqa.getId());

        assertNull(fqDb.getFeedbackQuestion(fqa.getId()));
    }

    @Test
    public void testDeleteFeedbackQuestions_deleteByCourseIdAndSessionName() throws Exception {
        ______TS("standard success case");

        // create a new question in current session
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        FeedbackQuestionAttributes oldFqa =
                fqDb.getFeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(), fqa.getQuestionNumber());
        if (oldFqa != null) {
            fqDb.deleteFeedbackQuestion(oldFqa.getId());
        }
        fqDb.createEntity(fqa);
        fqa = fqDb.getFeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(), fqa.getQuestionNumber());
        assertNotNull(fqa);

        // create another question under another session
        FeedbackQuestionAttributes anotherFqa = getNewFeedbackQuestionAttributes();
        anotherFqa.feedbackSessionName = "Another Session";
        fqDb.createEntity(anotherFqa);
        anotherFqa = fqDb.getFeedbackQuestion(
                anotherFqa.getFeedbackSessionName(), anotherFqa.getCourseId(), anotherFqa.getQuestionNumber());
        assertNotNull(anotherFqa);

        fqDb.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId(fqa.getCourseId())
                .withFeedbackSessionName(fqa.getFeedbackSessionName())
                .build());

        // the question under current session is deleted
        assertNull(fqDb.getFeedbackQuestion(fqa.getId()));
        // the question under different session remain
        assertNotNull(fqDb.getFeedbackQuestion(anotherFqa.getId()));

        ______TS("non-existent course ID");

        fqDb.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId("not_exist")
                .withFeedbackSessionName(fqa.getFeedbackSessionName())
                .build());

        // no accident deletion
        assertNotNull(fqDb.getFeedbackQuestion(anotherFqa.getId()));

        ______TS("non-existent feedback session name");

        fqDb.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId(fqa.getCourseId())
                .withFeedbackSessionName("not_exist")
                .build());

        // no accident deletion
        assertNotNull(fqDb.getFeedbackQuestion(anotherFqa.getId()));

        ______TS("non-existent course ID and feedback session name");

        fqDb.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId("not_exist")
                .withFeedbackSessionName("not_exist")
                .build());

        // no accident deletion
        assertNotNull(fqDb.getFeedbackQuestion(anotherFqa.getId()));
    }

    @Test
    public void testDeleteFeedbackQuestions_deleteByCourseId() throws Exception {
        ______TS("standard success case");

        // create a new question in current course
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        FeedbackQuestionAttributes oldFqa =
                fqDb.getFeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(), fqa.getQuestionNumber());
        if (oldFqa != null) {
            fqDb.deleteFeedbackQuestion(oldFqa.getId());
        }
        fqDb.createEntity(fqa);
        fqa = fqDb.getFeedbackQuestion(fqa.getFeedbackSessionName(), fqa.getCourseId(), fqa.getQuestionNumber());
        assertNotNull(fqa);

        // create another question under another course
        FeedbackQuestionAttributes anotherFqa = getNewFeedbackQuestionAttributes();
        anotherFqa.courseId = "AnotherCourse";
        fqDb.createEntity(anotherFqa);
        anotherFqa = fqDb.getFeedbackQuestion(
                anotherFqa.getFeedbackSessionName(), anotherFqa.getCourseId(), anotherFqa.getQuestionNumber());
        assertNotNull(anotherFqa);

        fqDb.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId(fqa.getCourseId())
                .build());

        // the question under current course is deleted
        assertNull(fqDb.getFeedbackQuestion(fqa.getId()));
        // the question under different course remain
        assertNotNull(fqDb.getFeedbackQuestion(anotherFqa.getId()));

        ______TS("non-existent course ID");

        fqDb.deleteFeedbackQuestions(AttributesDeletionQuery.builder()
                .withCourseId("not_exist")
                .build());

        // no accident deletion
        assertNotNull(fqDb.getFeedbackQuestion(anotherFqa.getId()));
    }

    @Test
    public void testCreateDeleteFeedbackQuestion() throws Exception {

        ______TS("standard success case");

        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        deleteFeedbackQuestion(fqa);

        fqDb.createEntity(fqa);
        verifyPresentInDatastore(fqa);

        ______TS("duplicate - with same question number.");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class, () -> fqDb.createEntity(fqa));
        assertEquals(
                String.format(FeedbackQuestionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, fqa.toString()), eaee.getMessage());

        ______TS("null params");

        assertThrows(AssertionError.class, () -> fqDb.createEntity(null));

        ______TS("invalid params");

        fqa.courseId = "there is space";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class, () -> fqDb.createEntity(fqa));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.COURSE_ID_ERROR_MESSAGE, fqa.courseId,
                        FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getMessage());
    }

    @Test
    public void testGetFeedbackQuestions() throws Exception {
        FeedbackQuestionAttributes expected = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        deleteFeedbackQuestion(expected);

        fqDb.createEntity(expected);

        ______TS("standard success case");

        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(expected.feedbackSessionName,
                                                                     expected.courseId,
                                                                     expected.questionNumber);

        assertEquals(expected.toString(), actual.toString());

        ______TS("non-existant question");

        assertNull(fqDb.getFeedbackQuestion("Non-existant feedback session", "non-existent-course", 1));

        ______TS("null fsName");

        assertThrows(AssertionError.class,
                () -> fqDb.getFeedbackQuestion(null, expected.courseId, 1));

        ______TS("null courseId");

        assertThrows(AssertionError.class, () -> fqDb.getFeedbackQuestion(expected.feedbackSessionName, null, 1));

        ______TS("get by id");

        assertEquals(expected.toString(), actual.toString());

        ______TS("get non-existent question by id");

        actual = fqDb.getFeedbackQuestion("non-existent id");

        assertNull(actual);
    }

    @Test
    public void testGetFeedbackQuestionsForSession() throws Exception {

        ______TS("standard success case");

        int numToCreate = 3;

        List<FeedbackQuestionAttributes> expected = createFeedbackQuestions(numToCreate);

        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForSession(expected.get(0).feedbackSessionName, expected.get(0).courseId);

        for (int i = 0; i < numToCreate; i++) {
            expected.get(i).setId(questions.get(i).getId());
        }

        assertEquals(questions.size(), numToCreate);
        AssertHelper.assertSameContentIgnoreOrder(expected, questions);

        ______TS("null params");

        assertThrows(AssertionError.class,
                () -> fqDb.getFeedbackQuestionsForSession(null, expected.get(0).courseId));

        assertThrows(AssertionError.class,
                () -> fqDb.getFeedbackQuestionsForSession(expected.get(0).feedbackSessionName, null));

        ______TS("non-existent session");

        assertTrue(fqDb.getFeedbackQuestionsForSession("non-existent session", expected.get(0).courseId).isEmpty());

        ______TS("no questions in session");

        assertTrue(fqDb.getFeedbackQuestionsForSession("Empty session", expected.get(0).courseId).isEmpty());

        deleteFeedbackQuestions(numToCreate);
    }

    @Test
    public void testGetFeedbackQuestionsForGiverType() throws Exception {
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        deleteFeedbackQuestion(fqa);

        int[] numOfQuestions = createNewQuestionsForDifferentRecipientTypes();

        ______TS("standard success case");

        List<FeedbackQuestionAttributes> questions =
                fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId,
                                                      FeedbackParticipantType.INSTRUCTORS);
        assertEquals(questions.size(), numOfQuestions[0]);

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName,
                                                          fqa.courseId, FeedbackParticipantType.STUDENTS);
        assertEquals(questions.size(), numOfQuestions[1]);

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName,
                                                          fqa.courseId, FeedbackParticipantType.SELF);
        assertEquals(questions.size(), numOfQuestions[2]);

        questions = fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName,
                                                          fqa.courseId, FeedbackParticipantType.TEAMS);
        assertEquals(questions.size(), numOfQuestions[3]);

        ______TS("null params");

        assertThrows(AssertionError.class,
                () -> fqDb.getFeedbackQuestionsForGiverType(null, fqa.courseId, FeedbackParticipantType.STUDENTS));

        assertThrows(AssertionError.class,
                () -> fqDb.getFeedbackQuestionsForGiverType(
                        fqa.feedbackSessionName, null, FeedbackParticipantType.STUDENTS));

        assertThrows(AssertionError.class,
                () -> fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId, null));

        ______TS("non-existant session");

        assertTrue(fqDb.getFeedbackQuestionsForGiverType("non-existant session", fqa.courseId,
                                                         FeedbackParticipantType.STUDENTS).isEmpty());

        ______TS("no questions in session");

        assertTrue(fqDb.getFeedbackQuestionsForGiverType("Empty session", fqa.courseId,
                                                         FeedbackParticipantType.STUDENTS).isEmpty());

        deleteFeedbackQuestions(numOfQuestions[0] + numOfQuestions[1] + numOfQuestions[2] + numOfQuestions[3]);
    }

    @Test
    public void testUpdateFeedbackQuestion_noChangeToQuestion_shouldNotIssueSaveRequest() throws Exception {
        FeedbackQuestionAttributes typicalQuestion = getNewFeedbackQuestionAttributes();
        deleteFeedbackQuestion(typicalQuestion);
        typicalQuestion = fqDb.createEntity(typicalQuestion);

        FeedbackQuestionAttributes updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .build());

        assertEquals(typicalQuestion.getUpdatedAt(), updatedQuestion.getUpdatedAt());
        assertEquals(JsonUtils.toJson(typicalQuestion), JsonUtils.toJson(updatedQuestion));

        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withQuestionDetails(typicalQuestion.getQuestionDetails())
                        .withQuestionDescription(typicalQuestion.getQuestionDescription())
                        .withQuestionNumber(typicalQuestion.getQuestionNumber())
                        .withGiverType(typicalQuestion.getGiverType())
                        .withRecipientType(typicalQuestion.getRecipientType())
                        .withNumberOfEntitiesToGiveFeedbackTo(typicalQuestion.getNumberOfEntitiesToGiveFeedbackTo())
                        .withShowResponsesTo(typicalQuestion.getShowResponsesTo())
                        .withShowGiverNameTo(typicalQuestion.getShowGiverNameTo())
                        .withShowRecipientNameTo(typicalQuestion.getShowRecipientNameTo())
                        .build());

        assertEquals(typicalQuestion.getUpdatedAt(), updatedQuestion.getUpdatedAt());
        assertEquals(JsonUtils.toJson(typicalQuestion), JsonUtils.toJson(updatedQuestion));
    }

    @Test
    public void testUpdateFeedbackQuestion() throws Exception {

        ______TS("null params");

        assertThrows(AssertionError.class, () -> fqDb.updateFeedbackQuestion(null));

        ______TS("invalid feedback question attributes");

        FeedbackQuestionAttributes invalidFqa = getNewFeedbackQuestionAttributes();
        deleteFeedbackQuestion(invalidFqa);
        fqDb.createEntity(invalidFqa);
        invalidFqa.setId(fqDb.getFeedbackQuestion(invalidFqa.feedbackSessionName, invalidFqa.courseId,
                                                  invalidFqa.questionNumber).getId());

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> fqDb.updateFeedbackQuestion(
                        FeedbackQuestionAttributes.updateOptionsBuilder(invalidFqa.getId())
                                .withGiverType(FeedbackParticipantType.TEAMS) // invalid feedback path
                                .withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS)
                                .build()));
        AssertHelper.assertContains(
                String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                        "Giver's team members",
                        "Teams in this course"),
                ipe.getMessage());

        ______TS("feedback session does not exist");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> fqDb.updateFeedbackQuestion(
                        FeedbackQuestionAttributes.updateOptionsBuilder("non-existent")
                                .withQuestionDescription("test")
                                .build()));
        AssertHelper.assertContains(FeedbackQuestionsDb.ERROR_UPDATE_NON_EXISTENT, ednee.getLocalizedMessage());

        ______TS("standard success case");

        FeedbackQuestionAttributes modifiedQuestion = getNewFeedbackQuestionAttributes();
        deleteFeedbackQuestion(modifiedQuestion);
        fqDb.createEntity(modifiedQuestion);
        verifyPresentInDatastore(modifiedQuestion);

        modifiedQuestion = fqDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                                    modifiedQuestion.courseId,
                                                    modifiedQuestion.questionNumber);
        FeedbackQuestionDetails fqd = modifiedQuestion.getQuestionDetails();
        fqd.setQuestionText("New question text!");
        modifiedQuestion.setQuestionDetails(fqd);

        FeedbackQuestionAttributes updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(modifiedQuestion.getId())
                        .withQuestionDetails(fqd)
                        .build());

        verifyPresentInDatastore(modifiedQuestion);
        modifiedQuestion = fqDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                                    modifiedQuestion.courseId,
                                                    modifiedQuestion.questionNumber);
        assertEquals("New question text!", modifiedQuestion.getQuestionDetails().getQuestionText());
        assertEquals("New question text!", updatedQuestion.getQuestionDetails().getQuestionText());

        deleteFeedbackQuestion(modifiedQuestion);
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateFeedbackQuestion_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        FeedbackQuestionAttributes typicalQuestion = getNewFeedbackQuestionAttributes();
        deleteFeedbackQuestion(typicalQuestion);
        typicalQuestion = fqDb.createEntity(typicalQuestion);
        verifyPresentInDatastore(typicalQuestion);

        assertNotEquals("New question text!", typicalQuestion.getQuestionDetails().getQuestionText());
        FeedbackQuestionAttributes updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withQuestionDetails(new FeedbackTextQuestionDetails("New question text!"))
                        .build());
        FeedbackQuestionAttributes actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals("New question text!", actualQuestion.getQuestionDetails().getQuestionText());
        assertEquals("New question text!", updatedQuestion.getQuestionDetails().getQuestionText());

        assertNotEquals("testDescription", actualQuestion.getQuestionDescription());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withQuestionDescription("testDescription")
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals("testDescription", actualQuestion.getQuestionDescription());
        assertEquals("testDescription", updatedQuestion.getQuestionDescription());

        assertNotEquals(5, actualQuestion.getQuestionNumber());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withQuestionNumber(5)
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(5, actualQuestion.getQuestionNumber());
        assertEquals(5, updatedQuestion.getQuestionNumber());

        assertNotEquals(FeedbackParticipantType.STUDENTS, actualQuestion.getGiverType());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withGiverType(FeedbackParticipantType.STUDENTS)
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(FeedbackParticipantType.STUDENTS, actualQuestion.getGiverType());
        assertEquals(FeedbackParticipantType.STUDENTS, updatedQuestion.getGiverType());

        assertNotEquals(FeedbackParticipantType.STUDENTS, actualQuestion.getRecipientType());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withRecipientType(FeedbackParticipantType.STUDENTS)
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(FeedbackParticipantType.STUDENTS, actualQuestion.getRecipientType());
        assertEquals(FeedbackParticipantType.STUDENTS, updatedQuestion.getRecipientType());

        assertNotEquals(8, actualQuestion.getNumberOfEntitiesToGiveFeedbackTo());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withNumberOfEntitiesToGiveFeedbackTo(8)
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(8, actualQuestion.getNumberOfEntitiesToGiveFeedbackTo());
        assertEquals(8, updatedQuestion.getNumberOfEntitiesToGiveFeedbackTo());

        assertTrue(actualQuestion.getShowResponsesTo().isEmpty());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withShowResponsesTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualQuestion.getShowResponsesTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), updatedQuestion.getShowResponsesTo());

        assertTrue(actualQuestion.getShowGiverNameTo().isEmpty());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withShowGiverNameTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualQuestion.getShowGiverNameTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), updatedQuestion.getShowGiverNameTo());

        assertTrue(actualQuestion.getShowRecipientNameTo().isEmpty());
        updatedQuestion = fqDb.updateFeedbackQuestion(
                FeedbackQuestionAttributes.updateOptionsBuilder(typicalQuestion.getId())
                        .withShowRecipientNameTo(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS))
                        .build());
        actualQuestion = fqDb.getFeedbackQuestion(typicalQuestion.getId());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), actualQuestion.getShowRecipientNameTo());
        assertEquals(Lists.newArrayList(FeedbackParticipantType.INSTRUCTORS), updatedQuestion.getShowRecipientNameTo());

        deleteFeedbackQuestion(typicalQuestion);
    }

    private FeedbackQuestionAttributes getNewFeedbackQuestionAttributes() {
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");

        return FeedbackQuestionAttributes.builder()
                .withCourseId("testCourse")
                .withFeedbackSessionName("testFeedbackSession")
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.SELF)
                .withNumberOfEntitiesToGiveFeedbackTo(1)
                .withQuestionNumber(1)
                .withQuestionDetails(questionDetails)
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .withShowResponsesTo(new ArrayList<>())
                .build();
    }

    private List<FeedbackQuestionAttributes> createFeedbackQuestions(int num) throws Exception {
        FeedbackQuestionAttributes fqa;
        List<FeedbackQuestionAttributes> returnVal = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = i;

            // remove possibly conflicting entity from the database
            deleteFeedbackQuestion(fqa);

            fqDb.createEntity(fqa);
            returnVal.add(fqa);
        }

        return returnVal;
    }

    private int[] createNewQuestionsForDifferentRecipientTypes() throws Exception {

        int[] numberOfQuestionsToCreate = new int[] {
                2,
                3,
                1,
                2,
        };

        FeedbackQuestionAttributes fqa;

        for (int i = 1; i <= numberOfQuestionsToCreate[0]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = i;
            fqa.giverType = FeedbackParticipantType.INSTRUCTORS;
            fqDb.createEntity(fqa);
        }

        for (int i = 1; i <= numberOfQuestionsToCreate[1]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = numberOfQuestionsToCreate[0] + i;
            fqa.giverType = FeedbackParticipantType.STUDENTS;
            fqDb.createEntity(fqa);
        }

        for (int i = 1; i <= numberOfQuestionsToCreate[2]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.giverType = FeedbackParticipantType.SELF;
            fqa.questionNumber = numberOfQuestionsToCreate[0] + numberOfQuestionsToCreate[1] + i;
            fqDb.createEntity(fqa);
        }

        for (int i = 1; i <= numberOfQuestionsToCreate[3]; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.giverType = FeedbackParticipantType.TEAMS;
            fqa.questionNumber = numberOfQuestionsToCreate[0] + numberOfQuestionsToCreate[1]
                                 + numberOfQuestionsToCreate[2] + i;
            fqDb.createEntity(fqa);
        }

        return numberOfQuestionsToCreate;
    }

    private void deleteFeedbackQuestions(int numToDelete) {
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        for (int i = 1; i <= numToDelete; i++) {
            fqa.questionNumber = i;
            deleteFeedbackQuestion(fqa);
        }
    }

    private void deleteFeedbackQuestion(FeedbackQuestionAttributes attributes) {
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion(
                attributes.getFeedbackSessionName(), attributes.getCourseId(), attributes.getQuestionNumber());
        if (fq != null) {
            fqDb.deleteFeedbackQuestion(fq.getId());
        }
    }

}
