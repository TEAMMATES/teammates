package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.TeamFormingSession;

/*
 * author Kalpit
 */
public class CoordTeamFormingSessionAddTest extends TestCase {
	
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {

		System.out.println("========== CoordTeamForming");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("CoordTeamForming ==========//");
	}

	@Test
	public void testCoordAddTeamFormingSession() {
		
		testCoordAddTeamFormingSessionSuccessful();

		testCoordAddDuplicateTeamFormingSessionFailed();
		
		testCoordAddTeamFormingSessionWithInvalidInputFailed();

	}

	public void testCoordAddTeamFormingSessionSuccessful() {
		
		bi.gotoTeamForming();
		bi.addTeamFormingSession(scn.teamFormingSession);
		
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMFORMINGSESSION_ADDED);
		
		bi.clickTeamFormingTab();
		bi.verifyTeamFormingSessionAdded(scn.teamFormingSession.courseID, bi.TEAMFORMINGSESSION_STATUS_AWAITING);
		
		System.out.println("========== testCoordAddTeamFormingSessionSuccessful ==========");
	}

	public void testCoordAddDuplicateTeamFormingSessionFailed() {
		bi.gotoTeamForming();
		bi.addTeamFormingSession(scn.teamFormingSession);
		
		assertEquals(bi.ERROR_MESSAGE_TEAMFORMINGSESSION_EXISTS, bi.getElementText(bi.statusMessage));
		System.out.println("========== testCoordAddDuplicateTeamFormingSessionFailed ==========");
	}

	public void testCoordAddTeamFormingSessionWithInvalidInputFailed() {
		TeamFormingSession teamForming = scn.teamFormingSession;
		Integer nextTimeValue = Integer.parseInt(teamForming.nextTimeValue)-2;
		
		bi.gotoTeamForming();
		bi.addTeamFormingSession(teamForming.courseID, teamForming.dateValue, nextTimeValue.toString(), 
				teamForming.gracePeriod, teamForming.instructions, teamForming.profileTemplate);
		
		assertEquals(bi.ERROR_INVALID_INPUT_TEAMFORMINGSESSION, bi.getElementText(bi.statusMessage));
		
		System.out.println("========== testCoordAddTeamFormingSessionWithInvalidInputFailed ==========");
	}
}
