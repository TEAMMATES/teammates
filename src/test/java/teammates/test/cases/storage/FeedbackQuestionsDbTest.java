package teammates.test.cases.storage;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackQuestionsDb}.
 */
public class FeedbackQuestionsDbTest extends BaseComponentTestCase {
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();

    @Test
    public void testTimestamp() throws InvalidParametersException, EntityAlreadyExistsException,
                                       EntityDoesNotExistException {

        ______TS("success : created");

        FeedbackQuestionAttributes fq = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        fqDb.deleteEntity(fq);

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

        feedbackQuestion.questionNumber++;
        fqDb.updateFeedbackQuestion(feedbackQuestion);

        FeedbackQuestionAttributes updatedFq =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);

        // Assert lastUpdate has changed, and is now.
        assertFalse(feedbackQuestion.getUpdatedAt().equals(updatedFq.getUpdatedAt()));
        AssertHelper.assertInstantIsNow(updatedFq.getUpdatedAt());

        ______TS("success : keep lastUpdated");

        feedbackQuestion.questionNumber++;
        fqDb.updateFeedbackQuestion(feedbackQuestion, true);

        FeedbackQuestionAttributes updatedFqTwo =
                fqDb.getFeedbackQuestion(feedbackSessionName, courseId, feedbackQuestion.questionNumber);

        // Assert lastUpdate has NOT changed.
        assertEquals(updatedFq.getUpdatedAt(), updatedFqTwo.getUpdatedAt());
    }

    @Test
    public void testCreateDeleteFeedbackQuestion() throws InvalidParametersException, EntityAlreadyExistsException {

        ______TS("standard success case");

        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        fqDb.deleteEntity(fqa);

        fqDb.createEntity(fqa);
        verifyPresentInDatastore(fqa);

        ______TS("duplicate - with same id.");

        try {
            fqDb.createEntity(fqa);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(FeedbackQuestionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                                                      fqa.getEntityTypeAsString()) + fqa.getIdentificationString(),
                                                      e.getMessage());
        }

        ______TS("delete - with id specified");

        fqDb.deleteEntity(fqa);
        verifyAbsentInDatastore(fqa);

        ______TS("null params");

        try {
            fqDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("invalid params");

        try {
            fqa.creatorEmail = "haha";
            fqDb.createEntity(fqa);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid creator's email", e.getLocalizedMessage());
        }
    }

    @Test
    public void testGetFeedbackQuestions() throws Exception {
        FeedbackQuestionAttributes expected = getNewFeedbackQuestionAttributes();

        // remove possibly conflicting entity from the database
        fqDb.deleteEntity(expected);

        fqDb.createEntity(expected);

        ______TS("standard success case");

        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(expected.feedbackSessionName,
                                                                     expected.courseId,
                                                                     expected.questionNumber);

        assertEquals(expected.toString(), actual.toString());

        ______TS("non-existant question");

        assertNull(fqDb.getFeedbackQuestion("Non-existant feedback session", "non-existent-course", 1));

        ______TS("null fsName");

        try {
            fqDb.getFeedbackQuestion(null, expected.courseId, 1);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("null courseId");

        try {
            fqDb.getFeedbackQuestion(expected.feedbackSessionName, null, 1);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

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

        try {
            fqDb.getFeedbackQuestionsForSession(null, expected.get(0).courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            fqDb.getFeedbackQuestionsForSession(expected.get(0).feedbackSessionName, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

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
        fqDb.deleteEntity(fqa);

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

        try {
            fqDb.getFeedbackQuestionsForGiverType(null, fqa.courseId, FeedbackParticipantType.STUDENTS);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, null, FeedbackParticipantType.STUDENTS);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            fqDb.getFeedbackQuestionsForGiverType(fqa.feedbackSessionName, fqa.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existant session");

        assertTrue(fqDb.getFeedbackQuestionsForGiverType("non-existant session", fqa.courseId,
                                                         FeedbackParticipantType.STUDENTS).isEmpty());

        ______TS("no questions in session");

        assertTrue(fqDb.getFeedbackQuestionsForGiverType("Empty session", fqa.courseId,
                                                         FeedbackParticipantType.STUDENTS).isEmpty());

        deleteFeedbackQuestions(numOfQuestions[0] + numOfQuestions[1] + numOfQuestions[2] + numOfQuestions[3]);
    }

    @Test
    public void testUpdateFeedbackQuestion() throws Exception {

        ______TS("null params");

        try {
            fqDb.updateFeedbackQuestion(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("invalid feedback question attributes");

        FeedbackQuestionAttributes invalidFqa = getNewFeedbackQuestionAttributes();
        fqDb.deleteEntity(invalidFqa);
        fqDb.createEntity(invalidFqa);
        invalidFqa.setId(fqDb.getFeedbackQuestion(invalidFqa.feedbackSessionName, invalidFqa.courseId,
                                                  invalidFqa.questionNumber).getId());
        invalidFqa.creatorEmail = "haha";

        try {
            fqDb.updateFeedbackQuestion(invalidFqa);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid creator's email", e.getLocalizedMessage());
        }

        ______TS("feedback session does not exist");

        FeedbackQuestionAttributes nonexistantFq = getNewFeedbackQuestionAttributes();
        nonexistantFq.setId("non-existent fq id");

        try {
            fqDb.updateFeedbackQuestion(nonexistantFq);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(FeedbackQuestionsDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
        }

        ______TS("standard success case");

        FeedbackQuestionAttributes modifiedQuestion = getNewFeedbackQuestionAttributes();
        fqDb.deleteEntity(modifiedQuestion);
        fqDb.createEntity(modifiedQuestion);
        verifyPresentInDatastore(modifiedQuestion);

        modifiedQuestion = fqDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                                    modifiedQuestion.courseId,
                                                    modifiedQuestion.questionNumber);
        FeedbackQuestionDetails fqd = modifiedQuestion.getQuestionDetails();
        fqd.setQuestionText("New question text!");
        modifiedQuestion.setQuestionDetails(fqd);
        fqDb.updateFeedbackQuestion(modifiedQuestion);

        verifyPresentInDatastore(modifiedQuestion);
        modifiedQuestion = fqDb.getFeedbackQuestion(modifiedQuestion.feedbackSessionName,
                                                    modifiedQuestion.courseId,
                                                    modifiedQuestion.questionNumber);
        assertEquals("New question text!", modifiedQuestion.getQuestionDetails().getQuestionText());

        fqDb.deleteEntity(modifiedQuestion);
    }

    private FeedbackQuestionAttributes getNewFeedbackQuestionAttributes() {
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");

        return FeedbackQuestionAttributes.builder()
                .withCourseId("testCourse")
                .withCreatorEmail("instructor@email.com")
                .withFeedbackSessionName("testFeedbackSession")
                .withGiverType(FeedbackParticipantType.INSTRUCTORS)
                .withRecipientType(FeedbackParticipantType.SELF)
                .withNumOfEntitiesToGiveFeedbackTo(1)
                .withQuestionNumber(1)
                .withQuestionType(FeedbackQuestionType.TEXT)
                .withQuestionMetaData(questionDetails)
                .withShowGiverNameTo(new ArrayList<>())
                .withShowRecipientNameTo(new ArrayList<>())
                .withShowResponseTo(new ArrayList<>())
                .build();
    }

    private List<FeedbackQuestionAttributes> createFeedbackQuestions(int num) throws Exception {
        FeedbackQuestionAttributes fqa;
        List<FeedbackQuestionAttributes> returnVal = new ArrayList<>();

        for (int i = 1; i <= num; i++) {
            fqa = getNewFeedbackQuestionAttributes();
            fqa.questionNumber = i;

            // remove possibly conflicting entity from the database
            fqDb.deleteEntity(fqa);

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
                2
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
            fqDb.deleteEntity(fqa);
        }
    }

}
