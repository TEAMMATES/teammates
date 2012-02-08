package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import teammates.testing.config.Config;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Evaluation;

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
		coordinatorLogin(Config.inst().TEAMMATES_APP_ACCOUNT, Config.inst().TEAMMATES_APP_PASSWD);
	}

	@AfterClass
	public static void classTearDown() {
		wrapUp();
	}
	
	@Test
	public void CoordEvaluation() {
		testAddEvaluation();
		testAddSameEvaluationInDifferentCourse();
		testDuplicateEvaluation();
		testEditEvaluation();
		testDeleteEvaluation();
		testAddDeletedEvaluation();
	}

	/**
	 * Try adding new evaluation
	 */
	public void testAddEvaluation() {
		cout("TestCoordEvaluation: Adding evaluation." + nsc.evaluation.name + "|" + nsc.evaluation.courseID);
		gotoEvaluations();
		addEvaluation(nsc.evaluation, 0);
		assertEquals("The evaluation has been added.", getElementText(statusMessage));
		
		gotoEvaluations();
		verifyEvaluationAdded(nsc.evaluation.courseID, nsc.evaluation.name, "AWAITING", "0 / " + nsc.students.size());
	}
	
	/**
	 * Try adding same evaluation under different course but same coordinator
	 */
	public void testAddSameEvaluationInDifferentCourse() {
		cout("TestCoordEvaluation: Adding same esvaluation name for a different course under same coordinator."
			+ sc.evaluation.name + "|" + sc.evaluation.courseID);

		gotoEvaluations();
		addEvaluation(sc.evaluation, 1);
		assertEquals("The evaluation has been added.", getElementText(statusMessage));

		}	

	/**
	 * Test add duplicated evaluation
	 */
	public void testDuplicateEvaluation() {
		cout("TestCoordEvaluation: TestCreatingEvaluationPreviouslyNamed");
		gotoEvaluations();
		
		Evaluation eval = nsc.evaluation;
		clickEvaluationTab();
		// Select the course
		waitAndClick(inputCourseID);
		cout("click " + eval.courseID);
		selectDropdownByValue(By.id("courseid"), eval.courseID);		
		// Fill in the evaluation name
		wdFillString(inputEvaluationName, eval.name);
		// Allow P2P comment
		wdClick(By.xpath("//*[@id='commentsstatus'][@value='" + eval.p2pcomments + "']"));
		// Fill in instructions
		wdFillString(inputInstruction, eval.instructions);
		// Select deadline date
		wdClick(inputClosingDate);
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		wdClick(By.xpath("//a[contains(@href, '" + eval.dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		selectDropdownByValue(inputClosingTime, eval.nextTimeValue);
		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(eval.gracePeriod));
		// Submit the form
		justWait();
		while(!getElementText(statusMessage).equals("The evaluation exists already."))
			wdClick(addEvaluationButton);
		
		assertEquals("The evaluation exists already.", getElementText(statusMessage));
	}
	
	/**
	 * Edit evaluation
	 */
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
	public void testDeleteEvaluation() {
		cout("TestCoordEvaluation: Deleting evaluation.");
		clickAndConfirm(By.id("deleteEvaluation0"));
		waitForElementText(statusMessage, "The evaluation has been deleted.");
	}
	
	public void testAddDeletedEvaluation() {
		cout("TestCoordEvaluation: Adding deleted evaluation.");
		
		addEvaluation(nsc.evaluation, 1);
		gotoEvaluations();
		verifyEvaluationAdded(nsc.course2.courseId, nsc.evaluation.name, "AWAITING", "0 / " + nsc.students.size());
	}
}