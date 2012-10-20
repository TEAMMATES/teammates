package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.api.SubmissionsDb;
import teammates.storage.datastore.Datastore;

public class SubmissionsDbTest extends BaseTestCase {

	private SubmissionsDb submissionsDb = new SubmissionsDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(SubmissionsDb.class);
		Datastore.initialize();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore);
		helper.setUp();
	}

	@SuppressWarnings("unused")
	private void ____COURSE_________________________________________() {
	}
	@Test
	public void testCreateSubmission() throws EntityAlreadyExistsException {
		// SUCCESS
		SubmissionData s = new SubmissionData();
		s.course = "Computing101";
		s.evaluation = "Basic Computing Evaluation1";
		s.team = "team1";
		s.reviewee = "student1@gmail.com";
		s.reviewer = "student2@gmail.com";
		submissionsDb.createSubmission(s);
			
		// FAIL : duplicate
		try {
			submissionsDb.createSubmission(s);
			fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(SubmissionsDb.ERROR_CREATE_SUBMISSION_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		s.reviewer = "invalid.email";
		try {
			submissionsDb.createSubmission(s);
			fail();
		} catch (AssertionError a) {
			assertEquals(a.getMessage(), SubmissionData.ERROR_FIELD_REVIEWER);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// Null params check:
		try {
			submissionsDb.createSubmission(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetSubmission() {
		SubmissionData s = prepareNewSubmission();
		
		// Get existent
		SubmissionData retrieved = submissionsDb.getSubmission(s.course,
																s.evaluation,
																s.reviewee,
																s.reviewer);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = submissionsDb.getSubmission(s.course,
												s.evaluation,
												"dovahkiin@skyrim.com",
												s.reviewer);
		assertNull(retrieved);
		
		// Null params check:
		try {
			submissionsDb.getSubmission(null, s.evaluation, s.reviewee, s.reviewer);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditSubmission() {
		SubmissionData s = prepareNewSubmission();
		
		// Edit existent
		s.justification = new Text("Hello World");
		submissionsDb.editSubmission(s);
		
		// Edit non-existent
		s.reviewer = "non@existent.email";
		try {
			submissionsDb.editSubmission(s);
			fail();
		} catch (AssertionError a) {
			assertContains(SubmissionsDb.ERROR_UPDATE_NON_EXISTENT, a.getMessage());
		}
		
		// Null params check:
		try {
			submissionsDb.editSubmission(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteSubmission() {
		SubmissionData s = prepareNewSubmission();
		
		// Delete
		submissionsDb.deleteAllSubmissionsForCourse(s.course);
		
		SubmissionData deleted = submissionsDb.getSubmission(s.course,
																s.evaluation,
																s.reviewee,
																s.reviewer);
		assertNull(deleted);
		
		// delete again - should fail silently
		submissionsDb.deleteAllSubmissionsForEvaluation(s.course, s.evaluation);
		
		// Null params check:
		try {
			submissionsDb.deleteAllSubmissionsForEvaluation(null, s.evaluation);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(SubmissionsDb.class);
		helper.tearDown();
	}
	
	private SubmissionData prepareNewSubmission() {
		SubmissionData s = new SubmissionData();
		s.course = "Computing101";
		s.evaluation = "Basic Computing Evaluation1";
		s.team = "team1";
		s.reviewee = "student1@gmail.com";
		s.reviewer = "student2@gmail.com";
		
		try {
			submissionsDb.createSubmission(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
}
