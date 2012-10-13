package teammates.test.cases;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

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

	/*
	 * EVALUATION TEST
	 */
	@Test
	public void testCreateEvaluation() {
		// SUCCESS
		EvaluationData e = new EvaluationData();
		e.course = "Winzor101";
		e.name = "Basic Herping Derping";
		e.startTime = new Date();
		e.endTime = new Date();
		
		try {
			evaluationsDb.createEvaluation(e);
		} catch (EntityAlreadyExistsException ex) {
			fail();
		}
		
		// FAIL : duplicate
		try {
			evaluationsDb.createEvaluation(e);
			fail();
		} catch (EntityAlreadyExistsException ex) {
			
		}
		
		// FAIL : invalid params
		e.startTime = null;
		try {
			evaluationsDb.createEvaluation(e);
			fail();
		} catch (AssertionError a) {
			
		} catch (EntityAlreadyExistsException ex) {
			fail();
		}
	}
	
	@Test
	public void testGetEvaluation() {
		// Prepare
		EvaluationData e = new EvaluationData();
		e.course = "Winzor101";
		e.name = "Basic Herping Derping";
		e.startTime = new Date();
		e.endTime = new Date();
		
		try {
			evaluationsDb.createEvaluation(e);
		} catch (EntityAlreadyExistsException ex) {

		}
		
		// Get existent
		EvaluationData retrieved = evaluationsDb.getEvaluation(e.course, e.name);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = evaluationsDb.getEvaluation("thuum", "the.dovahkiin");
		assertNull(retrieved);
	}
	
	@Test
	public void testEditEvaluation() {
		// Prepare
		EvaluationData e = new EvaluationData();
		e.course = "Winzor101";
		e.name = "Basic Herping Derping";
		e.startTime = new Date();
		e.endTime = new Date();
		
		try {
			evaluationsDb.createEvaluation(e);
		} catch (EntityAlreadyExistsException ex) {

		}
				
		// Edit existent
		e.instructions = "Foo Bar";
		evaluationsDb.editEvaluation(e);
				
		// Edit non-existent
		e.name = "I dont exist";
		try {
			evaluationsDb.editEvaluation(e);
			fail();
		} catch (AssertionError a) {
			
		}
	}
	
	@Test
	public void testDeleteEvaluation() {
		// Prepare
		EvaluationData e = new EvaluationData();
		e.course = "Winzor101";
		e.name = "Basic Herping Derping";
		e.startTime = new Date();
		e.endTime = new Date();
		
		try {
			evaluationsDb.createEvaluation(e);
		} catch (EntityAlreadyExistsException ex) {

		}
		
		// Delete
		evaluationsDb.deleteEvaluation(e.course, e.name);
		
		EvaluationData deleted = evaluationsDb.getEvaluation(e.course, e.name);
		assertNull(deleted);
		
		// delete again - should fail silently
		evaluationsDb.deleteEvaluation(e.course, e.name);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(EvaluationsDb.class);
		helper.tearDown();
	}
}
