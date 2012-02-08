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

			studentLogin(s.email, s.password);

			waitAndClickAndCheck(By.className("t_evaluations"),By.id("doEvaluation0"));
			justWait();

//			// Select the first evaluation available
//			if (isElementPresent(By.id("doEvaluation0"))) {
//				wdClick(By.id("doEvaluation0"));
//			} else {
//				wdClick(By.id("editEvaluation0"));
//			}
			//studentClickDoEvaluation(0);
			waitAndClickAndCheck(By.id("doEvaluation0"), By.id("points0"));
			// Fill in information
			// System.out.println(selenium.getCssCount(By.id("points0")));

			for (int i = 0; i < s.team.students.size(); i++) {
				//selenium.select("points" + i, "value=30");
				setSubmissionPoint(i, "30");
				setSubmissionJustification(i, String.format("Justification from %s to %s.", s.email, s.team.students.get(i).email));
				setSubmissionComments(i, String.format("Comments from %s to %s.", s.email, s.team.students.get(i).email));
			}

			// Submit the evaluation
			wdClick(studentSubmitEvaluationButton);

			// Check to see evaluation status is "Submitted"
			waitForElementText(studentGetEvaluationStatus(0), "SUBMITTED");

			logout();
		}
	}
}