package teammates.test.cases;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.datastore.Datastore;

public class EvaluationsDbTest extends BaseTestCase {

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

	@SuppressWarnings("unused")
	private void ____COURSE_________________________________________() {
	}
	@Test
	public void testCreateEvaluation() throws EntityAlreadyExistsException {
		// SUCCESS
		EvaluationData e = new EvaluationData();
		e.course = "Computing101";
		e.name = "Basic Computing Evaluation1";
		e.startTime = new Date();
		e.endTime = new Date();
		evaluationsDb.createEvaluation(e);
		
		// FAIL : duplicate
		try {
			evaluationsDb.createEvaluation(e);
			fail();
		} catch (EntityAlreadyExistsException ex) {
			assertContains(EvaluationsDb.ERROR_CREATE_EVALUATION_ALREADY_EXISTS, ex.getMessage());
		}
		
		// FAIL : invalid params
		e.startTime = null;
		try {
			evaluationsDb.createEvaluation(e);
			fail();
		} catch (AssertionError a) {
			assertEquals(a.getMessage(), EvaluationData.ERROR_FIELD_STARTTIME);
		} catch (EntityAlreadyExistsException ex) {
			fail();
		}
		
		// Null params check:
		try {
			evaluationsDb.createEvaluation(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetEvaluation() {
		EvaluationData e = prepareNewEvaluation();
		
		// Get existent
		EvaluationData retrieved = evaluationsDb.getEvaluation(e.course, e.name);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = evaluationsDb.getEvaluation("non-existent-course", "Non existent Evaluation");
		assertNull(retrieved);
		
		// Null params check:
		try {
			evaluationsDb.getEvaluation(e.course, null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditEvaluation() {
		EvaluationData e = prepareNewEvaluation();
				
		// Edit existent
		e.instructions = "Foo Bar";
		evaluationsDb.editEvaluation(e);
				
		// Edit non-existent
		e.name = "Non existent Evaluation";
		try {
			evaluationsDb.editEvaluation(e);
			fail();
		} catch (AssertionError a) {
			assertContains(EvaluationsDb.ERROR_UPDATE_NON_EXISTENT, a.getMessage());
		}
		
		// Null params check:
		try {
			evaluationsDb.editEvaluation(null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testDeleteEvaluation() {
		EvaluationData e = prepareNewEvaluation();
		
		// Delete
		evaluationsDb.deleteEvaluation(e.course, e.name);
		
		EvaluationData deleted = evaluationsDb.getEvaluation(e.course, e.name);
		assertNull(deleted);
		
		// delete again - should fail silently
		evaluationsDb.deleteEvaluation(e.course, e.name);
		
		// Null params check:
		try {
			evaluationsDb.deleteEvaluation(e.course, null);
			fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationsDb.class);
		helper.tearDown();
	}
	
	private EvaluationData prepareNewEvaluation() {
		EvaluationData e = new EvaluationData();
		e.course = "Computing101";
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
