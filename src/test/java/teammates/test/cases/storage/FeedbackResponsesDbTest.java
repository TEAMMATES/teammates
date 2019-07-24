package teammates.test.cases.storage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackResponsesDb}.
 */
public class FeedbackResponsesDbTest extends BaseComponentTestCase {

    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private DataBundle dataBundle;
    private Map<String, FeedbackResponseAttributes> fras;

    @BeforeClass
    public void beforeClass() throws Exception {
        dataBundle = getTypicalDataBundle();
        // Add questions to DB
        Set<String> keys = dataBundle.feedbackQuestions.keySet();
        for (String i : keys) {
            fqDb.createEntity(dataBundle.feedbackQuestions.get(i));
        }
    }

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dataBundle = getTypicalDataBundle();
        addQuestionsAndResponsesToDb();
        fras = dataBundle.feedbackResponses;
    }

    private void addQuestionsAndResponsesToDb() throws InvalidParametersException, EntityAlreadyExistsException {
        // Add responses for corresponding question to DB
        Set<String> keys = dataBundle.feedbackResponses.keySet();
        for (String i : keys) {
            FeedbackResponseAttributes fra = dataBundle.feedbackResponses.get(i);

            // Update feedbackQuestionId for response
            FeedbackQuestionAttributes fqa = logic.getFeedbackQuestion(fra.feedbackSessionName,
                    fra.courseId, Integer.parseInt(fra.feedbackQuestionId));
            fra.feedbackQuestionId = fqa.getId();
            frDb.createEntity(fra);
        }
    }

    @Test
    public void testGetGiverSetThatAnswerFeedbackSession_emptyResponses_shouldReturnEmptySet() {
        Set<String> giverSet = frDb.getGiverSetThatAnswerFeedbackSession("courseA", "session");

        assertTrue(giverSet.isEmpty());
    }

    @Test
    public void testGetGiverSetThatAnswerFeedbackSession_giverIsUser_shouldReturnCorrectIdentifier() {
        Set<String> giverSet = frDb.getGiverSetThatAnswerFeedbackSession("idOfTypicalCourse1", "First feedback session");

        assertEquals(Sets.newHashSet("student1InCourse1@gmail.tmt", "student2InCourse1@gmail.tmt",
                "student5InCourse1@gmail.tmt", "student3InCourse1@gmail.tmt", "instructor1@course1.tmt"),
                giverSet);
    }

    @Test
    public void testTimestamp()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {

        ______TS("success : created");

        FeedbackResponseAttributes fra = getNewFeedbackResponseAttributes();

        // remove possibly conflicting entity from the database
        deleteResponse(fra);

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
        frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(feedbackResponse.getId())
                        .withRecipient(newRecipientEmail)
                        .build());

        FeedbackResponseAttributes updatedFr = frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, newRecipientEmail);

        // Assert lastUpdate has changed, and is now.
        assertFalse(feedbackResponse.getUpdatedAt().equals(updatedFr.getUpdatedAt()));
        AssertHelper.assertInstantIsNow(updatedFr.getUpdatedAt());

    }

    @Test
    public void testDeleteFeedbackResponse() {
        ______TS("non-existent id");

        frDb.deleteFeedbackResponse("not-existent");

        ______TS("standard success case");

        FeedbackResponseAttributes fra = fras.get("response1ForQ1S1C1");
        fra = frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giver, fra.recipient);
        assertNotNull(fra);

        frDb.deleteFeedbackResponse(fra.getId());

        assertNull(frDb.getFeedbackResponse(fra.getId()));
    }

    @Test
    public void testDeleteFeedbackResponses_byQuestionId() {
        ______TS("standard success case");

        FeedbackResponseAttributes fra = fras.get("response1ForQ1S1C1");
        assertFalse(frDb.getFeedbackResponsesForQuestion(fra.feedbackQuestionId).isEmpty());
        FeedbackResponseAttributes fraFromAnotherQuestion = fras.get("response1ForQ2S1C1");
        assertFalse(frDb.getFeedbackResponsesForQuestion(fraFromAnotherQuestion.feedbackQuestionId).isEmpty());
        assertNotEquals(fra.feedbackQuestionId, fraFromAnotherQuestion.feedbackQuestionId);

        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withQuestionId(fra.feedbackQuestionId)
                        .build());

        // all response of questions are deleted
        assertTrue(frDb.getFeedbackResponsesForQuestion(fra.feedbackQuestionId).isEmpty());
        // responses of other questions remain
        assertFalse(frDb.getFeedbackResponsesForQuestion(fraFromAnotherQuestion.feedbackQuestionId).isEmpty());

        ______TS("non-existent question id");

        // should pass silently
        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withQuestionId("not-exist")
                        .build());

        // responses are not deleted accidentally
        assertFalse(frDb.getFeedbackResponsesForQuestion(fraFromAnotherQuestion.feedbackQuestionId).isEmpty());
    }

    @Test
    public void testDeleteFeedbackResponses_byCourseIdAndSessionName() {
        ______TS("standard success case");

        FeedbackResponseAttributes fra = fras.get("response1ForQ1S1C1");
        fra = frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giver, fra.recipient);
        assertNotNull(fra);
        FeedbackResponseAttributes fraFromAnotherSession = fras.get("response1ForQ1S2C1");
        fraFromAnotherSession = frDb.getFeedbackResponse(
                fraFromAnotherSession.feedbackQuestionId, fraFromAnotherSession.giver, fraFromAnotherSession.recipient);
        assertNotNull(fraFromAnotherSession);
        // response are belong to the same course
        assertEquals(fra.courseId, fraFromAnotherSession.courseId);
        // but in different session
        assertNotEquals(fra.feedbackSessionName, fraFromAnotherSession.feedbackSessionName);

        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId(fra.courseId)
                        .withFeedbackSessionName(fra.feedbackSessionName)
                        .build());

        assertNull(frDb.getFeedbackResponse(fra.getId()));
        // other responses remains
        assertNotNull(frDb.getFeedbackResponse(fraFromAnotherSession.getId()));

        ______TS("non-existent course id");

        // should pass silently
        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId("not_exist")
                        .withFeedbackSessionName(fra.feedbackSessionName)
                        .build());

        // other responses remain
        assertNotNull(frDb.getFeedbackResponse(fraFromAnotherSession.getId()));

        ______TS("non-existent session name");

        // should pass silently
        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId(fra.courseId)
                        .withFeedbackSessionName("not-exist")
                        .build());

        // other responses remain
        assertNotNull(frDb.getFeedbackResponse(fraFromAnotherSession.getId()));

        ______TS("non-existent course and session name");

        // should pass silently
        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId("not-exist")
                        .withFeedbackSessionName("not-exist")
                        .build());

        // other responses remain
        assertNotNull(frDb.getFeedbackResponse(fraFromAnotherSession.getId()));
    }

    @Test
    public void testDeleteFeedbackResponses_byCourseId() {
        ______TS("standard success case");

        FeedbackResponseAttributes fra = fras.get("response1ForQ1S1C1");
        fra = frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giver, fra.recipient);
        assertNotNull(fra);
        FeedbackResponseAttributes fraFromAnotherCourse = fras.get("response1ForQ1S1C2");
        fraFromAnotherCourse = frDb.getFeedbackResponse(
                fraFromAnotherCourse.feedbackQuestionId, fraFromAnotherCourse.giver, fraFromAnotherCourse.recipient);
        assertNotNull(fraFromAnotherCourse);
        // response are belong to different courses
        assertNotEquals(fra.courseId, fraFromAnotherCourse.courseId);

        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId(fra.courseId)
                        .build());

        // all response of courses are deleted
        assertNull(frDb.getFeedbackResponse(fra.getId()));
        // responses of other course remain
        assertNotNull(frDb.getFeedbackResponse(fraFromAnotherCourse.getId()));

        ______TS("non-existent course id");

        // should pass silently
        frDb.deleteFeedbackResponses(
                AttributesDeletionQuery.builder()
                        .withCourseId("not-exist")
                        .build());

        // responses are not deleted accidentally
        assertNotNull(frDb.getFeedbackResponse(fraFromAnotherCourse.getId()));
    }

    @Test
    public void testCreateFeedbackResponse() throws Exception {

        ______TS("standard success case");

        FeedbackResponseAttributes fra = getNewFeedbackResponseAttributes();

        // remove possibly conflicting entity from the database
        deleteResponse(fra);

        frDb.createEntity(fra);

        // sets the id for fra
        verifyPresentInDatastore(fra);

        ______TS("duplicate - with same id.");

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class, () -> frDb.createEntity(fra));
        assertEquals(
                String.format(FeedbackResponsesDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, fra.toString()), eaee.getMessage());

        ______TS("delete - with id specified");

        deleteResponse(fra);
        verifyAbsentInDatastore(fra);

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class, () -> frDb.createEntity(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("invalid params");

        fra.courseId = "invalid course id!";
        InvalidParametersException ipe = assertThrows(InvalidParametersException.class, () -> frDb.createEntity(fra));
        AssertHelper.assertContains(
                getPopulatedErrorMessage(
                        FieldValidator.COURSE_ID_ERROR_MESSAGE, "invalid course id!",
                        FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.COURSE_ID_MAX_LENGTH),
                ipe.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponse(null, "student1InCourse1@gmail.tmt", "student1InCourse1@gmail.tmt"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("null giverEmail");

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponse(expected.feedbackQuestionId, null, "student1InCourse1@gmail.tmt"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("null receiverEmail");

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponse(expected.feedbackQuestionId, "student1InCourse1@gmail.tmt", null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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
        assertEquals(2, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForQuestion(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesForQuestion("non-existent fq id").isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection() {

        ______TS("standard success case");

        String questionId = fras.get("response1ForQ2S1C1").feedbackQuestionId;

        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 1",
                SectionDetail.EITHER);
        assertEquals(3, responses.size());

        ______TS("show response after filtering by giver from section 1");

        responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 1", SectionDetail.GIVER);
        assertEquals(2, responses.size());

        ______TS("show response after filtering by recipient from section 2");

        responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 2", SectionDetail.EVALUEE);
        assertEquals(1, responses.size());

        ______TS("no responses as they are filtered by both giver and recipient from section 2");

        responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 2", SectionDetail.BOTH);
        assertEquals(0, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForQuestionInSection(null, "Section 1", SectionDetail.EITHER));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForQuestionInSection(questionId, null, SectionDetail.EITHER));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 1", null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("non-existent feedback question");

        assertTrue(frDb.getFeedbackResponsesForQuestionInSection("non-existent fq id", "Section 1",
                SectionDetail.EITHER).isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSession() {

        ______TS("standard success case");

        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;
        String courseId = fras.get("response1ForQ1S1C1").courseId;

        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);

        assertEquals(6, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSession(null, courseId));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSession(feedbackSessionName, null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForQuestion(null, "student1InCourse1@gmail.tmt"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForQuestion(questionId, null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                        null, "student1InCourse1@gmail.tmt", "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForQuestionInSection(questionId, null, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                        questionId, "student1InCourse1@gmail.tmt", null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        assertEquals(1, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForCourse(null, "student1InCourse1@gmail.tmt"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForReceiverForCourse(courseId, null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesFromGiverForQuestion(null, "student1InCourse1@gmail.tmt"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class, () -> frDb.getFeedbackResponsesFromGiverForQuestion(questionId, null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                        null, "student1InCourse1@gmail.tmt", "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesFromGiverForQuestionInSection(questionId, null, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                        questionId, "student1InCourse1@gmail.tmt", null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        assertEquals(2, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesFromGiverForCourse(null, "student1InCourse1@gmail.tmt"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class, () -> frDb.getFeedbackResponsesFromGiverForCourse(courseId, null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionWithinRange(null, courseId, 5));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, null, 4));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionInSection(null, courseId, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, null, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        assertEquals(1, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionFromSection(null, courseId, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, null, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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

        assertEquals(4, responses.size());

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionToSection(null, courseId, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ae = assertThrows(AssertionError.class,
                () -> frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, null, "Section 1"));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

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
    public void testUpdateFeedbackResponse_noChangeToResponse_shouldNotIssueSaveRequest() throws Exception {
        FeedbackResponseAttributes typicalResponse = getResponseAttributes("response3ForQ2S1C1");

        typicalResponse = frDb.getFeedbackResponse(typicalResponse.feedbackQuestionId,
                typicalResponse.giver, typicalResponse.recipient);

        FeedbackResponseAttributes updatedResponse = frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(typicalResponse.getId())
                        .build());

        assertEquals(JsonUtils.toJson(typicalResponse), JsonUtils.toJson(updatedResponse));
        assertEquals(typicalResponse.getUpdatedAt(), updatedResponse.getUpdatedAt());

        updatedResponse = frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(typicalResponse.getId())
                        .withGiver(typicalResponse.getGiver())
                        .withGiverSection(typicalResponse.getGiverSection())
                        .withRecipient(typicalResponse.getRecipient())
                        .withRecipientSection(typicalResponse.getRecipientSection())
                        .withResponseDetails(typicalResponse.getResponseDetails())
                        .build());

        assertEquals(JsonUtils.toJson(typicalResponse), JsonUtils.toJson(updatedResponse));
        assertEquals(typicalResponse.getUpdatedAt(), updatedResponse.getUpdatedAt());
    }

    @Test
    public void testUpdateFeedbackResponse() throws Exception {

        ______TS("null params");

        AssertionError ae = assertThrows(AssertionError.class, () -> frDb.updateFeedbackResponse(null));
        AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getLocalizedMessage());

        ______TS("feedback response does not exist");

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frDb.updateFeedbackResponse(
                        FeedbackResponseAttributes.updateOptionsBuilder("non-existent")
                                .withGiver("giverIdentifier")
                                .build()));
        AssertHelper.assertContains(FeedbackResponsesDb.ERROR_UPDATE_NON_EXISTENT, ednee.getLocalizedMessage());

        ______TS("standard success case");

        FeedbackResponseAttributes modifiedResponse = getResponseAttributes("response3ForQ2S1C1");

        modifiedResponse = frDb.getFeedbackResponse(modifiedResponse.feedbackQuestionId,
                modifiedResponse.giver, modifiedResponse.recipient);

        FeedbackResponseDetails frd = new FeedbackTextResponseDetails("New answer text!");
        modifiedResponse.setResponseDetails(frd);

        frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(modifiedResponse.getId())
                        .withResponseDetails(frd)
                        .build());

        verifyPresentInDatastore(modifiedResponse);
        modifiedResponse = frDb.getFeedbackResponse(modifiedResponse.feedbackQuestionId,
                                                    modifiedResponse.giver,
                                                    modifiedResponse.recipient);
        assertEquals("New answer text!", modifiedResponse.getResponseDetails().getAnswerString());

        ______TS("standard success case, recreate response when recipient/giver change");

        FeedbackResponseAttributes updatedResponse = frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(modifiedResponse.getId())
                        .withGiver("giver@email.com")
                        .withRecipient("recipient@email.com")
                        .build());

        assertNull(frDb.getFeedbackResponse(modifiedResponse.getId()));
        FeedbackResponseAttributes actualResponse = frDb.getFeedbackResponse(updatedResponse.getId());
        assertNotNull(actualResponse);
        assertEquals("giver@email.com", updatedResponse.giver);
        assertEquals(updatedResponse.giver, actualResponse.giver);
        assertEquals("recipient@email.com", updatedResponse.recipient);
        assertEquals(updatedResponse.recipient, actualResponse.recipient);
        assertEquals(modifiedResponse.courseId, updatedResponse.courseId);
        assertEquals(modifiedResponse.feedbackSessionName, updatedResponse.feedbackSessionName);
        assertEquals(modifiedResponse.getFeedbackQuestionType(), updatedResponse.getFeedbackQuestionType());
    }

    // the test is to ensure that optimized saving policy is implemented without false negative
    @Test
    public void testUpdateFeedbackResponse_singleFieldUpdate_shouldUpdateCorrectly() throws Exception {
        FeedbackResponseAttributes typicalResponse = getResponseAttributes("response3ForQ2S1C1");
        typicalResponse = frDb.getFeedbackResponse(
                typicalResponse.getFeedbackQuestionId(), typicalResponse.getGiver(), typicalResponse.getRecipient());

        assertNotEquals("testSection", typicalResponse.getGiverSection());
        FeedbackResponseAttributes updatedResponse = frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(typicalResponse.getId())
                        .withGiverSection("testSection")
                        .build());
        FeedbackResponseAttributes actualResponse = frDb.getFeedbackResponse(typicalResponse.getId());
        assertEquals("testSection", updatedResponse.getGiverSection());
        assertEquals("testSection", actualResponse.getGiverSection());

        assertNotEquals("testSection", typicalResponse.getRecipientSection());
        updatedResponse = frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(typicalResponse.getId())
                        .withRecipientSection("testSection")
                        .build());
        actualResponse = frDb.getFeedbackResponse(typicalResponse.getId());
        assertEquals("testSection", updatedResponse.getRecipientSection());
        assertEquals("testSection", actualResponse.getRecipientSection());

        assertNotEquals("testResponse", typicalResponse.getResponseDetails().getAnswerString());
        updatedResponse = frDb.updateFeedbackResponse(
                FeedbackResponseAttributes.updateOptionsBuilder(typicalResponse.getId())
                        .withResponseDetails(new FeedbackTextResponseDetails("testResponse"))
                        .build());
        actualResponse = frDb.getFeedbackResponse(typicalResponse.getId());
        assertEquals("testResponse", updatedResponse.getResponseDetails().getAnswerString());
        assertEquals("testResponse", actualResponse.getResponseDetails().getAnswerString());

        frDb.deleteFeedbackResponse(typicalResponse.getId());
    }

    private FeedbackResponseAttributes getNewFeedbackResponseAttributes() {
        return FeedbackResponseAttributes.builder(
                "testFeedbackQuestionId", "giver@email.tmt", "recipient@email.tmt")
                .withCourseId("testCourse")
                .withFeedbackSessionName("fsTest1")
                .withGiverSection("None")
                .withRecipientSection("None")
                .withResponseDetails(new FeedbackTextResponseDetails("Text response"))
                .build();
    }

    private FeedbackResponseAttributes getResponseAttributes(String id) {
        FeedbackResponseAttributes result = fras.get(id);

        return FeedbackResponseAttributes.builder(result.feedbackQuestionId, result.giver, result.recipient)
                .withCourseId(result.courseId)
                .withFeedbackSessionName(result.feedbackSessionName)
                .withGiverSection(result.giverSection)
                .withRecipientSection(result.recipientSection)
                .withResponseDetails(result.responseDetails)
                .build();
    }

    @AfterMethod
    public void afterMethod() {
        deleteResponsesFromDb();
    }

    private void deleteResponsesFromDb() {
        Set<String> keys = dataBundle.feedbackResponses.keySet();
        for (String i : keys) {
            deleteResponse(dataBundle.feedbackResponses.get(i));
        }
    }

    private void deleteResponse(FeedbackResponseAttributes attributes) {
        FeedbackResponseAttributes feedbackResponse =
                frDb.getFeedbackResponse(attributes.feedbackQuestionId, attributes.giver, attributes.recipient);
        if (feedbackResponse != null) {
            frDb.deleteFeedbackResponse(feedbackResponse.getId());
        }
    }

}
