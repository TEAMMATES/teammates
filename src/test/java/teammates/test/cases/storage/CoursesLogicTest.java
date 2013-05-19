package teammates.test.cases.storage;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.CoursesLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.datastore.Datastore;
import teammates.test.cases.BaseTestCase;
import teammates.test.cases.logic.LogicTest;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class CoursesLogicTest extends BaseTestCase {

	private CoursesLogic coursesLogic = new CoursesLogic();
	private AccountsDb accountsDb = new AccountsDb();
	private InstructorsDb instructorsDb = new InstructorsDb();
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
	public void testCreateCourse() throws Exception {
		
		/*Explanation:
		 * The SUT (i.e. CoursesLogic::createCourse) has only 1 path. Therefore, we
		 * should typically have 1 test cases here.
		 */
		______TS("typical case");
		CourseAttributes c = new CourseAttributes();
		c.id = "Computing101-fresh";
		c.name = "Basic Computing";
		coursesLogic.createCourse(c.id, c.name);
		LogicTest.verifyPresentInDatastore(c);
		
	}
	
	@Test
	public void testCreateCourseAndInstructor() throws Exception {
		
		/* Explanation: SUT has 5 paths. They are,
		 * path 1 - exit because the account doesn't' exist.
		 * path 2 - exit because the account exists but doesn't have instructor privileges.
		 * path 3 - exit because course creation failed.
		 * path 4 - exit because instructor creation failed.
		 * path 5 - success.
		 * Accordingly, we have 5 test cases.
		 */
		
		______TS("fails: account doesn't exist");
		
		CourseAttributes c = new CourseAttributes();
		c.id = "fresh-course-tccai";
		c.name = "Fresh course for tccai";
		
		InstructorAttributes i = new InstructorAttributes();
		i.googleId = "instructor-for-tccai";
		i.courseId = c.id;
		i.name = "Instructor for tccai";
		i.email = "ins.for.iccai@gmail.com";
		
		
		try {
			coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains("for a non-existent instructor", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		LogicTest.verifyAbsentInDatastore(i);
		
		
		______TS("fails: account doesn't have instructor privileges");
		AccountAttributes a = new AccountAttributes();
		a.googleId = i.googleId;
		a.name = i.name;
		a.email = i.email;
		a.institute = "NUS";
		a.isInstructor = false;
		accountsDb.createAccount(a);
		try {
			coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains("doesn't have instructor privileges", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		LogicTest.verifyAbsentInDatastore(i);
		
		______TS("fails: error during account creation");
		
		a.isInstructor = true;
		accountsDb.updateAccount(a);
		
		c.id = "invalid id";
		
		try {
			coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			assertContains("not acceptable to TEAMMATES as a Course ID", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		LogicTest.verifyAbsentInDatastore(i);
		
		______TS("fails: error during instructor creation");
		
		c.id = "fresh-course-tccai";
		instructorsDb.createInstructor(i); //create a duplicate instructor
		
		try {
			coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			assertContains("Unexpected exception while trying to create instructor for a new course", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		
		______TS("success: typical case");

		//remove the duplicate instructor object from the datastore.
		instructorsDb.deleteInstructor(i.courseId, i.googleId);
		
		coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
		LogicTest.verifyPresentInDatastore(c);
		LogicTest.verifyPresentInDatastore(i);
		
	}
	
	//TODO: add missing test cases
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		turnLoggingDown(CoursesDb.class);
		helper.tearDown();
	}

}
