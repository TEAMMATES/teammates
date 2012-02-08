package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import teammates.testing.config.Config;
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
		// sc.toJSONFile("target/test-classes/scenario.json.ext");
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
		cout("TestSendRegistrationKeys: Send registration keys.");

		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
		gotoCourses();
		clickCourseView(0);

		cout("Sending registration keys to students.");

		// Click on Send Registration Keys
		waitForElementPresent(By.id("dataform"));
		waitAndClick(remindStudentsButton);
		// Click yes to confirmation
		Alert alert = driver.switchTo().alert();
		alert.accept();

		waitForElementText(statusMessage, "Emails have been sent to unregistered students.");

		waitAWhile(5000);
		System.out.println("Collecting registration keys.");

		// Collect keys
		for (int i = 0; i < sc.students.size(); i++) {
			clickCourseDetailView(i);
			waitForElementPresent(studentDetailKey);
			sc.students.get(i).courseKey = getElementText(studentDetailKey);
			System.out.println(i + ":" + sc.students.get(i).courseKey);
			wdClick(studentDetailBackButton);
		}
		
		// Write key back to json file
		sc.toJSONFile("target/test-classes/scenario.json.ext");
		
	
		// Reserve more time to send email
		for (int i = 0; i < sc.students.size(); i++) {
			assertEquals(sc.students.get(i).courseKey,
					SharedLib.getRegistrationKeyFromGmail(sc.students.get(i).email,
							Config.inst().inst().TEAMMATES_APP_PASSWD, sc.course.courseId));
		}
    
	}

	/**
	 * Each student will join the course. They'll first use wrong key and then
	 * correct key.
	 */
	@Test
	public void testStudentsJoinCourse() throws Exception {
		cout("Test: Students joining course.");
		for (Student s : sc.students) {
			logout();
			studentLogin(s.email, Config.inst().TEAMMATES_APP_PASSWD);
			waitForElementPresent(By.id("dataform"));

			// Try a wrong course key
			wdFillString(inputRegKey, "totally_wrong_key");
			wdClick(studentJoinCourseButton);
			waitForElementText(statusMessage, "Registration key is invalid.");

			if (studentCountCourses() == 2) {
				// This time the correct one
				waitForElementPresent(inputRegKey);
				wdFillString(inputRegKey, s.courseKey);
				System.out.println("key for " + s.name + " : " + s.courseKey);
				wdClick(studentJoinCourseButton);
				waitForElementText(statusMessage, "You have successfully joined the course.");
			}

		}
		logout();
		// Verify number of unregistered student
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
		assertEquals(0, Integer.parseInt(getElementText(By.xpath("//table[@id='dataform']/tbody/tr[2]/td[5]"))));
	}
}