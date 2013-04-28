package teammates.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import org.testng.AssertJUnit;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
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

	@SuppressWarnings("unused")
	private void ____COURSE_________________________________________() {
	}
	@Test
	public void testCreateCourse() throws EntityAlreadyExistsException {
		// SUCCESS
		CourseData c = new CourseData();
		c.id = "Computing101-fresh";
		c.name = "Basic Computing";
		coursesDb.createCourse(c);
		
		// FAIL : duplicate
		try {
			coursesDb.createCourse(c);
			Assert.fail();
		} catch (EntityAlreadyExistsException e) {
			assertContains(CoursesDb.ERROR_CREATE_COURSE_ALREADY_EXISTS, e.getMessage());
		}
		
		// FAIL : invalid params
		c.id = "invalid id spaces";
		try {
			coursesDb.createCourse(c);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(CourseData.ERROR_ID_INVALIDCHARS, a.getMessage());
		} catch (EntityAlreadyExistsException e) {
			Assert.fail();
		}
		
		// Null params check:
		try {
			coursesDb.createCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetCourse() {
		CourseData c = createNewCourse();
		
		// Get existent
		CourseData retrieved = coursesDb.getCourse(c.id);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = coursesDb.getCourse("non-existent-course");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			coursesDb.getCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testEditCourse() {
		// Not implemented
	}
	
	@Test
	public void testDeleteCourse() {
		CourseData c = createNewCourse();
		
		// Delete
		coursesDb.deleteCourse(c.id);
		
		CourseData deleted = coursesDb.getCourse(c.id);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		coursesDb.deleteCourse(c.id);
		
		// Null params check:
		try {
			coursesDb.deleteCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(CoursesDb.class);
		helper.tearDown();
	}
	
	private CourseData createNewCourse() {
		CourseData c = new CourseData();
		c.id = "Computing101";
		c.name = "Basic Computing";
		
		try {
			coursesDb.createCourse(c);
		} catch (EntityAlreadyExistsException e) {
			// Okay if it's already inside
		}
		
		return c;
	}
}
