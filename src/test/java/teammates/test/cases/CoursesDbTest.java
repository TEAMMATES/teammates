package teammates.test.cases;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.api.CoursesDb;
import teammates.storage.datastore.Datastore;

public class CoursesDbTest extends BaseTestCase {

	private CoursesDb coursesDb = new CoursesDb();
	private static LocalServiceTestHelper helper;
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(CoursesDb.class);
		Datastore.initialize();
		LocalDatastoreServiceTestConfig localDatastore = new LocalDatastoreServiceTestConfig();
		helper = new LocalServiceTestHelper(localDatastore);
		helper.setUp();
	}

	/*
	 * COURSE TEST
	 */
	@Test
	public void testCreateCourse() {
		// SUCCESS
		CourseData c = new CourseData();
		c.id = "Winzor101";
		c.name = "Basic Herping Derping";
		c.coord = "herp.derp";
		
		try {
			coursesDb.createCourse(c);
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
		
		// FAIL : duplicate
		try {
			coursesDb.createCourse(c);
			fail();
		} catch (EntityAlreadyExistsException e) {
			
		}
		
		// FAIL : invalid params
		c.id = "herp mc derp";
		try {
			coursesDb.createCourse(c);
			fail();
		} catch (AssertionError a) {
			
		} catch (EntityAlreadyExistsException e) {
			fail();
		}
	}
	
	@Test
	public void testGetCourse() {
		// Prepare
		CourseData c = new CourseData();
		c.id = "Winzor101";
		c.name = "Basic Herping Derping";
		c.coord = "herp.derp";
		
		try {
			coursesDb.createCourse(c);
		} catch (EntityAlreadyExistsException e) {

		}
		
		// Get existent
		CourseData retrieved = coursesDb.getCourse(c.id);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = coursesDb.getCourse("the.dovahkiin");
		assertNull(retrieved);
	}
	
	@Test
	public void testEditCourse() {
		// Not implemented
	}
	
	@Test
	public void testDeleteCourse() {
		// Prepare
		CourseData c = new CourseData();
		c.id = "Winzor101";
		c.name = "Basic Herping Derping";
		c.coord = "herp.derp";
		
		try {
			coursesDb.createCourse(c);
		} catch (EntityAlreadyExistsException e) {

		}
		
		// Delete
		coursesDb.deleteCourse(c.id);
		
		CourseData deleted = coursesDb.getCourse(c.id);
		assertNull(deleted);
		
		// delete again - should fail silently
		coursesDb.deleteCourse(c.id);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(CoursesDb.class);
		helper.tearDown();
	}
}
