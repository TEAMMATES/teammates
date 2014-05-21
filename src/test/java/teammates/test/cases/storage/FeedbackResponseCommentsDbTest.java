package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.util.Date;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.util.TestHelper;

public class FeedbackResponseCommentsDbTest  extends BaseComponentTestCase {
    
    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    private static final FeedbackQuestionAttributes fqaData = dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
    private static final FeedbackResponseAttributes fraData = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
    private static final FeedbackResponseCommentAttributes frcaData = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
    private static String fqId = null;
    private static String frId = null;
    private static String frcId = null;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponseCommentsDb.class);
    }
    
    @Test
    public void testGetFeedbackResponseCommentFromId() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("null parameter");
        
        try {
            frcDb.getFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        ______TS("typical success case");
        
        FeedbackResponseCommentAttributes frcaExpected = frcDb.getFeedbackResponseComment(getResponseId(),
                frcaData.giverEmail, frcaData.createdAt);
        
        FeedbackResponseCommentAttributes frcaActual = frcDb.getFeedbackResponseComment(frcaExpected.getId());
        
        assertEquals(frcaExpected.toString(), frcaActual.toString());
        
        ______TS("non-existent comment");
        
        assertNull(frcDb.getFeedbackResponseComment(-1L));
    }
    
    @Test
    public void testGetFeedbackResponseCommentFromCommentDetails() throws Exception {
        
        restoreTypicalDataInDatastore();
        dataBundle = getTypicalDataBundle();
        
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
        FeedbackResponseCommentAttributes frca = frcDb.getFeedbackResponseComment(getResponseId(),
                frcaExpected.giverEmail, frcaExpected.createdAt);
        
        // fill back the Ids
        frcaExpected.feedbackResponseId = getResponseId();
        frcaExpected.setId(frca.getId());
        frcaExpected.feedbackQuestionId = frca.feedbackQuestionId;
        
        assertEquals(frcaExpected.toString(), frca.toString());

        ______TS("non-existent comment");
        
        assertNull(frcDb.getFeedbackResponseComment("123", frca.giverEmail, frca.createdAt));
    }
    
    @Test
    public void testGetFeedbackResponseCommentsForSession() {
        
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
        
        
        
        // TODO: implement this
    }
    
    @Test
    public void testUpdateFeedbackResponseComment () throws Exception {
        
        ______TS("null parameter");
        
        try {
            frcDb.updateFeedbackResponseComment(null);
            signalFailureToDetectException();
        } catch (AssertionError ae) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, ae.getMessage());
        }
        
        // TODO: implement this
    }
    
    @Test
    public void testCreateEntity() throws Exception {        
        FeedbackResponseCommentAttributes frcaTemp = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        frcDb.createEntity(frcaTemp);
        TestHelper.verifyPresentInDatastore(frcaTemp);
        
        frcDb.deleteEntity(frcaTemp);
        TestHelper.verifyAbsentInDatastore(frcaTemp);
    }
    
    private String getQuestionId() {
        if (fqId == null) {
            fqId = fqDb.getFeedbackQuestion(fqaData.feedbackSessionName, 
                    fqaData.courseId, fqaData.questionNumber).getId();
        }
        
        return fqId;
    }
    
    private String getResponseId() {
        if (frId == null) {
            frId = frDb.getFeedbackResponse(getQuestionId(), 
                    fraData.giverEmail, fraData.recipientEmail).getId();
        }
        
        return frId;
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
