package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class StudentEvaluationEditTest extends TestCase implements Runnable {

	static Scenario scn = setupScenarioInstance("scenario");
	private Student student;

	@BeforeClass
	public static void classSetup() {
		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.students, scn.course.courseId, scn.evaluation.name);
	}

	@AfterClass
	public static void classTearDown() {

	}

	@Test
	public void testStudentEditEvaluationSuccessful() throws Exception {
		// verifyEvaluationResultsPage
		List<Thread> threads = new ArrayList<Thread>();
		for (Student student : scn.students) {
			StudentEvaluationEditTest r = new StudentEvaluationEditTest();
			r.setStudent(student);
			Thread t = new Thread(r);
			t.start();
			threads.add(t);
		}
		// Wait for all threads to fininh
		for (Thread t : threads) {
			t.join();
		}

	}

	@Override
	public void run() {
		BrowserInstance bi = BrowserInstancePool.request();
		bi.waitAWhile(3000);
		bi.studentLogin(student.email, student.password);
		
		bi.clickEvaluationTab();
		bi.justWait();

		// Try open and cancel
		bi.studentClickEditEvaluation(scn.course.courseId, scn.evaluation.name);
		bi.waitAndClick(bi.editEvaluationBackButton);

		// Try open and edit
		bi.studentClickEditEvaluation(scn.course.courseId, scn.evaluation.name);
		// edit contents
		for (int i = 0; i < student.team.students.size(); i++) {
			bi.setSubmissionPoint(i, "80");
			bi.setSubmissionJustification(i, String.format("Student Edit:: Justification from %s to %s.", student.email, student.team.students.get(i).email));
			bi.setSubmissionComments(i, String.format("Student Edit:: Comments from %s to %s.", student.email, student.team.students.get(i).email));
		}
		bi.waitAndClick(bi.studentSubmitEvaluationButton);
		bi.waitForElementText(bi.statusMessage, "The evaluation has been submitted.");

		// check feedbacks updated:
		bi.studentClickEditEvaluation(scn.course.courseId, scn.evaluation.name);
		for (int i = 0; i < student.team.students.size(); i++) {
			assertEquals(bi.getDropdownSelectedValue(bi.getSubmissionPoint(i)), "80");
			assertEquals(bi.getElementValue(bi.getSubmissionJustification(i)), String.format("Student Edit:: Justification from %s to %s.", student.email, student.team.students.get(i).email));
			assertEquals(bi.getElementValue(bi.getSubmissionComments(i)), String.format("Student Edit:: Comments from %s to %s.", student.email, student.team.students.get(i).email));
		}
		bi.waitAndClick(bi.studentEvaluationCancelButton);// [cancel]

		bi.logout();
		BrowserInstancePool.release(bi);
	}

	private void setStudent(Student student) {
		this.student = student;
	}

}
