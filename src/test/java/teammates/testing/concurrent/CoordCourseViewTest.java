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

public class CoordCourseViewTest extends TestCase {

	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");

	static Student FIRST_STUDENT = scn.students.get(0);

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseViewTest");
		bi = BrowserInstancePool.getBrowserInstance();

		TMAPI.cleanupCourse(scn.course.courseId);

		TMAPI.createCourse(scn.course);
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		
		if(Config.inst().isLocalHost()){
			System.out.println("Omitting email testing because testing on local host");
		}else{
			System.out.println("Clean inbox for " + FIRST_STUDENT.name);
			SharedLib.markAllEmailsSeen(FIRST_STUDENT.email, Config.inst().TEAMMATES_APP_PASSWD);
			SharedLib.markAllEmailsSeen(Config.inst().INDIVIDUAL_ACCOUNT, Config.inst().TEAMMATES_APP_PASSWD);
		}
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);

	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab)) {
			bi.logout();
		}
		TMAPI.cleanupCourse(scn.course.courseId);
		BrowserInstancePool.release(bi);
		System.out.println("CoordCourseViewTest ==========//");
	}

	/**
	 * Test: coordinator send invitation to individual student
	 * 
	 * Remind all students to join course is defined in StudentCourseJoinTest.java
	 * */
	@Test
	public void testCoordRemindIndividualStudentSuccessful() {
		if(Config.inst().isLocalHost()){
			System.out.println("Omitting email testing in testCoordRemindIndividualStudentSuccessful because testing on local host");
			return;
		}
		
		System.out.println("testCoordRemindIndividualStudentSuccessful");
		String newStudent = Config.inst().INDIVIDUAL_NAME;
		String newEmail = Config.inst().INDIVIDUAL_ACCOUNT;


		bi.clickCourseTab();
		bi.clickCoordCourseEnroll(scn.course.courseId);
		bi.verifyCoordCourseEnrollPage();

		bi.fillString(bi.coordEnrollInfo, String.format("%s|%s|%s|", FIRST_STUDENT.teamName, newStudent, newEmail));
		bi.clickWithWait(bi.coordEnrollButton);

		bi.clickCourseTab();
		bi.clickCoordCourseView(scn.course.courseId);
		bi.clickCoordCourseDetailInvite(newStudent);

		//Collect key for the new student
		bi.clickCoordCourseView(newStudent);
		bi.waitForElementPresent(bi.studentDetailKey);
		String key = bi.getElementText(bi.studentDetailKey);
		bi.clickWithWait(bi.courseDetailBackButton);
		
		System.out.println("Key for new student: " + key);

		// Assert that student gets a notification email
		bi.waitAWhile(1500);
		assertEquals(key, SharedLib.getRegistrationKeyFromGmail(newEmail, Config.inst().TEAMMATES_APP_PASSWD, scn.course.courseId));

		// Assert that rest of the students don't get spammed
		bi.waitAWhile(1500);
		assertEquals("", SharedLib.getRegistrationKeyFromGmail(FIRST_STUDENT.email, Config.inst().TEAMMATES_APP_PASSWD, scn.course.courseId));
			
		bi.logout();
	}

	/*
	 * TODO:
	 * Student list under a course
	 * testCoordViewCourseSortByStudentNameSuccessful
	 * testCoordViewCourseSortByTeamSuccessful
	 * testCoordViewCourseSortByStatusSuccessful
	 */
	@Test
	public void testCoordViewCourseSortStudents() {

	}
}
