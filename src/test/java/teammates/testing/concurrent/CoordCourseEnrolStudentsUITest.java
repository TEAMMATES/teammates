package teammates.testing.concurrent;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

public class CoordCourseEnrolStudentsUITest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseEnrolStudentsTest");
		bi = BrowserInstancePool.request();
		
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
		
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("CoordCourseEnrolStudentsTest ==========//");
	}

	
	@Test
	public void testCoordEnrolStudents() {
		testCoordEnrolStudentsSuccessful();
		testCoordEnrolExistingStudentsSuccessful();
	}
	
	/**
	 * Test: enrol new students
	 * Condition: a course has been created
	 * Action: enter student list and click enrol button
	 * Expectation: studnets added successfully
	 * */
	public void testCoordEnrolStudentsSuccessful() {
		bi.gotoCourses();

		int half = scn.students.size() / 2;
		List<Student> ls = scn.students.subList(0, half);
		bi.enrollStudents(ls, scn.course.courseId);

		// Check for number of successful students enrolled
		bi.verifyEnrollment(half, 0);
		bi.waitAndClick(bi.enrolBackButton);

		// Calculate the number of TEAMs
		Set<String> set = new HashSet<String>();
		for (Student s : ls) {
			set.add(s.teamName);
		}

		assertEquals(String.valueOf(set.size()), bi.getCourseTeams(scn.course.courseId));
	}
	

	/**
	 * Test: enrol duplicate students
	 * Condition: a course has been created, students have been added into the course
	 * Action: enter same student list and click enrol button
	 * Expectation: studnets edited successfully, nothing change, no error message
	 * 
	 * Dependency: testCoordEnrolStudentsSuccessful()
	 * */
	public void testCoordEnrolExistingStudentsSuccessful() {
		int studentAdded = 0;
		int studentEdited = 0;
		bi.gotoCourses();
		
		int half = scn.students.size() / 2;
		List<Student> ls = scn.students.subList(0, half);
		bi.enrollStudents(ls, scn.course.courseId);
		
		// Check for number of successful students enrolled
		bi.verifyEnrollment(studentAdded, studentEdited);
		bi.waitAndClick(bi.enrolBackButton);

		// Calculate the number of TEAMs
		Set<String> set = new HashSet<String>();
		for (Student s : ls) {
			set.add(s.teamName);
		}

		assertEquals(String.valueOf(set.size()), bi.getCourseTeams(scn.course.courseId));
	}
	
	/**
	 *	Test: enrol students with Invalid input format
	 *	Condition: a course has been added
	 *	Action: enter invalid data format
	 *	Expectation: students not added, show error message
	 *	
	 *	testCoordEnrolStudentWithTabSuccessful
	 */
	@Test
	public void testCoordEnrolStudentsWithInvalidInputFailed() {
		bi.gotoCourses();
		bi.clickCourseEnrol(scn.course.courseId);
		bi.verifyEnrollPage();
		
		testCoordEnrolStudentsWithInvalidTeamFailed();
		testCoordEnrolStudentsWithInvalidNameFailed();
		testCoordEnrolStudentsWithInvalidEmailFailed();
		testCoordEnrolStudentWithInvalidCommentFailed();
		
		testCoordEnrolStudentsWithoutCommentSuccessful();
	}
	
	//TODO:
	private void testCoordEnrolStudentsWithInvalidTeamFailed() {
		//without team
		
		//invalid team
		
	}
	
	//TODO:
	private void testCoordEnrolStudentsWithInvalidNameFailed() {
		//without name
		
		//invalid name
	}
	
	private void testCoordEnrolStudentsWithInvalidEmailFailed() {
		String studentList = "Team 1|User 6|\n" + "Team 1|User 0|\n|" + "Team 1|User 1| |";
		
		bi.wdFillString(bi.enrolInfo, studentList);
		bi.waitAndClick(bi.enrolButton);
		
		assertEquals(bi.ERROR_MESSAGE_ENROL_INVALID_EMAIL, bi.getElementText(bi.courseErrorMessage));
	}	
	
	//TODO:
	private void testCoordEnrolStudentWithInvalidCommentFailed() {
		
	}
	
	//TODO:
	private void testCoordEnrolStudentsWithoutCommentSuccessful() {
		
	}
	
	
	
	//TODO: testCoordEnrolStudentWithRandomTeamSuccessful
	//TODO: testCoordEnrolStudentWithRandomStudentNameSuccessful
	
	
	/**
	 * testCoordEnrolStudentWithDuplicateEmailSuccessful
	 * testCoordEnrolStudentWithDuplicateStudentNameSuccessful
	 * */
//	@Test
//	public void testCoordEnrolStudentsWithDuplicateSuccessful() {
//		
//	}

}
