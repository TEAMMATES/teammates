package teammates.testing.concurrent;

import static org.junit.Assert.assertTrue;

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

public class StudentEvaluationResultsTest extends TestCase implements Runnable {
	
	static Scenario scn = setupScenarioInstance("scenario");
	private Student student;
	
	@BeforeClass
	public static void classSetup() {
		
		TMAPI.cleanupCourse(scn.course.courseId);
		
		TMAPI.createCourse(scn.course);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.students, scn.course.courseId, scn.evaluation.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation.name);
	}
	
	@AfterClass
	public static void classTearDown() {
		TMAPI.cleanupCourse(scn.course.courseId);
	}
	
	@Test
	public void testStudentViewEvaluationResultsSuccessful() throws Exception {
		List<Thread> threads = new ArrayList<Thread>();
		for (Student student : scn.students) {
			StudentEvaluationResultsTest r = new StudentEvaluationResultsTest();
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
		bi.studentLogin(student.email, student.password);

		bi.clickEvaluationTab();
		bi.justWait();
		
		bi.studentClickEvaluationViewResults(scn.course.courseId, scn.evaluation.name);
		bi.justWait();
		
		
		//comments order is random
		for(int i = 0; i < scn.students.size(); i++) {
			Student teammate = scn.students.get(i);
			if(teammate.teamName.equals(student.teamName) && !teammate.name.equals(student.name)){
				assertTrue(bi.studentGetFeedbackFromOthers(teammate.email, student.email));
			}
		}
		
		
		bi.logout();
		System.out.println("---------- studentViewEvaluationResults: " + student.name + " under test ----------" );
		
		BrowserInstancePool.release(bi);
	}
	
	private void setStudent(Student student) {
		this.student = student;
	}
}
