package teammates.testing.old;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Case description: After student submit feedbacks, he/she edits it again while
 * evaluation is OPEN
 * 
 * Test: 1.test student edit feedbacks
 * 
 * @author xialin
 * 
 */

public class TestStudentEditFeedbacks extends BaseTest {

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(sc.students, sc.course.courseId,
				sc.evaluation.name);

		setupSelenium();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testEditFeedbacks() throws Exception {

		// student login
		Student s = sc.students.get(0);
		studentLogin(s.email, s.password);

		waitAndClickAndCheck(By.className("t_evaluations"), By.id("editEvaluation0"));

		// click [Edit] first evaluation (OPEN):
		studentClickEditEvaluation(0);

		// click [Cancel]
		waitAndClick(editEvaluationBackButton);

		// click [Edit] first evaluation (OPEN):
		studentClickEditEvaluation(0);

		// edit contents
		for (int i = 0; i < s.team.students.size(); i++) {
			setSubmissionPoint(i, "80");
			setSubmissionJustification(i, String.format(
					"Student Edit:: Justification from %s to %s.", s.email,
					s.team.students.get(i).email));
			setSubmissionComments(i, String.format(
					"Student Edit:: Comments from %s to %s.", s.email,
					s.team.students.get(i).email));
		}
		// click [Submit]
		waitAndClick(studentSubmitEvaluationButton);

		waitForElementText(statusMessage, "The evaluation has been submitted.");

		// check feedbacks updated:
		cout("testEditFeedbacks: Check feedbacks have been updated");
		studentClickEditEvaluation(0);
		for (int i = 0; i < s.team.students.size(); i++) {
			assertEquals(getDropdownSelectedValue(getSubmissionPoint(i)), "80");
			assertEquals(getElementValue(getSubmissionJustification(i)),
					String.format(
							"Student Edit:: Justification from %s to %s.",
							s.email, s.team.students.get(i).email));
			assertEquals(getElementValue(getSubmissionComments(i)),
					String.format("Student Edit:: Comments from %s to %s.",
							s.email, s.team.students.get(i).email));
		}
		waitAndClick(studentEvaluationCancelButton);// [cancel]

	}

}