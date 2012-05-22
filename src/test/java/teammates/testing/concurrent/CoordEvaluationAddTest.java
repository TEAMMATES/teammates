package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;

public class CoordEvaluationAddTest extends TestCase {
	static final int COURSENAME_MAX_LENGTH = 38;
	
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	static Scenario scn2 = setupScenarioInstance("scenario");// different course name, same evaluation name

	@BeforeClass
	public static void classSetup() throws Exception {

		System.out.println("========== CoordEvaluation");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn2.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.createCourse(scn2.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.enrollStudents(scn2.course.courseId, scn2.students);

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn2.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("CoordEvaluation ==========//");
	}

	@Test
	public void testCoordAddEvaluation() {

		testInputFieldMaxLength();
		
		testCoordAddEvaluationSuccessful();
		
		testCoordAddDuplicateEvaluationInDifferentCourseSuccessful();

		testCoordAddDuplicateEvaluationFailed();
		
		testCoordAddEvaluationWithInvalidInputFailed();

	}
	
	public void testInputFieldMaxLength()
	{
		bi.goToEvaluation();
		
		String shortName = "This is a short name";
		assertEquals(shortName, bi.fillInEvalName(shortName));
		
		String longName = "This is a long name that exceeds "+ COURSENAME_MAX_LENGTH +" char limit for evaluation name";
		assertEquals(longName.substring(0, COURSENAME_MAX_LENGTH),bi.fillInEvalName(longName));
	}

	public void testCoordAddEvaluationSuccessful() {
		
		bi.goToEvaluation();
		bi.addEvaluation(scn.evaluation);
//		assertEquals(bi.MESSAGE_EVALUATION_ADDED, bi.getElementText(bi.statusMessage));

		bi.clickEvaluationTab();
		bi.verifyEvaluationAdded(scn.evaluation.courseID, scn.evaluation.name, bi.EVAL_STATUS_AWAITING, "0 / " + scn.students.size());
		
		System.out.println("========== testCoordAddEvaluationSuccessful ==========");
	}

	public void testCoordAddDuplicateEvaluationFailed() {
		bi.goToEvaluation();
		bi.addEvaluation(scn.evaluation);

//		assertEquals(bi.ERROR_MESSAGE_EVALUATION_EXISTS, bi.getElementText(bi.statusMessage));
		System.out.println("========== testCoordAddDuplicateEvaluationFailed ==========");
	}

	public void testCoordAddDuplicateEvaluationInDifferentCourseSuccessful() {
		bi.goToEvaluation();
		bi.addEvaluation(scn2.evaluation);

//		assertEquals(bi.MESSAGE_EVALUATION_ADDED, bi.getElementText(bi.statusMessage));
		bi.verifyEvaluationAdded(scn2.evaluation.courseID, scn2.evaluation.name, bi.EVAL_STATUS_AWAITING, "0 / " + scn2.students.size());
		System.out.println("========== testCoordAddDuplicateEvaluationInDifferentCourseSuccessful ==========");
	}

	// TODO:
	public void testCoordAddEvaluationWithInvalidInputFailed() {
		Evaluation eval = scn2.evaluation;
		String invalidEvalName = "Evaluation =)";
		
		bi.goToEvaluation();
		bi.addEvaluation(eval.courseID, invalidEvalName, eval.dateValue, eval.nextTimeValue, eval.p2pcomments, eval.instructions, eval.gracePeriod);

		assertEquals(bi.ERROR_INVALID_EVALUATION_NAME, bi.getElementText(bi.statusMessage));
		
		System.out.println("========== testCoordAddEvaluationWithInvalidInputFailed ==========");
	}

	// testCoordAddEvaluationWithMissingFieldsFailed

	// testCoordAddEvaluationWithInvalidNameFailed

	// testCoordAddEvaluationWithInvalidTimeFailed

}
