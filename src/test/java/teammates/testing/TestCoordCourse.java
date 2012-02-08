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

	@Test
	public void CoordCourse() throws Exception {
		testCreateCourseSuccess();
		testCreateCoursePreviouslyNamed();
		testCreateDuplicateCourseFail();
		testDeleteCourseSuccess();
		testCreateCourseMissingInfoFail();
		
		testCreateCourseAllowedSymbolsIDSuccess();
		testCreateCourseNonAllowedSymbolsIDFail();
	}
	
	/**
	* Successfully create a course with course ID containing permitted symbols
	*/
	public void testCreateCourseAllowedSymbolsIDSuccess() throws Exception {
		cout("TestCoordCourse: TestCreatingCourseWithAllowedSymbolsInID");
	 		
		// Try adding course with ID containing '.'
		addCourse(sc.course.courseId.replace('-', '.'), sc.course.courseName + " (.)");
		gotoCourses();
		verifyAddedCourse(sc.course.courseId.replace('-', '.'), sc.course.courseName + " (.)");
	
		// Try adding course with ID containing '_'
		clickAndConfirmCourseDelete(0);
		addCourse(sc.course.courseId.replace('-', '_'), sc.course.courseName + " (_)");
		gotoCourses();
		verifyAddedCourse(sc.course.courseId.replace('-', '_'), sc.course.courseName + " (_)");
		
		// Try adding course with ID containing '$'
		clickAndConfirmCourseDelete(0);
		addCourse(sc.course.courseId.replace('-', '$'), sc.course.courseName + " ($)");
		gotoCourses();
		verifyAddedCourse(sc.course.courseId.replace('-', '$'), sc.course.courseName + " ($)");
	}
		
	/**
	 * Fail to create a course with course ID containing non-permitted symbols
	 */
	public void testCreateCourseNonAllowedSymbolsIDFail() throws Exception {
		cout("TestCoordCourse: TestCreatingCourseWithNonPermittedSymbolsInID");
		
		addCourse(sc.course.courseId.replace("-", "!*}"), sc.course.courseName + " (!*})");
		assertEquals(true,isElementPresent(courseMessage));
	}
	
	/**
	 * Successfully create a course
	 */
	public void testCreateCourseSuccess() throws Exception {
		cout("TestCoordCourse: TestCreateCourseSuccess");

		addCourse(sc.course.courseId, sc.course.courseName);

		// Fix for DataStore delay problem, by clicking on Courses link again
		gotoCourses();
		verifyAddedCourse(sc.course.courseId, sc.course.courseName);
	}
	
	/**
	 * Create the course that previously was created
	 */
	public void testCreateCoursePreviouslyNamed() {
		cout("TestCoordCourse: TestCreatingCoursePreviouslyNamed");
		addCourse(sc.course.courseId, sc.course.courseName);
		gotoCourses();
		waitForElementPresent(getCourseID(0));
		verifyAddedCourse(sc.course.courseId, sc.course.courseName);
	}

	/**
	 * Fail to create duplicate course code (previous course created by the same
	 * coordinator)
	 */
	public void testCreateDuplicateCourseFail() throws Exception {
		cout("Test: Creating duplicated course.");
		// Add the second course with same ID
		while(!getElementText(courseMessage).equals("The course already exists."))
			addCourse(sc.course.courseId, sc.course.courseName);
		assertEquals("The course already exists.", getElementText(courseMessage));
		gotoCourses();
		waitForElementPresent(getCourseID(0));
		verifyAddedCourse(sc.course.courseId, sc.course.courseName);
	}

	/**
	 * Delete a course
	 */
	public void testDeleteCourseSuccess() throws Exception {
		cout("TestCoordCourse: Deleting course.");
		clickAndConfirmCourseDelete(0);
		
		justWait();
		waitForElementText(statusMessage, "The course has been deleted.");

		assertEquals(0,
				driver.findElements(By.cssSelector("#coordinatorCourseTable td.t_course_code"))
						.size());
	}

	/**
	 * Fail to create course that misses something
	 */
	public void testCreateCourseMissingInfoFail() throws Exception {
		cout("TestCoordCourse: Creating course with missing info.");
		// Trying adding course without ID
		addCourse("", sc.course.courseName);
		assertEquals(true,isElementPresent(courseMessage));
		// Adding course without name
		addCourse(sc.course.courseId, "");
		assertEquals(true,isElementPresent(courseMessage));
		
	}
}