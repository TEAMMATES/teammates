package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
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
		bi.goToUrl("http://localhost:8080/"+Common.JSP_COORD_HOME);
	}

	@Test
	public void verifyHeader() throws Exception{
		bi.goToCourses();
		bi.verifyHeader("src/test/resources/pages/coordAddCourseHeader.html");
	}
	
	@Test
	public void verifyFooter() throws Exception{
		bi.goToCourses();
		bi.verifyFooter("src/test/resources/pages/coordAddCourseFooter.html");
	}

	@Test
	public void verifyAddCourse() throws Exception {
		bi.goToCourses();
		bi.verifyObjectHTML("src/test/resources/pages/coordAddCourseNew.html", ADD_COURSE_TAG);
		bi.verifyObjectHTMLRegex("src/test/resources/pages/coordListCourseByIDNew.html");
	
		bi.clickCoordCourseSortByNameButton();
		bi.verifyObjectHTMLRegex("src/test/resources/pages/coordListCourseByNameNew.html");
		
		bi.clickCoordCourseSortByIdButton();
		bi.verifyObjectHTMLRegex("src/test/resources/pages/coordListCourseByIDNew.html");
		
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