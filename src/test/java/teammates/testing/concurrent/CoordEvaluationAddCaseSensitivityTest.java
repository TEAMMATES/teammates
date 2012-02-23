package teammates.testing.concurrent;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;

public class CoordEvaluationAddCaseSensitivityTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupNewScenarioInstance("scenario");

	private static final String EVALUATION_NAME_LOWER = "evaluation 1";
	private static final String EVALUATION_NAME_UPPER = "EVALUATION 1";

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordEvaluationAddCaseSensitivityTest");
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);

		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		TMAPI.cleanupCourse(scn.course.courseId);
		bi.logout();
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordEvaluationAddCaseSensitivityTest ==========//");
	}

	/**
	 * Test: EvaluationNameCaseSensitivity
	 * 
	 * Condition: sensitivity under same course
	 * 
	 * */
	@Test
	public void testEvaluationNameCaseSensitivity() {
		System.out.println("testEvaluationNameCaseSensitivity: evaluation name - insensitive");
		
		bi.gotoEvaluations();
		
		// evaluation 1
		Evaluation eval = Evaluation.createEvaluation(scn.course.courseId, EVALUATION_NAME_LOWER, "true", "Please please fill in the forth evaluation", 10);
		bi.addEvaluation(eval);
		
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_ADDED);
		assertTrue(bi.isEvaluationPresent(scn.course.courseId, EVALUATION_NAME_LOWER));
		
		// EVALUATION 1
		eval = Evaluation.createEvaluation(scn.course.courseId, EVALUATION_NAME_UPPER, "true", "Please please fill in the forth evaluation", 10);
		bi.addEvaluation(eval);
		bi.justWait();
		//TODO: temp sensitive, change to insensitive?
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_EVALUATION_ADDED);
		assertTrue(bi.isEvaluationPresent(scn.course.courseId, EVALUATION_NAME_UPPER));
	}
	
	/**
	 * Test: EvaluationNameCaseSensitivityUnderDifferentCourses
	 * 
	 * TODO: sensitivity under different courses
	 * 
	 * */
//	@Test
	public void EvaluationNameCaseSensitivityUnderDifferentCourses() {
		
	}
}
