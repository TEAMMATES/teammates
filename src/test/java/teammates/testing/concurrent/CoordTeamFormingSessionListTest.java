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
public class CoordTeamFormingSessionListTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");
	static Scenario scn2 = setupScenarioInstance("teamForming");
	static int teamFormingSessionsCount = 0;

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordTeamFormingSessionListTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn2.course.courseId);
			
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);

		TMAPI.createCourse(scn2.course);		
		TMAPI.enrollStudents(scn2.course.courseId, scn2.students);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		bi.goToTeamForming();
		teamFormingSessionsCount = bi.countTFS(); //count existing sessions
		
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		TMAPI.createTeamFormingSession(scn2.teamFormingSession);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn2.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordTeamFormingSessionListTest ==========//");
	}

	@Test
	public void verifyAddTeamFormingSessionPageSuccessful() throws Exception {
		bi.goToTeamForming();
		int count = bi.countTFS();
		assertEquals(teamFormingSessionsCount+2, count); //check if count increased by 2
		
		assertEquals(true, bi.isTextPresent(scn.course.courseId));
		assertEquals(true, bi.isTextPresent(scn2.course.courseId));
	}
	
	@Test
	public void coordTeamFormingSessionViewLog() throws Exception {
		bi.goToTeamForming();
		bi.clickCoordTFSViewLog(scn.course.courseId);
		
		String noLog = "There is no log currently available.";
		String actualLog = bi.getElementText
			(By.xpath(String.format("//div[@id='coordinatorEvaluationManagement']//form//table[@class='headerform']//tbody//tr")));
		assertEquals(noLog, actualLog);
	}
}
