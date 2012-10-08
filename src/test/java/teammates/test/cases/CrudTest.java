package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.SubmissionsDb;

/**
 * For additional testing on each CRUD operation
 * Otherwise, already tested in integrated testing
 * 
 * @author whsprwind
 */
public class CrudTest extends BaseTestCase {

	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(AccountsDb.class);
		turnLoggingUp(CoursesDb.class);
		turnLoggingUp(EvaluationsDb.class);
		turnLoggingUp(SubmissionsDb.class);
	}

	/*
	 * STUDENT TEST
	 */
	@Test
	public void testCreateStudent() {
	
	}
	
	@Test
	public void testGetStudent() {
	
	}
	
	@Test
	public void testEditStudent() {
	
	}
	
	@Test
	public void testDeleteStudent() {
	
	}
	
	/*
	 * COORDINATOR TEST
	 */
	@Test
	public void testCreateCoord() {
	
	}
	
	@Test
	public void testGetCoord() {
	
	}
	
	@Test
	public void testEditCoord() {
	
	}
	
	@Test
	public void testDeleteCoord() {
	
	}
	
	/*
	 * COURSE TEST
	 */
	@Test
	public void testCreateCourse() {
	
	}
	
	@Test
	public void testGetCourse() {
	
	}
	
	@Test
	public void testEditCourse() {
	
	}
	
	@Test
	public void testDeleteCourse() {
	
	}
	
	/*
	 * EVALUATION TEST
	 */
	@Test
	public void testCreateEvaluation() {
	
	}
	
	@Test
	public void testGetEvaluation() {
	
	}
	
	@Test
	public void testEditEvaluation() {
	
	}
	
	@Test
	public void testDeleteEvaluation() {
	
	}
	
	/*
	 * SUBMISSION TEST
	 */
	@Test
	public void testCreateSubmission() {
	
	}
	
	@Test
	public void testGetSubmission() {
	
	}
	
	@Test
	public void testEditSubmission() {
	
	}
	
	@Test
	public void testDeleteSubmission() {
	
	}
	
	

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(SubmissionsDb.class);
		turnLoggingDown(EvaluationsDb.class);
		turnLoggingDown(CoursesDb.class);
		turnLoggingDown(AccountsDb.class);
	}
}
