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

		cout("Clean inbox for " + sc.students.get(0).name);
		try {
			SharedLib.markAllEmailsSeen(sc.students.get(0).email,
					Config.TEAMMATES_APP_PASSWD);
			SharedLib.markAllEmailsSeen(Config.INDIVIDUAL_ACCOUNT,
					Config.TEAMMATES_APP_PASSWD);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		clickCourseTab();
		clickCourseEnrol(0);
		verifyEnrollPage();

		wdFillString(enrolInfo, String.format("%s|%s|%s|",
				sc.students.get(0).teamName, Config.INDiVIDUAL_NAME,
				Config.INDIVIDUAL_ACCOUNT));
		wdClick(enrolButton);
		justWait();

		clickCourseTab();
		clickCourseView(0);
		waitAndClick(courseJoinStatusSorting);
		clickCourseDetailInvite(0);
		
		
				
		// TODO Collect key for the new student
		waitAndClick(By.xpath(String.format(
				"//div[@id='coordinatorStudentTable']//table[@id='dataform']//tr[%d]//td[%d]//a[1]",2, 4)));

		waitForElementPresent(studentDetailKey);
		String key = getElementText(studentDetailKey);
		System.out.println("Key for new student: " + key);
		wdClick(courseViewBackButton);

		// Assert that student gets a notification email
		waitAWhile(1000);
		assertEquals(key, SharedLib.getRegistrationKeyFromGmail(
				Config.INDIVIDUAL_ACCOUNT, Config.TEAMMATES_APP_PASSWD,
				sc.course.courseId));

		// Assert that rest students don't get spamed.
		System.out.println("ensure " + sc.students.get(0).name
				+ " doesn't get spam message");
		assertEquals("", SharedLib.getRegistrationKeyFromGmail(
				sc.students.get(0).email, Config.TEAMMATES_APP_PASSWD,
				sc.course.courseId));

		// Delete the student
		clickCourseTab();
		clickCourseView(0);
		waitAndClick(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[4]", 6)));
		Alert alert = driver.switchTo().alert();
		alert.accept();
	}
}