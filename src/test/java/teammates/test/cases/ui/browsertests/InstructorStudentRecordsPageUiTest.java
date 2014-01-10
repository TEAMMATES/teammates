package teammates.test.cases.ui.browsertests;

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
import teammates.test.pageobjects.InstructorEvalSubmissionEditPage;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Covers the 'student records' view for instructors.
 */
public class InstructorStudentRecordsPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorStudentRecordsPage viewPage;
	private static DataBundle testDataNormal, testDataQuestionType, testDataLinks;	

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testDataNormal = loadDataBundle("/InstructorStudentRecordsPageUiTest.json");
		testDataQuestionType = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
		testDataLinks = loadDataBundle("/InstructorEvalSubmissionEditPageUiTest.json");
		browser = BrowserPool.getBrowser();
	}
	
	
	@Test
	public void testAll() throws Exception{

		testContent();
		testLinks();
		testScript();
		testAction();
	}


	private void testContent() {
		InstructorAttributes instructor;
		StudentAttributes student; 
		
		______TS("content: typical case, normal student records with comments");
		
		restoreTestDataOnServer(testDataNormal);
				
		instructor = testDataNormal.instructors.get("teammates.test.CS2104");
		student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		viewPage.verifyHtml("/instructorStudentRecordsPage.html");
		
		
		______TS("content: no student records");
		
		instructor = testDataNormal.instructors.get("teammates.noeval");
		student = testDataNormal.students.get("alice.b.tmms@ISR.NoEval");
		
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
		viewPage.verifyHtml("/instructorStudentRecordsPageMixedQuestionType.html");

	}
	
	private void testLinks() throws Exception{
		InstructorAttributes instructor;
		StudentAttributes student;
		restoreTestDataOnServer(testDataLinks);
		instructor = testDataLinks.instructors.get("CESubEditUiT.instructor");
		student = testDataLinks.students.get("Charlie");
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		InstructorEvalSubmissionEditPage editPage = viewPage.clickEvalEditLink("First Eval");
		editPage.verifyHtml("/instructorEvalSubmissionEdit.html");
	}
	
	private void testAction() throws Exception{
		InstructorAttributes instructor;
		StudentAttributes student;
		
		instructor = testDataNormal.instructors.get("teammates.test.CS2104");
		student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		
		______TS("add comment: success");

		viewPage.addComment("New comment from teammates.test for Benny C")
				.verifyStatus("New comment has been added for this student");
		
		______TS("delete comment: cancel");
		
		viewPage.clickDeleteCommentAndCancel(0);
		
		______TS("delete comment: success");
		
		viewPage.clickDeleteCommentAndConfirm(0)
				.verifyStatus("Comment deleted");
		
		______TS("edit comment: success");
		
		viewPage.editComment(0, "Edited comment 2 from CS2104 teammates.test Instructor to Benny")
				.verifyStatus("Comment edited");
		
		//Edit back so that restoreDataBundle can identify and delete the comment.
		viewPage.editComment(0, "Comment 2 from ISR.CS2104 teammates.test Instructor to Benny");
	}
	
	private void testScript() throws Exception{
		InstructorAttributes instructor;
		StudentAttributes student;
		
		instructor = testDataNormal.instructors.get("teammates.test.CS2104");
		student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
			.withUserId(instructor.googleId)
			.withCourseId(instructor.courseId)
			.withStudentEmail(student.email);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
		
		______TS("add comment button");
		viewPage.verifyAddCommentButtonClick();
		
		______TS("edit comment button");
		viewPage.verifyEditCommentButtonClick(0);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}