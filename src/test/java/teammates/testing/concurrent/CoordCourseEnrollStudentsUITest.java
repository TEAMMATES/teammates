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

public class CoordCourseEnrollStudentsUITest extends TestCase {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== CoordCourseEnrollStudentsTest");
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
		System.out.println("CoordCourseEnrollStudentsTest ==========//");
	}

	
	@Test
	public void testCoordEnrollStudents() {
		testCoordEnrollStudentsSuccessful();
		testCoordEnrollExistingStudentsSuccessful();
	}
	
	/**
	 * Test: enroll new students
	 * Condition: a course has been created
	 * Action: enter student list and click enroll button
	 * Expectation: studnets added successfully
	 * */
	public void testCoordEnrollStudentsSuccessful() {
		bi.goToCourses();

		int half = scn.students.size() / 2;
		List<Student> ls = scn.students.subList(0, half);
		bi.enrollStudents(ls, scn.course.courseId);

		// Check for number of successful students enrolled
		bi.verifyEnrollment(half, 0);
		bi.clickWithWait(bi.coordEnrollBackButton);

		// Calculate the number of TEAMs
		Set<String> set = new HashSet<String>();
		for (Student s : ls) {
			set.add(s.teamName);
		}

		// Verifies the number of teams
		assertEquals(String.valueOf(set.size()), bi.getCourseNumberOfTeams(scn.course.courseId));
	}
	

	/**
	 * Test: enroll duplicate students
	 * Condition: a course has been created, students have been added into the course
	 * Action: enter same student list and click enroll button
	 * Expectation: studnets edited successfully, nothing change, no error message
	 * 
	 * Dependency: testCoordEnrollStudentsSuccessful()
	 * */
	public void testCoordEnrollExistingStudentsSuccessful() {
		int studentAdded = 0;
		int studentEdited = 0;
		bi.goToCourses();
		
		int half = scn.students.size() / 2;
		List<Student> ls = scn.students.subList(0, half);
		bi.enrollStudents(ls, scn.course.courseId);
		
		// Check for number of successful students enrolled
		bi.verifyEnrollment(studentAdded, studentEdited);
		bi.clickWithWait(bi.coordEnrollBackButton);

		// Calculate the number of TEAMs
		Set<String> set = new HashSet<String>();
		for (Student s : ls) {
			set.add(s.teamName);
		}

		assertEquals(String.valueOf(set.size()), bi.getCourseNumberOfTeams(scn.course.courseId));
	}
	
	/**
	 *	Test: enroll students with Invalid input format
	 *	Condition: a course has been added
	 *	Action: enter invalid data format
	 *	Expectation: students not added, show error message
	 *	
	 *	testCoordEnrollStudentWithTabSuccessful
	 */
	@Test
	public void testCoordEnrollStudentsWithInvalidInputFailed() {
		bi.goToCourses();
		bi.clickCoordCourseEnroll(scn.course.courseId);
		bi.verifyCoordCourseEnrollPage();
		
		testCoordEnrollStudentsWithInvalidTeamFailed();
		testCoordEnrollStudentsWithInvalidNameFailed();
		testCoordEnrollStudentsWithInvalidEmailFailed();
		testCoordEnrollStudentWithInvalidCommentFailed();
		
		testCoordEnrollStudentsWithoutCommentSuccessful();
	}
	
	//TODO:
	private void testCoordEnrollStudentsWithInvalidTeamFailed() {
		//without team
		
		//invalid team
		
	}
	
	//TODO:
	private void testCoordEnrollStudentsWithInvalidNameFailed() {
		//without name
		
		//invalid name
	}
	
	private void testCoordEnrollStudentsWithInvalidEmailFailed() {
		String studentList = "Team 1|User 6|\n" + "Team 1|User 0|\n|" + "Team 1|User 1| |";
		
		bi.fillString(bi.coordEnrollInfo, studentList);
		bi.clickWithWait(bi.coordEnrollButton);
		
		assertEquals(bi.ERROR_MESSAGE_ENROLL_INVALID_EMAIL, bi.getElementText(bi.courseErrorMessage));
	}	
	
	//TODO:
	private void testCoordEnrollStudentWithInvalidCommentFailed() {
		
	}
	
	//TODO:
	private void testCoordEnrollStudentsWithoutCommentSuccessful() {
		
	}
	
	
	
	//TODO: testCoordEnrollStudentWithRandomTeamSuccessful
	//TODO: testCoordEnrollStudentWithRandomStudentNameSuccessful
	
	
	/**
	 * testCoordEnrollStudentWithDuplicateEmailSuccessful
	 * testCoordEnrollStudentWithDuplicateStudentNameSuccessful
	 * */
//	@Test
//	public void testCoordEnrollStudentsWithDuplicateSuccessful() {
//		
//	}

}
