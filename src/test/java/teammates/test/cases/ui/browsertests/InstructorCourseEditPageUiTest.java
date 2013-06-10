package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCoursesPage;

public class InstructorCourseEditPageUiTest extends BaseUiTestCase {
	private static DataBundle testData;
	private static Browser browser;
	private static InstructorCourseEditPage courseEditPage;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadTestData("/InstructorCourseEditPageUiTest.json");
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
		String instructorId = testData.instructors.get("CCDetailsUiT.test").googleId;
		String courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
		
		______TS("page load");
		
		Url courseEditPageUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE_EDIT)
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
			.verifyStatus("You must add at least 1 instructor in the course.");
		
		______TS("invalid info");
		
		courseEditPage.fillInstructorList(originalInformation + "GoogleID|NAME|InvalidEmail\n");
		courseEditPage.submitUnsuccessfully()
			.verifyStatus("The e-mail address is invalid. (at line: 3): GoogleID|NAME|InvalidEmail");
	}
	
	public void testEditAction() throws Exception{
		
		String instructorId = testData.instructors.get("CCDetailsUiT.test").googleId;
		String courseId = testData.courses.get("CCDetailsUiT.CS2104").id;
		Url courseEditPageUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE_EDIT)
			.withUserId(instructorId)
			.withCourseId(courseId);
		courseEditPage.navigateTo(courseEditPageUrl, InstructorCourseEditPage.class);
		
		String originalInformation = courseEditPage.getInstructorList();
		
		______TS("success: add an instructor");
		
		InstructorCoursesPage coursesPage = courseEditPage.editCourse(originalInformation + "CCDetailsUiT.instructor|Teammates Instructor|CCDetailsUiT.instructor@gmail.com");
		courseEditPage.verifyStatus(Common.MESSAGE_COURSE_EDITED);
		
		Url courseDetailsLink = new Url(Common.PAGE_INSTRUCTOR_COURSE_DETAILS)
			.withCourseId(courseId)
			.withUserId(testData.instructors.get("CCDetailsUiT.test").googleId);
		
		InstructorCourseDetailsPage courseDetailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsAddInstructor.html" );
		
		______TS("success: test edit existing instructor"); //TODO: this case should be removed. It should be covered by a lower level test
		
		courseEditPage = courseDetailsPage.navigateTo(courseEditPageUrl, InstructorCourseEditPage.class);
		coursesPage = courseEditPage.editCourse(originalInformation + "CCDetailsUiT.instructor|Teammates Instructor New|CCDetailsUiT.instructor.new@gmail.com");
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
		courseEditPage.fillInstructorList("CCDetailsUiT.instructor|Teammates Instructor|CCDetailsUiT.instructor@gmail.com");
		courseEditPage.clickSubmitButtonAndCancel();
		assertNotNull(BackDoor.getInstructor(instructorId, courseId));
		coursesPage = courseEditPage.clickSubmitButtonAndConfirm();
		assertEquals(
				"The course has been edited.\nYou have not created any courses yet. Use the form above to create a course.", 
				coursesPage.getStatus());
		assertNull(BackDoor.getInstructor(instructorId, courseId));
		
		courseDetailsLink = new Url(Common.PAGE_INSTRUCTOR_COURSE_DETAILS)
			.withCourseId(courseId)
			.withUserId(testData.accounts.get("CCDetailsUiT.instructor").googleId);
		courseDetailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		courseDetailsPage.verifyHtml("/instructorCourseDetailsOmitLoggedInInstructor.html");
		
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}