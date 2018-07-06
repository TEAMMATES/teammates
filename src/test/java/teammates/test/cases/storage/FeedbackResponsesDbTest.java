package teammates.test.cases.storage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbTest extends BaseComponentTestCase {

    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private DataBundle dataBundle = getTypicalDataBundle();
    private Map<String, FeedbackResponseAttributes> fras;

    @BeforeClass
    public void classSetup() throws Exception {
        addResponsesToDb();
        fras = dataBundle.feedbackResponses;
    }

    private void addResponsesToDb() throws Exception {
        Set<String> keys = dataBundle.feedbackResponses.keySet();
        for (String i : keys) {
            frDb.createEntity(dataBundle.feedbackResponses.get(i));
        }
    }

    @Test
    public void testTimestamp()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {

        ______TS("success : created");

        FeedbackResponseAttributes fra = getNewFeedbackResponseAttributes();

        // remove possibly conflicting entity from the database
        frDb.deleteEntity(fra);

        frDb.createEntity(fra);
        verifyPresentInDatastore(fra);

        String feedbackQuestionId = fra.feedbackQuestionId;
        String giverEmail = fra.giver;
        String recipientEmail = fra.recipient;

        FeedbackResponseAttributes feedbackResponse =
                frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipientEmail);

        // Assert dates are now.
        AssertHelper.assertInstantIsNow(feedbackResponse.getCreatedAt());
        AssertHelper.assertInstantIsNow(feedbackResponse.getUpdatedAt());

        ______TS("success : update lastUpdated");

        String newRecipientEmail = "new-email@tmt.com";
        feedbackResponse.recipient = newRecipientEmail;
        frDb.updateFeedbackResponse(feedbackResponse);

        FeedbackResponseAttributes updatedFr = frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, newRecipientEmail);

        // Assert lastUpdate has changed, and is now.
        assertFalse(feedbackResponse.getUpdatedAt().equals(updatedFr.getUpdatedAt()));
        AssertHelper.assertInstantIsNow(updatedFr.getUpdatedAt());

        ______TS("success : keep lastUpdated");

        String newRecipientEmailTwo = "new-email-two@tmt.com";
        feedbackResponse.recipient = newRecipientEmailTwo;
        frDb.updateFeedbackResponse(feedbackResponse, true);

        FeedbackResponseAttributes updatedFrTwo =
                frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, newRecipientEmailTwo);

        // Assert lastUpdate has NOT changed.
        assertEquals(updatedFr.getUpdatedAt(), updatedFrTwo.getUpdatedAt());
    }

    @Test
    public void testCreateDeleteFeedbackResponse() throws Exception {

        ______TS("standard success case");

        FeedbackResponseAttributes fra = getNewFeedbackResponseAttributes();

        // remove possibly conflicting entity from the database
        frDb.deleteEntity(fra);

        frDb.createEntity(fra);

        // sets the id for fra
        verifyPresentInDatastore(fra);

        ______TS("duplicate - with same id.");

        try {
            frDb.createEntity(fra);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(FeedbackResponsesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                                                      fra.getEntityTypeAsString())
                                            + fra.getIdentificationString(),
                                        e.getMessage());
        }

        ______TS("delete - with id specified");

        frDb.deleteEntity(fra);
        verifyAbsentInDatastore(fra);

        ______TS("null params");

        try {
            frDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("invalid params");

        try {
            fra.courseId = "invalid course id!";
            frDb.createEntity(fra);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    getPopulatedErrorMessage(
                        FieldValidator.COURSE_ID_ERROR_MESSAGE, "invalid course id!",
                        FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                    e.getLocalizedMessage());
        }

    }

    @Test
    public void testGetFeedbackResponses() {

        ______TS("standard success case");

        FeedbackResponseAttributes expected = getResponseAttributes("response1ForQ1S1C1");

        FeedbackResponseAttributes actual =
                frDb.getFeedbackResponse(expected.feedbackQuestionId, expected.giver, expected.recipient);

        assertEquals(expected.toString(), actual.toString());

        ______TS("non-existent response");

        assertNull(frDb.getFeedbackResponse(expected.feedbackQuestionId, "student1InCourse1@gmail.tmt",
                                            "student3InCourse1@gmail.tmt"));

        ______TS("null fqId");

        try {
            frDb.getFeedbackResponse(null, "student1InCourse1@gmail.tmt", "student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("null giverEmail");

        try {
            frDb.getFeedbackResponse(expected.feedbackQuestionId, null, "student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("null receiverEmail");

        try {
            frDb.getFeedbackResponse(expected.feedbackQuestionId, "student1InCourse1@gmail.tmt", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("get by id");

        actual = frDb.getFeedbackResponse(actual.getId()); //Id from first success case

        assertEquals(expected.toString(), actual.toString());

        ______TS("get non-existent response by id");

        actual = frDb.getFeedbackResponse("non-existent id");

        assertNull(actual);
    }

    @Test
    public void testGetFeedbackResponsesForQuestion() {

        ______TS("standard success case");

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForQuestion(fras.get("response1ForQ1S1C1").feedbackQuestionId);
        assertEquals(7, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForQuestion(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesForQuestion("non-existent fq id").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection() {

        ______TS("standard success case");

        String questionId = fras.get("response1ForQ1S1C1").feedbackQuestionId;

        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 1");

        assertEquals(4, responses.size());

        ______TS("No responses as they are filtered out");

        responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 2");

        assertEquals(0, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForQuestionInSection(null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForQuestionInSection(questionId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesForQuestionInSection("non-existent fq id", "Section 1").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSession() {

        ______TS("standard success case");

        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;
        String courseId = fras.get("response1ForQ1S1C1").courseId;

        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);

        assertEquals(6, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForSession(null, courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForSession(feedbackSessionName, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback session");

        assertTrue(frDb.getFeedbackResponsesForSession("non-existent feedback session", courseId).isEmpty());

        ______TS("non-existent course");

        assertTrue(frDb.getFeedbackResponsesForSession(feedbackSessionName, "non-existent courseId").isEmpty());

    }

    @Test
    public void testGetFeedbackResponsesForReceiverForQuestion() {

        ______TS("standard success case");

        String questionId = fras.get("response1ForQ1S1C1").feedbackQuestionId;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForReceiverForQuestion(questionId,
                        "student1InCourse1@gmail.tmt");

        assertEquals(1, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForReceiverForQuestion(null, "student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForReceiverForQuestion(questionId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion(
                "non-existent fq id", "student1InCourse1@gmail.tmt").isEmpty());

        ______TS("non-existent receiver");

        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion(
                questionId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForReceiverForQuestionInSection() {

        ______TS("standard success case");

        String questionId = fras.get("response1ForQ1S1C1").feedbackQuestionId;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForReceiverForQuestionInSection(questionId,
                        "student1InCourse1@gmail.tmt", "Section 1");

        assertEquals(1, responses.size());

        ______TS("No responses as they are filtered out");

        responses =
                frDb.getFeedbackResponsesForReceiverForQuestionInSection(questionId,
                        "student1InCourse1@gmail.tmt", "Section 2");

        assertEquals(responses.size(), 0);

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                                null, "student1InCourse1@gmail.tmt", "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForReceiverForQuestionInSection(questionId, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                                questionId, "student1InCourse1@gmail.tmt", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                "non-existent fq id", "student1InCourse1@gmail.tmt", "Section 1").isEmpty());

        ______TS("non-existent receiver");

        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                questionId, "non-existentStudentInCourse1@gmail.tmt", "Section 1").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForReceiverForCourse() {

        ______TS("standard success case");

        String courseId = fras.get("response1ForQ1S1C1").courseId;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForReceiverForCourse(courseId,
                        "student1InCourse1@gmail.tmt");

        assertEquals(2, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForReceiverForCourse(null, "student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForReceiverForCourse(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent courseId");

        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse(
                "non-existent courseId", "student1InCourse1@gmail.tmt").isEmpty());

        ______TS("non-existent receiver");

        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse(
                courseId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion() {

        ______TS("standard success case");

        String questionId = fras.get("response1ForQ1S1C1").feedbackQuestionId;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesFromGiverForQuestion(questionId,
                        "student1InCourse1@gmail.tmt");

        assertEquals(responses.size(), 1);

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesFromGiverForQuestion(null, "student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesFromGiverForQuestion(questionId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion(
                "non-existent fq id", "student1InCourse1@gmail.tmt").isEmpty());

        ______TS("non-existent receiver");

        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion(
                questionId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestionInSection() {

        ______TS("standard success case");

        String questionId = fras.get("response1ForQ1S1C1").feedbackQuestionId;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesFromGiverForQuestionInSection(questionId,
                        "student1InCourse1@gmail.tmt", "Section 1");

        assertEquals(responses.size(), 1);

        ______TS("No reponses as they are filtered out");

        responses =
                frDb.getFeedbackResponsesFromGiverForQuestionInSection(questionId,
                        "student1InCourse1@gmail.tmt", "Section 2");

        assertEquals(responses.size(), 0);

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                                            null, "student1InCourse1@gmail.tmt", "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesFromGiverForQuestionInSection(questionId, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                                            questionId, "student1InCourse1@gmail.tmt", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                            "non-existent fq id", "student1InCourse1@gmail.tmt", "Section 1").isEmpty());

        ______TS("non-existent receiver");

        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                            questionId, "non-existentstudentInCourse1@gmail.tmt", "Section 1").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForCourse() {

        ______TS("standard success case");

        String courseId = fras.get("response1ForQ1S1C1").courseId;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesFromGiverForCourse(courseId,
                        "student1InCourse1@gmail.tmt");

        assertEquals(3, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesFromGiverForCourse(null, "student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesFromGiverForCourse(courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse(
                "non-existent courseId", "student1InCourse1@gmail.tmt").isEmpty());

        ______TS("non-existent giver");

        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse(
                courseId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSessionWithinRange() {

        ______TS("standard success case");

        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, 1);

        assertEquals(responses.size(), 2);

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForSessionWithinRange(null, courseId, 5);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, null, 4);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback session");

        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange(
                "non-existent feedback session", courseId, 1).isEmpty());

        ______TS("non-existent course");

        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange(
                feedbackSessionName, "non-existent courseId", 1).isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection() {

        ______TS("standard success case");

        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, "Section 1");

        assertEquals(5, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForSessionInSection(null, courseId, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback session");

        assertTrue(frDb.getFeedbackResponsesForSessionInSection(
                "non-existent feedback session", courseId, "Section 1").isEmpty());

        ______TS("non-existent course");

        assertTrue(frDb.getFeedbackResponsesForSessionInSection(
                feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSessionFromSection() {

        ______TS("standard success case");

        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, courseId, "Section 2");

        assertEquals(0, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForSessionFromSection(null, courseId, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback session");

        assertTrue(frDb.getFeedbackResponsesForSessionFromSection(
                "non-existent feedback session", courseId, "Section 1").isEmpty());

        ______TS("non-existent course");

        assertTrue(frDb.getFeedbackResponsesForSessionFromSection(
                feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());

        ______TS("no responses for session");

        assertTrue(frDb.getFeedbackResponsesForSessionFromSection(
                "Empty feedback session", "idOfTypicalCourse1", "Section 1").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSessionToSection() {

        ______TS("standard success case");

        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;

        List<FeedbackResponseAttributes> responses =
                frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, courseId, "Section 1");

        assertEquals(5, responses.size());

        ______TS("null params");

        try {
            frDb.getFeedbackResponsesForSessionToSection(null, courseId, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("non-existent feedback session");

        assertTrue(frDb.getFeedbackResponsesForSessionToSection(
                "non-existent feedback session", courseId, "Section 1").isEmpty());

        ______TS("non-existent course");

        assertTrue(frDb.getFeedbackResponsesForSessionToSection(
                feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());

        ______TS("no responses for session");

        assertTrue(frDb.getFeedbackResponsesForSessionToSection(
                "Empty feedback session", "idOfTypicalCourse1", "Section 1").isEmpty());
    }

    @Test
    public void testUpdateFeedbackResponse() throws Exception {

        ______TS("null params");

        try {
            frDb.updateFeedbackResponse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        ______TS("invalid feedback response attributes");

        FeedbackResponseAttributes invalidFra = getResponseAttributes("response3ForQ2S1C1");
        invalidFra.setId(frDb.getFeedbackResponse(invalidFra.feedbackQuestionId,
                invalidFra.giver, invalidFra.recipient).getId());
        invalidFra.courseId = "invalid course_";
        try {
            frDb.updateFeedbackResponse(invalidFra);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    getPopulatedErrorMessage(
                        FieldValidator.COURSE_ID_ERROR_MESSAGE, "invalid course_",
                        FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                    e.getLocalizedMessage());
        }

        ______TS("feedback response does not exist");

        FeedbackResponseAttributes nonexistantFr = getResponseAttributes("response3ForQ2S1C1");
        nonexistantFr.setId("non-existent fr id");
        try {
            frDb.updateFeedbackResponse(nonexistantFr);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(FeedbackResponsesDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
        }

        ______TS("standard success case");

        FeedbackResponseAttributes modifiedResponse = getResponseAttributes("response3ForQ2S1C1");

        modifiedResponse = frDb.getFeedbackResponse(modifiedResponse.feedbackQuestionId,
                modifiedResponse.giver, modifiedResponse.recipient);
        FeedbackResponseDetails frd = modifiedResponse.getResponseDetails();

        Map<String, String[]> requestParameters = new HashMap<>();
        requestParameters.put("questiontype-1", new String[] { "TEXT" });
        requestParameters.put("responsetext-1-0", new String[] { "New answer text!" });

        String[] answer = {"New answer text!"};
        frd = FeedbackResponseDetails.createResponseDetails(
                    answer, FeedbackQuestionType.TEXT,
                    null, requestParameters, 1, 0);
        modifiedResponse.setResponseDetails(frd);
        frDb.updateFeedbackResponse(modifiedResponse);

        verifyPresentInDatastore(modifiedResponse);
        modifiedResponse = frDb.getFeedbackResponse(modifiedResponse.feedbackQuestionId,
                                                    modifiedResponse.giver,
                                                    modifiedResponse.recipient);
        assertEquals("New answer text!", modifiedResponse.getResponseDetails().getAnswerString());

    }

    private FeedbackResponseAttributes getNewFeedbackResponseAttributes() {
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes();

        fra.feedbackSessionName = "fsTest1";
        fra.courseId = "testCourse";
        fra.feedbackQuestionType = FeedbackQuestionType.TEXT;
        fra.giver = "giver@email.tmt";
        fra.giverSection = "None";
        fra.recipient = "recipient@email.tmt";
        fra.recipientSection = "None";
        fra.feedbackQuestionId = "testFeedbackQuestionId";

        FeedbackResponseDetails responseDetails = new FeedbackTextResponseDetails("Text response");
        fra.setResponseDetails(responseDetails);

        return fra;
    }

    private FeedbackResponseAttributes getResponseAttributes(String id) {
        FeedbackResponseAttributes result = fras.get(id);
        return new FeedbackResponseAttributes(result.feedbackSessionName,
                result.courseId, result.feedbackQuestionId,
                result.feedbackQuestionType, result.giver, result.giverSection,
                result.recipient, result.recipientSection, result.responseMetaData);
    }

    @AfterClass
    public void classTearDown() {
        deleteResponsesFromDb();
    }

    private void deleteResponsesFromDb() {
        Set<String> keys = dataBundle.feedbackResponses.keySet();
        for (String i : keys) {
            frDb.deleteEntity(dataBundle.feedbackResponses.get(i));
        }
    }

}
