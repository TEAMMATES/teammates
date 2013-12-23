package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Covers the 'student list' view for instructors.
 */
public class InstructorStudentRecordsPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorStudentRecordsPage viewPage;
	private static DataBundle testDataNormal, testDataNoRecords, testDataQuestionType;
	

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testDataNormal = loadDataBundle("/MashupPageUiTest.json");
		testDataNoRecords = loadDataBundle("/InstructorCourseEnrollPageUiTest.json");
		testDataQuestionType = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		browser = BrowserPool.getBrowser();
	}
	
	
	@Test
	public void testAll() throws Exception{
		
		testContent();
		testLinks();
		//no action to test
		//no script to test
	}


	private void testContent() {
		InstructorAttributes instructor;
		StudentAttributes student; 
		
		______TS("content: typical case, normal student records");
		
		restoreTestDataOnServer(testDataNormal);
				
		instructor = testDataNormal.instructors.get("teammates.test.CS2104");
		student = testDataNormal.students.get("benny.c.tmms@CS2104");
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		viewPage.verifyHtml("/instructorStudentRecordsPage.html");
		
		
		______TS("content: no student records");
		
		restoreTestDataOnServer(testDataNoRecords);
				
		instructor = testDataNoRecords.instructors.get("CCEnrollUiT.teammates.test");
		student = testDataNoRecords.students.get("alice.b.tmms@CCEnrollUiT.CS2104");
		
		viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		viewPage.verifyHtml("/instructorStudentRecordsPageNoRecords.html");
		
		______TS("content: multiple feedback session type student record");
		
		restoreTestDataOnServer(testDataQuestionType);
				
		instructor = testDataQuestionType.instructors.get("instructor1OfCourse1");
		student = testDataQuestionType.students.get("student1InCourse1");
		
		viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		viewPage.verifyHtml("/instructorStudentRecordsPageMCQ.html");

	}
	
	public void testLinks() throws Exception{
		//TODO: implement this
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}