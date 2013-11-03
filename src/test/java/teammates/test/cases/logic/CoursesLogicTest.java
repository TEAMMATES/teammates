package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.CoursesLogic;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class CoursesLogicTest extends BaseComponentTestCase {

	//TODO: add missing test cases

	//TODO: test getCourseSummaryWithoutStats 
	//TODO: test getCoursesSummaryWithoutStatsForInstructor
	
	private CoursesLogic coursesLogic = new CoursesLogic();
	private CoursesDb coursesDb = new CoursesDb();
	private AccountsDb accountsDb = new AccountsDb();
	private InstructorsDb instructorsDb = new InstructorsDb();
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(CoursesLogic.class);
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
			AssertHelper.assertContains("for a non-existent instructor", e.getMessage());
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
			AssertHelper.assertContains("doesn't have instructor privileges", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		LogicTest.verifyAbsentInDatastore(i);
		
		______TS("fails: error during course creation");
		
		a.isInstructor = true;
		accountsDb.updateAccount(a);
		
		c.id = "invalid id";
		
		try {
			coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains("not acceptable to TEAMMATES as a Course ID", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		LogicTest.verifyAbsentInDatastore(i);
		
		______TS("fails: error during instructor creation");
		
		c.id = "fresh-course-tccai";
		instructorsDb.createEntity(i); //create a duplicate instructor
		
		try {
			coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			AssertHelper.assertContains("Unexpected exception while trying to create instructor for a new course", e.getMessage());
		}
		LogicTest.verifyAbsentInDatastore(c);
		
		______TS("success: typical case");

		//remove the duplicate instructor object from the datastore.
		instructorsDb.deleteInstructor(i.courseId, i.googleId);
		
		coursesLogic.createCourseAndInstructor(i.googleId, c.id, c.name);
		LogicTest.verifyPresentInDatastore(c);
		LogicTest.verifyPresentInDatastore(i);
		
	}
	
	@Test
	public void testGetCourse() throws Exception {

		______TS("failure: course doesn't exist");

		assertNull(coursesLogic.getCourse("nonexistant-course"));

		______TS("success: typical case");

		CourseAttributes c = new CourseAttributes();
		c.id = "Computing101-getthis";
		c.name = "Basic Computing Getting";
		coursesDb.createEntity(c);

		assertEquals(c.id, coursesLogic.getCourse(c.id).id);
		assertEquals(c.name, coursesLogic.getCourse(c.id).name);

	}
			
}
