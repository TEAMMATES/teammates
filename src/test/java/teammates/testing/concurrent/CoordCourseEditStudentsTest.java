package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordCourseEditStudentsTest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	
	static int FIRST_STUDENT = 0;
	static final String STUDENT_NAME = "Jack (New)";
	static final String STUDENT_TEAM = "Jack's New Team";
	static final String STUDENT_EMAIL = "student@gmail.com";
	static final String STUDENT_GOOGLE = "student";
	static final String STUDENT_COMMENT = "Comments: Do you know His Master's Voice?";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== TestCoordEditStudentsConcurrent");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		TMAPI.cleanupCourse(scn.course.courseId);
		
		bi.logout();
		
		BrowserInstancePool.release(bi);
		System.out.println("TestCoordEditStudentsConcurrent ==========//");
	}
	
	
	/**
	 *	testCoordEditStudentNameSuccessful
	 *	testCoordEditStudentEmailSuccessful
	 *	testCoordEditStudentCommentSuccessful
	 * */
	@Test
	public void testCoordEditIndividualStudentSuccessful() {
		bi.clickCourseTab();
		bi.clickCoordCourseView(scn.course.courseId);
		bi.clickCoordCourseDetailStudentEdit(FIRST_STUDENT);
		
		bi.fillString(bi.studentDetailName, STUDENT_NAME);
		bi.fillString(bi.studentDetailTeam, STUDENT_TEAM);
		bi.fillString(bi.studentDetailEmail, STUDENT_EMAIL);
		//TODO: google ID cannot be changed after student registered
		bi.fillString(bi.studentDetailComment, STUDENT_COMMENT);
		
		bi.clickWithWait(bi.coordCourseDetailsStudentEditSaveButton);
		
		//wait for page loading
		bi.waitForElementPresent(bi.coordCourseDetailCourseID);
		
		bi.clickCoordCourseDetailStudentView(STUDENT_NAME);
		
		//checking value updated
		assertEquals(STUDENT_NAME, bi.getElementText(bi.studentDetailName));
		assertEquals(STUDENT_TEAM, bi.getElementText(bi.studentDetailTeam));
		assertEquals(STUDENT_EMAIL, bi.getElementText(bi.studentDetailEmail));
		assertEquals(STUDENT_COMMENT, bi.getElementText(bi.studentDetailComment));
		
	}
	
	@Test
	public void testCoordEditIndividualStudentsWithInvalidInputFailed() {
		bi.clickCourseTab();
		bi.clickCoordCourseView(scn.course.courseId);
		bi.clickCoordCourseDetailStudentEdit(FIRST_STUDENT);
		
		//TODO: define the rule for input
		
		testCoordEditStudentWithInvalidStudentName();
		testCoordEditStudentWithInvalidTeamName();
		testCoordEditStudentWithInvalidEmail();
		testCoordEditStudentWithInvalidGoogleID();
	}
	
	public void testCoordEditStudentWithInvalidStudentName() {
		//TODO: test student name exceed the limit
		bi.fillString(bi.studentDetailName, "ASAGSAJAJ JSHA JSH AGSHA GSAJ JAHS ASJA GJASG AJHSAJHSA");
	}
	
	public void testCoordEditStudentWithInvalidTeamName() {
		
	}
	
	public void testCoordEditStudentWithInvalidEmail() {
		//TODO: cannot contain certain characters e.g. ' ! 
		bi.fillString(bi.studentDetailEmail, "");
	}
	
	public void testCoordEditStudentWithInvalidGoogleID() {
		
	}
	
	
	//TODO: testMassEditStudents
	@Test
	public void testMassEditStudents() throws Exception {
		String students = "Team 1|User 6|\n" + "Team 1|User 0|\n" + "Team 1|User 1|";

		// To Enroll page
		bi.clickCourseTab();
		bi.clickCoordCourseEnroll(scn.course.courseId);
		bi.verifyCoordCourseEnrollPage();
		bi.fillString(bi.coordEnrollInfo, students);
		bi.clickWithWait(bi.coordEnrollButton);

		// Make sure the error message is there
		assertTrue(bi.isElementPresent(bi.courseErrorMessage));

		bi.clickWithWait(bi.coordEnrollBackButton);
	}
}
