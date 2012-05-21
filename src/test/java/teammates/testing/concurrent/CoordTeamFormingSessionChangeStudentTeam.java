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
public class CoordTeamFormingSessionChangeStudentTeam extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");
	

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordTeamFormingSessionChangeStudentTeam");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		ArrayList teams = new ArrayList();
		teams.add("Team 1");
		teams.add("Team 2");
		TMAPI.createProfileOfExistingTeams(scn.course.courseId, scn.course.courseName, teams);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordTeamFormingSessionChangeStudentTeam ==========//");
	}
	
	@Test
	public void testCoordTeamFormingSessionChangeStudentTeam() {
		verifyChangeStudentTeamPage();
		testCoordChangeStudentTeam();
		testCoordAllocateTeamToStudent();
	}

	/**
	 * Change student team page verification
	 */
	public void verifyChangeStudentTeamPage() {		
		bi.goToTeamForming();
		
		bi.clickCoordTFSEdit(scn.course.courseId);

		bi.clickWithWait(bi.coordChangeStudentTeam11);
		bi.verifyChangeStudentTeamPage();
		bi.clickWithWait(bi.resultBackButton);
		
		bi.clickWithWait(bi.coordAllocateStudentTeam1);
		bi.verifyChangeStudentTeamPage();
	}
	
	/**
	 * Change student team
	 */
	public void testCoordChangeStudentTeam() {		
		bi.goToTeamForming();
		
		bi.clickCoordTFSEdit(scn.course.courseId);

		bi.clickWithWait(bi.coordChangeStudentTeam11);
		bi.verifyChangeStudentTeamPage();
		
		String newTeamName = "Team 1";
		bi.selectDropdownByValue(bi.inputTeamName, newTeamName);
		bi.clickWithWait(bi.saveChangeStudentTeam);
		
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMCHANGE_SAVED);
		assertEquals("Danny", bi.getElementText(bi.getStudentNameFromManageTeamFormingSession(4, 1)));
	}
	
	/**
	 * Allocate Student Team
	 */
	public void testCoordAllocateTeamToStudent() {		
		bi.goToTeamForming();
		
		bi.clickCoordTFSEdit(scn.course.courseId);

		bi.clickWithWait(bi.coordAllocateStudentTeam1);
		bi.verifyChangeStudentTeamPage();
		
		String newTeamName = "Team 3";
		bi.clickWithWait(By.xpath("//*[@id='teamchange_newteam'][@value='false']"));
		bi.fillString(bi.inputNewTeamName, newTeamName);
		bi.clickWithWait(bi.saveChangeStudentTeam);
		
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMCHANGE_SAVED);
		assertEquals(true, bi.isTextPresent("Team 3"));
	}
}
