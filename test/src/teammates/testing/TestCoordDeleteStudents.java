package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;

/**
 * Coordinator enrolling students
 * 
 * @author Kalpit
 * 
 */
public class TestCoordDeleteStudents extends BaseTest {

	public static String DISPLAY_COURSE_DELETEDSTUDENT = "The student has been removed from the course.";
	public static String DISPLAY_COURSE_DELETEDALLSTUDENTS = "All students have been removed from the course. Click here to enrol students.";
	public static int tableHeaderSize = 1;
	@BeforeClass
	public static void classSetup() throws Exception {
		setupScenario();
		TMAPI.cleanup();
		TMAPI.createCourse(sc.course);
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		
		setupSelenium();
		coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		wrapUp();
	}

	/**
	 * Delete a single student who has not joined the course
	 */
	@Test
	public void testDeleteUnregisteredStudent() throws Exception {
		cout("Test: Deleting an Unregistered Student.");
		
		// To View Course page
		waitAndClick(By.className("t_courses"));
		clickCourseView(0);
		
		clickAndConfirm(By.className("t_student_delete"));
		
		waitForElementText(statusMessage, DISPLAY_COURSE_DELETEDSTUDENT);
		
		//check for total number of teams
		assertEquals("2", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 3, 2))));
		
		//check for total number of students
		assertEquals("3", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 4, 2))));
		
		//check for number of table entries in student table
		assertEquals(3,
				driver.findElements(By.cssSelector("#coordinatorStudentTable tr"))
				.size() - tableHeaderSize);
	}
	
	/**
	 * Delete all students who have not joined the course
	 */
	@Test
	public void testDeleteAllUnregisteredStudents() throws Exception {
		cout("Test: Deleting all Unregistered Students.");
		
		clickAndConfirm(By.id("button_delete"));
		
		assertEquals(getElementText(By.id("statusMessage")), DISPLAY_COURSE_DELETEDALLSTUDENTS);
		
		//check for total number of teams
		assertEquals("0", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 3, 2))));
		
		//check for total number of students
		assertEquals("0", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 4, 2))));
		
		//check for number of table entries in student table
		assertEquals(1,
				driver.findElements(By.cssSelector("#coordinatorStudentTable tr"))
				.size() - tableHeaderSize);
	}
	
	/**
	 * Delete a single student who has joined the course
	 */
	@Test
	public void testDeleteRegisteredStudent() throws Exception {
		waitAndClick(By.className("t_courses"));
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		cout("Test: Deleting a Registered Student.");
		
		// To View Course page
		clickCourseView(0);
		
		clickAndConfirm(By.className("t_student_delete"));
		
		waitForElementText(statusMessage, DISPLAY_COURSE_DELETEDSTUDENT);
		
		//check for total number of teams
		assertEquals("2", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 3, 2))));
		
		//check for total number of students
		assertEquals("3", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 4, 2))));
		
		//check for number of table entries in student table
		assertEquals(3,
				driver.findElements(By.cssSelector("#coordinatorStudentTable tr"))
				.size() - tableHeaderSize);
	}
	
	/**
	 * Delete all students who have joined the course
	 */
	@Test
	public void testDeleteAllRegisteredStudents() throws Exception {
		waitAndClick(By.className("t_courses"));
		TMAPI.enrollStudents(sc.course.courseId, sc.students);
		TMAPI.studentsJoinCourse(sc.students, sc.course.courseId);
		cout("Test: Deleting all Registered Students.");
		
		// To View Course page
		clickCourseView(0);
		
		clickAndConfirm(By.id("button_delete"));
		
		assertEquals(getElementText(By.id("statusMessage")), DISPLAY_COURSE_DELETEDALLSTUDENTS);
		
		//check for total number of teams
		assertEquals("0", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 3, 2))));
		
		//check for total number of students
		assertEquals("0", getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 4, 2))));
		
		//check for number of table entries in student table
		assertEquals(1,
				driver.findElements(By.cssSelector("#coordinatorStudentTable tr"))
				.size() - tableHeaderSize);
	}
}