package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Course;
import teammates.testing.object.Scenario;

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
		setupNewScenarioForMultipleCourses();
		setupScenario();
		TMAPI.cleanup();
		//creating and enrolling students in first course
		TMAPI.createCourse(nsc.course2);
		TMAPI.enrollStudents(nsc.course2.courseId, nsc.students);
		//creating and enrolling students in second course
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
	 * Try adding same evaluation under different course but same coordinator
	 */
	@Test
	public void testAddSameEvaluationInDifferentCourse() {
		cout("TestCoordEvaluation: Adding same esvaluation name for a different course under same coordinator.");
		
		gotoEvaluations();
		addEvaluation(nsc.evaluation);
		assertEquals("The evaluation has been added.", getElementText(statusMessage));
		
		gotoEvaluations();
		verifyEvaluationAdded(nsc.course2.courseId, nsc.evaluation.name, "AWAITING", "0 / " + nsc.students.size());
	}	

	/**
	 * Test add duplicated evaluation
	 */
	@Test
	public void testDuplicateEvaluation() {
		cout("TestCoordEvaluation: TestCreatingEvaluationPreviouslyNamed");
		gotoEvaluations();
		
		addEvaluation(nsc.evaluation);
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
		clickAndConfirm(By.id("deleteEvaluation0"));
		waitForElementText(statusMessage, "The evaluation has been deleted.");
	}
	
	@Test
	public void testAddDeletedEvaluation() {
		cout("TestCoordEvaluation: Adding deleted evaluation.");
		
		addEvaluation(nsc.evaluation);
		gotoEvaluations();
		verifyEvaluationAdded(nsc.course2.courseId, nsc.evaluation.name, "AWAITING", "0 / " + nsc.students.size());
	}
}