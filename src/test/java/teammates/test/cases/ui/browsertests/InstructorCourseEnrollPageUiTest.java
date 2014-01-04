package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseEnrollResultPage;
import teammates.test.pageobjects.InstructorCoursesDetailsPage;


/**
 * Covers 'enroll' view for instructors.
 * SUT: {@link InstructorCourseEnrollPage}.
 */
public class InstructorCourseEnrollPageUiTest extends BaseUiTestCase {
	private static DataBundle testData;
	private static Browser browser;
	private InstructorCourseEnrollPage enrollPage;
	
	private static String enrollString = "";
	private Url enrollUrl;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorCourseEnrollPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		// A new student
		enrollString += "Team 3 | Emily France | emily.f.tmms@gmail.com | This student has just been added\n";
		// A new student with no comment
		enrollString += "Team 3 | Frank Galoe | frank.g.tmms@gmail.com\n";
		// A new student with no team
		enrollString += " | Gary Harbine | gary.h.tmms@gmail.com | This student has no team\n";
		// A new student with name containing accented characters
		enrollString += "Team 3 | José Gómez | jose.gomez.tmns@gmail.com | This student name contains accented characters\n";
		// A student to be modified
		enrollString += "Team 1 | Alice Betsy | alice.b.tmms@gmail.com | This comment has been changed\n";
		// An existing student with no modification
		enrollString += "Team 1 | Benny Charles | benny.c.tmms@gmail.com | This student's name is Benny Charles";
		
		browser = BrowserPool.getBrowser();
		
	}
	
	@Test
	public void testInstructorCourseEnrollPage() throws Exception{
		testContent();
		testSampleLink();
		testEnrollAction();
	}

	private void testContent() {
		
		______TS("typical enroll page");
		
		enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
		.withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
		.withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);
		
		enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);
		enrollPage.verifyHtml("/instructorCourseEnrollPage.html");
	}

	private void testSampleLink() throws Exception {
		
		______TS("link for the sample spreadsheet");
		
		enrollPage.verifyDownloadableFile(enrollPage.getSpreadsheetLink(),"B2F8A93F24ACAC5713BCBC42DAF1FDA59F7AE04B");
	}

	private void testEnrollAction() throws Exception {
		/* We test both empty and non-empty courses because the generated
		 * enroll result page is slightly different for the two cases.
		 */
		______TS("enroll action: non-empty course");
		
		InstructorCourseEnrollResultPage resultsPage = enrollPage.enroll(enrollString);
		resultsPage.verifyHtml("/instructorCourseEnrollPageResult.html");
		
		//check 'edit' link
		enrollPage = resultsPage.clickEditLink();
		enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
		//TODO: At times, this assertion doesn't work for remoter server + Firefox testing,
		//  but works for Chrome.
		assertEquals(enrollString, enrollPage.getEnrollText());
		
		//ensure students were actually enrolled
		String courseId = testData.courses.get("CCEnrollUiT.CS2104").id;
		Url coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
			.withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
			.withCourseId(courseId);
		InstructorCoursesDetailsPage detailsPage = loginAdminToPage(browser, coursesPageUrl, InstructorCoursesDetailsPage.class);
		assertEquals(8, detailsPage.getStudentCountForCourse("CCEnrollUiT.CS2104"));
		
		______TS("enroll action: empty course");
		
		//make the course empty
		BackDoor.deleteCourse(courseId);
		BackDoor.createCourse(testData.courses.get("CCEnrollUiT.CS2104"));
		BackDoor.createInstructor(testData.instructors.get("CCEnrollUiT.teammates.test"));
		
		enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
			.withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
			.withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);
		
		enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);
		enrollPage.enroll(enrollString)
			.verifyHtml("/instructorCourseEnrollPageResultForEmptyCourse.html");
	}

	@AfterClass
		public static void classTearDown() throws Exception {
			BrowserPool.release(browser);
		}
}