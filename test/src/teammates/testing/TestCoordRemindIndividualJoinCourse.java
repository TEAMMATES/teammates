package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;

/**
 * Test send individual reminder to join a course (Pre-condition: Existing
 * Students have all joined the course) This test will add a new student to the
 * course. Send an individual 'Remind Join' email. Make sure only that student
 * receives a notification. Delete that student in the end.
 */
public class TestCoordRemindIndividualJoinCourse extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);

		// Clean up students' mailbox
		for (int i = 0; i < sc.students.size(); i++) {
			// Keep reading until there's no reminder mail left (by this time all
			// reminder mails are marked read)
			while (!SharedLib.getRegistrationKeyFromGmail(sc.students.get(i).email,
					Config.TEAMMATES_APP_PASSWD, sc.course.courseId).equals(""))
				;
		}

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTeardown() {
		wrapUp();
	}

	@Test
	public void testRemindIndividualJoinCourse() throws Exception {
		// Add New Individual (make sure the rest has completed the evaluation)
		wdClick(By.className("t_course_enrol"));
		verifyEnrollPage();

		wdFillString(By.id("information"), String.format("%s|%s|%s|",
				sc.students.get(0).teamName, Config.INDiVIDUAL_NAME,
				Config.INDIVIDUAL_ACCOUNT));
		wdClick(By.id("button_enrol"));
		justWait();

		waitAndClick(By.className("t_courses"));
		waitAndClick(By.className("t_course_view"));
		waitAndClick(By.className("t_student_resend"));

		// Collect key for the new student
		waitAndClick(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[1]", 6)));

		waitForElementPresent(By.id("t_courseKey"));
		String key = getElementText(By.id("t_courseKey"));
		System.out.println("Key for new student: " + key);
		wdClick(By.className("t_back")); // Back

		// Assert that student gets a notification email
		waitAWhile(1000);
		assertEquals(key, SharedLib.getRegistrationKeyFromGmail(
				Config.INDIVIDUAL_ACCOUNT, Config.TEAMMATES_APP_PASSWD,
				sc.course.courseId));

		// Assert that rest students don't get spamed.
		for (int i = 0; i < sc.students.size(); i++) {
			assertEquals("", SharedLib.getRegistrationKeyFromGmail(
					sc.students.get(i).email, Config.TEAMMATES_APP_PASSWD,
					sc.course.courseId));
		}

		// Delete the student
		wdClick(By.className("t_courses"));

		// Click Evaluations
		waitAndClick(By.className("t_course_view"));
		waitAndClick(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[4]", 6)));
		// Click yes to confirmation
		Alert alert = driver.switchTo().alert();
		alert.accept();
	}

}
