package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
import java.io.FileNotFoundException;
import java.util.HashMap;

import org.json.JSONException;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.NoAlertException;
import teammates.test.driver.TestProperties;

/**
 * Tests instructorCourse.jsp from UI functionality and HTML test
 */
public class InstructorCourseAddPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		ts = loadTestScenario(Common.TEST_DATA_FOLDER + "/instructorCourseAddUiTest.json");
		BackDoor.deleteCourse(ts.validCourse.id);
		BackDoor.deleteCourse(ts.CS1101.id);
		BackDoor.deleteCourse(ts.CS2104.id);
		BackDoor.deleteCourse("MultipleInstructorsCourse");
		BackDoor.deleteCourse("OmitInstructor");
		for (InstructorAttributes id : ts.instructor.values()) {
			BackDoor.deleteAccount(id.googleId);
		}
		
		String backDoorOperationStatus = BackDoor.createAccount(ts.account);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_INSTRUCTOR_COURSE;
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,ts.instructor.get("instructor1course1").googleId);
		bi.goToUrl(link);		
	}



	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
		
		// Always cleanup
		BackDoor.deleteCourse(ts.validCourse.id);
		BackDoor.deleteCourse(ts.CS1101.id);
		BackDoor.deleteCourse(ts.CS2104.id);
	}

	@Test
	public void testInstructorCourseAddPage() throws Exception{
		testInstructorCourseAddHTML();
		testInstructorCourseAddUiPaths();
		testInstructorCourseAddLinks();
	}

	public void testInstructorCourseAddHTML() throws Exception{
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseEmpty.html");

		BackDoor.createCourse(ts.CS1101);
		BackDoor.createCourse(ts.CS2104);
		BackDoor.createInstructor(ts.instructor.get("instructor1CS1101"));
		BackDoor.createInstructor(ts.instructor.get("instructor1CS2104"));
		bi.clickCourseTab();

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseById.html");
		
		bi.click(By.id("button_sortcoursename"));
		bi.assertDataTablePattern(1,"{*}Programming Language Concept{*}Programming Methodology");

		bi.click(By.id("button_sortcoursename"));
		bi.assertDataTablePattern(1,"{*}Programming Methodology{*}Programming Language Concept");
		
		bi.click(By.id("button_sortcourseid"));
		bi.assertDataTablePattern(0,"{*}CCAddUiTest.CS1101{*}CCAddUiTest.CS2104");
		
		bi.click(By.id("button_sortcourseid"));
		bi.assertDataTablePattern(0,"{*}CCAddUiTest.CS2104{*}CCAddUiTest.CS1101");		
	}

	public void testInstructorCourseAddUiPaths() throws Exception{
		
		String link = appUrl+Common.PAGE_INSTRUCTOR_COURSE;
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,ts.instructor.get("instructor1course1").googleId);
		
		// Course id only contains alphabets, numbers, dots, hyphens, underscores and dollars
		String courseId = ts.validCourse.id;
		// Course name can be any character including special characters
		String courseName = ts.validCourse.name;
		
		______TS("testInstructoraddCourseSuccessful");
		
		bi.addCourse(courseId, courseName);
		
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);

		bi.verifyCurrentPageHTMLWithRetry(Common.TEST_PAGES_FOLDER+"/instructorCourseAddSuccessful.html", link);
		
		______TS("testInstructoraddCourseWithInvalidInputsFailed");

		bi.addCourse("", courseName);
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD + "\n" + Common.MESSAGE_COURSE_INVALID_ID);
		
		// Adding course without name
		bi.addCourse(courseId, "");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD);
		
		//Not-allowed characters
		bi.addCourse(courseId+"!*}", courseName + " (!*})");
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_INVALID_ID);

		______TS("testMaxLengthOfInputFields");
		
		String shortCourseId = Common.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH);
		String longCourseId = Common.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH+1);
		
		String shortCourseName = Common.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH);
		String longCourseName = Common.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH+1);
		
		assertEquals(shortCourseId, bi.fillInCourseID(shortCourseId));
		assertEquals(longCourseId.substring(0, FieldValidator.COURSE_ID_MAX_LENGTH), bi.fillInCourseID(longCourseId));
		
		assertEquals(shortCourseName, bi.fillInCourseName(shortCourseName));
		assertEquals(longCourseName.substring(0, FieldValidator.COURSE_NAME_MAX_LENGTH), bi.fillInCourseName(longCourseName));

		______TS("testInstructoraddCourseWithDuplicateIdFailed");
		
		bi.addCourse(courseId, "different course name");
		
		start = System.currentTimeMillis();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseAddDupIdFailed.html");
		print("Time to assert page: "+(System.currentTimeMillis()-start)+" ms");

		
		______TS("testInstructoraddCourseWithMultipleWrongInstructorsEntries");
		
		String instructorListTEST = bi.getElementText(bi.instructorCourseInputInstructorList);
		String instructor1Details = "Instructor1ID" + "|" + "Instructor1Name";
		String instructor2Details = "Instructor2ID" + "|" + "Instructor2Name" + "|" + "instructor2Email@.gmailcom" + "|" + "EXTRA";
		String instructor3Details = "Instruct@r3ID" + "|" + "Instructor3Name" + "|" + "instructor3Email@gmail.com";
		String instructor4Details = "Instructor4ID" + "|" + "Instruct@r4Name" + "|" + "instructor4Email@gmail.com";
		String instructor5Details = "Instructor5ID" + "|" + "Instructor5Name" + "|" + "instructor5Email@.com";
		instructorListTEST += "\n" + instructor1Details;
		instructorListTEST += "\n" + instructor2Details;
		instructorListTEST += "\n" + instructor3Details;
		instructorListTEST += "\n" + instructor4Details;
		instructorListTEST += "\n" + instructor5Details;
		bi.fillString(bi.instructorCourseInputInstructorList, instructorListTEST);
		bi.addCourse("wr@ngC@urseID", "");
		
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_MISSING_FIELD + "\n"
				+ Common.MESSAGE_COURSE_INVALID_ID + "\n"
				+ Common.MESSAGE_COURSE_INPUT_FIELDS_MISSING + " (at line: 2): " + instructor1Details + "\n"
				+ Common.MESSAGE_COURSE_INPUT_FIELDS_EXTRA + " (at line: 3): " + instructor2Details + "\n"
				+ Common.MESSAGE_COURSE_GOOGLEID_INVALID + " (at line: 4): " + instructor3Details + "\n"
				+ Common.MESSAGE_COURSE_INSTRUCTORNAME_INVALID + " (at line: 5): " + instructor4Details + "\n"
				+ Common.MESSAGE_COURSE_EMAIL_INVALID + " (at line: 6): " + instructor5Details);
		
		______TS("testInstructoraddCourseWithMultipleInstructors");
		
		String instructorList = bi.getElementText(bi.instructorCourseInputInstructorList);
		InstructorAttributes instructor2 = ts.instructor.get("instructor2CS1101");
		instructorList += "\n" + instructor2.googleId + "|" + instructor2.name + "|" + instructor2.email;
		bi.fillString(bi.instructorCourseInputInstructorList, instructorList);
		bi.addCourse("MultipleInstructorsCourse", "Course with multiple instructors");
		
		bi.waitForStatusMessage(Common.MESSAGE_COURSE_ADDED);

		String newLink = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		newLink = Common.addParamToUrl(newLink,Common.PARAM_USER_ID,ts.instructor.get("instructor1course1").googleId);
		newLink = Common.addParamToUrl(newLink,Common.PARAM_COURSE_ID,"MultipleInstructorsCourse");
		
		//Go to course details page for testing
		bi.goToUrl(newLink);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsMultipleInstructors.html");
		bi.goToUrl(link);
				
		______TS("test verification for omit logged in instructor");
		bi.goToUrl(link);
		bi.fillString(bi.instructorCourseInputInstructorList, instructor2.googleId + "|" + instructor2.name + "|" + instructor2.email);
		bi.fillString(bi.instructorCourseInputCourseID, "OmitInstructor");
		bi.fillString(bi.instructorCourseInputCourseName, "Omit Instructor");
		bi.clickAndConfirm(bi.instructorCourseAddButton);
		
		newLink = appUrl+Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		newLink = Common.addParamToUrl(newLink,Common.PARAM_USER_ID,ts.instructor.get("instructor2CS1101").googleId);
		newLink = Common.addParamToUrl(newLink,Common.PARAM_COURSE_ID,"OmitInstructor");
		bi.goToUrl(newLink);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDetailsVerifyInstructorList.html");
		
		BackDoor.deleteCourse("MultipleInstructorsCourse");
		BackDoor.deleteCourse("OmitInstructor");
		bi.goToUrl(link);
		
	}

	public void testInstructorCourseAddLinks() throws Exception{
		
		String courseId = ts.validCourse.id;
		int courseRowId = bi.getCourseRowID(courseId);
		assertTrue(courseRowId!=-1);
		
		// Check delete link
		By deleteLinkLocator = bi.getInstructorCourseDeleteLinkLocator(courseRowId);
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String course = BackDoor.getCourseAsJson(ts.validCourse.id);
			if(isNullJSON(course)) Assert.fail("Course was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			Assert.fail("No alert box when clicking delete button at course page.");
		}

		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorCourseDeleteSuccessful.html");
		} catch (NoAlertException e){
			Assert.fail("No alert box when clicking delete button at course page.");
		}
	}
	
	private static TestScenario loadTestScenario(String scenarioJsonFile) throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = scenarioJsonFile;
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public AccountAttributes account;
		public HashMap<String,InstructorAttributes> instructor;
		public CourseAttributes validCourse;
		public CourseAttributes CS1101;
		public CourseAttributes CS2104;
	}
}