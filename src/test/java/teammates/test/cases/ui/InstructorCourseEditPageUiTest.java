package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

/**
 * Tests instructorEvalEdit.jsp from functionality and UI
 */
public class InstructorCourseEditPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;
	private static String link;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorCourseEditUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);

	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
		
		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}

	@Test
	public void testInstructorCourseEditPage() throws Exception{
		
		______TS("test course edit page");
		link = appUrl+Common.PAGE_INSTRUCTOR_COURSE_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,scn.instructors.get("CCDetailsUiT.test").googleId);
		bi.goToUrl(link);
		bi.verifyNotServerSideError();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseEdit.html");
		
		______TS("test empty instructor list");
		String originalInformation = bi.getElementValue(bi.instructorCourseInputInstructorList);
		bi.fillString(bi.instructorCourseInputInstructorList, "");
		By submitButton = By.id("button_submit");
		bi.click(submitButton);
		bi.waitForStatusMessage("You must add at least 1 instructor in the course.");
		
		______TS("test invalid info");
		bi.fillString(bi.instructorCourseInputInstructorList, originalInformation + "GoogleID|NAME|InvalidEmail\n");
		bi.click(submitButton);
		bi.waitForStatusMessage("The e-mail address is invalid. (at line: 3): GoogleID|NAME|InvalidEmail");
			
		______TS("test add new instructor");
		bi.fillString(bi.instructorCourseInputInstructorList, originalInformation + "CCDetailsUiT.instructor|Teammates Instructor|CCDetailsUiT.instructor@gmail.com");
		bi.click(submitButton);
		bi.waitForStatusMessage("The course has been edited.");
		String courseDetailsLink = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_USER_ID,scn.instructors.get("CCDetailsUiT.test").googleId);
		bi.goToUrl(courseDetailsLink);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsAddInstructor.html");
		
		______TS("test edit existing instructor");
		bi.goToUrl(link);
		bi.fillString(bi.instructorCourseInputInstructorList, originalInformation + "CCDetailsUiT.instructor|Teammates Instructor New|CCDetailsUiT.instructor.new@gmail.com");
		bi.click(submitButton);
		bi.waitForStatusMessage("The course has been edited.");
		courseDetailsLink = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_USER_ID,scn.instructors.get("CCDetailsUiT.test").googleId);
		bi.goToUrl(courseDetailsLink);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsEditInstructor.html");
		
		______TS("test delete existing instructor");
		bi.goToUrl(link);
		bi.fillString(bi.instructorCourseInputInstructorList, originalInformation);
		bi.click(submitButton);
		bi.waitForStatusMessage("The course has been edited.");
		courseDetailsLink = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_USER_ID,scn.instructors.get("CCDetailsUiT.test").googleId);
		bi.goToUrl(courseDetailsLink);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsDeleteInstructor.html");
		
		______TS("test instructor list without logged-in instructor");
		bi.goToUrl(link);
		bi.fillString(bi.instructorCourseInputInstructorList, "CCDetailsUiT.instructor|Teammates Instructor|CCDetailsUiT.instructor@gmail.com");
		bi.clickAndConfirm(submitButton);
		bi.waitForStatusMessage("The course has been edited.\nYou have not created any courses yet. Use the form above to create a course.");
		courseDetailsLink = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		courseDetailsLink = Common.addParamToUrl(courseDetailsLink,Common.PARAM_USER_ID,scn.accounts.get("CCDetailsUiT.instructor").googleId);
		bi.goToUrl(courseDetailsLink);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsOmitLoggedInInstructor.html");
	}
}