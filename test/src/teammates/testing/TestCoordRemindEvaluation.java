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
 * Coordinator clicks "Remind" button next to Evaluation, in Evaluations page
 */
public class TestCoordRemindEvaluation extends BaseTest {

	@BeforeClass
	public static void classSetup() {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTeardown() {
		wrapUp();
	}

	@Test
	public void testRemindEvaluation() throws Exception {
		// Click Evaluations
		wdClick(By.className("t_evaluations"));
		waitAndClick(By.className("t_eval_remind"));
	// Click yes to confirmation
			Alert alert = driver.switchTo().alert();
			alert.accept();

		justWait();

		// Confirm Email
		for (int i = 0; i < sc.students.size(); i++) {
			assertEquals(sc.course.courseId,
					SharedLib.getEvaluationReminderFromGmail(sc.students.get(i).email,
							Config.TEAMMATES_APP_PASSWD, sc.course.courseId, sc.evaluation.name));
		}

	}

}
