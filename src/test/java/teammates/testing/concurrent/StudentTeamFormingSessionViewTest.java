package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

/*
 * author Kalpit
 */
public class StudentTeamFormingSessionViewTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");
	static Scenario scn2 = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== StudentTeamFormingSessionViewTest");
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.createTeamFormingSession(scn.teamFormingSession);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		TMAPI.openTeamFormingSession(scn.course.courseId);
		
		TMAPI.cleanupCourse(scn2.course.courseId);
		TMAPI.createCourse(scn2.course);
		TMAPI.enrollStudents(scn2.course.courseId, scn2.students);
		TMAPI.studentsJoinCourse(scn2.students, scn2.course.courseId);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if (bi.isElementPresent(bi.logoutTab))
			bi.logout();
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.cleanupCourse(scn2.course.courseId);

		BrowserInstancePool.release(bi);
		System.out.println("StudentTeamFormingSessionViewTest ==========//");
	}
	
	@Test
	public void testStudentTeamFormingSessionView() throws Exception {
		for (Student student : scn.students) {
			studentViewTeamFormingSession(student);
		}
	}
	
	private void studentViewTeamFormingSession(Student student) {
		bi.studentLogin(student.email, student.password);
		bi.clickCoursesTab();
		bi.clickTeamFormingSessionViewTeams(scn.course.courseId);
		bi.justWait();
		
		assertEquals(bi.MESSAGE_LOG_REMINDSTUDENTS, bi.getElementText(bi.statusMessage));
		assertTrue(bi.isElementPresent(By.id("studentprofiledetail")));
		assertTrue(bi.isElementPresent(bi.saveStudentProfile));
		
		assertTrue(bi.isTextPresent(student.name));
		assertTrue(bi.isTextPresent(student.email));
		assertTrue(bi.isTextPresent(student.teamName));
		
		assertTrue(bi.isTextPresent(scn.teamFormingSession.instructions));
		assertTrue(bi.isTextPresent(scn.teamFormingSession.profileTemplate));
		
		bi.verifyViewTeamsPage(scn.students);
		bi.logout();
	}
	
	@Test
	public void testStudentViewTeamsWithoutNullTeamFormingSession() throws Exception {
		Student student = scn2.students.get(0);
		bi.studentLogin(student.email, student.password);
		bi.clickCoursesTab();
		bi.clickTeamFormingSessionViewTeams(scn2.course.courseId);
		bi.justWait();
		
		String header = "TEAM DETAIL FOR "+student.courseID;
		
		assertTrue(bi.getElementText(By.id("headerOperation")).contains(header.toUpperCase()));
		assertTrue(bi.isElementPresent(bi.resultBackButton));
	}
}
