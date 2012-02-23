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

public class StudentEvaluationSubmitTest extends TestCase implements Runnable {

	static Scenario scn = setupScenarioInstance("scenario");
	private Student student;

	@BeforeClass
	public static void classSetup() throws Exception {
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
		TMAPI.cleanupCourse(scn.course.courseId);
	}

	@Test
	public void testStudentSubmitEvaluationSuccessful() throws Exception {
		List<Thread> threads = new ArrayList<Thread>();
		for (Student student : scn.students) {
			StudentEvaluationSubmitTest r = new StudentEvaluationSubmitTest();
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

	private void setStudent(Student student) {
		this.student = student;
	}

	/**
	 * Test body
	 */
	@Override
	public void run() {
		studentSubmitEvaluation();
	}
	
	private void studentSubmitEvaluation() {
		BrowserInstance bi = BrowserInstancePool.request();
		bi.waitAWhile(3000);
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
		System.out.println("---------- StudentSubmitEvaluation: " + student.name + " under test ----------" );
				
		// Remember to release this instance once done
		BrowserInstancePool.release(bi);
	}
}
