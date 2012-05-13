package teammates.testing.concurrent;

import java.util.ArrayList;
import java.util.List;

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
public class CoordTeamFormingSessionAddWithoutStudentsTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordTeamFormingSessionAddWithoutStudents");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);

		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		TMAPI.cleanupCourse(scn.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("CoordTeamFormingSessionAddWithoutStudents ==========//");
	}

	@Test
	public void testCoordAddTeamFormingSession() {
		bi.gotoTeamForming();
		bi.addTeamFormingSession(scn.teamFormingSession);
		bi.justWait();
		bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMFORMINGSESSION_ADDED_WITH_EMPTY_CLASS);		
		System.out.println("========== testCoordTeamFormingSessionAddWithoutStudentsFailed ==========");
	}
}
