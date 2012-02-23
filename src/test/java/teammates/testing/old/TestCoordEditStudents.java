package teammates.testing.old;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
		String students = "Team 1|User 6|\n" + "Team 1|User 0|\n" + "Team 1|User 1|";

		// To Enroll page
		clickCourseEnrol(0);
		verifyEnrollPage();
		wdFillString(enrolInfo, students);
		wdClick(enrolButton);

		// Make sure the error message is there
		assertTrue(isElementPresent(courseErrorMessage));

		wdClick(enrolBackButton);
	}

	/**
	 * Test edit individual student
	 */
	@Test
	public void testIndividualEditStudent() throws Exception {
		cout("TestCoordEditStudents: test individual edit student");
		
		gotoCourses();
		clickCourseView(0);
		justWait();

		// Edit
		clickCourseDetailEdit(0);
		

		// TODO navigate through xpath and fill in the information accordinaly

	}
}