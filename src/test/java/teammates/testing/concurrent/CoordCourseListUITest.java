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
	static Scenario scn = Scenario.scenarioForPageVerification("target/test-classes/data/page_verification.json");
	static BrowserInstance bi;
	private static final String LIST_COURSE_TAG = "<div id=\"coordinatorCourseTable\">";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.getBrowserInstance();
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab))
			bi.logout();
		
		BrowserInstancePool.release(bi);
	}
	
	@Test
	public void verifyAddCoursePageSuccessful() throws Exception {
		bi.goToCourses();
		//Click sort by ID
		bi.clickWithWait(bi.coordCourseSortByIdButton);
		bi.verifyObjectHTML("target/test-classes/pages/coordListCourseByID.html", LIST_COURSE_TAG);
		
		//Click sort by Name
		bi.clickWithWait(bi.coordCourseSortByNameButton);
		bi.verifyObjectHTML("target/test-classes/pages/coordListCourseByName.html", LIST_COURSE_TAG);
	}
}