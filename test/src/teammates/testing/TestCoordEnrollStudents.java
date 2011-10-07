package teammates.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Coordinator enrolling students
 * 
 * @author Huy
 * 
 */
public class TestCoordEnrollStudents extends BaseTest {

	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	/**
	 * Enroll a list of new students (half the from the test scenario)
	 */
	@Test
	public void testEnrollNewStudentsSuccess() throws Exception {
		cout("Test: Enrolling new students.");

		wdClick(By.className("t_courses"));
		waitForElementPresent(By.id("courseid"));

		int half = sc.students.size() / 2;
		List<Student> ls = sc.students.subList(0, half);
		enrollStudents(ls);

		// Check for number of successful students enrolled
		verifyEnrollment(half, 0);

		wdClick(By.className("t_back"));

		// Calculate the number of teams
		Set<String> set = new HashSet<String>();
		for (Student s : ls) {
			set.add(s.teamName);
		}

		assertEquals(set.size(), Integer.parseInt(getElementText(By
				.className("t_course_teams"))));
	}

	/**
	 * Enroll the entire student list. Making sure that the old students are not
	 * mistakenly re-enrolled.
	 */
	@Test
	public void testEnrollExistingStudentsSuccess() throws Exception {
		cout("Test: Enrolling more students (mixed new and old).");

		int left = sc.students.size() - sc.students.size() / 2;
		enrollStudents(sc.students);
		verifyEnrollment(left, 0);
		wdClick(By.className("t_back"));

		// Check number of teams
		assertEquals(sc.teams.size(), Integer.parseInt(getElementText(By
				.className("t_course_teams"))));
	}

	/**
	 * Fail to enroll students in without email addresses
	 */
	@Test
	public void testEnrollStudentsNoEmailsFail() throws Exception {
		cout("Test: Enrolling students with missing email addresses.");
		String students = "Team 1|User 6|\n" + "Team 1|User 0|\n"
				+ "Team 1|User 1|";

		// To Enroll page
		wdClick(By.className("t_course_enrol"));
		verifyEnrollPage();

		wdFillString(By.id("information"), students);
		wdClick(By.id("button_enrol"));

		// Make sure the error message is there
		assertTrue(isElementPresent(By
				.xpath("//div[@id='statusMessage']/font[2]")));

		wdClick(By.className("t_back"));
	}

}
