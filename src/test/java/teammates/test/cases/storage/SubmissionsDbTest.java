package teammates.test.cases.storage;

import static teammates.common.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.FieldValidator.REASON_INCORRECT_FORMAT;
import static org.testng.AssertJUnit.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.SubmissionsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;

public class SubmissionsDbTest extends BaseTestCase {
	
	//TODO: add missing test cases, refine existing ones. Follow the example
	//  of CoursesDbTest::testCreateCourse().

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

	@Test
	public void testCreateSubmission() throws EntityAlreadyExistsException, InvalidParametersException {
		// SUCCESS
		SubmissionAttributes s = new SubmissionAttributes();
		s.course = "Computing101";
		s.evaluation = "Very First Evaluation";
		s.team = "team1";
		s.reviewee = "student1@gmail.com";
		s.reviewer = "student2@gmail.com";
		s.p2pFeedback = new Text("");
		s.justification = new Text("");
		
		submissionsDb.createEntity(s);
		
		// SUCCESS even if keyword 'group' appears in the middle of the name (see Issue 380) 
		s = new SubmissionAttributes();
		s.course = "Computing102";
		s.evaluation = "text group text";
		s.team = "team2";
		s.reviewee = "student1@gmail.com";
		s.reviewer = "student2@gmail.com";
		s.p2pFeedback = new Text("");
		s.justification = new Text("");
		submissionsDb.createEntity(s);
			
		// FAIL : duplicate
		try {
			submissionsDb.createEntity(s);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(String.format(SubmissionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, s.getEntityTypeAsString())
					+ s.getIdentificationString(), e.getMessage());
		}
		
		// FAIL : invalid params
		s.reviewer = "invalid.email";
		try {
			submissionsDb.createEntity(s);
			Assert.fail();
		} catch (InvalidParametersException a) {
			assertContains(
					String.format("Invalid email address for the student giving the evaluation: "+ EMAIL_ERROR_MESSAGE, s.reviewer, REASON_INCORRECT_FORMAT),
					a.getMessage());
		} 
		
		// Null params check:
		try {
			submissionsDb.createEntity(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetSubmission() throws InvalidParametersException {
		SubmissionAttributes s = createNewSubmission();
		
		// Get existent
		SubmissionAttributes retrieved = submissionsDb.getSubmission(s.course,
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
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			submissionsDb.getSubmission(s.course, null, s.reviewee, s.reviewer);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			submissionsDb.getSubmission(s.course, s.evaluation, null, s.reviewer);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			submissionsDb.getSubmission(s.course, s.evaluation, s.reviewee, null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditSubmission() throws Exception {
		SubmissionAttributes s = createNewSubmission();
		
		// Edit existent
		s.justification = new Text("Hello World");
		submissionsDb.updateSubmission(s);
		
		// Edit non-existent
		s.reviewer = "non@existent.email";
		try {
			submissionsDb.updateSubmission(s);
			Assert.fail();
		} catch (EntityDoesNotExistException e) {
			assertContains(SubmissionsDb.ERROR_UPDATE_NON_EXISTENT, e.getMessage());
		}
		
		// Null params check:
		try {
			submissionsDb.updateSubmission(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteSubmission() throws InvalidParametersException {
		SubmissionAttributes s = createNewSubmission();
		
		// Delete
		submissionsDb.deleteAllSubmissionsForCourse(s.course);
		
		SubmissionAttributes deleted = submissionsDb.getSubmission(s.course,
																s.evaluation,
																s.reviewee,
																s.reviewer);
		assertNull(deleted);
		
		// delete again - should fail silently
		submissionsDb.deleteAllSubmissionsForEvaluation(s.course, s.evaluation);
		
		// Null params check:
		try {
			submissionsDb.deleteAllSubmissionsForEvaluation(null, s.evaluation);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			submissionsDb.deleteAllSubmissionsForEvaluation(s.course, null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(SubmissionsDb.class);
		helper.tearDown();
	}
	
	private SubmissionAttributes createNewSubmission() throws InvalidParametersException {
		SubmissionAttributes s = new SubmissionAttributes();
		s.course = "Computing101";
		s.evaluation = "Basic Computing Evaluation1";
		s.team = "team1";
		s.reviewee = "student1@gmail.com";
		s.reviewer = "student2@gmail.com";
		s.p2pFeedback = new Text("");
		s.justification = new Text("");
		
		try {
			submissionsDb.createEntity(s);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return s;
	}
}
