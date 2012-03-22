package teammates.testing.concurrent;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/**
 * PUT: Coord Add Course
 * OUT: Course List
 * 
 * PUT = page under test
 * OUT = object under test
 * 
 * */

public class CoordCourseListUITest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseListTest");
		bi = BrowserInstancePool.request();
		
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordCourseListTest ==========//");
	}
	
	
	@Test
	public void testCoordDeleteCourseSuccessful() {
		System.out.println("TestCoordDeleteCourseSuccessful:");
		bi.waitForElementPresent(bi.getCourseID(scn.course.courseId));
		bi.clickAndConfirmCourseDelete(scn.course.courseId);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_DELETED);
		assertFalse(bi.isCoursePresent(scn.course.courseId, scn.course.courseName));
		
		//check that the evaluation has also been delete
		bi.gotoEvaluations();
		assertFalse(bi.isEvaluationPresent(scn.course.courseId, scn.evaluation.name));
		
		bi.logout();
		
		// Check that the course also 
		bi.studentLogin(scn.students.get(0).email, Config.inst().TEAMMATES_APP_PASSWD);
		bi.clickCourseTab();
		bi.justWait();
		assertFalse(bi.isCoursePresent(scn.course.courseId, scn.course.courseName));
		
	}
	
	/**
	 * Test: sort course by course ID and course name
	 * Condition: course list should be fixed (cannot run in parallel)
	 * Action: click sorting button
	 * Expectation: sort from A-Z and Z-A
	 * TODO: separate the test from parallel run
	 * */
//	@Test
	public void testCoordSortCourseListSuccessful() {
		//By ID
		
		//ByName
	}
}
