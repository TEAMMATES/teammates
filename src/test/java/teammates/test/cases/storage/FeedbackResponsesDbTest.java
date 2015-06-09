package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseDetails;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackTextResponseDetails;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class FeedbackResponsesDbTest extends BaseComponentTestCase {
    
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static HashMap<String, FeedbackResponseAttributes> fras;
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponsesDb.class); 
        addResponsesToDb();
        fras = dataBundle.feedbackResponses;
    }
    
    private static void addResponsesToDb() throws Exception {
        Set<String> keys = dataBundle.feedbackResponses.keySet();
        for (String i : keys) {
            frDb.createEntity(dataBundle.feedbackResponses.get(i));
        }
    }


    @Test
    public void testCreateDeleteFeedbackResponse() 
            throws InvalidParametersException, EntityAlreadyExistsException {    
        
        ______TS("standard success case");
        
        FeedbackResponseAttributes fra = getNewFeedbackResponseAttributes();
        frDb.createEntity(fra);
        
        // sets the id for fra
        TestHelper.verifyPresentInDatastore(fra, true);
        
        ______TS("duplicate - with same id.");
        
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
        
        ______TS("standard success case");    
        
        FeedbackResponseAttributes expected = getResponseAttributes("response1ForQ1S1C1");
        
        FeedbackResponseAttributes actual =
                frDb.getFeedbackResponse(expected.feedbackQuestionId, expected.giverEmail, expected.recipientEmail);
        
        assertEquals(expected.toString(), actual.toString());
        
        ______TS("non-existent response");
        
        assertNull(frDb.getFeedbackResponse(expected.feedbackQuestionId, "student1InCourse1@gmail.tmt","student3InCourse1@gmail.tmt"));
        
        ______TS("null fqId");
        
        try {
            frDb.getFeedbackResponse(null, "student1InCourse1@gmail.tmt","student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("null giverEmail");
        
        try {
            frDb.getFeedbackResponse(expected.feedbackQuestionId, null,"student1InCourse1@gmail.tmt");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("null receiverEmail");
        
        try {
            frDb.getFeedbackResponse(expected.feedbackQuestionId, "student1InCourse1@gmail.tmt",null);
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
        
        ______TS("standard success case");
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestion(fras.get("response1ForQ1S1C1").feedbackQuestionId);
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
    public void testGetFeedbackResponsesForQuestionInSection() throws Exception {
        
        ______TS("standard success case");  
        
        String questionId = fras.get("response1ForQ1S1C1").feedbackQuestionId;
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForQuestionInSection(questionId, "Section 1");
        
        assertEquals(3, responses.size());

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
    public void testGetFeedbackResponsesForSession() throws Exception {
        
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
    public void testGetFeedbackResponsesForReceiverForQuestion() throws Exception {
        
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
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion("non-existent fq id", "student1InCourse1@gmail.tmt").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForQuestion(questionId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());        
    }

    @Test
    public void testGetFeedbackResponsesForReceiverForQuestionInSection() throws Exception {
        
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
    public void testGetFeedbackResponsesForReceiverForCourse() throws Exception {
        
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
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse("non-existent courseId", "student1InCourse1@gmail.tmt").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesForReceiverForCourse(courseId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());        
    }
    
    @Test
    public void testGetFeedbackResponsesFromGiverForQuestion() throws Exception {
                
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
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion("non-existent fq id", "student1InCourse1@gmail.tmt").isEmpty());
        
        ______TS("non-existent receiver");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForQuestion(questionId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());        
    }

    @Test
    public void testGetFeedbackResponsesFromGiverForQuestionInSection() throws Exception {
        
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
    public void testGetFeedbackResponsesFromGiverForCourse() throws Exception {
        
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
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse("non-existent courseId", "student1InCourse1@gmail.tmt").isEmpty());
        
        ______TS("non-existent giver");
        
        assertTrue(frDb.getFeedbackResponsesFromGiverForCourse(courseId, "non-existentStudentInCourse1@gmail.tmt").isEmpty());        
    }

    @Test
    public void testGetFeedbackResponsesForSessionWithinRange() throws Exception {

        ______TS("standard success case");  
        
        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, courseId, 1);
        
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
        
        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange("non-existent feedback session", courseId, 1).isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionWithinRange(feedbackSessionName, "non-existent courseId", 1).isEmpty());
    }

    @Test
    public void testGetFeedbackResponsesForSessionInSection() throws Exception {

        
        
        ______TS("standard success case");  
        
        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, courseId, "Section 1");
        
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
        
        assertTrue(frDb.getFeedbackResponsesForSessionInSection("non-existent feedback session", courseId, "Section 1").isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionInSection(feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
    }

    @Test 
    public void testGetFeedbackResponsesForSessionFromSection() throws Exception {
        
        ______TS("standard success case");  
        
        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, courseId, "Section 2");
        
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
        
        assertTrue(frDb.getFeedbackResponsesForSessionFromSection("non-existent feedback session", courseId, "Section 1").isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionFromSection(feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
        
        ______TS("no responses for session");
        
        assertTrue(frDb.getFeedbackResponsesForSessionFromSection("Empty feedback session", "idOfTypicalCourse1", "Section 1").isEmpty()); 
    }

    @Test 
    public void testGetFeedbackResponsesForSessionToSection() throws Exception {
        
        ______TS("standard success case");  
        
        String courseId = fras.get("response1ForQ1S1C1").courseId;
        String feedbackSessionName = fras.get("response1ForQ1S1C1").feedbackSessionName;
        
        List<FeedbackResponseAttributes> responses = frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, courseId, "Section 1");
        
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
        
        assertTrue(frDb.getFeedbackResponsesForSessionToSection("non-existent feedback session", courseId, "Section 1").isEmpty());
        
        ______TS("non-existent course");
        
        assertTrue(frDb.getFeedbackResponsesForSessionToSection(feedbackSessionName, "non-existent courseId", "Section 1").isEmpty());
        
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
        
        FeedbackResponseAttributes invalidFra = getResponseAttributes("response3ForQ2S1C1");
        invalidFra.setId(frDb.getFeedbackResponse(invalidFra.feedbackQuestionId, 
                invalidFra.giverEmail, invalidFra.recipientEmail).getId());
        invalidFra.giverEmail = "haha";
        try {
            frDb.updateFeedbackResponse(invalidFra);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains("Invalid answerer's email", e.getLocalizedMessage());
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
                modifiedResponse.giverEmail, modifiedResponse.recipientEmail);
        FeedbackResponseDetails frd = modifiedResponse.getResponseDetails();
        
        HashMap<String, String[]> requestParameters = new HashMap<String, String[]>();
        requestParameters.put("questiontype-1", new String[] { "TEXT" });
        requestParameters.put("responsetext-1-0", new String[] { "New answer text!" });
        
        String answer[] = {"New answer text!"};
        frd = frd.createResponseDetails(
                    answer, FeedbackQuestionType.TEXT,
                    null, requestParameters, 1, 0);
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
        fra.giverEmail = "giver@email.tmt";
        fra.giverSection = "None";
        fra.recipientEmail = "recipient@email.tmt";
        fra.recipientSection = "None";
        fra.feedbackQuestionId = "testFeedbackQuestionId";
        
        FeedbackResponseDetails responseDetails = new FeedbackTextResponseDetails("Text response");
        fra.setResponseDetails(responseDetails);
        
        return fra;
    }
    
    private FeedbackResponseAttributes getResponseAttributes(String id) {
        FeedbackResponseAttributes result = fras.get("response3ForQ2S1C1");
        return new FeedbackResponseAttributes(result.feedbackSessionName,
                result.courseId, result.feedbackQuestionId,
                result.feedbackQuestionType, result.giverEmail, result.giverSection,
                result.recipientEmail, result.recipientSection, result.responseMetaData);
    }
    
    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(FeedbackResponsesDb.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
        deleteResponsesFromDb();
    }
    
    private static void deleteResponsesFromDb() throws Exception {
        Set<String> keys = dataBundle.feedbackResponses.keySet();
        for (String i : keys) {
            frDb.deleteEntity(dataBundle.feedbackResponses.get(i));
        }
    }
    
}
