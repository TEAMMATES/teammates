package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;

/**
 * Covers the 'student details' view for instructors.
 * SUT: {@link InstructorCourseStudentDetailsViewPage}.
 */
public class InstructorCourseStudentDetailsPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorCourseStudentDetailsViewPage viewPage;
	private static DataBundle testData;
	

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorCourseStudentDetailsPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}
	
	
	@Test
	public void testAll() throws Exception{
		
		String instructorId = testData.instructors.get("CCSDetailsUiT.instr").googleId;
		String courseId = testData.courses.get("CCSDetailsUiT.CS2104").id;
		
		______TS("content: registered student");
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
			.withUserId(instructorId)
			.withCourseId(courseId)
			.withStudentEmail(testData.students.get("registeredStudent").email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorCourseStudentDetailsViewPage.class);
		viewPage.verifyHtml("/InstructorCourseStudentDetailsPage.html");

		______TS("content: unregistered student");
		
		
		viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
			.withUserId(instructorId)
			.withCourseId(courseId)
			.withStudentEmail(testData.students.get("unregisteredStudent").email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorCourseStudentDetailsViewPage.class);
		viewPage.verifyHtml("/InstructorCourseStudentDetailsUnregisteredPage.html");
		
		//No links, input validation, or actions to test.
		
	}
	

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}