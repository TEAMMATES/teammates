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

public class StudentViewResultsUITest extends TestCase {
	
	static BrowserInstance bi;
	static Scenario scn;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		bi = BrowserInstancePool.getBrowserInstance();
		scn = setupBumpRatioScenarioInstance("bump_ratio_scenario", 0);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);

		TMAPI.createEvaluation(scn.evaluation);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitDynamicFeedbacks(scn.students, scn.course.courseId, scn.evaluation.name, scn.submissionPoints);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation.name);

		TMAPI.createEvaluation(scn.evaluation2);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.studentsSubmitDynamicFeedbacks(scn.students, scn.course.courseId, scn.evaluation2.name, scn.submissionPoints);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation2.name);
		
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation2.name);

		TMAPI.disableEmail();
	}

	@AfterClass
	public static void classTearDown() {
		TMAPI.cleanupCourse(scn.course.courseId);
		BrowserInstancePool.release(bi);
	}
	
	
	@Test
	public void testStudentViewResultPoints() {
		for (int i = 0; i < scn.students.size(); i++) {
			Student s = scn.students.get(i);
			bi.loginStudent(s.email, s.password);

			studentViewResultPoints(scn.course.courseId, scn.evaluation.name, i);
			studentViewResultPoints(scn.course.courseId, scn.evaluation2.name, i);

			bi.logout();
		}
	}

	public void studentViewResultPoints(String courseId, String evalName, int studentIndex) {
		bi.clickEvaluationTab();

		bi.studentClickEvaluationViewResults(courseId, evalName);

		String claimed = TMAPI.studentGetClaimedPoints(scn.submissionPoints, studentIndex);
		assertEquals(claimed, bi.studentGetEvaluationResultClaimedPoints());

		String perceived = TMAPI.studentGetPerceivedPoints(scn.submissionPoints, studentIndex);
		assertEquals(perceived, bi.studentGetEvaluationResultPerceivedPoints());
	}

}
