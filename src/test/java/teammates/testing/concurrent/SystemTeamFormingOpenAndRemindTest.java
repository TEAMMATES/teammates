package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;



import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

/*
 * author Kalpit
 */
public class SystemTeamFormingOpenAndRemindTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== SystemTeamFormingOpenAndRemindTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openTeamFormingSession(scn.course.courseId);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if (bi.isElementPresent(bi.logoutTab))
			bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("StudentTeamFormingSessionActionsTest ==========//");
	}

	@Test
	public void systemRemindTeamFormingTest() throws Exception {
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		bi.gotoTeamForming();
		bi.clickTeamFormingSessionRemind(scn.course.courseId);
		assertEquals(bi.MESSAGE_TEAMFORMINGSESSION_REMINDED, bi.getElementText(bi.statusMessage));
		
		TMAPI.activateAutomatedReminder();
		bi.logout();
	}
}
