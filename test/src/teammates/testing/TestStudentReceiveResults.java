package teammates.testing;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Test if students have received the results.
 * 
 * TODO: Check students' email accounts. TODO: Student to move around in the
 * page.
 * 
 */
public class TestStudentReceiveResults extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.course.students, sc.course.courseId,
				sc.evaluation.name);
		TMAPI.publishEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}

	/**
	 * Each student should receive their respective results.
	 */
	@Test
	public void testStudentViewResults() {
		studentLogin(sc.students.get(0).email, sc.students.get(0).password);

		// Click Evaluations
		waitAndClick(By.className("t_evaluations"));

		// Click View Results
		waitAndClick(By.id("viewEvaluation0"));

		// Click Back
		waitAndClick(By.className("t_back"));
		justWait();

		logout();

	}
}
