package teammates.test.cases.storage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.api.EntitiesDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.util.TestHelper;

public class FeedbackResponseCommentsDbTest extends BaseComponentTestCase {

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    private static DataBundle dataBundle = getTypicalDataBundle();

    private static FeedbackResponseCommentAttributes frcaData = dataBundle.feedbackResponseComments
            .get("comment1FromT1C1ToR1Q1S1C1");
    private static String frId = dataBundle.feedbackResponseComments
            .get("comment1FromT1C1ToR1Q1S1C1").feedbackResponseId;

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponseCommentsDb.class);
        frcDb.createEntity(frcaData);
        frcaData = frcDb.getFeedbackResponseComment(frcaData.feedbackResponseId, 
                frcaData.giverEmail, frcaData.createdAt);
    }

    @Test
    public void testAll() throws Exception {
        
        testEntityCreationAndDeletion();
        
        testGetFeedbackResponseCommentFromId();
        
        testGetFeedbackResponseCommentFromCommentDetails();
        
        testUpdateFeedbackResponseComment();
        
        testGetFeedbackResponseCommentsForSession();
        
        testDeleteFeedbackResponseCommentsForResponse();
    }
    
    public void testEntityCreationAndDeletion() throws Exception {
        FeedbackResponseCommentAttributes frcaTemp = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q2S1C1");
        frcaTemp.createdAt = new Date();
        frcaTemp.commentText = new Text("test creation and deletion");
        frcDb.createEntity(frcaTemp);
        TestHelper.verifyPresentInDatastore(frcaTemp);

        frcDb.deleteEntity(frcaTemp);
        TestHelper.verifyAbsentInDatastore(frcaTemp);
    }
    
    public void testGetFeedbackResponseCommentFromId() throws Exception {
        
        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        ______TS("typical success case");

        FeedbackResponseCommentAttributes frcaExpected = frcDb
                .getFeedbackResponseComment(frcaData.courseId, frcaData.createdAt,
                        frcaData.giverEmail);

        FeedbackResponseCommentAttributes frcaActual = frcDb
                .getFeedbackResponseComment(frcaExpected.getId());

        assertEquals(frcaExpected.toString(), frcaActual.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment(-1L));
    }

    public void testGetFeedbackResponseCommentFromCommentDetails() throws Exception {

        ______TS("null parameter");

        try {
            frcDb.getFeedbackResponseComment(null, "", new Date());
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }

        try {
            frcDb.getFeedbackResponseComment("", null, new Date());
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
        FeedbackResponseCommentAttributes frca = frcDb
                .getFeedbackResponseComment(frId,
                        frcaExpected.giverEmail, frcaExpected.createdAt);

        // fill back the Ids
        frcaExpected.feedbackResponseId = frId;
        frcaExpected.setId(frca.getId());
        frcaExpected.feedbackQuestionId = frca.feedbackQuestionId;

        assertEquals(frcaExpected.toString(), frca.toString());

        ______TS("non-existent comment");

        assertNull(frcDb.getFeedbackResponseComment("123", frca.giverEmail,
                frca.createdAt));
    }

    public void testUpdateFeedbackResponseComment () throws Exception {
        
        ______TS("null parameter");
        
        try {
            frcDb.updateFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        ______TS("typical success case");
        
        FeedbackResponseCommentAttributes frcaExpected = frcaData;
        frcaExpected.setId(frcDb.getFeedbackResponseComment(frcaExpected.courseId, frcaExpected.createdAt, 
                frcaExpected.giverEmail).getId());
        frcaExpected.commentText = new Text("This is new Text");
        
        frcDb.updateFeedbackResponseComment(frcaExpected);
        
        FeedbackResponseCommentAttributes frcaActual = frcDb.getFeedbackResponseComment(
                frcaExpected.courseId, frcaExpected.createdAt,
                frcaExpected.giverEmail);
        
        frcaExpected.setId(frcaActual.getId());
        frcaExpected.feedbackQuestionId = frcaActual.feedbackQuestionId;
        assertEquals(frcaExpected.courseId, frcaActual.courseId);
        assertEquals(frcaExpected.commentText, frcaActual.commentText);
        
        ______TS("non-existent comment");
        
        frcaExpected.setId(-1L);
        
        try {
            frcDb.updateFeedbackResponseComment(frcaExpected);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException edne) {
            assertEquals(EntitiesDb.ERROR_UPDATE_NON_EXISTENT + frcaExpected.toString(), 
                    edne.getMessage());
        }
        
        // set responseId back
        frcaExpected.feedbackResponseId = frId;
        
        ______TS("invalid parameters");
        
        frcaExpected.courseId = "";
        frcaExpected.feedbackSessionName = "%asdt";
        frcaExpected.giverEmail = "test-no-at-funny.com";
        
        try {
            frcDb.updateFeedbackResponseComment(frcaExpected);
            signalFailureToDetectException();
        } catch (InvalidParametersException ipe) {
            assertEquals(StringHelper.toString(frcaExpected.getInvalidityInfo()), ipe.getMessage());
        }
    }

    public void testGetFeedbackResponseCommentsForSession() throws Exception {
        
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
        
        List<FeedbackResponseCommentAttributes> actualFrcas = frcDb.getFeedbackResponseCommentsForSession(
                frcaData.courseId, 
                frcaData.feedbackSessionName);
        
        verifyListsContainSameResponseCommentAttributes(
                new ArrayList<FeedbackResponseCommentAttributes>(dataBundle.feedbackResponseComments.values()), 
                actualFrcas);
    }
    
    public void testUpdateFeedbackResponseCommentsGiverEmail() throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseCommentAttributes frcaDataOfNewGiver = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q3S1C1");
        String giverEmail = "frcdb.newGiver@email.com";
        String courseId = "frcdb.giver.courseId";
        Date createdAt = new Date();
        frcaDataOfNewGiver.createdAt = createdAt;
        frcaDataOfNewGiver.commentText = new Text("another comment for this response");
        frcaDataOfNewGiver.setId(null);
        frcaDataOfNewGiver.giverEmail = giverEmail;
        frcaDataOfNewGiver.courseId = courseId;
        frcDb.createEntity(frcaDataOfNewGiver);
        assertNotNull(frcDb.getFeedbackResponseComment(courseId, createdAt, giverEmail));
        
        ______TS("typical success case");
        
        String updatedEmail = "frcdb.updatedGiver@email.com";;
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, giverEmail, updatedEmail);
        assertNull(frcDb.getFeedbackResponseComment(courseId, createdAt, giverEmail));
        assertNotNull(frcDb.getFeedbackResponseComment(courseId, createdAt, updatedEmail));
        
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

    public void testDeleteFeedbackResponseCommentsForResponse() throws InvalidParametersException, EntityAlreadyExistsException {
        
        ______TS("typical success case");
        
        // get another frc from data bundle and use it to create another feedback response
        FeedbackResponseCommentAttributes anotherFrcaData = dataBundle.feedbackResponseComments
                .get("comment1FromT1C1ToR1Q2S1C1");
        anotherFrcaData.createdAt = new Date();
        anotherFrcaData.commentText = new Text("another comment for this response");
        // for some reason, the id is 0 instead of null. so we explicitly set it to be null
        anotherFrcaData.setId(null);
        // set this comment to have the same responseId as frcaData
        String responseId = "1%student1InCourse1@gmail.com%student1InCourse1@gmail.com";
        anotherFrcaData.feedbackResponseId = responseId;
        frcDb.createEntity(anotherFrcaData);
        
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
        
        TestHelper.isSameContentIgnoreOrder(expectedFrcas, actualFrcas);
        
    }

    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(FeedbackResponseCommentsDb.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }

}
