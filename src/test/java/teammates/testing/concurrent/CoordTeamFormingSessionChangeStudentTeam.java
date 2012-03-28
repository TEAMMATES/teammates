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
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
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
		bi.gotoTeamForming();
		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.justWait();

		bi.wdClick(bi.coordChangeStudentTeam11);
		bi.verifyChangeStudentTeamPage();
		bi.wdClick(bi.resultBackButton);
		
		bi.wdClick(bi.coordAllocateStudentTeam1);
		bi.verifyChangeStudentTeamPage();
	}
	
	/**
	 * Change student team
	 */
	public void testCoordChangeStudentTeam() {		
		bi.gotoTeamForming();
		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.justWait();

		bi.wdClick(bi.coordChangeStudentTeam11);
		bi.verifyChangeStudentTeamPage();
		
		String newTeamName = "Team 1";
		bi.selectDropdownByValue(bi.inputTeamName, newTeamName);
		bi.wdClick(bi.saveChangeStudentTeam);
		
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_TEAMCHANGE_SAVED);
		assertEquals("Danny", bi.getElementText(bi.getStudentNameFromManageTeamFormingSession(4, 1)));
	}
	
	/**
	 * Allocate Student Team
	 */
	public void testCoordAllocateTeamToStudent() {		
		bi.gotoTeamForming();
		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.justWait();

		bi.wdClick(bi.coordAllocateStudentTeam1);
		bi.verifyChangeStudentTeamPage();
		
		String newTeamName = "Team 1";
		bi.wdClick(By.xpath("//*[@id='teamchange_newteam'][@value='false']"));
		bi.wdFillString(bi.inputNewTeamName, newTeamName);
		bi.wdClick(bi.saveChangeStudentTeam);
		
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_TEAMCHANGE_SAVED);
		assertEquals("Frank", bi.getElementText(bi.getStudentNameFromManageTeamFormingSession(5, 1)));
	}
}
