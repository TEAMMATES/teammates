package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
public class CoordTeamFormingSessionDeleteTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {

		System.out.println("========== CoordTeamFormingSessionDelete");
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
		System.out.println("CoordTeamFormingSessionDelete ==========//");
	}
	
	@Test
	public void testCoordTeamFormingSessionDelete() {
		testCoordDeleteTeamFormingSession();
		testCoordDeleteCourseAndTeamFormingSession();		
	}
	
	/**
	 * Test: delete team forming session
	 * */
	public void testCoordDeleteTeamFormingSession() {
		bi.goToTeamForming();
		
		bi.clickCoordTFSDelete(scn.course.courseId);
		
		assertEquals(bi.MESSAGE_TEAMFORMINGSESSION_DELETED, bi.getElementText(bi.statusMessage));

		bi.clickTeamFormingTab();
	}
	
	/**
	 * Test: delete course leads to delete team forming session
	 * */
	public void testCoordDeleteCourseAndTeamFormingSession() {
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		
		bi.goToCourses();
		bi.waitForElementPresent(bi.getCourseIDCell(scn.course.courseId));
		bi.clickCoordCourseDeleteAndConfirm(scn.course.courseId);
		bi.waitForTextInElement(bi.statusMessage, BrowserInstance.MESSAGE_COURSE_DELETED);
		assertFalse(bi.isCoursePresent(scn.course.courseId, scn.course.courseName));
		
		bi.goToTeamForming();
		assertFalse(bi.isTeamFormingSessionPresent(scn.course.courseId));
	}
}
