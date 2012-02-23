package teammates.testing.old;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
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
		clickEvaluationTab();
		clickAndConfirmEvaluationRemind(0);

		justWait();

		// Confirm Email
		for (int i = 0; i < sc.students.size(); i++) {
			assertEquals(sc.course.courseId,
					SharedLib.getEvaluationReminderFromGmail(sc.students.get(i).email,
							Config.inst().TEAMMATES_APP_PASSWD, sc.course.courseId, sc.evaluation.name));
		}
	}
}