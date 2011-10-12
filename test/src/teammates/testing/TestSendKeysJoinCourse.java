package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

public class TestSendKeysJoinCourse extends BaseTest {

	/**
	 * Test Send Registration Keys feature. Make sure all participants received
	 * an email with the key that matches the key displayed in the Admin CP
	 * 
	 * Then also test
	 * 
	 * Currently depends on TestCourse & TestEnrollStudents. Run those first.
	 * 
	 */

	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);

		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		// Write to the new json file (since we have collected the
		// registration keys for students)
		// sc.toJSONFile("./scenario.json.ext");
		wrapUp();
	}

	/**
	 * Test to send out registration keys, check students' email for keys and
	 * compare the keys with keys collected from Coordinator's page
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSendRegistrationKeys() throws Exception {
		cout("Test: Send registration keys.");

		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
		gotoCourses();
		wdClick(By.className("t_course_view"));

		System.out.println("Sending registration keys to students.");

		// Click on Send Registration Keys
		waitForElementPresent(By.id("dataform"));
		waitAndClick(By.className("t_remind_students"));
		// Click yes to confirmation
		Alert alert = driver.switchTo().alert();
		alert.accept();

		waitForElementText(By.id("statusMessage"),
				"Emails have been sent to unregistered students.");

		waitAWhile(5000);
		System.out.println("Collecting registration keys.");

		// Collect keys
		for (int i = 0; i < sc.students.size(); i++) {
			waitAndClick(By.xpath(String.format(
					"//table[@id='dataform']//tr[%d]//a[1]", i + 2)));
			waitForElementPresent(By.id("t_courseKey"));
			sc.students.get(i).courseKey = getElementText(By.id("t_courseKey"));
			System.out.println(i + ":" + sc.students.get(i).courseKey);
			wdClick(By.className("t_back")); // Back
		}
/*
		// Reserve more time to send email
		for (int i = 0; i < sc.students.size(); i++) {
			assertEquals(sc.students.get(i).courseKey,
					SharedLib.getRegistrationKeyFromGmail(
							sc.students.get(i).email,
							Config.TEAMMATES_APP_PASSWD, sc.course.courseId));
		}
*/
	}

	/**
	 * Each student will join the course. They'll first use wrong key and then
	 * correct key.
	 */
	@Test
	public void testStudentsJoinCourse() throws Exception {
		cout("Test: Students joining course.");
		for (Student s : sc.students) {
			// Logout
			logout();

			// First log that student in
			studentLogin(s.email, Config.TEAMMATES_APP_PASSWD);

			waitForElementPresent(By.id("dataform"));

			// Try a wrong course key
			wdFillString(By.id("regkey"), "totally_wrong_key");
			wdClick(By.id("btnJoinCourse"));
			waitForElementText(By.id("statusMessage"),
					"Registration key is invalid.");

			WebElement dataform = driver.findElement(By.id("dataform"));
			if (dataform.findElements(By.tagName("tr")).size() == 1) {
				// This time the correct one
				waitForElementPresent(By.id("regkey"));
				wdFillString(By.id("regkey"), s.courseKey);
				System.out.println("key for " + s.name + " : " + s.courseKey);
				wdClick(By.id("btnJoinCourse"));
				waitForElementText(By.id("statusMessage"),
						"You have successfully joined the course.");
			}

		}
	}

}
