package teammates.testing;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.testing.lib.TMAPI;

/**
 * Test all sort of actions to Course page The tests must run in the order it's
 * written
 * 
 * @author Huy
 */
public class TestCoordCourse extends BaseTest {

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

	/**
	 * Successfully create a course
	 */
	@Test
	public void testCreateCourseSuccess() throws Exception {
		cout("Test: Creating course.");

		addCourse(sc.course.courseId, sc.course.courseName);

		// Fix for DataStore delay problem, by clicking on Courses link again
		gotoCourses();

		verifyAddedCourse(sc.course.courseId, sc.course.courseName);
	}
	
	/**
	 * Create the course that previously was created
	 */
	@Test
	public void testCreateCoursePreviouslyNamed() {
		cout("Test: Creating course previously named.");

		addCourse(sc.course.courseId, sc.course.courseName);
		gotoCourses();
		waitForElementPresent(By
				.cssSelector("#coordinatorCourseTable td.t_course_code"));
		verifyAddedCourse(sc.course.courseId, sc.course.courseName);
	}

	/**
	 * Fail to create duplicate course code (previous course created by the same
	 * coordinator)
	 */
	@Test
	public void testCreateDuplicateCourseFail() throws Exception {
		cout("Test: Creating duplicated course.");
		// Add the second course with same ID
		addCourse(sc.course.courseId, sc.course.courseName);
		assertEquals("The course already exists.",
				getElementText(By.xpath("//div[@id='statusMessage']/font[1]")));
	}

	/**
	 * Delete a course
	 */
	@Test
	public void testDeleteCourseSuccess() throws Exception {
		cout("Test: Deleting course.");

		clickAndConfirm(By.className("t_course_delete")); // delete link)

		justWait();
		waitForElementText(By.id("statusMessage"), "The course has been deleted.");

		assertEquals(3,
				driver.findElements(By.cssSelector("#coordinatorCourseTable tr"))
						.size());
	}

	/**
	 * Fail to create course that misses something
	 */
	@Test
	public void testCreateCourseMissingInfoFail() throws Exception {
		cout("Test: Creating course with missing info.");

		// Trying adding course without ID
		addCourse("", sc.course.courseName);
		assertEquals(true,
				isElementPresent(By.xpath("//div[@id='statusMessage']/font[1]")));

		// Adding course without name
		addCourse(sc.course.courseId, "");
		assertEquals(true,
				isElementPresent(By.xpath("//div[@id='statusMessage']/font[1]")));
	}

	

}