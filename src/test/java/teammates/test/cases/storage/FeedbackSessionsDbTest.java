package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static teammates.common.util.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.FEEDBACK_SESSION_NAME;
import static teammates.common.util.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

import com.google.appengine.api.datastore.Text;

public class FeedbackSessionsDbTest extends BaseComponentTestCase {
    
    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackSessionsDb.class);
        addSessionsToDb();
    }
    
    private static void addSessionsToDb() throws Exception {
        Set<String> keys = dataBundle.feedbackSessions.keySet();
        for (String i : keys) {
            try {
                fsDb.createEntity(dataBundle.feedbackSessions.get(i));
            } catch (EntityAlreadyExistsException e) {
                fsDb.updateFeedbackSession(dataBundle.feedbackSessions.get(i));
            }
        }
    }


    @Test
    public void testCreateDeleteFeedbackSession() 
            throws Exception {    
        
        ______TS("standard success case");
        
        FeedbackSessionAttributes fsa = getNewFeedbackSession();
        fsDb.createEntity(fsa);
        TestHelper.verifyPresentInDatastore(fsa);
        
        ______TS("duplicate");
        try {
            fsDb.createEntity(fsa);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(FeedbackSessionsDb.
                    ERROR_CREATE_ENTITY_ALREADY_EXISTS, fsa.getEntityTypeAsString())
                    + fsa.getIdentificationString(), e.getMessage());
        }
        
        fsDb.deleteEntity(fsa);
        TestHelper.verifyAbsentInDatastore(fsa);
        
        ______TS("null params");
        
        try {
            fsDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("invalid params");
        
        try {
            fsa.startTime = new Date();            
            fsDb.createEntity(fsa);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            // start time is now after end time
            AssertHelper.assertContains("start time", e.getLocalizedMessage());
        }
        
    }
    
    @Test
    public void testAllGetFeedbackSessions() throws Exception{

        testGetFeedbackSessions();
        testGetFeedbackSessionsForCourse();
        testGetNonPrivateFeedbackSessions();
        testGetFeedbackSessionsWithUnsentOpenEmail();
        testGetFeedbackSessionsWithUnsentPublishedEmail();
    }
    
    private void testGetFeedbackSessions() throws Exception {
        
        ______TS("standard success case");
        
        FeedbackSessionAttributes expected =
                dataBundle.feedbackSessions.get("session1InCourse2");
        FeedbackSessionAttributes actual =
                fsDb.getFeedbackSession("idOfTypicalCourse2", "Private feedback session");
        
        assertEquals(expected.toString(), actual.toString());
        
        ______TS("non-existant session");
        
        assertNull(fsDb.getFeedbackSession("non-course", "Non-existant feedback session"));
        
        ______TS("null fsName");
        
        try {
            fsDb.getFeedbackSession("idOfTypicalCourse1", null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("null courseId");
        
        try {
            fsDb.getFeedbackSession(null, "First feedback session");
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
    }
    
    private void testGetFeedbackSessionsForCourse() throws Exception {
        
        ______TS("standard success case");    
        
        List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsForCourse("idOfTypicalCourse1");
        
        String expected =
                dataBundle.feedbackSessions.get("session1InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("session2InCourse1").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("empty.session").toString() + Const.EOL +                
                dataBundle.feedbackSessions.get("awaiting.session").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("closedSession").toString() + Const.EOL +
                dataBundle.feedbackSessions.get("gracePeriodSession").toString() + Const.EOL;
        
        for (FeedbackSessionAttributes session : sessions) {
            AssertHelper.assertContains(session.toString(), expected);
        }
        Assert.assertTrue(sessions.size() == 6);
        
        ______TS("null params");
        
        try {
            fsDb.getFeedbackSessionsForCourse(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("non-existant course");
        
        assertTrue(fsDb.getFeedbackSessionsForCourse("non-existant course").isEmpty());
            
        ______TS("no sessions in course");
        
        assertTrue(fsDb.getFeedbackSessionsForCourse("idOfCourseNoEvals").isEmpty());    
    }
    
    private void testGetNonPrivateFeedbackSessions() throws Exception {
        
        ______TS("standard success case"); 
        
        List<FeedbackSessionAttributes> fsaList = fsDb.getNonPrivateFeedbackSessions();
        
        assertEquals(8, fsaList.size());
        for(FeedbackSessionAttributes fsa : fsaList){
            assertFalse(fsa.isPrivateSession());
        }
        
    }
    
    private void testGetFeedbackSessionsWithUnsentOpenEmail() throws Exception {
        
        ______TS("standard success case"); 
        
        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsWithUnsentOpenEmail();
        
        assertEquals(2, fsaList.size());
        for(FeedbackSessionAttributes fsa : fsaList){
            assertFalse(fsa.sentOpenEmail);
        }
        
    }
    
    private void testGetFeedbackSessionsWithUnsentPublishedEmail() throws Exception {
        
        ______TS("standard success case"); 
        
        List<FeedbackSessionAttributes> fsaList = fsDb.getFeedbackSessionsWithUnsentPublishedEmail();
        
        assertEquals(8, fsaList.size());
        for(FeedbackSessionAttributes fsa : fsaList){
            assertFalse(fsa.sentPublishedEmail);
        }
        
    }
    
    @Test
    public void testUpdateFeedbackSession() throws Exception {
        
        ______TS("null params");        
        try {
            fsDb.updateFeedbackSession(null);
            signalFailureToDetectException();
        } catch (AssertionError e) {
            AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        ______TS("invalid feedback sesion attributes");
        FeedbackSessionAttributes invalidFs = getNewFeedbackSession();
        fsDb.deleteEntity(invalidFs);
        fsDb.createEntity(invalidFs);
        Calendar calendar = TimeHelper.dateToCalendar(invalidFs.endTime);
        calendar.add(Calendar.MONTH, 1);
        invalidFs.startTime = calendar.getTime();
        invalidFs.resultsVisibleFromTime = calendar.getTime();
        try {
            fsDb.updateFeedbackSession(invalidFs);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            assertEquals(
                    String.format(TIME_FRAME_ERROR_MESSAGE, END_TIME_FIELD_NAME,
                            FEEDBACK_SESSION_NAME, START_TIME_FIELD_NAME),
                            e.getLocalizedMessage());
        }
        ______TS("feedback session does not exist");
        FeedbackSessionAttributes nonexistantFs = getNewFeedbackSession();
        nonexistantFs.feedbackSessionName = "non existant fs";
        nonexistantFs.courseId = "non.existant.course";
        try {
            fsDb.updateFeedbackSession(nonexistantFs);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(FeedbackSessionsDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
        }
        ______TS("standard success case");
        FeedbackSessionAttributes modifiedSession = getNewFeedbackSession();
        fsDb.deleteEntity(modifiedSession);
        fsDb.createEntity(modifiedSession);
        TestHelper.verifyPresentInDatastore(modifiedSession);
        modifiedSession.instructions = new Text("new instructions");
        modifiedSession.gracePeriod = 0;
        modifiedSession.sentOpenEmail = false;
        fsDb.updateFeedbackSession(modifiedSession);
        TestHelper.verifyPresentInDatastore(modifiedSession);
    }
    
    private FeedbackSessionAttributes getNewFeedbackSession() {
        FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
        fsa.feedbackSessionType = FeedbackSessionType.STANDARD;
        fsa.feedbackSessionName = "fsTest1";
        fsa.courseId = "testCourse";
        fsa.creatorEmail = "valid@email.com";
        fsa.createdTime = new Date();
        fsa.startTime = new Date();
        fsa.endTime = new Date();
        fsa.sessionVisibleFromTime = new Date();
        fsa.resultsVisibleFromTime = new Date();
        fsa.gracePeriod = 5;
        fsa.sentOpenEmail = true;
        fsa.sentPublishedEmail = true;
        fsa.instructions = new Text("Give feedback.");
        return fsa;
    }
    
    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(FeedbackSessionsDb.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        deleteSessionsFromDb();
        printTestClassFooter();
    }
    
    private static void deleteSessionsFromDb() throws Exception {
        Set<String> keys = dataBundle.feedbackSessions.keySet();
        for (String i : keys) {
            fsDb.deleteEntity(dataBundle.feedbackSessions.get(i));
        }
    }
    
}
