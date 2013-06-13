package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.test.driver.BackDoor;
import teammates.test.driver.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCoursesPage;

/**
 * Covers the 'Courses' page for instructors. 
 * The main SUT is {@link InstructorCoursesPage}. 
 */
public class InstructorCoursesPageUiTest extends BaseUiTestCase {
	private static Browser browser;
	/* Comments given as 'Explanation:' are extra comments added to train 
	 * developers. They are not meant to be repeated when you write similar 
	 * classes. 
	 * This class is used for training developers. Hence, the high percentage
	 * of explanatory comments, which is contrary to our usual policy of 
	 * 'minimal comments'. 
	 */
	
	/* Explanation: This is made a static variable for convenience 
	 * (no need to declare it multiple times in multiple methods) */
	private static InstructorCoursesPage coursesPage;
	private static DataBundle testData;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		/* Explanation: These two lines persist the test data on the server. */
		testData = loadTestData("/instructorCoursesPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		/* Explanation: Ideally, there should not be 'state leaks' between 
		 * tests. i.e. Changes to data done by one test should not affect 
		 * another test. To that end, we should make the dataset in the .json 
		 * file independent from other tests. Our approach is to add a unique
		 * prefix to identifiers in the json file. e.g., Google IDs, course IDs,
		 * etc. This identifer can be based on the name of the test class.
		 * e.g., "ICPUiT.inst.withnocourses" can be a Google ID unique to this
		 * class.
		 */
		
		/* Explanation: Gets a browser instance to be used for this class. */
		browser = BrowserPool.getBrowser();
	}


	@Test
	public void allTests() throws Exception{
		/* Explanation: We bunch together everything as one test case instead
		 * of having multiple test cases. The advantage is that the time for 
		 * the whole test class will be reduced because we minimize repetitive
		 * per-method setup/tear down. The downside is that it increases the 
		 * time spent on re-running failed tests as the whole class has to be
		 * re-run. We opt for this approach because we expect tests to pass 
		 * more frequently than to fail.
		 */
		
		
		/* Explanation: We do 'non-invasive' (i.e., no changes to datastore) tests first */
		
		// Explanation: Checks the rendering of the page content.
		testContent();  
		
		// Explanation: Checks if links going out of the page are correct 
		testLinks();
		
		// Explanation: Checks if client-side input validation for fields
		testInputValidation();
		
		/* Explanation: We do 'invasive' tests last */
		
		// Explanation: Checks 'actions' that can be performed using the page.
		testAddAction();
		testDeleteAction();
		
		/* Explanation: The above categorization of test cases is useful in
		 * identifying test cases. However, do not follow it blindly. 
		 * Some SUTs might require additional test cases. Examining the
		 * relevant JSP pages to check if all Java code paths are covered
		 *  might help you identify further test cases.
		 */
	}

	public void testContent() throws Exception{
		
		/* Explanation: The page rendering is slightly different based on 
		 * whether the table is empty or not. We should test both cases. 
		 * In addition, we should test the sorting.
		 */
		
		______TS("no courses");
		
		Url coursesUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE)
			.withUserId(testData.accounts.get("instructorWithoutCourses").googleId);
		coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
		coursesPage.verifyHtml("/instructorCourseEmpty.html");
		
		______TS("multiple course");
		
		coursesUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE)
			.withUserId(testData.accounts.get("instructorWithCourses").googleId);
		coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
		
		//this is sorted by Id (default sorting)
		coursesPage.loadCoursesTab().verifyHtml("/instructorCourseById.html");
		
		______TS("sorting");
		
		coursesPage.sortByCourseName()
			.verifyTablePattern(1,"{*}Programming Language Concept{*}Programming Methodology");

		coursesPage.sortByCourseName()
			.verifyTablePattern(1,"{*}Programming Methodology{*}Programming Language Concept");
		
		coursesPage.sortByCourseId()
			.verifyTablePattern(0,"{*}CCAddUiTest.CS1101{*}CCAddUiTest.CS2104");
		
		coursesPage.sortByCourseId()
			.verifyTablePattern(0,"{*}CCAddUiTest.CS2104{*}CCAddUiTest.CS1101");		
	}

	public void testLinks() throws Exception{
		
		/* Explanation: We test each of 'view' links and 'enroll' links.
		 * 'Delete' is not a link, but an action.
		 */
	
		String courseId = testData.courses.get("CS1101").id;
		
		______TS("view link");
		
		InstructorCourseDetailsPage detailsPage = coursesPage.loadViewLink(courseId)
				.verifyIsCorrectPage(courseId);
		
		coursesPage = detailsPage.goToPreviousPage(InstructorCoursesPage.class);
		
		______TS("enroll link");
		
		InstructorCourseEnrollPage enrollPage = coursesPage.loadEnrollLink(courseId)
				.verifyContents(courseId);
		
		coursesPage = enrollPage.goToPreviousPage(InstructorCoursesPage.class);
		
	}


	public void testInputValidation() {
		
		/* Explanation: If the validation is done through one JS function 
		 * (e.g., the entire form is validated in one go), we need to check only
		 * one invalid case here, if the form validation function is 
		 * thoroughly unit tested elsewhere. 
		 * If each field is validated as they are typed, each field should be 
		 * validated for one invalid case.
		 */
		
		______TS("input validation");
		
		//TODO: reduce test cases here after unit testing the JS validation function.
		
		String validCourseId = "valid.course.id";
		String validCourseName = "Valid Course Name";
	
		coursesPage.addCourse("", validCourseId, null)
			.verifyStatus(Common.MESSAGE_COURSE_MISSING_FIELD + "\n" + Common.MESSAGE_COURSE_INVALID_ID);
		
		// Adding course without name
		coursesPage.addCourse(validCourseId, "", null)
			.verifyStatus(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		coursesPage.addCourse(validCourseId+"!*}", validCourseName + " (!*})", null)
			.verifyStatus(Common.MESSAGE_COURSE_INVALID_ID);
		
	
		//Invalid lengths
		String maxLengthCourseId = Common.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH);
		String longCourseId = Common.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH+1);
		
		String maxLengthCourseName = Common.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH);
		String longCourseName = Common.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH+1);
		
		assertEquals(maxLengthCourseId, coursesPage.fillCourseIdTextBox(maxLengthCourseId));
		assertEquals(longCourseId.substring(0, FieldValidator.COURSE_ID_MAX_LENGTH), coursesPage.fillCourseIdTextBox(longCourseId));
		
		assertEquals(maxLengthCourseName, coursesPage.fillCourseNameTextBox(maxLengthCourseName));
		assertEquals(longCourseName.substring(0, FieldValidator.COURSE_NAME_MAX_LENGTH), coursesPage.fillCourseNameTextBox(longCourseName));
		
		//invalid instructor list
		
		String instructor1Details = "Instructor1ID" + "|" + "Instructor1Name";
		String instructor2Details = "Instructor2ID" + "|" + "Instructor2Name" + "|" + "instructor2Email@.gmailcom" + "|" + "EXTRA";
		String instructor3Details = "Instruct@r3ID" + "|" + "Instructor3Name" + "|" + "instructor3Email@gmail.com";
		String instructor4Details = "Instructor4ID" + "|" + "Instruct@r4Name" + "|" + "instructor4Email@gmail.com";
		String instructor5Details = "Instructor5ID" + "|" + "Instructor5Name" + "|" + "instructor5Email@.com";
		String originalInstructorsList = coursesPage.getInstructorList();
		String invalidInstructorsList = originalInstructorsList 
										+ "\n" + instructor1Details
										+ "\n" + instructor2Details
										+ "\n" + instructor3Details
										+ "\n" + instructor4Details
										+ "\n" + instructor5Details;
		
		String expectedStatusMessage = Common.MESSAGE_COURSE_MISSING_FIELD + "\n"
				+ Common.MESSAGE_COURSE_INVALID_ID + "\n"
				+ Common.MESSAGE_COURSE_INPUT_FIELDS_MISSING + " (at line: 2): " + instructor1Details + "\n"
				+ Common.MESSAGE_COURSE_INPUT_FIELDS_EXTRA + " (at line: 3): " + instructor2Details + "\n"
				+ Common.MESSAGE_COURSE_GOOGLEID_INVALID + " (at line: 4): " + instructor3Details + "\n"
				+ Common.MESSAGE_COURSE_INSTRUCTORNAME_INVALID + " (at line: 5): " + instructor4Details + "\n"
				+ Common.MESSAGE_COURSE_EMAIL_INVALID + " (at line: 6): " + instructor5Details;
		
		coursesPage.addCourse("invalidCourseId*%#", "", invalidInstructorsList)
			.verifyStatus(expectedStatusMessage);
	}


	public void testAddAction() throws Exception{
		
		/* Explanation: We test at least one valid case and one invalid case.
		 * If the action involves a confirmation dialog, we should test both
		 * 'confirm' and 'cancel' cases.
		 * 
		 */
		
		Url coursesUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE)
			.withUserId(testData.accounts.get("instructorWithCourses").googleId);
		coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
		
		______TS("add action success: add valid course");
		
		CourseAttributes validCourse =  new CourseAttributes("CCAddUiTest.course1","Software Engineering $%^&*()");
		/* Before creating an entity, we should delete it (in may have been
		 * created in a previous test run).
		 */
		BackDoor.deleteCourse(validCourse.id); //delete if it exists
		coursesPage.addCourse(validCourse.id, validCourse.name, null)
			.verifyStatus(Common.MESSAGE_COURSE_ADDED);

		coursesPage.verifyHtml("/instructorCourseAddSuccessful.html");

		______TS("add action fail: duplicate course ID");
		
		coursesPage.addCourse(validCourse.id, "different course name", null)
			.verifyHtml("/instructorCourseAddDupIdFailed.html");
		
		______TS("add action success: add course with multiple instructors");
		
		/* Explanation: This test case is unnecessary because we have already
		 * tested a success case. It is OK to throw in a few 'interesting' 
		 * test cases on top of the minimum required test cases. Such 'wild
		 * card' test cases can uncover bugs that we may have missed otherwise.
		 * However, we should not add too many redundant test cases.
		 */
		
		String instructorList = coursesPage.getInstructorList();
		InstructorAttributes instructor2 = testData.instructors.get("instructor2CS1101");
		instructorList += "\n" + instructor2.googleId + "|" + instructor2.name + "|" + instructor2.email;
		BackDoor.deleteCourse("MultipleInstructorsCourse"); //delete if it exists
		coursesPage.addCourse("MultipleInstructorsCourse", "Course with multiple instructors", instructorList)
			.verifyStatus(Common.MESSAGE_COURSE_ADDED);

		Url courseDetailsLink = new Url(Common.PAGE_INSTRUCTOR_COURSE_DETAILS)
			.withUserId(testData.accounts.get("instructorWithCourses").googleId)
			.withCourseId("MultipleInstructorsCourse");
		
		InstructorCourseDetailsPage detailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		detailsPage.verifyHtml("/instructorCourseDetailsMultipleInstructors.html");
		
		BackDoor.deleteCourse("MultipleInstructorsCourse"); // no longer used
		
		______TS("add action success: omit logged in instructor");
		
		coursesPage = detailsPage.navigateTo(coursesUrl, InstructorCoursesPage.class);
		BackDoor.deleteCourse("OmitInstructor"); //delete if it exists
		coursesPage.fillCourseIdTextBox("OmitInstructor");
		coursesPage.fillCourseNameTextBox("Omit Instructor");
		coursesPage.fillInstructorListTextBox(instructor2.googleId + "|" + instructor2.name + "|" + instructor2.email);
		coursesPage.submitAndCancel();
		assertNull(BackDoor.getCourse("OmitInstructor"));
		coursesPage.submitAndConfirm();
		
		courseDetailsLink = new Url(Common.PAGE_INSTRUCTOR_COURSE_DETAILS)
			.withUserId(instructor2.googleId)
			.withCourseId("OmitInstructor");
		
		detailsPage = coursesPage.navigateTo(courseDetailsLink, InstructorCourseDetailsPage.class);
		detailsPage.verifyHtml("/instructorCourseDetailsVerifyInstructorList.html");
		
	}


	public void testDeleteAction() throws Exception{
		
		/* Explanation: We test both 'confirm' and 'cancel' cases here.
		 */
		
		Url coursesUrl = new Url(Common.PAGE_INSTRUCTOR_COURSE)
			.withUserId(testData.accounts.get("instructorWithCourses").googleId);
		coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
	
		String courseId = "CCAddUiTest.course1";
		coursesPage.clickAndCancel(coursesPage.getDeleteLink(courseId));
		assertNotNull(BackDoor.getCourseAsJson(courseId));

		coursesPage.clickAndConfirm(coursesPage.getDeleteLink(courseId))
			.verifyHtml("/instructorCourseDeleteSuccessful.html");
		
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		//Explanation: release the Browser back to be reused by other tests.
		BrowserPool.release(browser);
		
		/* Explanation: We don't delete leftover data at the end of a test. 
		 * Instead, we delete such data at the beginning or at the point that
		 * data are accessed. This means there will be leftover data in the 
		 * datastore at the end of a test run. Not deleting data at the end
		 * saves time and helps in debugging if a test failed.
		 * 
		 */
	}

}