package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.test.pageobjects.InstructorStudentListPage;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Covers the 'student list' view for instructors.
 */
public class InstructorStudentListPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	private static InstructorStudentListPage viewPage;
	private static DataBundle testData;
	

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/InstructorStudentListPageUiTest.json");
		restoreTestDataOnServer(testData);
		browser = BrowserPool.getBrowser();
	}
	
	
	@Test
	public void testAll() throws Exception{
		
		testContent();
		testLinks();
		testDeleteAction();
		testSearchScript();
	}


	private void testContent() {
		String instructorId;
		
		______TS("content: 2 course with students");
		
		instructorId = testData.instructors.get("instructorOfCourse2").googleId;
		
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
			.withUserId(instructorId);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
		viewPage.verifyHtml("/instructorStudentListPage.html");
		
		______TS("content: search student");

		viewPage.setSearchKey("ben");
		viewPage.verifyHtml("/instructorStudentListPageSearchStudent.html");
		
		______TS("content: search and toggle show email");
		
		viewPage.clickShowEmail();
		viewPage.verifyHtml("/instructorStudentListPageSearchShowEmail.html");
		viewPage.clickShowEmail();
		viewPage.verifyHtml("/instructorStudentListPageSearchStudent.html");
		
		______TS("content: live search");

		viewPage.setLiveSearchKey("charlie");
		viewPage.verifyHtml("/instructorStudentListPageLiveSearch.html");
		
		______TS("content: search course");
		
		viewPage.setSearchKey("course3");
		viewPage.verifyHtml("/instructorStudentListPageSearchCourse.html");
		
		______TS("content: search no match");
		
		viewPage.setSearchKey("noMatch");
		viewPage.verifyHtml("/instructorStudentListPageSearchNoMatch.html");
		
		______TS("content: 1 course with no students");
		
		instructorId = testData.instructors.get("instructorOfCourse1").googleId;
		
		viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
			.withUserId(instructorId);
		
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
		viewPage.verifyHtml("/instructorStudentListPageNoStudent.html");

		
		______TS("content: no course");
		
		instructorId = testData.accounts.get("instructorWithoutCourses").googleId;
		
		viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
				.withUserId(instructorId);
			
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
		viewPage.verifyHtml("/instructorStudentListPageNoCourse.html");
	}
	
	public void testLinks() throws Exception{
		
		String instructorId = testData.instructors.get("instructorOfCourse2").googleId;
		Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE)
					.withUserId(instructorId);
			
		viewPage = loginAdminToPage(browser, viewPageUrl, InstructorStudentListPage.class);
		
		______TS("link: enroll");
		String courseId = testData.courses.get("course2").id;
		InstructorCourseEnrollPage enrollPage = viewPage.clickEnrollStudents(courseId);
		enrollPage.verifyIsCorrectPage(courseId);
		viewPage = enrollPage.goToPreviousPage(InstructorStudentListPage.class);
		
		______TS("link: view");
		
		StudentAttributes student1 = testData.students.get("Student1Course2");
		InstructorCourseStudentDetailsViewPage studentDetailsPage = viewPage.clickViewStudent(student1.name);
		studentDetailsPage.verifyIsCorrectPage(student1.email);
		viewPage = studentDetailsPage.goToPreviousPage(InstructorStudentListPage.class);
		
		______TS("link: edit");
		
		StudentAttributes student2 = testData.students.get("Student3Course3");
		InstructorCourseStudentDetailsEditPage studentEditPage = viewPage.clickEditStudent(student2.name);
		studentEditPage.verifyIsCorrectPage(student2.email);
		viewPage = studentEditPage.goToPreviousPage(InstructorStudentListPage.class);
		
		______TS("link: view records");
		
		InstructorStudentRecordsPage studentRecordsPage = viewPage.clickViewRecordsStudent(student2.name);
		studentRecordsPage.verifyIsCorrectPage();
		viewPage = studentRecordsPage.goToPreviousPage(InstructorStudentListPage.class);
	}
	
	private void testDeleteAction() {
		
		______TS("action: delete");
		
		String studentName = testData.students.get("Student2Course3").name;
		String studentEmail = testData.students.get("Student2Course3").email;
		String courseId = testData.courses.get("course3").id;
		
		viewPage.clickDeleteAndCancel(studentName);
		assertNotNull(BackDoor.getStudent(courseId, studentEmail));

		viewPage.clickDeleteAndConfirm(studentName)
			.verifyHtml("/instructorStudentListDeleteSuccessful.html");
	}
	
	private void testSearchScript() {
		// already covered under testContent() ______TS("content: search active")
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}