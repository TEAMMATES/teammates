package teammates.testing;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.Evaluation;

/**
 * Test System check case sensitivity Test cover: 
 * 1. create course: CourseID - insensitive; Course Name - sensitive 
 * 2. enrol students: Team Name - insensitive; Student Name - insensitive; Email - insensitive 
 * 3. create evaluation: Evaluation Name - insensitive
 * 
 * @author Xialin
 * @date Nov 5 2011
 * 
 */

public class TestSystemCaseSensitivity extends BaseTest {

	private static final String COURSE_ID_LOWER = "cs2103";
	private static final String COURSE_ID_UPPER = "CS2103";
	private static final String COURSE_NAME_LOWER = "software engineering";
	private static final String COURSE_NAME_UPPER = "SOFTWARE ENGINEERING";
	private static final String STUDENT_TEAM_LOWER = "team 1";
	private static final String STUDENT_TEAM_UPPER = "TEAM 1";
	private static final String STUDENT_NAME_LOWER = "alice";
	private static final String STUDENT_NAME_UPPER = "ALICE";
	private static final String STUDENT_EMAIL_LOWER = "alice.tmms@gmail.com";
	private static final String STUDENT_EMAIL_UPPER = "ALICE.TMMS@GMAIL.COM";
	private static final String EVALUATION_NAME_LOWER = "evaluation 1";
	private static final String EVALUATION_NAME_UPPER = "EVALUATION 1";

	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();
		TMAPI.cleanup();

		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	@Test
	public void testCaseSensitivityAtCreateCourse() throws Exception {
		cout("testSystemCaseSensitivity: courseID - insensitive");
		//-------------------------lower or upper courseID + different names
		addCourse(COURSE_ID_LOWER, "testing 1st course ID");
		clickCourseTab();
		verifyAddedCourse(COURSE_ID_LOWER, "testing 1st course ID");
		
		addCourse(COURSE_ID_UPPER, "testing 2nd course ID");
		
		//TODO: VERIFY
		assertEquals(MESSAGE_COURSE_EXISTS, getElementText(statusMessage));
		assertTrue(isCoursePresent(COURSE_ID_LOWER, "testing 1st course ID"));
		assertFalse(isCoursePresent(COURSE_ID_UPPER, "testing 2nd course ID"));
		//CLEANUP
		deleteAllCourses();
		
		//-------------------------lower or upper courseID + same name
		addCourse(COURSE_ID_LOWER, COURSE_NAME_LOWER);
		clickCourseTab();
		verifyAddedCourse(COURSE_ID_LOWER, COURSE_NAME_LOWER);
		
		addCourse(COURSE_ID_UPPER, COURSE_NAME_LOWER);
		
		//TODO: VERIFY
		assertEquals(MESSAGE_COURSE_EXISTS, getElementText(statusMessage));
		assertTrue(isCoursePresent(COURSE_ID_LOWER, COURSE_NAME_LOWER));
		assertFalse(isCoursePresent(COURSE_ID_UPPER, COURSE_NAME_LOWER));
		//CLEANUP
		deleteAllCourses();
		
		

		cout("testSystemCaseSensitivity: course name - sensitive");
		//-------------------------different IDs + upper or lower course name
		clickCourseTab();
		addCourse("testing01", COURSE_NAME_LOWER);
		clickCourseTab();
		verifyAddedCourse("testing01", COURSE_NAME_LOWER);
		addCourse("testing02", COURSE_NAME_UPPER);
		//TODO: VERIFY
		assertEquals(MESSAGE_COURSE_ADDED, getElementText(statusMessage));
		assertTrue(isCoursePresent("testing01", COURSE_NAME_LOWER));
		assertFalse(isCoursePresent("testing02", COURSE_NAME_LOWER));
	}

	@Test
	public void testCaseSensitivityAtEnrolStudents() throws Exception {
		String students = "";
		
		cout("testSystemCaseSensitivity: team name - insensitive");
		students = STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_LOWER + "|" + '\n' +
				   STUDENT_TEAM_UPPER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_LOWER + "|";
		clickCourseTab();
		clickCourseEnrol(0);
		wdFillString(enrolInfo, students);
		waitAndClick(enrolButton);
		waitAndClick(enrolBackButton);
		//verify teams: team 1 == TEAM 1
		assertEquals("1", getCourseTeams(0));

		//delete students:
		clickCourseView(0);
		clickAndConfirm(deleteStudentsButton);
		waitForElementText(statusMessage, MESSAGE_COURSE_DELETEDALLSTUDENTS);
		waitAndClick(By.className("t_course_enrol"));//inside message link
		
		
		
		cout("testSystemCaseSensitivity: student name - insensitive");
		//-------------------------same student
		students = STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_LOWER + "|" + '\n' +
				   STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_UPPER + "|" + STUDENT_EMAIL_LOWER + "|";
		wdFillString(enrolInfo, students);
		waitAndClick(enrolButton);
		waitAndClick(enrolBackButton);
		//verify students: alice == Alice?
		assertEquals("1", getCourseTotalStudents(0));
		
		//delete students:
		clickCourseView(0);
		clickAndConfirm(deleteStudentsButton);
		waitForElementText(statusMessage, MESSAGE_COURSE_DELETEDALLSTUDENTS);
		waitAndClick(By.className("t_course_enrol"));//inside message link

		//-------------------------what if two students have the same name?
		students = STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|benny.tmms@gmail.com|" + '\n' +
				   STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_UPPER + "|danny.tmms@gmail.com|";
		wdFillString(enrolInfo, students);
		waitAndClick(enrolButton);
		waitAndClick(enrolBackButton);
		//TODO: verify students: alice (benny.tmms@gmail.com) != Alice (danny.tmms@gmail.com)
		assertEquals("2", getCourseTotalStudents(0));
		
		//delete students:
		clickCourseView(0);
		clickAndConfirm(deleteStudentsButton);
		waitForElementText(statusMessage, MESSAGE_COURSE_DELETEDALLSTUDENTS);
		waitAndClick(By.className("t_course_enrol"));//inside message link
		
		
		
		cout("testSystemCaseSensitivity: student email - insensitive");
		students = STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_LOWER + "|" + '\n' +
				   STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_UPPER + "|";
		wdFillString(enrolInfo, students);
		waitAndClick(enrolButton);
		waitAndClick(enrolBackButton);
		//TODO: verify students: alice (alice.tmms@gmail.com) == alice (ALICE.TMMS@GMAIL.COM)
		assertEquals("1", getCourseTotalStudents(0));
		
	}

	@Test
	public void testCaseSensitivityAtCreateEvaluation() throws Exception {
		//setup:
		deleteAllCourses();
		addCourse(COURSE_ID_LOWER, COURSE_NAME_LOWER);
		String students = STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_LOWER + "|";
		clickCourseTab();
		clickCourseEnrol(0);
		wdFillString(enrolInfo, students);
		waitAndClick(enrolButton);
		waitAndClick(enrolBackButton);
		
		
		cout("testSystemCaseSensitivity: evaluation name - insensitive");
		clickEvaluationTab();
		//evaluation 1
		Evaluation eval = Evaluation.createEvaluation(COURSE_ID_LOWER, EVALUATION_NAME_LOWER, "true", "Please please fill in the forth evaluation", 10);
		addEvaluation(eval);
		//EVALUATION 1
		eval = Evaluation.createEvaluation(COURSE_ID_LOWER, EVALUATION_NAME_UPPER, "true", "Please please fill in the forth evaluation", 10);
		addEvaluation(eval);
		//verify evaluations: evaluation 1 == EVALUATION 1
		assertEquals(1, countTotalEvaluations());
	}
	
	@Test
	public void testSameEvaluationNameInDifferentCourses() {
		//add one more course
		clickCourseTab();
		addCourse("cs1101", "testing course");
		String students = STUDENT_TEAM_LOWER + "|" + STUDENT_NAME_LOWER + "|" + STUDENT_EMAIL_LOWER + "|";
		clickCourseTab();
		clickCourseEnrol(0);
		wdFillString(enrolInfo, students);
		waitAndClick(enrolButton);
		waitAndClick(enrolBackButton);
		
		clickEvaluationTab();
		//evaluation 1
		Evaluation eval = Evaluation.createEvaluation("cs1101", EVALUATION_NAME_LOWER, "true", "Please please fill in the forth evaluation", 10);
		addEvaluation(eval);
		//verify evaluations: cs1101 evaluation 1 != cs2103 evaluation 1
		assertEquals(2, countTotalEvaluations());
	}
}