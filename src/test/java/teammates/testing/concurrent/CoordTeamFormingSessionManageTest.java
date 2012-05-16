package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/*
 * author Kalpit
 */
public class CoordTeamFormingSessionManageTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");
	

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordTeamFormingSessionManageTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordTeamFormingSessionManageTest ==========//");
	}

	/**
	 * Edit team forming session
	 */
	@Test
	public void testCoordEditTeamFormingSession() {
		String newInstruction = "Max team size is 5 and Min team size is 3.";
		String newProfileTemplate = "Strengths, past projects, semester schedule and area of interest";
		
		bi.gotoTeamForming();
		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);

		Integer gracePeriod = 5;
		bi.wdFillString(bi.inputInstruction, newInstruction);
		bi.wdFillString(bi.inputProfileTemplate, newProfileTemplate);
		bi.selectDropdownByValue(bi.inputGracePeriod, gracePeriod.toString());

		bi.waitAndClick(bi.editTeamFormingSessionButton);
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMFORMINGSESSION_EDITED);
		
		// Now click Edit again to see if the text is updated.
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		assertEquals(newInstruction, bi.getElementText(bi.inputInstruction));

		// Click back
		bi.clickTeamFormingTab();
	}
	
	@Test
	public void verifyManageTeamFormingSessionPage(){
		bi.gotoTeamForming();
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.verifyManageTeamFormingPage(scn.students);
	}
}
