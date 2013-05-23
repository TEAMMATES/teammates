package teammates.test.cases.storage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.AssertJUnit;
import java.util.Date;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;

public class EvaluationsDbTest extends BaseTestCase {
	
	//TODO: add missing test cases, refine existing ones. Follow the example
	//  of CoursesDbTest::testCreateCourse().

	private EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationsDb.class);
		Datastore.initialize();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore);
		helper.setUp();
	}

	@Test
	public void testCreateEvaluation() throws EntityAlreadyExistsException, InvalidParametersException {
		// SUCCESS
		EvaluationAttributes e = new EvaluationAttributes();
		e.courseId = "Computing101";
		e.name = "Very First Evaluation";
		e.startTime = new Date();
		e.endTime = new Date();
		evaluationsDb.createEvaluation(e);
		
		// SUCCESS even if keyword 'group' appears in the middle of the name (see Issue 380)
		e = new EvaluationAttributes();
		e.courseId = "Computing102";
		e.name = "text group text";
		e.startTime = new Date();
		e.endTime = new Date();
		evaluationsDb.createEvaluation(e);
		
		// FAIL : duplicate
		try {
			evaluationsDb.createEvaluation(e);
			Assert.fail();
		} catch (EntityAlreadyExistsException ex) {
			assertContains(EvaluationsDb.ERROR_CREATE_EVALUATION_ALREADY_EXISTS, ex.getMessage());
		}
		
		// FAIL : invalid params
		e.startTime = null;
		try {
			evaluationsDb.createEvaluation(e);
			signalFailureToDetectException();
		} catch (AssertionError e1) {
			ignoreExpectedException();
		}
	
		
		// Null params check:
		try {
			evaluationsDb.createEvaluation(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetEvaluation() throws InvalidParametersException {
		EvaluationAttributes e = createNewEvaluation();
		
		// Get existent
		EvaluationAttributes retrieved = evaluationsDb.getEvaluation(e.courseId, e.name);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = evaluationsDb.getEvaluation("non-existent-course", "Non existent Evaluation");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			evaluationsDb.getEvaluation(e.courseId, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditEvaluation() throws EntityDoesNotExistException, InvalidParametersException {
		EvaluationAttributes e = createNewEvaluation();
				
		// Edit existent
		e.instructions = "Foo Bar";
		evaluationsDb.updateEvaluation(e);
				
		// Edit non-existent
		e.name = "Non existent Evaluation";
		try {
			evaluationsDb.updateEvaluation(e);
			Assert.fail();
		} catch (EntityDoesNotExistException a) {
			assertContains(EvaluationsDb.ERROR_UPDATE_NON_EXISTENT, a.getMessage());
		}
		
		// Null params check:
		try {
			evaluationsDb.updateEvaluation(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteEvaluation() throws InvalidParametersException {
		EvaluationAttributes e = createNewEvaluation();
		
		// Delete
		evaluationsDb.deleteEvaluation(e.courseId, e.name);
		
		EvaluationAttributes deleted = evaluationsDb.getEvaluation(e.courseId, e.name);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		evaluationsDb.deleteEvaluation(e.courseId, e.name);
		
		// Null params check:
		try {
			evaluationsDb.deleteEvaluation(null, e.name);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			evaluationsDb.deleteEvaluation(e.courseId, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationsDb.class);
		helper.tearDown();
	}
	
	private EvaluationAttributes createNewEvaluation() throws InvalidParametersException {
		EvaluationAttributes e = new EvaluationAttributes();
		e.courseId = "Computing101";
		e.name = "Basic Computing Evaluation1";
		e.startTime = new Date();
		e.endTime = new Date();
		
		try {
			evaluationsDb.createEvaluation(e);
		} catch (EntityAlreadyExistsException ex) {
			// Okay if it's already inside
		}
		
		return e;
	}
}
