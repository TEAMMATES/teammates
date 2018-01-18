package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.driver.StringHelperExtension;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCoursesPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSES_PAGE}.
 */
public class InstructorCoursesPageUiTest extends BaseUiTestCase {
    /* Comments given as 'Explanation:' are extra comments added to train
     * developers. They are not meant to be repeated when you write similar
     * classes.
     * This class is used for training developers. Hence, the high percentage
     * of explanatory comments, which is contrary to our usual policy of
     * 'minimal comments'.
     */

    /* Explanation: This is made a global variable for convenience
     * (i.e. no need to declare it multiple times in multiple methods) */
    private InstructorCoursesPage coursesPage;

    private String instructorId;

    private CourseAttributes validCourse =
            CourseAttributes
                    .builder(" CCAddUiTest.course1 ", " Software Engineering $^&*() ", "Asia/Singapore")
                    .build();

    @Override
    protected void prepareTestData() {
        /* Explanation: These two lines persist the test data on the server. */
        testData = loadDataBundle("/InstructorCoursesPageUiTest.json");
        removeAndRestoreDataBundle(testData);

        /* Explanation: Ideally, there should not be 'state leaks' between
         * tests. i.e. Changes to data done by one test should not affect
         * another test. To that end, we should make the dataset in the .json
         * file independent from other tests. Our approach is to add a unique
         * prefix to identifiers in the json file. e.g., Google IDs, course IDs,
         * etc. This identifier can be based on the name of the test class.
         * e.g., "ICPUiT.inst.withnocourses" can be a Google ID unique to this
         * class.
         */
    }

    @BeforeClass
    public void classSetup() {
        /*
         * Any entity that is created in previous test run must be deleted.
         * If that previous test run fails, the entity persists and that will
         * break tests.
         */
        BackDoor.deleteCourse(validCourse.getId()); // delete if it exists
    }

    @Test
    public void allTests() throws Exception {
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

        // Explanation: Checks the ajax request for course stats.
        testCourseStats();

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

    private void testContent() throws Exception {

        /* Explanation: The page rendering is slightly different based on
         * whether the table is empty or not. We should test both cases.
         * In addition, we should test the sorting.
         */

        ______TS("no courses");

        instructorId = testData.accounts.get("instructorWithoutCourses").googleId;
        coursesPage = getCoursesPage();

        // This is the full HTML verification for Instructor Courses Page, the rest can all be verifyMainHtml
        coursesPage.verifyHtml("/instructorCoursesNoCourse.html");

        ______TS("multiple course");

        instructorId = testData.accounts.get("instructorWithCourses").googleId;
        coursesPage = getCoursesPage();
        // for course CS1101, current instructor cannot modify course or modify students
        coursesPage.verifyHtmlMainContent("/instructorCoursesMultipleCourses.html");

        ______TS("Failure Case: Ajax error");
        coursesPage = getCoursesPage();
        coursesPage.changeUserIdInAjaxLoadCoursesForm("invalidUserId");
        coursesPage.triggerAjaxLoadCourses();
        coursesPage.waitForAjaxLoadCoursesError();
        coursesPage = getCoursesPage();
    }

    private void testCourseStats() throws Exception {
        ______TS("Course Stats");
        coursesPage = getCoursesPage();
        coursesPage.triggerAjaxLoadCourseStats(1);
        coursesPage.verifyHtmlMainContent("/instructorCoursesStatsAjaxSuccessful.html");

        ______TS("Course Stats Failed");
        coursesPage = getCoursesPage();
        coursesPage.changeHrefInAjaxLoadCourseStatsLink("invalidLink");
        coursesPage.triggerAjaxLoadCourseStats(1);
        coursesPage.verifyHtmlMainContent("/instructorCoursesStatsAjaxFailure.html");
        coursesPage = getCoursesPage();
    }

    private void testLinks() {

        /* Explanation: We test each of 'view' links and 'enroll' links.
         * 'Delete' is not a link, but an action.
         */

        String courseId = testData.courses.get("CS2104").getId();

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

    private void testInputValidation() throws Exception {

        /* Explanation: If the validation is done through one JS function
         * (e.g., the entire form is validated in one go), we need to check only
         * one invalid case here, provided the form validation function is
         * thoroughly unit tested elsewhere {@see instructorCourseJsTest.js}.
         * If each field is validated as they are keyed in, each field should be
         * validated for one invalid case.
         */

        ______TS("input validation");

        //one invalid case
        coursesPage.addCourse("", "").verifyStatus(
                getPopulatedEmptyStringErrorMessage(FieldValidator.COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                    FieldValidator.COURSE_ID_FIELD_NAME, FieldValidator.COURSE_ID_MAX_LENGTH) + "\n"
                + getPopulatedEmptyStringErrorMessage(
                      FieldValidator.SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                      FieldValidator.COURSE_NAME_FIELD_NAME, FieldValidator.COURSE_NAME_MAX_LENGTH));

        //Checking max-length enforcement by the text boxes
        String maxLengthCourseId = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH);
        String longCourseId = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_ID_MAX_LENGTH + 1);

        assertEquals(maxLengthCourseId, coursesPage.fillCourseIdTextBox(maxLengthCourseId));
        assertEquals(longCourseId.substring(0, FieldValidator.COURSE_ID_MAX_LENGTH),
                     coursesPage.fillCourseIdTextBox(longCourseId));

        String maxLengthCourseName = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH);
        String longCourseName = StringHelperExtension.generateStringOfLength(FieldValidator.COURSE_NAME_MAX_LENGTH + 1);

        assertEquals(maxLengthCourseName, coursesPage.fillCourseNameTextBox(maxLengthCourseName));
        assertEquals(longCourseName.substring(0, FieldValidator.COURSE_NAME_MAX_LENGTH),
                     coursesPage.fillCourseNameTextBox(longCourseName));

    }

    private void testAddAction() throws Exception {

        /* Explanation: We test at least one valid case and one invalid case.
         * If the action involves a confirmation dialog, we should test both
         * 'confirm' and 'cancel' cases.
         *
         */

        instructorId = testData.accounts.get("instructorWithCourses").googleId;
        coursesPage = getCoursesPage();

        ______TS("add action success: add course with leading/trailing space in parameters");

        coursesPage.addCourse(validCourse.getId(), validCourse.getName());

        coursesPage.verifyHtmlMainContent("/instructorCoursesAddSuccessful.html");

        ______TS("add action fail: duplicate course ID");

        coursesPage.addCourse(validCourse.getId(), "different course name");

        coursesPage.verifyHtmlMainContent("/instructorCoursesAddDupIdFailed.html");

        ______TS("add action fail: invalid course ID");

        String invalidId = "Invalid ID";

        coursesPage.addCourse(invalidId, "random course name");

        coursesPage.verifyHtmlMainContent("/instructorCoursesAddInvalidIdFailed.html");

        ______TS("add action fail: missing parameters");

        String validId = "Valid.ID";
        String missingCourseName = "";

        coursesPage.addCourse(validId, missingCourseName);

        coursesPage.verifyHtmlMainContent("/instructorCoursesAddMissingParamsFailed.html");
    }

    private void testSortCourses() {

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

    private void testDeleteAction() throws Exception {

        /* Explanation: We test both 'confirm' and 'cancel' cases here.
         */

        String courseId = "CCAddUiTest.course1";
        coursesPage.clickAndCancel(coursesPage.getDeleteLink(courseId));
        assertNotNull(BackDoor.getCourse(courseId));

        coursesPage.clickAndConfirm(coursesPage.getDeleteLink(courseId))
                   .verifyHtmlMainContent("/instructorCoursesDeleteSuccessful.html");
    }

    private void testArchiveAction() throws Exception {

        InstructorAttributes instructor1CS1101 = testData.instructors.get("instructor1CS1101");

        ______TS("archive action success");
        String courseId = "CCAddUiTest.CS1101";

        InstructorAttributes instructorWithNullArchiveStatus = BackDoor.getInstructorByGoogleId(instructor1CS1101.googleId,
                                                                                                instructor1CS1101.courseId);

        //this is a old instructor whose archive status is not set and is by default false
        assertFalse(instructorWithNullArchiveStatus.isArchived);

        coursesPage.archiveCourse(courseId);
        coursesPage.waitForAjaxLoadCoursesSuccess();
        coursesPage.verifyHtmlMainContent("/instructorCoursesArchiveSuccessful.html");

        instructorWithNullArchiveStatus = BackDoor.getInstructorByGoogleId(instructor1CS1101.googleId,
                                                                           instructor1CS1101.courseId);

        //after click archive button, new value will be assigned to instructor's isArchive attribute
        //after this, his own archive status for this course will not be affected by other instructors
        //of the same course
        assertTrue(instructorWithNullArchiveStatus.isArchived.booleanValue());

        ______TS("archive status of another instructor from same course not affected");

        //this instructor already has his own non-null archive status
        //so other instructors' archiving actions will not affect his own status
        instructorId = testData.accounts.get("OtherInstructorWithoutCourses").googleId;
        coursesPage = getCoursesPage();
        coursesPage.verifyHtmlMainContent("/instructorArchiveStatusNotAffected.html");

        ______TS("unarchive action success");

        instructorId = testData.accounts.get("instructorWithCourses").googleId;
        coursesPage = getCoursesPage();

        coursesPage.unarchiveCourse(courseId);
        coursesPage.waitForAjaxLoadCoursesSuccess();
        coursesPage.verifyHtmlMainContent("/instructorCoursesUnarchiveSuccessful.html");

        // TODO: Handling for the failure of archive and unarchive is still not good
        // Need more improvement

        ______TS("archive action failed");
        // only possible if someone else delete the course while the user is viewing the page

        String anotherCourseId = "CCAddUiTest.CS2104";

        BackDoor.deleteCourse(anotherCourseId);
        coursesPage.archiveCourse(anotherCourseId);
        coursesPage.verifyContains("You are not authorized to view this page.");

        ______TS("unarchive action failed");
        // only possible if someone else delete the course while the user is viewing the page

        coursesPage = getCoursesPage();

        coursesPage.archiveCourse(courseId);
        BackDoor.deleteCourse(courseId);
        coursesPage.unarchiveCourse(courseId);
        coursesPage.verifyContains("You are not authorized to view this page.");

    }

    private InstructorCoursesPage getCoursesPage() {
        AppUrl coursesUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE)
                .withUserId(instructorId);
        InstructorCoursesPage page = loginAdminToPage(coursesUrl, InstructorCoursesPage.class);
        page.waitForAjaxLoadCoursesSuccess();
        return page;
    }

}
