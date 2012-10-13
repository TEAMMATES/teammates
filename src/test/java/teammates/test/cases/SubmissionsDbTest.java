package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

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

	/*
	 * SUBMISSION TEST
	 */
	@Test
	public void testCreateSubmission() {
		// SUCCESS
		SubmissionData s = new SubmissionData();
		s.course = "Winzor101";
		s.evaluation = "Evaluation for Winzor101";
		s.team = "Team Derp";
		s.reviewee = "herp.derp@gmail.com";
		s.reviewer = "derp.herp@gmail.com";
		
		try {
			submissionsDb.createSubmission(s);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// FAIL : duplicate
		try {
			submissionsDb.createSubmission(s);
			fail();
		} catch (EntityAlreadyExistsException e) {
			
		}
		
		// FAIL : invalid params
		s.reviewer = "herp mc derp";
		try {
			submissionsDb.createSubmission(s);
			fail();
		} catch (AssertionError a) {
			
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testGetSubmission() {
		// Prepare
		SubmissionData s = new SubmissionData();
		s.course = "Winzor101";
		s.evaluation = "Evaluation for Winzor101";
		s.team = "Team Derp";
		s.reviewee = "herp.derp@gmail.com";
		s.reviewer = "derp.herp@gmail.com";
		
		try {
			submissionsDb.createSubmission(s);
		} catch (EntityAlreadyExistsException e) {

		}
		
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
	}
	
	@Test
	public void testEditSubmission() {
		// Prepare
		SubmissionData s = new SubmissionData();
		s.course = "Winzor101";
		s.evaluation = "Evaluation for Winzor101";
		s.team = "Team Derp";
		s.reviewee = "herp.derp@gmail.com";
		s.reviewer = "derp.herp@gmail.com";
		
		try {
			submissionsDb.createSubmission(s);
		} catch (EntityAlreadyExistsException e) {

		}
		
		// Edit existent
		s.justification = new Text("Hello World");
		submissionsDb.editSubmission(s);
		
		// Edit non-existent
		s.reviewer = "I@dont.exist";
		try {
			submissionsDb.editSubmission(s);
			fail();
		} catch (AssertionError a) {
			
		}
	}
	
	@Test
	public void testDeleteSubmission() {
		// Prepare
		SubmissionData s = new SubmissionData();
		s.course = "Winzor101";
		s.evaluation = "Evaluation for Winzor101";
		s.team = "Team Derp";
		s.reviewee = "herp.derp@gmail.com";
		s.reviewer = "derp.herp@gmail.com";
		
		try {
			submissionsDb.createSubmission(s);
		} catch (EntityAlreadyExistsException e) {

		}
		
		// Delete
		submissionsDb.deleteAllSubmissionsForCourse(s.course);
		
		SubmissionData deleted = submissionsDb.getSubmission(s.course,
																s.evaluation,
																s.reviewee,
																s.reviewer);
		assertNull(deleted);
		
		// delete again - should fail silently
		submissionsDb.deleteAllSubmissionsForEvaluation(s.course, s.evaluation);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(SubmissionsDb.class);
		helper.tearDown();
	}
}
