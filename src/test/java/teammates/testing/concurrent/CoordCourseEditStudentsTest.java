package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.BaseTest2;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordCourseEditStudentsTest extends BaseTest2 {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	
	static int FIRST_STUDENT = 0;
	static final String STUDENT_NAME = "Jack (New)";
	static final String STUDENT_TEAM = "New Team";
	static final String STUDENT_EMAIL = "student@gmail.com";
	static final String STUDENT_GOOGLE = "student";
	static final String STUDENT_COMMENT = "This is a new comment for edit testing.";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== TestCoordEditStudentsConcurrent");
		bi = BrowserInstancePool.request();

		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
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
		bi.clickCourseView(scn.course.courseId);
		bi.clickCourseDetailEdit(FIRST_STUDENT);
		
		bi.wdFillString(bi.studentEditName, STUDENT_NAME);
		bi.wdFillString(bi.studentEditTeam, STUDENT_TEAM);
		bi.wdFillString(bi.studentEditEmail, STUDENT_EMAIL);
		//TODO: google ID cannot be changed after student registered
		bi.wdFillString(bi.studentEditComments, STUDENT_COMMENT);
		
		bi.waitAndClick(bi.studentEditSaveButton);
		
		//wait for page loading
		bi.waitForElementPresent(bi.courseDetailCourseID);
		
		bi.clickCourseDetailView(STUDENT_NAME);
		
		//checking value updated
		assertEquals(STUDENT_NAME, bi.getElementText(bi.studentDetailName));
		assertEquals(STUDENT_TEAM, bi.getElementText(bi.studentDetailTeam));
		assertEquals(STUDENT_EMAIL, bi.getElementText(bi.studentDetailEmail));
		assertEquals(STUDENT_COMMENT, bi.getElementText(bi.studentDetailComment));
		
	}
	
	@Test
	public void testCoordEditIndividualStudentsWithInvalidInputFailed() {
		bi.clickCourseTab();
		bi.clickCourseView(scn.course.courseId);
		bi.clickCourseDetailEdit(FIRST_STUDENT);
		
		//TODO: define the rule for input
		
		//TODO: test student name exceed the limit
		bi.wdFillString(bi.studentEditName, "ASAGSAJAJ JSHA JSH AGSHA GSAJ JAHS ASJA GJASG AJHSAJHSA");
		
		//TODO: cannot contain certain characters e.g. ' !
		bi.wdFillString(bi.studentEditTeam, "ALICE'S NEW TEAM");
		
		//TODO: cannot contain certain characters e.g. ' ! 
		bi.wdFillString(bi.studentEditEmail, "");

		//TODO: cannot contain \'
		bi.wdFillString(bi.studentEditComments, STUDENT_COMMENT);
	}
	
	
	
	//TODO: testMassEditStudents
	@Test
	public void testMassEditStudents() throws Exception {
		String students = "Team 1|User 6|\n" + "Team 1|User 0|\n" + "Team 1|User 1|";

		// To Enroll page
		bi.clickCourseTab();
		bi.clickCourseEnrol(scn.course.courseId);
		bi.verifyEnrollPage();
		bi.wdFillString(bi.enrolInfo, students);
		bi.wdClick(bi.enrolButton);

		// Make sure the error message is there
		assertTrue(bi.isElementPresent(bi.courseErrorMessage));

		bi.wdClick(bi.enrolBackButton);
	}
}
