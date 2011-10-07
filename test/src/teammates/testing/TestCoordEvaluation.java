package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

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
		// Login to Evaluation page
		coordinatorLogin(Config.TEAMMATES_APP_ACCOUNT, Config.TEAMMATES_APP_PASSWD);
		gotoEvaluations();
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
		cout("Test: Adding evaluation.");

		addEvaluation(sc.evaluation);
		gotoEvaluations();
		verifyEvaluationAdded(sc.course.courseId, sc.evaluation.name,
				"AWAITING", "0 / " + sc.students.size());
	}

	/**
	 * Edit evaluation
	 */
	@Test
	public void testEditEvaluation() {
		System.out.println("Test: Editing evaluation.");

		String inst_new = "Something fancy and new";

		wdClick(By.id("editEvaluation0"));
		justWait();
		// Change instruction text
		
		wdFillString(By.id("instr"), inst_new);
		
		wdClick(By.id("button_editevaluation"));
		waitForElementText(By.id("statusMessage"), "The evaluation has been edited.");

		// Now click Edit again to see if the text is updated.
		wdClick(By.name("editEvaluation0"));
		justWait();
		assertEquals(inst_new, getElementText(By.id("instr")));

		// Click back
		wdClick(By.className("t_back"));
		justWait();
	}
	
	/**
	 * Remove all evaluations
	 */
	@Test
	public void testDeleteEvaluation() {
		System.out.println("Test: Deleting evaluation.");
		deleteAllEvaluations();
	}
	
	@Test
	public void testAddDeletedEvaluation() {
		System.out.println("Test: Adding deleted evaluation.");
		
		addEvaluation(sc.evaluation);
		gotoEvaluations();
		verifyEvaluationAdded(sc.course.courseId, sc.evaluation.name,
				"AWAITING", "0 / " + sc.students.size());

	}


}
