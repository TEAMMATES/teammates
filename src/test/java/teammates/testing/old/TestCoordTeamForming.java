package teammates.testing.old;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.config.Config;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.TeamFormingSession;

/**
 * Test actions to a Team Forming Session. Actions tested: - Add - Manage - Delete
 * 
 * @author Kalpit
 * 
 */
public class TestCoordTeamForming extends BaseTest {

	@BeforeClass
	public static void classSetUp() throws Exception {
		// Bring the system's state to desired state
		setupScenario();
		TMAPI.cleanup();
		
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
	public void CoordTeamForming() {
		testAddTeamFormingSession();
		testDuplicateEvaluation();
		testEditTeamFormingSession();
		testDeleteTeamFormingSession();
		testAddDeletedTeamFormingSession();
	}

	/**
	 * Try adding new team forming session
	 */
	public void testAddTeamFormingSession() {
		cout("TestCoordTeamForming: Adding team forming session." + sc.teamFormingSession.courseID);
		gotoTeamForming();
		addTeamFormingSession(sc.teamFormingSession, 0);
		assertEquals("The team forming session has been added.", getElementText(statusMessage));
		
		gotoTeamForming();
		verifyTeamFormingSessionAdded(sc.teamFormingSession.courseID, "AWAITING");
	}

	/**
	 * Test add duplicated teamFormingSession
	 */
	public void testDuplicateEvaluation() {
		cout("TestCoordTeamForming: TestCreatingTeamFormingSession previously created");
		gotoTeamForming();
		
		TeamFormingSession teamForming = sc.teamFormingSession;
		clickTeamFormingTab();
		// Select the course
		waitAndClick(inputCourseID);
		cout("click " + teamForming.courseID);
		selectDropdownByValue(By.id("courseid"), teamForming.courseID);
		// Fill in instructions
		wdFillString(inputInstruction, teamForming.instructions);
		// Fill in instructions
		wdFillString(inputProfileTemplate, teamForming.profileTemplate);
		// Select deadline date
		wdClick(inputClosingDate);
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		wdClick(By.xpath("//a[contains(@href, '" + teamForming.dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		selectDropdownByValue(inputClosingTime, teamForming.nextTimeValue);
		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(teamForming.gracePeriod));
		// Submit the form
		justWait();
		while(!getElementText(statusMessage).equals("The team forming session exists already."))
			wdClick(createTeamFormingSessionButton);
		
		assertEquals("The team forming session exists already.", getElementText(statusMessage));
	}
	
	/**
	 * Edit team forming session
	 */
	public void testEditTeamFormingSession() {
		cout("TestCoordTeamForming: Editing team forming session.");

		String inst_new = "Max team size is 5 and Min team size is 3.";

		clickAndConfirm(By.id("manageTeamFormingSession0"));
		justWait();
		
		wdFillString(inputInstruction, inst_new);
		
		wdClick(editTeamFormingSessionButton);
		waitForElementText(statusMessage, "The team forming session has been edited.");

		// Now click Edit again to see if the text is updated.
		clickAndConfirm(By.id("manageTeamFormingSession0"));
		justWait();
		assertEquals(inst_new, getElementText(inputInstruction));

		gotoTeamForming();
	}
	
	/**
	 * Remove the team forming session
	 */
	public void testDeleteTeamFormingSession() {
		cout("TestCoordTeamForming: Deleting team forming session.");
		clickAndConfirm(By.id("deleteTeamFormingSession0"));
		waitForElementText(statusMessage, "The team forming session has been deleted.");
	}
	
	public void testAddDeletedTeamFormingSession() {
		cout("TestCoordTeamForming: Adding deleted team forming session.");
		
		addTeamFormingSession(sc.teamFormingSession, 0);
		gotoTeamForming();
		verifyTeamFormingSessionAdded(sc.teamFormingSession.courseID, "AWAITING");
	}
}