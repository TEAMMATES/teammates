package teammates.test.cases.storage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link FeedbackResponseCommentsDb}.
 */
public class FeedbackResponseCommentsDbTest extends BaseComponentTestCase {

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

    private DataBundle dataBundle;
    private FeedbackResponseCommentAttributes frcaData;
    private String frId;
    private FeedbackResponseCommentAttributes anotherFrcaData;
    private List<FeedbackResponseCommentAttributes> frcasData;

    @BeforeMethod
    public void beforeMethod() throws Exception {
        dataBundle = getTypicalDataBundle();

        frcaData = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        frId = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1").feedbackResponseId;
        anotherFrcaData = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcasData = new ArrayList<>();

        FeedbackResponseCommentAttributes oldFrcaData =
                frcDb.getFeedbackResponseComment(frcaData.feedbackResponseId, frcaData.commentGiver, frcaData.createdAt);
        if (oldFrcaData != null) {
            frcDb.deleteFeedbackResponseComment(oldFrcaData.getId());
        }
        frcDb.createEntity(frcaData);
        frcaData = frcDb.getFeedbackResponseComment(frcaData.feedbackResponseId,
                frcaData.commentGiver, frcaData.createdAt);

        FeedbackResponseCommentAttributes oldAnotherFrcaData =
                frcDb.getFeedbackResponseComment(
                        anotherFrcaData.feedbackResponseId, anotherFrcaData.commentGiver, anotherFrcaData.createdAt);
        if (oldAnotherFrcaData != null) {
            frcDb.deleteFeedbackResponseComment(oldAnotherFrcaData.getId());
        }
        frcDb.createEntity(anotherFrcaData);
        anotherFrcaData = frcDb.getFeedbackResponseComment(anotherFrcaData.feedbackResponseId,
                anotherFrcaData.commentGiver, anotherFrcaData.createdAt);

        frcasData.add(frcaData);
        frcasData.add(anotherFrcaData);
    }

    @Test
    public void testAll() throws Exception {

        testEntityCreationAndDeletion();

        testGetFeedbackResponseCommentFromId();

        testGetFeedbackResponseCommentFromCommentDetails();

        testGetFeedbackResponseCommentForGiver();

        testGetFeedbackResponseCommentForResponse();

        testUpdateFeedbackResponseComment();

        testGetFeedbackResponseCommentsForSession();

        testUpdateFeedbackResponseCommentsGiverEmail();
    }

    private void testEntityCreationAndDeletion() throws Exception {
        FeedbackResponseCommentAttributes frcaTemp =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.createdAt = Instant.now();
        frcaTemp.commentText = "test creation and deletion";

        ______TS("Entity creation");

        frcDb.createEntity(frcaTemp);
        verifyPresentInDatastore(frcaTemp);

        ______TS("Entity deletion");

        frcaTemp = frcDb.getFeedbackResponseComment(
                frcaTemp.feedbackResponseId, frcaTemp.commentGiver, frcaTemp.createdAt);
        assertNotNull(frcaTemp);
        frcDb.deleteFeedbackResponseComment(frcaTemp.getId());
        verifyAbsentInDatastore(frcaTemp);
    }

    private void testGetFeedbackResponseCommentFromId() {

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected =
                frcDb.getFeedbackResponseComment(frcaData.courseId, frcaData.createdAt, frcaData.commentGiver);

        FeedbackResponseCommentAttributes frcaActual =
                frcDb.getFeedbackResponseComment(frcaExpected.getId());

        assertEquals(frcaExpected.toString(), frcaActual.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment(-1L));
    }

    private void testGetFeedbackResponseCommentFromCommentDetails() {

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseComment(null, "", Instant.now()));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseComment("", null, Instant.now()));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseComment("", "", null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected = frcaData;
        FeedbackResponseCommentAttributes frca =
                frcDb.getFeedbackResponseComment(frId, frcaExpected.commentGiver, frcaExpected.createdAt);

        // fill back the Ids
        frcaExpected.feedbackResponseId = frId;
        frcaExpected.setId(frca.getId());
        frcaExpected.feedbackQuestionId = frca.feedbackQuestionId;

        assertEquals(frcaExpected.toString(), frca.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment("123", frca.commentGiver, frca.createdAt));

        ______TS("non-existent giver");

        assertNull(frcDb.getFeedbackResponseComment(frca.getId().toString(), "nonExistentGiverEmail", frca.createdAt));
        assertNull(frcDb.getFeedbackResponseComment(frcaData.courseId, frcaData.createdAt, "nonExistentGiverEmail"));
    }

    private void testGetFeedbackResponseCommentForGiver() {
        List<FeedbackResponseCommentAttributes> frcasExpected = frcasData;

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentForGiver(null, frcaData.commentGiver));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> frcas =
                frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, frcaData.commentGiver);
        verifyListsContainSameResponseCommentAttributes(new ArrayList<>(frcasExpected), frcas);

        ______TS("non-existent course id");

        frcas = frcDb.getFeedbackResponseCommentForGiver("idOfNonExistentCourse", frcaData.commentGiver);
        assertTrue(frcas.isEmpty());

        ______TS("non-existent giver");

        frcas = frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, "nonExistentGiverEmail");
        assertTrue(frcas.isEmpty());
    }

    private void testGetFeedbackResponseCommentForResponse() {
        String responseId = "1%student1InCourse1@gmail.tmt%student1InCourse1@gmail.tmt";
        List<FeedbackResponseCommentAttributes> frcasExpected = new ArrayList<>();
        frcasExpected.add(frcaData);

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> frcas = frcDb.getFeedbackResponseCommentsForResponse(responseId);
        verifyListsContainSameResponseCommentAttributes(new ArrayList<>(frcasExpected), frcas);
    }

    private void testUpdateFeedbackResponseComment() throws Exception {

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> frcDb.updateFeedbackResponseComment(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaTemp =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.createdAt = Instant.now();
        frcaTemp.commentText = "Update feedback response comment";
        frcDb.createEntity(frcaTemp);
        frcaTemp = frcDb.getFeedbackResponseComment(frcaTemp.feedbackResponseId,
                                 frcaTemp.commentGiver, frcaTemp.createdAt);

        FeedbackResponseCommentAttributes frcaExpected =
                frcDb.getFeedbackResponseComment(frcaTemp.courseId, frcaTemp.createdAt, frcaTemp.commentGiver);
        frcaExpected.commentText = "This is new Text";
        FeedbackResponseCommentAttributes updatedComment = frcDb.updateFeedbackResponseComment(
                FeedbackResponseCommentAttributes.updateOptionsBuilder(frcaExpected.getId())
                        .withCommentText("This is new Text")
                        .build()
        );
        assertEquals(frcaExpected.commentText, updatedComment.commentText);

        FeedbackResponseCommentAttributes frcaActual =
                frcDb.getFeedbackResponseComment(
                              frcaExpected.courseId, frcaExpected.createdAt, frcaExpected.commentGiver);

        frcaExpected.setId(frcaActual.getId());
        frcaExpected.feedbackQuestionId = frcaActual.feedbackQuestionId;
        assertEquals(frcaExpected.courseId, frcaActual.courseId);
        assertEquals(frcaExpected.commentText, frcaActual.commentText);

        frcDb.deleteFeedbackResponseComment(frcaActual.getId());

        ______TS("non-existent comment");

        FeedbackResponseCommentAttributes.UpdateOptions updateOptions =
                FeedbackResponseCommentAttributes.updateOptionsBuilder(-1L)
                        .withCommentText("This is new Text")
                        .build();
        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> frcDb.updateFeedbackResponseComment(updateOptions));
        assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + updateOptions, ednee.getMessage());
    }

    private void testGetFeedbackResponseCommentsForSession() {

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentsForSession(null, ""));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> frcDb.getFeedbackResponseCommentsForSession("", null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> actualFrcas =
                frcDb.getFeedbackResponseCommentsForSession(frcaData.courseId, frcaData.feedbackSessionName);
        List<FeedbackResponseCommentAttributes> expectedFrcas = new ArrayList<>();
        expectedFrcas.add(frcaData);
        expectedFrcas.add(anotherFrcaData);
        verifyListsContainSameResponseCommentAttributes(expectedFrcas, actualFrcas);
    }

    private void testUpdateFeedbackResponseCommentsGiverEmail()
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseCommentAttributes frcaDataOfNewGiver =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q3S1C1");
        String giverEmail = "frcdb.newGiver@email.com";
        String courseId = "frcdb.giver.courseId";
        Instant createdAt = Instant.now();
        frcaDataOfNewGiver.createdAt = createdAt;
        frcaDataOfNewGiver.commentText = "another comment for this response";
        frcaDataOfNewGiver.setId(null);
        frcaDataOfNewGiver.commentGiver = giverEmail;
        frcaDataOfNewGiver.courseId = courseId;
        frcDb.createEntity(frcaDataOfNewGiver);
        assertNotNull(frcDb.getFeedbackResponseComment(courseId, createdAt, giverEmail));

        ______TS("typical success case");

        String updatedEmail = "frcdb.updatedGiver@email.com";
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, updatedEmail);
        assertNull(frcDb.getFeedbackResponseComment(courseId, createdAt, giverEmail));
        assertNotNull(frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail));

        ______TS("Same email");

        FeedbackResponseCommentAttributes expectedFrca =
                frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail);
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, updatedEmail, updatedEmail);
        FeedbackResponseCommentAttributes actualFrca =
                frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail);
        assertEquals(actualFrca.courseId, expectedFrca.courseId);
        assertEquals(actualFrca.createdAt, expectedFrca.createdAt);
        assertEquals(actualFrca.commentGiver, expectedFrca.commentGiver);

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class,
                () -> frcDb.updateGiverEmailOfFeedbackResponseComments(null, giverEmail, updatedEmail));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, null, updatedEmail));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

        ae = assertThrows(AssertionError.class,
                () -> frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    @Test
    public void testDeleteFeedbackResponseComment() {

        ______TS("delete non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment(-123L));

        frcDb.deleteDocumentByCommentId(-123L);

        ______TS("typical success case");

        assertNotNull(frcDb.getFeedbackResponseComment(frcaData.getId()));

        frcDb.deleteFeedbackResponseComment(frcaData.getId());

        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));

        ______TS("delete again same comment");

        frcDb.deleteFeedbackResponseComment(frcaData.getId());

        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_byResponseId()
            throws InvalidParametersException, EntityAlreadyExistsException {

        ______TS("non-existent response id");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId("not_exist")
                        .build());

        ______TS("typical success case");

        // get another frc from data bundle and use it to create another feedback response
        FeedbackResponseCommentAttributes tempFrcaData =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        tempFrcaData.createdAt = Instant.now();
        tempFrcaData.commentText = "another comment for this response";
        // for some reason, the id is 0 instead of null. so we explicitly set it to be null
        tempFrcaData.setId(null);
        // set this comment to have the same responseId as frcaData
        String responseId = "1%student1InCourse1@gmail.com%student1InCourse1@gmail.com";
        tempFrcaData.feedbackResponseId = responseId;
        frcDb.createEntity(tempFrcaData);

        // two comments exist in the DB
        assertFalse(frcDb.getFeedbackResponseCommentsForResponse(responseId).isEmpty());
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));

        // delete one
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(responseId)
                        .build());

        assertEquals(0, frcDb.getFeedbackResponseCommentsForResponse(responseId).size());
        // other irrelevant comment remains
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_byQuestionId() {
        ______TS("non-existent question id");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withQuestionId("not_exist")
                        .build());

        ______TS("typical success case");

        assertNotNull(frcDb.getFeedbackResponseComment(frcaData.getId()));

        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withQuestionId(frcaData.feedbackQuestionId)
                        .build());

        // comment deleted
        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
        // other irrelevant comment remains
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_byCourseIdAndSessionName() {
        ______TS("non-existent course");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId("course_not_exist")
                        .withFeedbackSessionName(frcaData.feedbackSessionName)
                        .build());

        ______TS("non-existent session");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frcaData.courseId)
                        .withFeedbackSessionName("session_not_exist")
                        .build());

        ______TS("non-existent course and session");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId("course_not_exist")
                        .withFeedbackSessionName("session_not_exist")
                        .build());

        ______TS("typical success case");

        assertFalse(
                frcDb.getFeedbackResponseCommentsForSession(frcaData.courseId, frcaData.feedbackSessionName).isEmpty());

        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frcaData.courseId)
                        .withFeedbackSessionName(frcaData.feedbackSessionName)
                        .build());

        assertEquals(0,
                frcDb.getFeedbackResponseCommentsForSession(frcaData.courseId, frcaData.feedbackSessionName).size());
    }

    @Test
    public void testDeleteFeedbackResponseComments_byCourseId() throws Exception {

        ______TS("non-existent course");

        // should pass silently
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId("course_not_exist")
                        .build());

        ______TS("typical success case");

        assertFalse(
                frcDb.getFeedbackResponseCommentsForSession(frcaData.courseId, frcaData.feedbackSessionName).isEmpty());
        // the two existing comment are in the same course
        assertEquals(frcaData.courseId, anotherFrcaData.courseId);
        assertNotNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
        assertNotNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));

        // create another comments different course
        FeedbackResponseCommentAttributes tempFrcaData =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        tempFrcaData.feedbackResponseId = "randomId";
        tempFrcaData.courseId = "anotherCourse";
        frcDb.createEntity(tempFrcaData);
        tempFrcaData = frcDb.getFeedbackResponseComment(tempFrcaData.feedbackResponseId,
                tempFrcaData.commentGiver, tempFrcaData.createdAt);
        assertNotNull(tempFrcaData);

        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withCourseId(frcaData.courseId)
                        .build());

        assertTrue(
                frcDb.getFeedbackResponseCommentsForSession(frcaData.courseId, frcaData.feedbackSessionName).isEmpty());
        // same course's comments are deleted
        assertNull(frcDb.getFeedbackResponseComment(frcaData.getId()));
        assertNull(frcDb.getFeedbackResponseComment(anotherFrcaData.getId()));
        // other course's data is not affected
        assertNotNull(frcDb.getFeedbackResponseComment(tempFrcaData.getId()));
    }

    @Test
    public void testDeleteFeedbackResponseComments_nullInput_shouldThrowException() {
        AssertionError ae = assertThrows(AssertionError.class, () -> frcDb.deleteFeedbackResponseComments(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
    }

    private void verifyListsContainSameResponseCommentAttributes(
            List<FeedbackResponseCommentAttributes> expectedFrcas,
            List<FeedbackResponseCommentAttributes> actualFrcas) {

        for (FeedbackResponseCommentAttributes frca : expectedFrcas) {
            frca.feedbackQuestionId = "";
            frca.feedbackResponseId = "";
            frca.setId(0L);
        }

        for (FeedbackResponseCommentAttributes frca : actualFrcas) {
            frca.feedbackQuestionId = "";
            frca.feedbackResponseId = "";
            frca.setId(0L);
        }

        AssertHelper.assertSameContentIgnoreOrder(expectedFrcas, actualFrcas);

    }

}
