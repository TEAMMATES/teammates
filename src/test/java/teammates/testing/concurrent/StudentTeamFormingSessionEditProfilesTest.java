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
public class StudentTeamFormingSessionEditProfilesTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("teamForming");

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== StudentTeamFormingSessionEditProfilesTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		ArrayList<String> teams = new ArrayList<String>();
		teams.add("Team 1");
		teams.add("Team 2");
		TMAPI.createProfileOfExistingTeams(scn.course.courseId, scn.course.courseName, teams);
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
		System.out.println("StudentTeamFormingSessionEditProfilesTest ==========//");
	}
	
	@Test
	public void testStudentTeamFormingSessionEditProfiles() throws Exception {
		testStudentTeamFormingSaveProfile(scn.students.get(0));
		testStudentTeamFormingSaveProfile(scn.students.get(5));
		testCoordinatorCheckStudentTeamProfiles();
	}
	
	public void testStudentTeamFormingSaveProfile(Student student) throws Exception {
		bi.studentLogin(student.email, student.password);
		bi.clickCoursesTab();
		bi.clickTeamFormingSessionViewTeams(scn.course.courseId);
		
		verifyCourseDetailsForStudent(student);
		
		String studentProfile = "This is "+ student.name +"'s profile. It is purposely made long" +
		" to test if more than 500 characters are accepted in the student profile section."+
		" I have done several projects before. Some of the major ones include the ones in"+
		" software engineering, networking, Mozilla firefox and also google summer of code."+
		" I enjoy coding and testing the product. I would be happy to be in team with anyone"+
		" who wants to dedicate good amount of time to this project as I find this really interesting."+
		" I live out of campus but I will be available in the college most of the times. I am taking only"+
		" 4 modules this semester so I will have ample of time to design a good product.";
		
		bi.wdFillString(bi.inputStudentProfileDetail, studentProfile);
		bi.waitAndClick(bi.saveStudentProfile);
		assertEquals(bi.MESSAGE_STUDENTPROFILE_SAVED, bi.getElementText(bi.statusMessage));
		
		if(!student.teamName.equals("")){
			int i = scn.students.indexOf(student);
			String viewTeamProfile = "viewTeamProfile" + i;
			bi.waitAndClick(By.id(viewTeamProfile));
			bi.verifyTeamDetailPage();
			
			//save team profile unsuccessful: same profile exists
			String newTeamName = "Team 2";
			bi.wdFillString(bi.inputTeamName, newTeamName);
			bi.wdClick(bi.saveTeamProfile);
			bi.waitForTextInElement(bi.statusMessage, bi.ERROR_MESSAGE_TEAMPROFILE_EXISTS);		
			
			//save team profile successful
			bi.waitAndClick(bi.resultBackButton);
			bi.waitAndClick(By.id(viewTeamProfile));
			bi.wdFillString(bi.inputTeamName, "Team 3");
			bi.wdFillString(bi.inputTeamProfile, studentProfile);
			bi.wdClick(bi.saveTeamProfile);
			bi.waitForTextInElement(bi.statusMessage, bi.MESSAGE_TEAMPROFILE_SAVED);
		}
		
		bi.logout();
	}
	
	private void verifyCourseDetailsForStudent(Student student) {
		assertEquals(bi.MESSAGE_LOG_REMINDSTUDENTS, bi.getElementText(bi.statusMessage));
		assertTrue(bi.isElementPresent(By.id("studentprofiledetail")));
		assertTrue(bi.isElementPresent(bi.saveStudentProfile));
		
		assertTrue(bi.isTextPresent(student.name));
		assertTrue(bi.isTextPresent(student.email));
		assertTrue(bi.isTextPresent(student.teamName));
		
		assertTrue(bi.isTextPresent(scn.teamFormingSession.instructions));
		assertTrue(bi.isTextPresent(scn.teamFormingSession.profileTemplate));
	}
	
	public void testCoordinatorCheckStudentTeamProfiles() throws Exception {
		String studentProfile = "This is Alice's profile. It is purposely made long" +
		" to test if more than 500 characters are accepted in the student profile section."+
		" I have done several projects before. Some of the major ones include the ones in"+
		" software engineering, networking, Mozilla firefox and also google summer of code."+
		" I enjoy coding and testing the product. I would be happy to be in team with anyone"+
		" who wants to dedicate good amount of time to this project as I find this really interesting."+
		" I live out of campus but I will be available in the college most of the times. I am taking only"+
		" 4 modules this semester so I will have ample of time to design a good product.";
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		bi.gotoTeamForming();		
		bi.clickTeamFormingSessionEdit(scn.course.courseId);
		bi.verifyManageTeamFormingPage(scn.students);
		
		assertEquals(studentProfile, bi.getElementText(bi.getStudentNameFromManageTeamFormingSession(2, 2)));
		
		bi.wdClick(bi.coordEditTeamProfile0);
		bi.verifyTeamDetailPage();
		assertEquals("Team 3", bi.getElementValue(bi.inputTeamName));
		assertEquals(studentProfile, bi.getElementText(bi.inputTeamProfile));
		
		bi.logout();
	}
}
