package teammates.testing;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Test system behavior when a student change team or drop class
 * 
 * @author Wang Sha
 */
// Assume: - 6 students enrolled. 1 evaluation created, and done by 6 students.
// 2 teams.
// To test: coordinator move Danny to team A
// - coordinator create evaluation 2
// - students fill evaluation 2
// - coordinator close evaluation. review evaluation 1 and 2.
// - coordinaotr delete Benny
// - results released. Make sure all students still see all the results they're
// supposed to see.

public class TestStudentMoveDropTeam extends BaseTest {

	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();

		TMAPI.cleanup();

		// set up pre-condition
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation.name);

		setupSelenium();
		setupNewScenario();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	private static void setupNewScenario() {
		// Swap Benny and Charlie's team

		sc.students.get(1).teamName = sc.students.get(2).teamName;
		sc.students.get(2).teamName = sc.students.get(0).teamName;

		sc.students.get(0).team.students.remove(sc.students.get(1));
		sc.students.get(3).team.students.remove(sc.students.get(2));
		sc.students.get(0).team.students.add(sc.students.get(2));
		sc.students.get(3).team.students.add(sc.students.get(1));

		sc.students.get(1).team = sc.students.get(2).team;
		sc.students.get(2).team = sc.students.get(0).team;

		System.out.println(sc.students.get(0).team.toString());
		System.out.println(sc.students.get(3).team.toString());
	}

	/**
	 * Test Student Move
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testMoveStudent() throws Exception {

		// Enroll Student

		wdClick(By.className("t_courses"));
		waitForElementPresent(By.id("courseid"));
		enrollStudents(sc.students);

		// Create New Evaluation
		cout("Creating second evaluation.");
		addEvaluation(sc.evaluation2);

		// Open New Evaluation
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation2.name);
		TMAPI.studentsSubmitFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation2.name);

		// Close Evaluation
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation2.name);

		// Coordinator verify
		waitAndClick(By.className("t_evaluations"));
		// Verify First Evaluation
		// waitAndClick(By.id("viewEvaluation0"));
		// waitAndClick(By.className("t_back"));

		// Verify Second Evaluation
		waitAndClick(By.id("viewEvaluation1"));
		waitForElementPresent(By.id("radio_detail"));

		WebElement htmldiv = driver.findElement(By
				.id("coordinatorEvaluationSummaryTable"));
		assertEquals(5, htmldiv.findElements(By.tagName("tr")).size());
	}

	/**
	 * Test Student Drop Class before submitting feedback
	 */
	public void testStrudentDropBeforeSubmission() {
		
	}
	/**
	 * Test Student Drop
	 */
	@Test
	public void testStudentDrop() {

		// Delete a student
		System.out.println("delete Alice");
		wdClick(By.className("t_courses"));
		waitAndClick(By.className("t_course_view"));
		waitForElementPresent(By.cssSelector("#dataform tr"));

		WebElement htmldiv = driver.findElement(By.id("coordinatorStudentTable"));

		assertEquals(5, htmldiv.findElements(By.tagName("tr")).size());

		waitAndClickAndConfirm((By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[3]", 2))));

		justWait();
		assertEquals(4, htmldiv.findElements(By.tagName("tr")).size());

		// Verify Report
		waitAndClick(By.className("t_evaluations"));
		// Verify Coordinator View
		waitAndClick(By.id("viewEvaluation0"));
		waitAndClick(By.id("viewEvaluationResults0"));
		waitAndClick(By.className("t_evaluations"));
		waitAndClick(By.id("viewEvaluation1"));
		waitAndClick(By.id("viewEvaluationResults1"));

		// Publish Evaluation
		waitAndClick(By.className("t_evaluations"));
		waitAndClickAndConfirm(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[3]", 2)));
		// Click yes to confirmation

		waitAndClickAndConfirm(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[3]", 3)));
		// Click yes to confirmation

		// Verify Student View (using Carlie account)
		logout();
		justWait();
		studentLogin(sc.students.get(2).email, Config.TEAMMATES_APP_PASSWD);
		waitAndClick(By.className("t_evaluations"));
		waitAndClick(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[1]", 2)));
		justWait();
		waitAndClick(By.className("t_evaluations"));
		waitAndClick(By.xpath(String.format(
				"//table[@id='dataform']//tr[%d]//a[1]", 2)));
		waitAndClick(By.className("t_back"));

	}

	
}