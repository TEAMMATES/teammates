package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Url;
import teammates.common.util.FieldValidator.FieldType;
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
	
	private static String instructorId;
	private static String courseId;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorCourseEditPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
		
		instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
		courseId = testData.courses.get("InsCrsEdit.CS2104").id;
	}
	
	@Test
	public void allTests() throws Exception{
		testContent();
		
		testNewInstructorLink();
		testEditInstructorLink();
		
		testAddInstructorAction();
		testEditInstructorAction();
		testDeleteInstructorAction();
		testDeleteCourseAction();
	}
	
	public void testContent() throws Exception{

		______TS("page load");
		
		courseEditPage = getCourseEditPage();
		courseEditPage.verifyHtml("/instructorCourseEdit.html" );
		
	}
	
	private void testEditInstructorLink() {
		courseEditPage = getCourseEditPage();
		
		______TS("edit instructor link");
		assertEquals(true, courseEditPage.clickEditInstructorLink());
	}

	private void testNewInstructorLink() {
		courseEditPage = getCourseEditPage();
		
		______TS("add new instructor link");
		assertEquals(true, courseEditPage.clickShowNewInstructorFormButton());
	}

	private void testAddInstructorAction() {

		______TS("success: add an instructor");
		
		courseEditPage = getCourseEditPage();
		courseEditPage.addNewInstructor("InsCrsEdit.instructor", "Teammates Instructor", "InsCrsEdit.instructor@gmail.com");
		courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED);
		
		Url courseDetailsLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
				.withCourseId(courseId)
				.withUserId(testData.instructors.get("InsCrsEdit.test").googleId);
			
		InstructorCourseDetailsPage courseDetailsPage = courseEditPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsAddInstructor.html" );
	
		______TS("failure: add an existing instructor");
		
		courseEditPage = getCourseEditPage();
		courseEditPage.addNewInstructor("InsCrsEdit.instructor", "Teammates Instructor", "InsCrsEdit.instructor@gmail.com");
		courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
		
		______TS("failure: add an instructor with an invalid parameter");
		String invalidEmail = "InsCrsEdit.email.com";
		
		courseEditPage = getCourseEditPage();
		courseEditPage.addNewInstructor("InsCrsEdit.instructor", "Teammates Instructor", invalidEmail);
		courseEditPage.verifyStatus((new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail));
	}

	private void testEditInstructorAction() {

		______TS("success: edit an instructor");
		
		courseEditPage = getCourseEditPage();
		courseEditPage.editInstructor(instructorId, "New name", "new_email@email.com");
		courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED);
		
		______TS("failure: edit failed due to invalid parameters");
		String invalidEmail = "InsCrsEdit.email.com";
		
		courseEditPage = getCourseEditPage();
		courseEditPage.editInstructor(instructorId, "New name", invalidEmail);
		courseEditPage.verifyStatus((new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail));
	}
	
	private void testDeleteInstructorAction() {
		courseEditPage = getCourseEditPage();
		
		______TS("delete instructor then cancel");
		courseEditPage.clickDeleteInstructorLinkAndCancel();
		assertNotNull(BackDoor.getInstructorAsJson(instructorId, courseId));
		
		______TS("delete instructor successfully");
		courseEditPage.clickDeleteInstructorLinkAndConfirm();
		courseEditPage.verifyHtml("/instructorCourseEditDeleteInstructorSuccessful.html");
		
		______TS("failed to delete the last instructor");
		courseEditPage.clickDeleteInstructorLinkAndConfirm();
		courseEditPage.clickDeleteInstructorLinkAndConfirm();
		courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED);
		
		______TS("deleted own instructor role and redirect to courses page");
		// Change login id to another instructor
		BackDoor.createInstructor(testData.instructors.get("InsCrsEdit.coord"));
		instructorId = testData.instructors.get("InsCrsEdit.coord").googleId;
		
		courseEditPage = getCourseEditPage();
		courseEditPage.clickDeleteInstructorLinkAndConfirm();

		InstructorCoursesPage coursesPage = courseEditPage.changePageType(InstructorCoursesPage.class);
		coursesPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED + "\n"
							+ Const.StatusMessages.COURSE_EMPTY);
		
		// Change back login id to original instructor to ensure remaining test cases work properly
		instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
	}
	
	private void testDeleteCourseAction() {
		courseEditPage = getCourseEditPage();
		
		______TS("delete course then cancel");
		courseEditPage.clickDeleteCourseLinkAndCancel();
		assertNotNull(BackDoor.getCourseAsJson(courseId));
		
		______TS("delete course then proceed");
		InstructorCoursesPage coursePage = 
				courseEditPage.clickDeleteCourseLinkAndConfirm();
		coursePage.verifyContains("Add New Course");

	}
	
	private InstructorCourseEditPage getCourseEditPage() {		
		Url courseEditPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
				.withUserId(instructorId)
				.withCourseId(courseId);
		
		return loginAdminToPage(browser, courseEditPageLink, InstructorCourseEditPage.class);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
	
}