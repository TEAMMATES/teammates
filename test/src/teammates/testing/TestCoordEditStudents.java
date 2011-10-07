package teammates.testing;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * Test edit students feature 
 * 
 * TODO: not complete yet, and to comply to the new
 * suite
 * 
 * @author wangsha
 * 
 */
public class TestCoordEditStudents extends BaseTest {

	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	/**
	 * Test Mass Edit Students
	 */
	@Test
	public void testMassEditStudents() throws Exception {
		String students = "Team 1|User 6|\n" + "Team 1|User 0|\n"
				+ "Team 1|User 1|";

		// To Enroll page
		wdClick(By.className("t_course_enrol"));
		verifyEnrollPage();
		wdFillString(By.id("information"), students);
		wdClick(By.id("button_enrol"));

		// Make sure the error message is there
		assertTrue(isElementPresent(By.xpath("//div[@id='statusMessage']/font[2]")));

		wdClick(By.className("t_back"));
	}

	/**
	 * Test edit individual student
	 */
	public void testIndividualEditStudent() throws Exception {
		wdClick(By.className("t_courses"));
		justWait();

		wdClick(By.className("t_course_view"));
		justWait();

		// Edit
		driver.findElement(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[2]", 2)));
		justWait();

		// TODO navigate through xpath and fill in the information accordinaly

	}

}
