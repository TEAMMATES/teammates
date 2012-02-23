package teammates.testing.concurrent;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;

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

		//page title:
		bi.waitForElementText(bi.addCoursePageTitle, "ADD NEW COURSE");
		
		//input table:
		assertEquals(true, bi.isElementPresent(bi.inputCourseID));
		assertEquals(true, bi.isElementPresent(bi.inputCourseName));
		assertEquals(true, bi.isElementPresent(bi.addCourseButton));
		
		//list table:
		bi.waitForElementText(By.xpath("//div[@id='coordinatorCourseTable']/table/tbody//tr[1]//th[1]"), "COURSE ID");
		bi.waitForElementText(By.xpath("//div[@id='coordinatorCourseTable']/table/tbody//tr[1]//th[2]"), "COURSE NAME");
		bi.waitForElementText(By.xpath("//div[@id='coordinatorCourseTable']/table/tbody//tr[1]//th[3]"), "TEAMS");
		bi.waitForElementText(By.xpath("//div[@id='coordinatorCourseTable']/table/tbody//tr[1]//th[4]"), "TOTAL STUDENTS");
		bi.waitForElementText(By.xpath("//div[@id='coordinatorCourseTable']/table/tbody//tr[1]//th[5]"), "TOTAL UNREGISTERED");
		bi.waitForElementText(By.xpath("//div[@id='coordinatorCourseTable']/table/tbody//tr[1]//th[6]"), "ACTION(S)");
		
		//course data:
	}


}