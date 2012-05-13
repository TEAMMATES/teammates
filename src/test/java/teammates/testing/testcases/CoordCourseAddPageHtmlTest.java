package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

public class CoordCourseAddPageHtmlTest extends BaseTestCase {
	
	static Scenario scn = Scenario.scenarioForPageVerification("target/test-classes/data/page_verification.json");
	static BrowserInstance bi;
	private static final String ADD_COURSE_TAG = "<div id=\"coordinatorCourseManagement\">";
	private static final String LIST_COURSE_TAG = "<div id=\"coordinatorCourseTable\">";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseAddPageHtmlTest");
		bi = BrowserInstancePool.getBrowserInstance();
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}


	@Test
	public void verifyAddCourse() throws Exception {
		bi.gotoCourses();
		bi.verifyObjectHTML("target/test-classes/pages/coordAddCourse.html", ADD_COURSE_TAG);
		bi.verifyObjectHTML("target/test-classes/pages/coordListCourseByID.html", LIST_COURSE_TAG);
	
		bi.clickCoordCourseSortByNameButton();
		bi.verifyObjectHTML("target/test-classes/pages/coordListCourseByName.html", LIST_COURSE_TAG);
		
		bi.clickCoordCourseSortByIdButton();
		bi.verifyObjectHTML("target/test-classes/pages/coordListCourseByID.html", LIST_COURSE_TAG);
		
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab)){
			bi.logout();
		}	
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseAddPageHtmlTest");
	}
}