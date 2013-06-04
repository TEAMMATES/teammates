package teammates.test.cases.storage;

import static org.testng.AssertJUnit.*;
import static teammates.common.FieldValidator.*;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;
import teammates.test.cases.BaseTestCase;
import teammates.test.cases.logic.LogicTest;

public class FeedbackSessionsDbTest extends BaseTestCase {
	
	private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		turnLoggingUp(FeedbackSessionsDb.class);
		Datastore.initialize();
	}
	
	@BeforeMethod
	public void caseSetUp() throws ServletException, IOException {
		helper = new LocalServiceTestHelper(
				new LocalDatastoreServiceTestConfig());
		setHelperTimeZone(helper);
		helper.setUp();
	}
	
	@Test
	public void testCreateDeleteFeedbackSession() 
			throws InvalidParametersException, EntityAlreadyExistsException {	
				
		______TS("standard success case");
		
		FeedbackSessionAttributes fsa = getNewFeedbackSession();
		fsDb.createEntity(fsa);
		LogicTest.verifyPresentInDatastore(fsa);
		
		______TS("duplicate");
		try {
			fsDb.createEntity(fsa);
			signalFailureToDetectException();
		} catch (EntityAlreadyExistsException e) {
			assertContains(String.format(FeedbackSessionsDb.
					ERROR_CREATE_ENTITY_ALREADY_EXISTS, fsa.getEntityTypeAsString())
					+ fsa.getIdentificationString(), e.getMessage());
		}
		
		fsDb.deleteEntity(fsa);
		LogicTest.verifyAbsentInDatastore(fsa);
		
		______TS("null params");
		
		try {
			fsDb.createEntity(null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains(Common.ERROR_DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
		}
		
		______TS("invalid params");
		
		try {
			fsa.startTime = new Date();			
			fsDb.createEntity(fsa);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			// start time is now after end time
			assertContains("start time", e.getLocalizedMessage());
		}
		
	}
	
	@Test
	public void testGetFeedbackSessions() throws Exception {
		
		restoreTypicalDataInDatastore();		
		DataBundle dataBundle = getTypicalDataBundle();
		
		______TS("standard success case");	
		
		FeedbackSessionAttributes expected =
				dataBundle.feedbackSessions.get("session1InCourse2");
		FeedbackSessionAttributes actual =
				fsDb.getFeedbackSession("Private feedback session", "idOfTypicalCourse2");
		
		assertEquals(actual.toString(), expected.toString());
		
		______TS("non-existant session");
		
		assertNull(fsDb.getFeedbackSession("Non-existant feedback session", "non-course"));
		
		______TS("null params");
		
		try {
			fsDb.getFeedbackSession(null, "idOfTypicalCourse1");
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains(Common.ERROR_DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
		}
		
	}
	
	@Test
	public void testGetFeedbackSessionsForCourse() throws Exception {
		
		restoreTypicalDataInDatastore();		
		DataBundle dataBundle = getTypicalDataBundle();
		
		______TS("standard success case");	
		
		List<FeedbackSessionAttributes> sessions = fsDb.getFeedbackSessionsForCourse("idOfTypicalCourse1");
		
		String expected =
				dataBundle.feedbackSessions.get("session1InCourse1").toString() + Common.EOL +
				dataBundle.feedbackSessions.get("session2InCourse1").toString() + Common.EOL +
				dataBundle.feedbackSessions.get("session3InCourse1").toString() + Common.EOL;
		
		for (FeedbackSessionAttributes session : sessions) {
			assertContains(session.toString(), expected);
		}
		Assert.assertTrue(sessions.size() == 3);
		
		______TS("null params");
		
		try {
			fsDb.getFeedbackSessionsForCourse(null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains(Common.ERROR_DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
		}
		
		______TS("non-existant course");
		
		assertTrue(fsDb.getFeedbackSessionsForCourse("non-existant course").isEmpty());
			
		______TS("no sessions in course");
		
		assertTrue(fsDb.getFeedbackSessionsForCourse("idOfCourseNoEvals").isEmpty());	
	}
	
	@Test
	public void testUpdateFeedbackSession() throws Exception {
		
		______TS("null params");		
		try {
			fsDb.updateFeedbackSession(null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains(Common.ERROR_DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
		}
		______TS("invalid feedback sesion attributes");
		FeedbackSessionAttributes invalidFs = getNewFeedbackSession();
		Calendar calendar = Calendar.getInstance();
		invalidFs.endTime = calendar.getTime();
		calendar.add(Calendar.MONTH, 1);
		invalidFs.startTime = calendar.getTime();
		try {
			fsDb.updateFeedbackSession(invalidFs);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			assertContains(
					String.format(TIME_FRAME_ERROR_MESSAGE, START_TIME_FIELD_NAME,
							FEEDBACK_SESSION_NAME, END_TIME_FIELD_NAME),
							e.getLocalizedMessage());
		}
		______TS("feedback session does not exist");
		FeedbackSessionAttributes nonexistantFs = getNewFeedbackSession();
		nonexistantFs.feedbackSessionName = "non-existant fs";
		nonexistantFs.courseId = "non-existantCourse";
		try {
			fsDb.updateFeedbackSession(nonexistantFs);
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			assertContains(FeedbackSessionsDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
		}
		______TS("standard success case");
		FeedbackSessionAttributes modifiedSession = getNewFeedbackSession();
		fsDb.createEntity(modifiedSession);
		LogicTest.verifyPresentInDatastore(modifiedSession);
		modifiedSession.instructions = new Text("new instructions");
		modifiedSession.gracePeriod = 0;
		modifiedSession.sentOpenEmail = false;
		fsDb.updateFeedbackSession(modifiedSession);
		LogicTest.verifyPresentInDatastore(modifiedSession);
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
		helper.tearDown();
		turnLoggingDown(FeedbackSessionsDb.class);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		printTestClassFooter();
	}
	
}
