package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import teammates.testing.lib.TMAPI;

/**
 * Test actions to an Evaluation. Actions tested: - Add - Edit - Delete
 * 
 * @author nvquanghuy
 * 
 */
public class TestCoordEvaluation extends BaseTest {

	@BeforeClass
	public static void classSetUp() throws Exception {
		// Bring the system's state to desired state
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);

		setupSelenium();
		coordinatorLogin(Config.TEAMMATES_APP_ACCOUNT, Config.TEAMMATES_APP_PASSWD);
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}

	/**
	 * Try adding new evaluation
	 */
	@Test
	public void testAddEvaluation() {
		cout("TestCoordEvaluation: Adding evaluation.");

		gotoEvaluations();
		addEvaluation(sc.evaluation);
		
		gotoEvaluations();
		verifyEvaluationAdded(sc.course.courseId, sc.evaluation.name, "AWAITING", "0 / " + sc.students.size());
	}

	/**
	 * Test add duplicated evaluation
	 */
	@Test
	public void testDuplicateEvaluation() {
		cout("TestCoordEvaluation: TestCreatingCoursePreviouslyNamed");
		gotoEvaluations();
		addEvaluation(sc.evaluation);
		
		assertEquals("The evaluation exists already.", getElementText(statusMessage));

	}
	/**
	 * Edit evaluation
	 */
	@Test
	public void testEditEvaluation() {
		cout("TestCoordEvaluation: Editing evaluation.");

		String inst_new = "Something fancy and new";

		clickEvaluationEdit(0);
		justWait();
		
		wdFillString(inputInstruction, inst_new);
		
		wdClick(editEvaluationButton);
		waitForElementText(statusMessage, "The evaluation has been edited.");

		// Now click Edit again to see if the text is updated.
		clickEvaluationEdit(0);
		justWait();
		assertEquals(inst_new, getElementText(inputInstruction));

		// Click back
		wdClick(editEvaluationBackButton);
		justWait();
	}
	
	/**
	 * Remove all evaluations
	 */
	@Test
	public void testDeleteEvaluation() {
		cout("TestCoordEvaluation: Deleting evaluation.");
		deleteAllEvaluations();
	}
	
	@Test
	public void testAddDeletedEvaluation() {
		cout("TestCoordEvaluation: Adding deleted evaluation.");
		
		addEvaluation(sc.evaluation);
		gotoEvaluations();
		verifyEvaluationAdded(sc.course.courseId, sc.evaluation.name, "AWAITING", "0 / " + sc.students.size());
	}
}