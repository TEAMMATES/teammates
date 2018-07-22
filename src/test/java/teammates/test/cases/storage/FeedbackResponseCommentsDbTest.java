package teammates.test.cases.storage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
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
        frcaTemp.commentText = new Text("test creation and deletion");

        ______TS("Entity creation");

        frcDb.createEntity(frcaTemp);
        verifyPresentInDatastore(frcaTemp);

        ______TS("Entity deletion");

        frcDb.deleteEntity(frcaTemp);
        verifyAbsentInDatastore(frcaTemp);
    }

    private void testGetFeedbackResponseCommentFromId() {

        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

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

        try {
            frcDb.getFeedbackResponseComment(null, "", Instant.now());
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseComment("", null, Instant.now());
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseComment("", "", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

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

        try {
            frcDb.getFeedbackResponseCommentForGiver(null, frcaData.commentGiver);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseCommentForGiver(frcaData.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

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
        ArrayList<FeedbackResponseCommentAttributes> frcasExpected = new ArrayList<>();
        frcasExpected.add(frcaData);

        ______TS("typical success case");

        List<FeedbackResponseCommentAttributes> frcas = frcDb.getFeedbackResponseCommentsForResponse(responseId);
        verifyListsContainSameResponseCommentAttributes(new ArrayList<>(frcasExpected), frcas);
    }

    private void testUpdateFeedbackResponseComment() throws Exception {

        ______TS("null parameter");

        try {
            frcDb.updateFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaTemp =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.createdAt = Instant.now();
        frcaTemp.commentText = new Text("Update feedback response comment");
        frcDb.createEntity(frcaTemp);
        frcaTemp = frcDb.getFeedbackResponseComment(frcaTemp.feedbackResponseId,
                                 frcaTemp.commentGiver, frcaTemp.createdAt);

        FeedbackResponseCommentAttributes frcaExpected =
                frcDb.getFeedbackResponseComment(frcaTemp.courseId, frcaTemp.createdAt, frcaTemp.commentGiver);
        frcaExpected.commentText = new Text("This is new Text");
        frcDb.updateFeedbackResponseComment(frcaExpected);

        FeedbackResponseCommentAttributes frcaActual =
                frcDb.getFeedbackResponseComment(
                              frcaExpected.courseId, frcaExpected.createdAt, frcaExpected.commentGiver);

        frcaExpected.setId(frcaActual.getId());
        frcaExpected.feedbackQuestionId = frcaActual.feedbackQuestionId;
        assertEquals(frcaExpected.courseId, frcaActual.courseId);
        assertEquals(frcaExpected.commentText, frcaActual.commentText);

        frcDb.deleteEntity(frcaTemp);

        ______TS("non-existent comment");

        frcaExpected.setId(-1L);

        try {
            frcDb.updateFeedbackResponseComment(frcaExpected);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + frcaExpected.toString(), edne.getMessage());
        }

        // set responseId back
        frcaExpected.feedbackResponseId = frId;

        ______TS("invalid parameters");

        frcaExpected.courseId = "";
        frcaExpected.feedbackSessionName = "%asdt";
        frcaExpected.commentGiver = "test-no-at-funny.com";
        frcaExpected.commentGiverType = FeedbackParticipantType.NONE;

        try {
            frcDb.updateFeedbackResponseComment(frcaExpected);
            signalFailureToDetectException();
        } catch (InvalidParametersException ipe) {
            assertEquals(StringHelper.toString(frcaExpected.getInvalidityInfo()), ipe.getMessage());
        }
    }

    private void testGetFeedbackResponseCommentsForSession() {

        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseCommentsForSession(null, "");
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseCommentsForSession("", null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

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
        frcaDataOfNewGiver.commentText = new Text("another comment for this response");
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

        try {
            frcDb.updateGiverEmailOfFeedbackResponseComments(null, giverEmail, updatedEmail);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, null, updatedEmail);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
    }

    private void testDeleteFeedbackResponseCommentsForResponse()
            throws InvalidParametersException, EntityAlreadyExistsException {

        ______TS("typical success case");

        // get another frc from data bundle and use it to create another feedback response
        FeedbackResponseCommentAttributes tempFrcaData =
                dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q2S1C1");
        tempFrcaData.createdAt = Instant.now();
        tempFrcaData.commentText = new Text("another comment for this response");
        // for some reason, the id is 0 instead of null. so we explicitly set it to be null
        tempFrcaData.setId(null);
        // set this comment to have the same responseId as frcaData
        String responseId = "1%student1InCourse1@gmail.com%student1InCourse1@gmail.com";
        tempFrcaData.feedbackResponseId = responseId;
        frcDb.createEntity(tempFrcaData);

        frcDb.deleteFeedbackResponseCommentsForResponse(responseId);
        assertEquals(frcDb.getFeedbackResponseCommentsForResponse(responseId).size(), 0);

        ______TS("null parameter");

        try {
            frcDb.deleteFeedbackResponseCommentsForResponse(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
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
