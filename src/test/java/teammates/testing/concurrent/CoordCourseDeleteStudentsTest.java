package teammates.testing.concurrent;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.BaseTest2;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordCourseDeleteStudentsTest extends BaseTest2 {
	static BrowserInstance bi;
	static Scenario scn = setupScenarioInstance("scenario");
	public static int FIRST_STUDENT = 0;
	public static int TABLE_HEADER_SIZE = 1;

	@BeforeClass
	public static void classSetup() throws Exception {
		System.out.println("========== TestCoordDeleteStudents");
		
		bi = BrowserInstancePool.request();
		
		TMAPI.cleanupCourse(scn.course.courseId);
		TMAPI.createCourse(scn.course);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		if(bi.isElementPresent(bi.logoutTab)) {
			bi.logout();
		}
		TMAPI.cleanupCourse(scn.course.courseId);
		
		BrowserInstancePool.release(bi);
		System.out.println("TestCoordDeleteStudents ==========//");
	}
	
	/**
	 * Test: delete unregistered students
	 * 
	 * Dependency: testing delete individual student first, then delete all students
	 * */
	@Test
	public void testCoordDeleteUnregisteredStudentsSuccessful() {
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
		
		//condition: unregistered
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		
		testCoordDeleteIndividualStudentSuccessful();
		testCoordDeleteAllStudentsSuccessful();
		
		bi.logout();
	}
	
	//delete registered students
	@Test
	public void testCoordDeleteRegisteredStudentsSuccessful() {
		bi.coordinatorLogin(scn.coordinator.username, scn.coordinator.password);
		//condition: students joined course
		TMAPI.enrollStudents(scn.course.courseId, scn.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		
		testCoordDeleteIndividualStudentSuccessful();
		testCoordDeleteAllStudentsSuccessful();
		
		bi.logout();
	}
	
	public void testCoordDeleteIndividualStudentSuccessful() {
		bi.gotoCourses();
		bi.clickCourseView(scn.course.courseId);
		bi.justWait();
		bi.clickAndConfirmCourseDetailDelete(FIRST_STUDENT);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_DELETED_STUDENT);
		
		// check for total number of teams
		assertEquals("2", bi.getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 3, 2))));

		// check for total number of students
		assertEquals("3", bi.getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 4, 2))));

		// check for number of table entries in student table
		assertEquals(3, bi.getDriver().findElements(By.cssSelector("#coordinatorStudentTable tr")).size() - TABLE_HEADER_SIZE);
	}
	
	public void testCoordDeleteAllStudentsSuccessful() {
		bi.gotoCourses();
		bi.clickCourseView(scn.course.courseId);
		bi.justWait();
		// bi.clickAndConfirm(By.id("button_delete"));
		bi.clickAndConfirm(bi.deleteStudentsButton);
		bi.waitForElementText(bi.statusMessage, bi.MESSAGE_COURSE_DELETED_ALLSTUDENTS);

		// check for total number of teams
		assertEquals("0", bi.getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 3, 2))));

		// check for total number of students
		assertEquals("0", bi.getElementText(By.xpath(String.format("//div[@id='coordinatorCourseInformation']//table[@class='headerform']//tbody//tr[%d]//td[%d]", 4, 2))));

		// check for number of table entries in student table
		assertEquals(1, bi.getDriver().findElements(By.cssSelector("#coordinatorStudentTable tr")).size() - TABLE_HEADER_SIZE);
	}
}
