package teammates.testing.concurrent;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

/*
 * author Kalpit
 */
public class CoordTeamFormingSessionEditTeamProfile extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");
	

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordTeamFormingSessionEditTeamProfile");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		ArrayList<String> teams = new ArrayList<String>();
		teams.add("Team 1");
		teams.add("Team 2");
		TMAPI.createProfileOfExistingTeams(scn.course.courseId, scn.course.courseName, teams);
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordTeamFormingSessionEditTeamProfile ==========//");
	}

	/**
	 * Edit team profile: team profile already exists
	 */
	@Test
	public void testCoordTeamFormingSessionEditTeamProfileUnsuccessful() {		
		bi.goToTeamForming();
		
		bi.clickCoordTFSEdit(scn.course.courseId);
		bi.verifyCoordManageTeamFormingPage(scn.students);

		bi.clickWithWait(bi.coordEditTeamProfile0);
		bi.verifyStudentTeamDetailPage();
		
		String newTeamName = "Team 2";
		bi.fillString(bi.inputTeamName, newTeamName);
		bi.clickWithWait(bi.saveTeamProfile);
		bi.waitForTextInElement(bi.statusMessage, bi.ERROR_MESSAGE_TEAMPROFILE_EXISTS);
	}
	
	/**
	 * Edit team profile: team profile saved
	 */
	@Test
	public void testCoordTeamFormingSessionEditTeamProfileSuccessful() {		
		bi.goToTeamForming();
		
		bi.clickCoordTFSEdit(scn.course.courseId);
		bi.verifyCoordManageTeamFormingPage(scn.students);

		bi.clickWithWait(bi.coordEditTeamProfile0);
		bi.verifyStudentTeamDetailPage();
		
		String newTeamName = "Team 3";
		String newTeamProfile = "This is team 3's profile.";
		bi.fillString(bi.inputTeamName, newTeamName);
		bi.fillString(bi.inputTeamProfile, newTeamProfile);
		bi.clickWithWait(bi.saveTeamProfile);
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMPROFILE_SAVED);
	}
}
