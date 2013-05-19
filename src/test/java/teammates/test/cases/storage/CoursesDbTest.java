package teammates.test.cases.storage;

import static teammates.common.FieldValidator.*;
import static org.testng.AssertJUnit.*;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.CoursesDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;
import teammates.test.cases.logic.LogicTest;

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
	public void testCreateCourse() throws EntityAlreadyExistsException, InvalidParametersException {
		
		/*Explanation:
		 * The SUT (i.e. CoursesDb::createCourse) has 4 paths. Therefore, we
		 * have 4 test cases here, one for each path.
		 */

		______TS("success: typical case");
		CourseAttributes c = new CourseAttributes();
		c.id = "Computing101-fresh";
		c.name = "Basic Computing";
		coursesDb.createCourse(c);
		LogicTest.verifyPresentInDatastore(c);
		
		______TS("fails: entity already exists");
		try {
			coursesDb.createCourse(c);
			signalFailureToDetectException();
		} catch (EntityAlreadyExistsException e) {
			assertContains(CoursesDb.ERROR_CREATE_COURSE_ALREADY_EXISTS, e.getMessage());
		}
		
		______TS("fails: invalid parameters");
		c.id = "invalid id spaces";
		try {
			coursesDb.createCourse(c);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			assertContains(
					String.format(COURSE_ID_ERROR_MESSAGE, c.id, REASON_INCORRECT_FORMAT), 
					e.getMessage());
		} 
		
		______TS("fails: null parameter");
		try {
			coursesDb.createCourse(null);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetCourse() throws InvalidParametersException {
		CourseAttributes c = createNewCourse();
		
		// Get existent
		CourseAttributes retrieved = coursesDb.getCourse(c.id);
		assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = coursesDb.getCourse("non-existent-course");
		assertNull(retrieved);
		
		// Null params check:
		try {
			coursesDb.getCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testUpdateCourse() {
		// Not implemented
	}
	
	@Test
	public void testDeleteCourse() throws InvalidParametersException {
		CourseAttributes c = createNewCourse();
		
		// Delete
		coursesDb.deleteCourse(c.id);
		
		CourseAttributes deleted = coursesDb.getCourse(c.id);
		assertNull(deleted);
		
		// delete again - should fail silently
		coursesDb.deleteCourse(c.id);
		
		// Null params check:
		try {
			coursesDb.deleteCourse(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(CoursesDb.class);
		helper.tearDown();
	}
	
	private CourseAttributes createNewCourse() throws InvalidParametersException {
		CourseAttributes c = new CourseAttributes();
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
