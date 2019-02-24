package teammates.test.cases.storage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
    private DataBundle dataBundle = getTypicalDataBundle();

    private FeedbackResponseCommentAttributes frcaData =
            dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
    private String frId = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1").feedbackResponseId;
    private FeedbackResponseCommentAttributes anotherFrcaData =
            dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
    private List<FeedbackResponseCommentAttributes> frcasData = new ArrayList<>();

    @BeforeClass
    public void classSetup() throws Exception {
        frcDb.createEntity(frcaData);
        frcDb.createEntity(anotherFrcaData);
        frcaData = frcDb.getFeedbackResponseComment(frcaData.feedbackResponseId,
                                 frcaData.commentGiver, frcaData.createdAt);
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

        testDeleteFeedbackResponseCommentsForResponse();

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

        frcDb.deleteEntity(frcaTemp);
        verifyAbsentInDatastore(frcaTemp);
    }

    private void testGetFeedbackResponseCommentFromId() {

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> frcDb.getFeedbackResponseComment(null));
        assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());

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

        frcDb.deleteEntity(frcaTemp);

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

    private void testDeleteFeedbackResponseCommentsForResponse()
            throws InvalidParametersException, EntityAlreadyExistsException {

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

        frcDb.deleteFeedbackResponseCommentsForResponse(responseId);
        assertEquals(frcDb.getFeedbackResponseCommentsForResponse(responseId).size(), 0);

        ______TS("null parameter");

        AssertionError ae = assertThrows(AssertionError.class, () -> frcDb.deleteFeedbackResponseCommentsForResponse(null));
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
