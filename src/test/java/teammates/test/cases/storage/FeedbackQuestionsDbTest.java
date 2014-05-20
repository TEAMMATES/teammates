package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackTextQuestionDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class FeedbackQuestionsDbTest extends BaseComponentTestCase {
    
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackQuestionsDb.class);
    }
    
    
    @Test
    public void testCreateDeleteFeedbackQuestion() 
            throws InvalidParametersException, EntityAlreadyExistsException {    
        
        ______TS("standard success case");
        
        FeedbackQuestionAttributes fqa = getNewFeedbackQuestionAttributes();
        fqDb.createEntity(fqa);
        TestHelper.verifyPresentInDatastore(fqa, true);
      
        ______TS("duplicate - with same id.");
        fqa.setId(fqDb.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber).getId());
        try {
            fqDb.createEntity(fqa);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(FeedbackQuestionsDb.
                    ERROR_CREATE_ENTITY_ALREADY_EXISTS, fqa.getEntityTypeAsString())
                    + fqa.getIdentificationString(), e.getMessage());
        }
        
        ______TS("delete - with id specified");
        fqDb.deleteEntity(fqa);
        TestHelper.verifyAbsentInDatastore(fqa);
        
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
        
        restoreTypicalDataInDatastore();        
        DataBundle dataBundle = getTypicalDataBundle();
        
        ______TS("standard success case");    
        
        FeedbackQuestionAttributes expected =
                dataBundle.feedbackQuestions.get("qn1InSession1InCourse1");
        FeedbackQuestionAttributes actual =
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        assertEquals(expected.toString(), actual.toString());
        
        ______TS("non-existant question");
        
        assertNull(fqDb.getFeedbackQuestion( "Non-existant feedback session", "non-existent-course", 1));
        
        ______TS("null fsName");
        
        try {
            fqDb.getFeedbackQuestion(null, "idOfTypicalCourse1", 1);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("null courseId");
        
        try {
            fqDb.getFeedbackQuestion("First feedback session", null, 1);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("get by id");
        
        actual = fqDb.getFeedbackQuestion(actual.getId());//Id from first success case
        
        assertEquals(expected.toString(), actual.toString());
        
        ______TS("get non-existent question by id");
        
        actual = fqDb.getFeedbackQuestion("non-existent id");
        
        assertNull(actual);
        
    }
    
    @Test
    public void testGetFeedbackQuestionsForSession() throws Exception {
        
        restoreTypicalDataInDatastore();        
        DataBundle dataBundle = getTypicalDataBundle();
        
        ______TS("standard success case");    
        
        List<FeedbackQuestionAttributes> questions = fqDb.getFeedbackQuestionsForSession("First feedback session" ,"idOfTypicalCourse1");
        
        // RemoveIrrelevantVisibilityOptions, so that the test cases matches.
        // This is done when creating the feedback question in fqLogic.
        for(FeedbackQuestionAttributes fqa : dataBundle.feedbackQuestions.values()){
            fqa.removeIrrelevantVisibilityOptions();
        }
        
        String expected =
                dataBundle.feedbackQuestions.get("qn1InSession1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackQuestions.get("qn2InSession1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackQuestions.get("qn3InSession1InCourse1").toString() + Const.EOL +                
                dataBundle.feedbackQuestions.get("qn4InSession1InCourse1").toString() + Const.EOL;
        
        for (FeedbackQuestionAttributes fqa : questions) {
            AssertHelper.assertContains(fqa.toString(), expected);
        }
        assertEquals(questions.size(),4);
        
        ______TS("null params");
        
        try {
            fqDb.getFeedbackQuestionsForSession(null, "idOfTypicalCourse1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            fqDb.getFeedbackQuestionsForSession("First feedback session", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existant session");
        
        assertTrue(fqDb.getFeedbackQuestionsForSession("non-existant session", "idOfTypicalCourse1").isEmpty());
            
        ______TS("no questions in session");
        
        assertTrue(fqDb.getFeedbackQuestionsForSession("Empty session", "idOfTypicalCourse1").isEmpty());    
        
    }
    
    @Test
    public void testGetFeedbackQuestionsForGiverType() throws Exception {
        
        restoreTypicalDataInDatastore();        
        DataBundle dataBundle = getTypicalDataBundle();
        
        //TODO: implement.
    }
    
    @Test
    public void testUpdateFeedbackSession() throws Exception {
        //TODO: implement.
    }
    
    private FeedbackQuestionAttributes getNewFeedbackQuestionAttributes() {
        FeedbackQuestionAttributes fqa = new FeedbackQuestionAttributes();
       
        fqa.courseId = "testCourse";
        fqa.creatorEmail = "instructor@email.com";
        fqa.feedbackSessionName = "testFeedbackSession";
        fqa.giverType = FeedbackParticipantType.SELF;
        fqa.recipientType = FeedbackParticipantType.SELF;
        fqa.numberOfEntitiesToGiveFeedbackTo = 1;
        
        FeedbackTextQuestionDetails questionDetails = new FeedbackTextQuestionDetails("Question text.");
        fqa.setQuestionDetails(questionDetails);
        
        fqa.showGiverNameTo =  new ArrayList<FeedbackParticipantType>();
        fqa.showRecipientNameTo = new ArrayList<FeedbackParticipantType>();
        fqa.showResponsesTo = new ArrayList<FeedbackParticipantType>();
        
        fqa.setId("*");//Use a wildcard for Id. Auto generated when persisting in Db.
        
        return fqa;
    }
    
    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(FeedbackQuestionsDb.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
}
