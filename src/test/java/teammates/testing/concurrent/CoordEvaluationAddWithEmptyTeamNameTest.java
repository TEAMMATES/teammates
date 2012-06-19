package teammates.testing.concurrent;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class CoordEvaluationAddWithEmptyTeamNameTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordEvaluationWithEmptyTeamName");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		//bi.gotoEvaluations();
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("CoordEvaluationWithEmptyTeamName ==========//");
	}

	@Test
	public void testCoordAddEvaluation() {
		List<Student> students = new ArrayList<Student>();
		
		Student newStudent = new Student();
		newStudent.teamName = "   ";
		newStudent.name = "Alice";
		newStudent.email = "alice.tmms@gmail.com";
		students.add(newStudent);
		
		newStudent = new Student();
		newStudent.teamName = "";
		newStudent.name = "Benny";
		newStudent.email = "benny.tmms@gmail.com";
		students.add(newStudent);
		
		newStudent = new Student();
		newStudent.teamName = "Team1";
		newStudent.name = "Charlie";
		newStudent.email = "charlie.tmms@gmail.com";
		students.add(newStudent);
		bi.goToCourses();
		bi.enrollStudents(students, scn.course.courseId);
		
		bi.goToEvaluation();
		bi.addEvaluation(scn.evaluation);

		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_EVALUATION_ADDED_WITH_EMPTY_TEAMS);

		bi.clickEvaluationTab();
		bi.verifyEvaluationAdded(scn.evaluation.courseID, scn.evaluation.name, Common.EVALUATION_STATUS_AWAITING, "0 / 3");

		
		
		System.out.println("========== testCoordAddEvaluationWithEmptyTeamNameSuccessful ==========");
	}
}
