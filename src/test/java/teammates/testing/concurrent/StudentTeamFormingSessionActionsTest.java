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
public class StudentTeamFormingSessionActionsTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== StudentTeamFormingSessionActionsTest");
		bi = BrowserInstancePool.request();

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
	public void testStudentTeamFormingSessionActions() throws Exception {
		testStudentTeamFormingAddJoinLeaveTeam(scn.students.get(5));
		testStudentTeamFormingAddJoinLeaveTeam(scn.students.get(0));
		testCoordinatorCheckStudentTeamsAndLog(scn.students);
	}
	
	public void testStudentTeamFormingAddJoinLeaveTeam(Student student) throws Exception {
		bi.studentLogin(student.email, student.password);
		 
		bi.clickTeamFormingSessionViewTeams(scn.course.courseId);
		bi.justWait();
		
		bi.verifyViewTeamsPage(scn.students);
		
		if(!student.teamName.equals("")){
			if(bi.isElementPresent(By.id("buttonAdd0"))){
				bi.wdClick(By.id("buttonAdd0"));
				assertEquals(bi.MESSAGE_STUDENT_ADDTOTEAM, bi.getElementText(bi.statusMessage));
				bi.wdClick(By.id("buttonJoin1"));
				assertEquals(bi.MESSAGE_STUDENT_JOINEDTEAM, bi.getElementText(bi.statusMessage));
				bi.wdClick(By.id("buttonLeave0"));
				assertEquals(bi.MESSAGE_STUDENT_LEFTTEAM, bi.getElementText(bi.statusMessage));
				bi.wdClick(By.id("buttonAdd1"));
				assertEquals(bi.MESSAGE_STUDENT_NEWTEAMCREATED, bi.getElementText(bi.statusMessage));
				assertTrue(bi.isTextPresent("Team "+student.google_id));
			}
		}
		else {
			if(bi.isElementPresent(By.id("buttonJoin0"))){
				bi.wdClick(By.id("buttonJoin0"));
				assertEquals(bi.MESSAGE_STUDENT_JOINEDTEAM, bi.getElementText(bi.statusMessage));
				bi.wdClick(By.id("buttonLeave0"));
				assertEquals(bi.MESSAGE_STUDENT_LEFTTEAM, bi.getElementText(bi.statusMessage));
			}
		}
		
		bi.logout();
	}
	
	public void testCoordinatorCheckStudentTeamsAndLog(ArrayList<Student> students) throws Exception {		
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
		bi.gotoTeamForming();		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		verifyAllStudents(students);
		verifyTeams();		
		
		bi.gotoTeamForming();
		bi.clickTeamFormingSessionViewLog(scn.course.courseId);	
		bi.justWait();
		
		String actualLog;
		for(int n=1;n<=6;n++){
			actualLog = bi.getElementText
				(By.xpath(String.format("//div[@id='coordinatorEvaluationManagement']//form//table[@class='headerform']//tbody//tr[%d]",n)));
			if(actualLog.contains("Frank"))
				checkLog(actualLog, students.get(5));
			else
				checkLog(actualLog, students.get(0));
		}
		
		bi.logout();
	}
	
	private void verifyAllStudents(ArrayList<Student> students) {
		for(int i=0; i<students.size(); i++)
			assertTrue(bi.isTextPresent(students.get(i).name));
	}
	
	private void verifyTeams() {
		assertTrue(bi.isTextPresent("TEAMS FORMED"));
		assertTrue(bi.isTextPresent("Team 1"));
		assertTrue(bi.isTextPresent("Team 2"));
		assertTrue(bi.isTextPresent("Team alice.tmms"));
		assertEquals("Alice", bi.getElementText(bi.getStudentNameFromManageTeamFormingSession(2, 1)));
		assertEquals("Frank", bi.getElementText(bi.getStudentNameFromManageTeamFormingSession(3, 1)));
	}
	
	private void checkLog(String log, Student student) {
		String studentJoinTeam1 = "Frank (frank.tmms@gmail.com) : Joined team: Team 1";
		String studentLeftTeam1 = "Frank (frank.tmms@gmail.com) : Left team: Team 1";
		String studentJoinTeam2 = "Alice (alice.tmms@gmail.com) : Joined team: Team 2";
		String studentLeftTeam2 = "Alice (alice.tmms@gmail.com) : Left team: Team 2";
		String studentAddedToTeam = "Alice (alice.tmms@gmail.com) : Added Emily (emily.tmms@gmail.com) to his team: Team 1";
		String studentCreatedNewTeam = "Alice (alice.tmms@gmail.com) : Created a new team: Team alice.tmms with Frank (frank.tmms@gmail.com)";
		
		if(student.name.equals("Alice"))
			assertTrue(log.contains(studentAddedToTeam)||log.contains(studentCreatedNewTeam)
					||log.contains(studentJoinTeam2)||log.contains(studentLeftTeam2));
		else 
			assertTrue(log.contains(studentLeftTeam1)||log.contains(studentJoinTeam1)||log.contains(studentCreatedNewTeam));		
	}
}
