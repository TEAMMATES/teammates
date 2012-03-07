package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class StudentEvaluationSubmitTest2 extends TestCase {

	static Scenario scn = setupScenarioInstance("scenario");
	static BrowserInstance bi;

	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.request();
		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);

	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab))
			bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		BrowserInstancePool.release(bi);
	}

	@Test
	public void testStudentSubmitEvaluationSuccessful() throws Exception {
		for (Student student : scn.students) {
			studentSubmitEvaluation(student);
		}
	}


	
	private void studentSubmitEvaluation(Student student) {
		
		bi.studentLogin(student.email, student.password);
		
		bi.clickEvaluationTab();
		bi.waitForElementPresent(bi.studentGetPendingEvaluationName(scn.course.courseId, scn.evaluation.name));
		bi.studentClickDoEvaluation(scn.course.courseId, scn.evaluation.name);
		bi.justWait();

		// Fill in information
		for (int i = 0; i < student.team.students.size(); i++) {
			bi.setSubmissionPoint(i, "30");
			bi.setSubmissionJustification(i, String.format("Justification from %s to %s.", student.email, student.team.students.get(i).email));
			bi.setSubmissionComments(i, String.format("Comments from %s to %s.", student.email, student.team.students.get(i).email));
		}

		// Submit the evaluation
		bi.wdClick(bi.studentSubmitEvaluationButton);

		// Check to see evaluation status is "Submitted"
		assertEquals("SUBMITTED", bi.studentGetEvaluationStatus(scn.course.courseId, scn.evaluation.name));

		bi.logout();
	}
}
