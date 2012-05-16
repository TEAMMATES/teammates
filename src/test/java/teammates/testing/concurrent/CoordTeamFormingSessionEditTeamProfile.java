package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

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
		ArrayList teams = new ArrayList();
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
		bi.gotoTeamForming();
		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.verifyManageTeamFormingPage(scn.students);

		bi.waitAndClick(bi.coordEditTeamProfile0);
		bi.verifyTeamDetailPage();
		
		String newTeamName = "Team 2";
		bi.wdFillString(bi.inputTeamName, newTeamName);
		bi.waitAndClick(bi.saveTeamProfile);
		bi.waitForTextInElement(bi.statusMessage, bi.ERROR_MESSAGE_TEAMPROFILE_EXISTS);
	}
	
	/**
	 * Edit team profile: team profile saved
	 */
	@Test
	public void testCoordTeamFormingSessionEditTeamProfileSuccessful() {		
		bi.gotoTeamForming();
		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.verifyManageTeamFormingPage(scn.students);

		bi.waitAndClick(bi.coordEditTeamProfile0);
		bi.verifyTeamDetailPage();
		
		String newTeamName = "Team 3";
		String newTeamProfile = "This is team 3's profile.";
		bi.wdFillString(bi.inputTeamName, newTeamName);
		bi.wdFillString(bi.inputTeamProfile, newTeamProfile);
		bi.waitAndClick(bi.saveTeamProfile);
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMPROFILE_SAVED);
	}
}
