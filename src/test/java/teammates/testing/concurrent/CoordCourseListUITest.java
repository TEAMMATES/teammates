package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

/**
 * Page under test: Coordinator Add Course
 * Object under test: Course List
 * 
 * */
public class CoordCourseListUITest extends TestCase {
	static Scenario scn = setupScenarioInstance("scenario");
	static BrowserInstance bi;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.request();
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab))
			bi.logout();
		
		BrowserInstancePool.release(bi);
	}
	
	/**
	 * Test: sort course by course ID and course name
	 * Condition: course list should be fixed (cannot run in parallel)
	 * Action: click sorting button
	 * Expectation: sort from A-Z and Z-A
	 * TODO: separate the test from parallel run
	 * */
	@Test
	public void testCoordSortCourseListSuccessful() throws Exception {
		bi.gotoCourses();
		
		//Click sort by ID
		
		bi.verifyCurrentPageHTML("target/test-classes/pages/coordAddCourse.html");
		
		//Click sort by Name
		
		
	}

}
