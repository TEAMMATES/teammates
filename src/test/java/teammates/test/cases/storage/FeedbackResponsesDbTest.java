package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackAbstractResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class FeedbackResponsesDbTest extends BaseComponentTestCase {
    
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponsesDb.class);
    }
    
    
    @Test
    public void testCreateDeleteFeedbackResponse() 
            throws InvalidParametersException, EntityAlreadyExistsException {    
                
        ______TS("standard success case");
        
        FeedbackResponseAttributes fra = getNewFeedbackResponseAttributes();
        frDb.createEntity(fra);
        TestHelper.verifyPresentInDatastore(fra, true);
        
        ______TS("duplicate - with same id.");
        
        fra.setId(frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giverEmail, fra.recipientEmail).getId());
        try {
            frDb.createEntity(fra);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(FeedbackResponsesDb.
                    ERROR_CREATE_ENTITY_ALREADY_EXISTS, fra.getEntityTypeAsString())
                    + fra.getIdentificationString(), e.getMessage());
        }
        
        ______TS("delete - with id specified");
        
        frDb.deleteEntity(fra);
        TestHelper.verifyAbsentInDatastore(fra);
        
        ______TS("null params");
        
        try {
            frDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("invalid params");
        
        try {
            fra.giverEmail = "haha";            
            frDb.createEntity(fra);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid answerer's email", e.getLocalizedMessage());
        }
        
    }

    @Test
    public void testGetFeedbackResponses() throws Exception {
        
        restoreTypicalDataInDatastore();        
        DataBundle dataBundle = getTypicalDataBundle();
        
        ______TS("standard success case");    
        
        FeedbackResponseAttributes expected =
                dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        expected.feedbackQuestionId = fqa.getId();
        
        FeedbackResponseAttributes actual =
                frDb.getFeedbackResponse(fqa.getId(), "student1InCourse1@gmail.com","student1InCourse1@gmail.com");
        
        assertEquals(expected.toString(), actual.toString());
        
        ______TS("non-existent response");
        
        assertNull(frDb.getFeedbackResponse(fqa.getId(), "student1InCourse1@gmail.com","student3InCourse1@gmail.com"));
        
        ______TS("null fqId");
        
        try {
            frDb.getFeedbackResponse(null, "student1InCourse1@gmail.com","student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("null giverEmail");
        
        try {
            frDb.getFeedbackResponse(fqa.getId(), null,"student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("null receiverEmail");
        
        try {
            frDb.getFeedbackResponse(fqa.getId(), "student1InCourse1@gmail.com",null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("get by id");
        
        actual = frDb.getFeedbackResponse(actual.getId());//Id from first success case
        
        assertEquals(expected.toString(), actual.toString());
        
        ______TS("get non-existent response by id");
        
        actual = frDb.getFeedbackResponse("non-existent id");
        
        assertNull(actual);
        
    }
    
    @Test
    public void testGetFeedbackResponsesForQuestion() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestion(fqa.getId());
        
        assertEquals(responses.size(), 2);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForQuestion(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesForQuestion("non-existent fq id").isEmpty());
            
        ______TS("no responses for question");
        
        fqa = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 4);
        
        assertTrue(frDb.getFeedbackResponsesForQuestion(fqa.getId()).isEmpty());    
        
    }

    @Test
    public void testGetFeedbackResponsesForQuestionInSection() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestionInSection(fqa.getId(), "Section 1");
        
        assertEquals(responses.size(), 2);

        ______TS("No responses as they are filtered out");

        responses = frDb.getFeedbackResponsesForQuestionInSection(fqa.getId(), "Section 2");
        
        assertEquals(responses.size(), 0);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForQuestionInSection(null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForQuestionInSection(fqa.getId(), null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesForQuestionInSection("non-existent fq id", "Section 1").isEmpty());
            
        ______TS("no responses for question");
        
        fqa = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 4);
        
        assertTrue(frDb.getFeedbackResponsesForQuestionInSection(fqa.getId(), "Section 1").isEmpty());    
        
    }
    
    @Test
    public void testGetFeedbackResponsesForSession() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSession(fqa.feedbackSessionName, fqa.courseId);
        
        assertEquals(responses.size(), 6);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForSession(null, fqa.courseId);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForSession(fqa.feedbackSessionName, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback session");
        
        assertTrue(frDb.getFeedbackResponsesForSession("non-existent feedback session", fqa.courseId).isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSession(fqa.feedbackSessionName, "non-existent courseId").isEmpty());
        
        ______TS("no responses for session");
        
        assertTrue(frDb.getFeedbackResponsesForSession("Empty feedback session", "idOfTypicalCourse1").isEmpty());    
        
    }
    
    @Test
    public void testGetFeedbackResponsesForReceiverForQuestion() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = 
                frDb.getFeedbackResponsesForReceiverForQuestion(fqa.getId(),
                        "student1InCourse1@gmail.com");
        
        assertEquals(responses.size(), 1);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForReceiverForQuestion(null, "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForReceiverForQuestion(fqa.getId(), null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion("non-existent fq id", "student1InCourse1@gmail.com").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion(fqa.getId(), "non-existentStudentInCourse1@gmail.com").isEmpty());
        
        ______TS("no responses for question for receiver");
        
        fqa = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 4);
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion(fqa.getId(), "student1InCourse1@gmail.com").isEmpty());    
        
    }

    @Test
    public void testGetFeedbackResponsesForReceiverForQuestionInSection() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = 
                frDb.getFeedbackResponsesForReceiverForQuestionInSection(fqa.getId(),
                        "student1InCourse1@gmail.com", "Section 1");
        
        assertEquals(responses.size(), 1);

        ______TS("No responses as they are filtered out");  
        
        responses = 
                frDb.getFeedbackResponsesForReceiverForQuestionInSection(fqa.getId(),
                        "student1InCourse1@gmail.com", "Section 2");
        
        assertEquals(responses.size(), 0);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                                null, "student1InCourse1@gmail.com", "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForReceiverForQuestionInSection(fqa.getId(), null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                                fqa.getId(), "student1InCourse1@gmail.com", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                "non-existent fq id", "student1InCourse1@gmail.com", "Section 1").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                fqa.getId(), "non-existentStudentInCourse1@gmail.com", "Section 1").isEmpty());
        
        ______TS("no responses for question for receiver");
        
        fqa = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 4);
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestionInSection(
                                fqa.getId(), "student1InCourse1@gmail.com", "Section 1").isEmpty());    
        
    }
    
    @Test
    public void testGetFeedbackResponsesForReceiverForCourse() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = 
                frDb.getFeedbackResponsesForReceiverForCourse(fqa.courseId,
                        "student1InCourse1@gmail.com");
        
        assertEquals(responses.size(), 2);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForReceiverForCourse(null, "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForReceiverForCourse(fqa.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse("non-existent courseId", "student1InCourse1@gmail.com").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse(fqa.courseId, "non-existentStudentInCourse1@gmail.com").isEmpty());
        
        ______TS("no responses for receiver for course");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse(fqa.courseId, "student3InCourse1@gmail.com").isEmpty());    
        
    }
    
    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = 
                frDb.getFeedbackResponsesFromGiverForQuestion(fqa.getId(),
                        "student1InCourse1@gmail.com");
        
        assertEquals(responses.size(), 1);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesFromGiverForQuestion(null, "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesFromGiverForQuestion(fqa.getId(), null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion("non-existent fq id", "student1InCourse1@gmail.com").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion(fqa.getId(), "non-existentStudentInCourse1@gmail.com").isEmpty());
        
        ______TS("no responses from giver for question");
        
        fqa = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 4);
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion(fqa.getId(), "student1InCourse1@gmail.com").isEmpty());    
        
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestionInSection() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = 
                frDb.getFeedbackResponsesFromGiverForQuestionInSection(fqa.getId(),
                        "student1InCourse1@gmail.com", "Section 1");
        
        assertEquals(responses.size(), 1);
        
        ______TS("No reponses as they are filtered out");

        responses = 
                frDb.getFeedbackResponsesFromGiverForQuestionInSection(fqa.getId(),
                        "student1InCourse1@gmail.com", "Section 2");
        
        assertEquals(responses.size(), 0);

        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                                            null, "student1InCourse1@gmail.com", "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesFromGiverForQuestionInSection(fqa.getId(), null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        try {
            frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                                            fqa.getId(), "student1InCourse1@gmail.com", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                            "non-existent fq id", "student1InCourse1@gmail.com", "Section 1").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                            fqa.getId(), "non-existentStudentInCourse1@gmail.com", "Section 1").isEmpty());
        
        ______TS("no responses from giver for question");
        
        fqa = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 4);
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestionInSection(
                            fqa.getId(), "student1InCourse1@gmail.com", "Section 1").isEmpty());    
        
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForCourse() throws Exception {
        
        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = 
                frDb.getFeedbackResponsesFromGiverForCourse(fqa.courseId,
                        "student1InCourse1@gmail.com");
        
        assertEquals(responses.size(), 3);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesFromGiverForCourse(null, "student1InCourse1@gmail.com");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesFromGiverForCourse(fqa.courseId, null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback question");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse("non-existent courseId", "student1InCourse1@gmail.com").isEmpty());
        
        ______TS("non-existent giver");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse(fqa.courseId, "non-existentStudentInCourse1@gmail.com").isEmpty());
        
        ______TS("no responses from giver for course");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse(fqa.courseId, "student5InCourse1@gmail.com").isEmpty());    
        
    }

    @Test
    public void testGetFeedbackResponsesForSessionWithinRange() throws Exception {

        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionWithinRange(fqa.feedbackSessionName, fqa.courseId, 4);
        
        assertEquals(responses.size(), 4);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForSessionWithinRange(null, fqa.courseId, 5);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForSessionWithinRange(fqa.feedbackSessionName, null, 4);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange("non-existent feedback session", fqa.courseId, 1).isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange(fqa.feedbackSessionName, "non-existent courseId", 1).isEmpty());
        
        ______TS("no responses for session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange("Empty feedback session", "idOfTypicalCourse1", 1).isEmpty()); 
    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection() throws Exception {

        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("Second feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionInSection(fqa.feedbackSessionName, fqa.courseId, "Section 1");
        
        assertEquals(responses.size(), 3);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForSessionInSection(null, fqa.courseId, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForSessionInSection(fqa.feedbackSessionName, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionInSection("non-existent feedback session", fqa.courseId, "Section 1").isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionInSection(fqa.feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
        
        ______TS("no responses for session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionInSection("Empty feedback session", "idOfTypicalCourse1", "Section 1").isEmpty()); 
    }

    @Test 
    public void testGetFeedbackResponsesForSessionFromSection() throws Exception {

        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("Second feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionFromSection(fqa.feedbackSessionName, fqa.courseId, "Section 1");
        
        assertEquals(responses.size(), 3);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForSessionFromSection(null, fqa.courseId, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForSessionFromSection(fqa.feedbackSessionName, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionFromSection("non-existent feedback session", fqa.courseId, "Section 1").isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionFromSection(fqa.feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
        
        ______TS("no responses for session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionFromSection("Empty feedback session", "idOfTypicalCourse1", "Section 1").isEmpty()); 
    }

    @Test 
    public void testGetFeedbackResponsesForSessionToSection() throws Exception {

        restoreTypicalDataInDatastore();
        
        ______TS("standard success case");  
        
        FeedbackQuestionAttributes fqa = 
                fqDb.getFeedbackQuestion("Second feedback session", "idOfTypicalCourse1", 1);
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionToSection(fqa.feedbackSessionName, fqa.courseId, "Section 2");
        
        assertEquals(responses.size(), 1);
        
        ______TS("null params");
        
        try {
            frDb.getFeedbackResponsesForSessionToSection(null, fqa.courseId, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        try {
            frDb.getFeedbackResponsesForSessionToSection(fqa.feedbackSessionName, null, "Section 1");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existent feedback session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionToSection("non-existent feedback session", fqa.courseId, "Section 1").isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionToSection(fqa.feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
        
        ______TS("no responses for session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionToSection("Empty feedback session", "idOfTypicalCourse1", "Section 1").isEmpty()); 
    }


    
    @SuppressWarnings("static-access")
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
        
        FeedbackResponseAttributes invalidFra = getNewFeedbackResponseAttributes();
        frDb.deleteEntity(invalidFra);
        frDb.createEntity(invalidFra);
        invalidFra.setId(frDb.getFeedbackResponse("testFeedbackQuestionId", invalidFra.giverEmail, invalidFra.recipientEmail).getId());
        invalidFra.giverEmail = "haha";
        try {
            frDb.updateFeedbackResponse(invalidFra);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid answerer's email", e.getLocalizedMessage());
        }
        
        ______TS("feedback response does not exist");
        
        FeedbackResponseAttributes nonexistantFr = getNewFeedbackResponseAttributes();
        nonexistantFr.setId("non-existent fr id");
        try {
            frDb.updateFeedbackResponse(nonexistantFr);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(FeedbackResponsesDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
        }
        
        ______TS("standard success case");
        
        FeedbackResponseAttributes modifiedResponse = getNewFeedbackResponseAttributes();
        frDb.deleteEntity(modifiedResponse);
        frDb.createEntity(modifiedResponse);
        TestHelper.verifyPresentInDatastore(modifiedResponse, true);
        
        modifiedResponse = frDb.getFeedbackResponse(modifiedResponse.feedbackQuestionId, modifiedResponse.giverEmail, modifiedResponse.recipientEmail);
        FeedbackAbstractResponseDetails frd = modifiedResponse.getResponseDetails();
        String answer[] = {"New answer text!"};
        frd = frd.createResponseDetails(
                    answer, FeedbackQuestionType.TEXT,
                    null);
        modifiedResponse.setResponseDetails(frd);
        frDb.updateFeedbackResponse(modifiedResponse);
        
        TestHelper.verifyPresentInDatastore(modifiedResponse);
        modifiedResponse = frDb.getFeedbackResponse(modifiedResponse.feedbackQuestionId, modifiedResponse.giverEmail, modifiedResponse.recipientEmail);
        assertEquals("New answer text!", modifiedResponse.getResponseDetails().getAnswerString());
        
    }
    
    private FeedbackResponseAttributes getNewFeedbackResponseAttributes() {
        FeedbackResponseAttributes fra = new FeedbackResponseAttributes();
        
        fra.feedbackSessionName = "fsTest1";
        fra.courseId = "testCourse";
        fra.feedbackQuestionType = FeedbackQuestionType.TEXT;
        fra.giverEmail = "giver@email.com";
        fra.giverSection = "None";
        fra.recipientEmail = "recipient@email.com";
        fra.recipientSection = "None";
        fra.feedbackQuestionId = "testFeedbackQuestionId";
        
        FeedbackAbstractResponseDetails responseDetails = new FeedbackTextResponseDetails("Text response");
        fra.setResponseDetails(responseDetails);
        
        fra.setId("*");
        
        return fra;
    }
    
    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(FeedbackResponsesDb.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
}
