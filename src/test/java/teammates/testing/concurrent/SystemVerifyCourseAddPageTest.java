package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.object.Scenario;

public class SystemVerifyCourseAddPageTest extends TestCase {
	
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
	
	@Test
	public void verifyAddCoursePageSuccessful() throws Exception {
		bi.gotoCourses();
		bi.verifyCurrentPageHTML("target/test-classes/pages/coordAddCourse.html");
	}
}