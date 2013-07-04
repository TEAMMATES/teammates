package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCoursesPage;

/**
 * Tests 'Edit Course Details' functionality for Instructors.
 * SUT {@link InstructorCourseEditPage}. <br>
 */
public class InstructorCourseEditPageUiTest extends BaseUiTestCase {
	private static DataBundle testData;
	private static Browser browser;
	private static InstructorCourseEditPage courseEditPage;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorCourseEditPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}
	
	@Test
	public void allTests() throws Exception{
		testContent();
		testInputValidation();
		testEditAction();
		//no links to test
	}
	
	public void testContent() throws Exception{
		String instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
		String courseId = testData.courses.get("InsCrsEdit.CS2104").id;
		
		______TS("page load");
		
		Url courseEditPageUrl = new Url(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
			.withUserId(instructorId)
			.withCourseId(courseId);
		
		courseEditPage = loginAdminToPage(browser, courseEditPageUrl, InstructorCourseEditPage.class);
		
		courseEditPage.verifyHtml("/instructorCourseEdit.html" );
		
	}
	
	public void testInputValidation() throws Exception {
		
		______TS("empty instructor list");
		
		String originalInformation = courseEditPage.getInstructorList();
		courseEditPage.verifyStatus("");
		courseEditPage.fillInstructorList("");
		courseEditPage.submitUnsuccessfully()
			.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_LIST_EMPTY);
		
		______TS("invalid info");
		
		courseEditPage.fillInstructorList(originalInformation + "\nGoogleID|NAME|InvalidEmail\n");
		courseEditPage.submitUnsuccessfully()
			.verifyStatus("The e-mail address is invalid. Incorrect line : GoogleID|NAME|InvalidEmail");
	}
	
	public void testEditAction() throws Exception{
		
		String instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
		String courseId = testData.courses.get("InsCrsEdit.CS2104").id;
		Url courseEditPageUrl = new Url(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
			.withUserId(instructorId)
			.withCourseId(courseId);
		courseEditPage.navigateTo(courseEditPageUrl, InstructorCourseEditPage.class);
		
		String originalInformation = courseEditPage.getInstructorList();
		
		______TS("success: add an instructor");
		
		InstructorCoursesPage coursesPage = courseEditPage.editCourse(originalInformation + "\nInsCrsEdit.instructor|Teammates Instructor|InsCrsEdit.instructor@gmail.com");
		courseEditPage.verifyStatus(Const.StatusMessages.COURSE_EDITED);
		
		Url courseDetailsLink = new Url(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
			.withCourseId(courseId)
			.withUserId(testData.instructors.get("InsCrsEdit.test").googleId);
		
		InstructorCourseDetailsPage courseDetailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsAddInstructor.html" );
		
		______TS("success: test edit existing instructor"); //TODO: this case should be removed. It should be covered by a lower level test
		
		courseEditPage = courseDetailsPage.navigateTo(courseEditPageUrl, InstructorCourseEditPage.class);
		coursesPage = courseEditPage.editCourse(originalInformation + "\nInsCrsEdit.instructor|Teammates Instructor New|InsCrsEdit.instructor.new@gmail.com");
		assertEquals("The course has been edited.", coursesPage.getStatus());
		
		courseDetailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsEditInstructor.html");
		
		______TS("success: delete existing instructor"); //TODO: this case should be covered by a lower level test
		
		courseEditPage = courseDetailsPage.navigateTo(courseEditPageUrl, InstructorCourseEditPage.class);
		coursesPage = courseEditPage.editCourse(originalInformation);
		assertEquals("The course has been edited.", coursesPage.getStatus());
		
		courseDetailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsDeleteInstructor.html");
		
		______TS("success: instructor list without logged-in instructor");
		
		courseEditPage = courseDetailsPage.navigateTo(courseEditPageUrl, InstructorCourseEditPage.class);
		courseEditPage.fillInstructorList("InsCrsEdit.instructor|Teammates Instructor|InsCrsEdit.instructor@gmail.com");
		courseEditPage.clickSubmitButtonAndCancel();
		assertNotNull(BackDoor.getInstructor(instructorId, courseId));
		coursesPage = courseEditPage.clickSubmitButtonAndConfirm();
		assertEquals(
				"The course has been edited.\nYou have not created any courses yet. Use the form above to create a course.", 
				coursesPage.getStatus());
		assertNull(BackDoor.getInstructor(instructorId, courseId));
		
		courseDetailsLink = new Url(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
			.withCourseId(courseId)
			.withUserId(testData.accounts.get("InsCrsEdit.instructor").googleId);
		courseDetailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsOmitLoggedInInstructor.html");
		
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}