package teammates.testing.old;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

public class TestCoordSubmitFeedbacks extends BaseTest {

	@BeforeClass
	public static void classSetup() throws IOException {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.createEvaluation(sc.evaluation);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		TMAPI.openEvaluation(sc.course.courseId, sc.evaluation.name);
		TMAPI.closeEvaluation(sc.course.courseId, sc.evaluation.name);
		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);

	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}

	/**
	 * Coordinator submit feedbacks points only
	 */
	@Test
	public void testCoordinatorSubmitFeedbacksSuccess() throws Exception {
		cout("Test: Coordinator submitting feedback.");

		// Click Evaluation Tab
		gotoEvaluations();

		// click 'View Results':
		clickEvaluationViewResults(0);

		// click 'Edit' 1st student:
		clickReviewerSummaryEdit(0);
		
		Student s = sc.students.get(0);
		
		for (int i = 0; i < s.team.students.size(); i++) {
			setSubmissionPoint(i, "100");
		}
		 
		// Submit the evaluation
		wdClick(coordEvaluationSubmitButton);
		assertEquals("The particular evaluation results have been edited.",
		getElementText(statusMessage));
	}
}