package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
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
     * (i.e. no need to declare it multiple times in multiple methods) */
    private static InstructorCoursesPage coursesPage;
    private static DataBundle testData;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        
        /* Explanation: These two lines persist the test data on the server. */
        testData = loadDataBundle("/InstructorCoursesPageUiTest.json");
        restoreTestDataOnServer(testData);
        
        /* Explanation: Ideally, there should not be 'state leaks' between 
         * tests. i.e. Changes to data done by one test should not affect 
         * another test. To that end, we should make the dataset in the .json 
         * file independent from other tests. Our approach is to add a unique
         * prefix to identifiers in the json file. e.g., Google IDs, course IDs,
         * etc. This identifier can be based on the name of the test class.
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
        testSortCourses();
        testDeleteAction();
        testArchiveAction();
        
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
        
        Url coursesUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
            .withUserId(testData.accounts.get("instructorWithoutCourses").googleId);
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
        coursesPage.verifyHtml("/instructorCourseEmpty.html");
        
        ______TS("multiple course");
        
        coursesUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
            .withUserId(testData.accounts.get("instructorWithCourses").googleId);
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);

    }

    public void testLinks() throws Exception{
        
        /* Explanation: We test each of 'view' links and 'enroll' links.
         * 'Delete' is not a link, but an action.
         */
    
        String courseId = testData.courses.get("CS2104").id;
        
        ______TS("view link");
        
        /* Explanation: When checking links, we check the destination page
         * for some keywords rather than do a full content match.
         */
        InstructorCourseDetailsPage detailsPage = coursesPage.loadViewLink(courseId)
                .verifyIsCorrectPage(courseId);
        
        coursesPage = detailsPage.goToPreviousPage(InstructorCoursesPage.class);
        
        ______TS("enroll link");
        
        InstructorCourseEnrollPage enrollPage = coursesPage.loadEnrollLink(courseId)
                .verifyIsCorrectPage(courseId);
        
        coursesPage = enrollPage.goToPreviousPage(InstructorCoursesPage.class);

        ______TS("edit link");

        InstructorCourseEditPage editPage = coursesPage.loadEditLink(courseId)
                .verifyIsCorrectPage(courseId);

        coursesPage = editPage.goToPreviousPage(InstructorCoursesPage.class);
        
    }


    public void testInputValidation() {
        
        /* Explanation: If the validation is done through one JS function 
         * (e.g., the entire form is validated in one go), we need to check only
         * one invalid case here, provided the form validation function is 
         * thoroughly unit tested elsewhere {@see instructorCourseJsTest.js}. 
         * If each field is validated as they are keyed in, each field should be 
         * validated for one invalid case.
         */
        
        ______TS("input validation");
        
        //one invalid case
        coursesPage.addCourse("", "")
            .verifyStatus(Const.StatusMessages.COURSE_COURSE_ID_EMPTY + "\n"
                    + Const.StatusMessages.COURSE_COURSE_NAME_EMPTY);
        
        //Checking max-length enforcement by the text boxes
        String maxLengthCourseId = StringHelper.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH);
        String longCourseId = StringHelper.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH+1);
        
        assertEquals(maxLengthCourseId, coursesPage.fillCourseIdTextBox(maxLengthCourseId));
        assertEquals(longCourseId.substring(0, FieldValidator.COURSE_ID_MAX_LENGTH), coursesPage.fillCourseIdTextBox(longCourseId));
        
        String maxLengthCourseName = StringHelper.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH);
        String longCourseName = StringHelper.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH+1);
        
        assertEquals(maxLengthCourseName, coursesPage.fillCourseNameTextBox(maxLengthCourseName));
        assertEquals(longCourseName.substring(0, FieldValidator.COURSE_NAME_MAX_LENGTH), coursesPage.fillCourseNameTextBox(longCourseName));
        
    }


    public void testAddAction() throws Exception{
        
        /* Explanation: We test at least one valid case and one invalid case.
         * If the action involves a confirmation dialog, we should test both
         * 'confirm' and 'cancel' cases.
         * 
         */
        
        Url coursesUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
            .withUserId(testData.accounts.get("instructorWithCourses").googleId);
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
        
        ______TS("add action success: add course with leading/trailing space in parameters");
        
        CourseAttributes validCourse =  new CourseAttributes(" CCAddUiTest.course1 "," Software Engineering $^&*() ");
        /* Before creating an entity, we should delete it (in may have been
         * created in a previous test run).
         */
        BackDoor.deleteCourse(validCourse.id); //delete if it exists
        coursesPage.addCourse(validCourse.id, validCourse.name);

        coursesPage.verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseAddSuccessful.html");

        ______TS("add action fail: duplicate course ID");
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
        
        coursesPage.addCourse(validCourse.id, "different course name");

        coursesPage.verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseAddDupIdFailed.html");
        
        ______TS("add action fail: invalid course ID");
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
        String invalidID = "Invalid ID";
        
        coursesPage.addCourse(invalidID, "random course name");

        coursesPage.verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseAddInvalidIdFailed.html");

        ______TS("add action fail: missing parameters");
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
        String validID = "Valid.ID";
        String missingCourseName = "";

        coursesPage.addCourse(validID, missingCourseName);

        coursesPage.verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseAddMissingParamsFailed.html");
    }
    
    public void testSortCourses() {
        
        ______TS("sorting");
        
        String patternString = "Programming Language Concept{*}Programming Methodology{*}Software Engineering $^&*()";
        coursesPage.sortByCourseName().verifyTablePattern(1, patternString);
        patternString = "Software Engineering $^&*(){*}Programming Methodology{*}Programming Language Concept";
        coursesPage.sortByCourseName().verifyTablePattern(1, patternString);
        
        patternString = "CCAddUiTest.course1{*}CCAddUiTest.CS1101{*}CCAddUiTest.CS2104";
        coursesPage.sortByCourseId().verifyTablePattern(0, patternString);
        patternString = "CCAddUiTest.CS2104{*}CCAddUiTest.CS1101{*}CCAddUiTest.course1";
        coursesPage.sortByCourseId().verifyTablePattern(0, patternString);
    }

    public void testDeleteAction() throws Exception{
        
        /* Explanation: We test both 'confirm' and 'cancel' cases here.
         */
        
        Url coursesUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
            .withUserId(testData.accounts.get("instructorWithCourses").googleId);
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
    
        String courseId = "CCAddUiTest.course1";
        coursesPage.clickAndCancel(coursesPage.getDeleteLink(courseId));
        assertNotNull(BackDoor.getCourseAsJson(courseId));

        coursesPage.clickAndConfirm(coursesPage.getDeleteLink(courseId))
            .verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseDeleteSuccessful.html");
        
    }
    
    public void testArchiveAction() throws Exception {
        
        Url coursesUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
                .withUserId(testData.accounts.get("instructorWithCourses").googleId);
        coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
            
        ______TS("archive action success");
       String courseId = "CCAddUiTest.CS1101";

       coursesPage.archiveCourse(courseId);
       coursesPage.verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseArchiveSuccessful.html");

       ______TS("unarchive action success");
       coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
            
       coursesPage.unarchiveCourse(courseId);
       coursesPage.verifyHtmlPart(By.id("frameBodyWrapper"), "/instructorCourseUnarchiveSuccessful.html");

       // TODO: Handling for the failure of archive and unarchive is still not good
       // Need more improvement
            
       ______TS("archive action failed");
       // only possible if someone else delete the course while the user is viewing the page
            
       String anotherCourseId = "CCAddUiTest.CS2104";

       coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
            
       BackDoor.deleteCourse(anotherCourseId);

       coursesPage.archiveCourse(anotherCourseId);
       coursesPage.verifyContains("You are not authorized to view this page.");

       ______TS("unarchive action failed");
       // only possible if someone else delete the course while the user is viewing the page
            
       coursesPage = loginAdminToPage(browser, coursesUrl, InstructorCoursesPage.class);
       coursesPage.archiveCourse(courseId);
 
       BackDoor.deleteCourse(courseId);

       coursesPage.unarchiveCourse(courseId);
       coursesPage.verifyContains("You are not authorized to view this page.");
            
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
