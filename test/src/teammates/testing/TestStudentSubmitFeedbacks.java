package teammates.testing;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

public class TestStudentSubmitFeedbacks extends BaseTest {

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);

		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}

	/**
	 * Student gives feedback to others Sometimes it'll give missing info
	 */
	@Test
	public void testStudentsSubmitFeedbacksSuccess() throws Exception {
		cout("Test: Students submitting feedback.");

		for (Student s : sc.students) {

			// First we need to login
			studentLogin(s.email, s.password);

			System.out.println("Submitting feedback for student " + s.email);

			// To evaluation page
			waitAndClick(By.className("t_evaluations"));
			justWait();

			// Select the first evaluation available
			if (isElementPresent(By.id("doEvaluation0"))) {
				wdClick(By.id("doEvaluation0"));
			} else {
				wdClick(By.id("editEvaluation0"));
			}
			justWait();

			// Fill in information
			// System.out.println(selenium.getCssCount(By.id("points0")));

			for (int i = 0; i < s.team.students.size(); i++) {
				//selenium.select("points" + i, "value=30");
				selectDropdownByValue(By.id("points" + i), "30");
				wdFillString(By.name("justification" + i), String.format(
						"Justification from %s to %s.", s.email,
						s.team.students.get(i).email));
				wdFillString(
						By.name("commentstostudent" + i),
						String.format("Comments from %s to %s.", s.email,
								s.team.students.get(i).email));
			}

			// Submit the evaluation
			wdClick(By.name("submitEvaluation"));
			justWait();

			// Check to see evaluation status is "Submitted"
			waitForElementText(By.className("t_eval_status"), "SUBMITTED");

			logout();
			justWait();
		}

	}
}
