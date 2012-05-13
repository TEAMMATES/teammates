package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordEvaluationEditTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordEvaluationEditTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createEvaluation(scn.evaluation);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordEvaluationEditTest ==========//");
	}

	/**
	 * Edit evaluation
	 */
	@Test
	public void testCoordEditEvaluation() {
		String newInstruction = "New Instruction: I'm writing a new instruction to test char '. It should work fine.";
		
		bi.gotoEvaluations();
		
		bi.clickEvaluationEdit(scn.course.courseId, scn.evaluation.name);
		bi.justWait();

		bi.wdFillString(bi.inputInstruction, newInstruction);

		bi.wdClick(bi.editEvaluationButton);
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_EVALUATION_EDITED);

		// Now click Edit again to see if the text is updated.
		bi.clickEvaluationEdit(scn.course.courseId, scn.evaluation.name);
		bi.justWait();
		assertEquals(newInstruction, bi.getElementText(bi.inputInstruction));

		// Click back
		bi.wdClick(bi.editEvaluationBackButton);
		bi.justWait();
		
	}
}
