package teammates.test.cases.browsertests;

import java.io.File;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.FileHelper;
import teammates.test.driver.Priority;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.test.pageobjects.InstructorStudentListPage;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_STUDENT_LIST_PAGE}.
 */
@Priority(-1)
public class InstructorStudentListPageUiTest extends BaseUiTestCase {
    private InstructorStudentListPage viewPage;

    @Override
    protected void prepareTestData() throws Exception {
        testData = loadDataBundle("/InstructorStudentListPageUiTest.json");
        removeAndRestoreDataBundle(testData);

        DataBundle studentsOnly = new DataBundle();
        studentsOnly.students = testData.students;
        putDocuments(studentsOnly); // put the search document for students only

        // upload a profile picture for one of the students
        StudentAttributes student = testData.students.get("Student3Course3");
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = JsonUtils.toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
        assertEquals("Unable to upload profile picture", "[BACKDOOR_STATUS_SUCCESS]",
                     BackDoor.uploadAndUpdateStudentProfilePicture(student.googleId, pictureData));
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testLinks();
        testSearch();
        testDeleteAction();
        testSearchScript();
        testDisplayArchive();
        testShowPhoto();
    }

    private void testSearch() throws Exception {

        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        String instructorId = instructorWith2Courses.googleId;

        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        ______TS("content: search no match");
        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.setSearchKey("noMatch");

        viewPage.verifyHtmlMainContent("/instructorStudentListPageSearchNoMatch.html");

        ______TS("content: search student with 1 result");

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.setSearchKey("charlie");
        viewPage.verifyHtmlMainContent("/instructorStudentListPageSearchStudent.html");

        ______TS("content: search student with multiple results");

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.setSearchKey("alice");
        viewPage.verifyHtmlMainContent("/instructorStudentListPageSearchStudentMultiple.html");

    }

    private void testContent() throws Exception {
        String instructorId;

        ______TS("content: 2 course with students");

        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        instructorId = instructorWith2Courses.googleId;

        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        // This is the full HTML verification for Instructor Student List Page, the rest can all be verifyMainHtml
        viewPage.verifyHtml("/instructorStudentListWithHelperView.html");

        // verify copy email functionality
        viewPage.toggleShowEmailCheckbox();
        assertFalse(viewPage.isCopyEmailButtonVisible());
        viewPage.toggleShowEmailCheckbox();
        viewPage.clickCopyEmailButton();
        viewPage.waitForCopyEmailPopoverVisible();
        assertEquals(viewPage.getShownEmailsText(), viewPage.getSelectedText().trim());

        // update current instructor privileges
        BackDoor.deleteInstructor(instructorWith2Courses.courseId, instructorWith2Courses.email);
        instructorWith2Courses.privileges.setDefaultPrivilegesForCoowner();
        BackDoor.createInstructor(instructorWith2Courses);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.verifyHtmlMainContent("/instructorStudentList.html");

        ______TS("content: 1 course with no students");

        instructorId = testData.instructors.get("instructorOfCourse1").googleId;

        viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageNoStudent.html");

        ______TS("content: no course");

        instructorId = testData.accounts.get("instructorWithoutCourses").googleId;

        viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageNoCourse.html");

        ______TS("content: data required sanitization");

        instructorId = testData.accounts.get("instructor1OfTestingSanitizationCourse").googleId;

        viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageTestingSanitization.html");
    }

    private void testShowPhoto() throws Exception {
        // Mouseover actions do not work on Selenium-Chrome
        if ("chrome".equals(TestProperties.BROWSER)) {
            return;
        }

        if (System.getenv("APPVEYOR") != null) {
            // The following photo-related tests somehow does not work in AppVeyor environment.
            // This should not be the same "bug" as the Chrome-related above as similar tests in
            // InstructorFeedbackResultsPageUiTest still pass.
            // TODO fix this
            return;
        }

        String instructorId = testData.instructors.get("instructorOfCourse2").googleId;
        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);

        ______TS("default image");

        StudentAttributes student = testData.students.get("Student1Course2");
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);

        viewPage.clickShowPhoto(student.course, student.name);
        viewPage.verifyProfilePhoto(student.course, student.name, Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH);

        ______TS("student has uploaded an image");

        StudentAttributes student2 = testData.students.get("Student3Course3");
        viewPage.clickShowPhoto(student2.course, student2.name);
        String photoUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PICTURE)
                                        .withStudentEmail(StringHelper.encrypt(student2.email))
                                        .withCourseId(StringHelper.encrypt(student2.course))
                                        .withUserId(instructorId)
                                        .toAbsoluteString();
        viewPage.verifyProfilePhoto(student2.course, student2.name, photoUrl);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageWithPicture.html");
    }

    private void testLinks() {

        String instructorId = testData.instructors.get("instructorOfCourse2").googleId;
        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);

        ______TS("link: enroll");
        String courseId = testData.courses.get("course2").getId();
        InstructorCourseEnrollPage enrollPage = viewPage.clickEnrollStudents(courseId);
        enrollPage.verifyIsCorrectPage(courseId);
        viewPage = enrollPage.goToPreviousPage(InstructorStudentListPage.class);

        ______TS("link: view");

        StudentAttributes student1 = testData.students.get("Student2Course2");
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        InstructorCourseStudentDetailsViewPage studentDetailsPage = viewPage.clickViewStudent(student1.course,
                                                                                              student1.name);
        studentDetailsPage.verifyIsCorrectPage(student1.email);
        studentDetailsPage.closeCurrentWindowAndSwitchToParentWindow();
        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);

        ______TS("link: edit");

        StudentAttributes student2 = testData.students.get("Student3Course3");
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        InstructorCourseStudentDetailsEditPage studentEditPage = viewPage.clickEditStudent(student2.course,
                                                                                           student2.name);
        studentEditPage.verifyIsCorrectPage(student2.email);
        studentEditPage.submitButtonClicked();
        studentEditPage.closeCurrentWindowAndSwitchToParentWindow();
        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);

        ______TS("link: view records");

        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        InstructorStudentRecordsPage studentRecordsPage = viewPage.clickViewRecordsStudent(student2.course,
                                                                                           student2.name);
        studentRecordsPage.verifyIsCorrectPage(student2.name);
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();
        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
    }

    private void testDeleteAction() {
        InstructorAttributes instructorWith2Courses = testData.instructors.get("instructorOfCourse2");
        String instructorId = instructorWith2Courses.googleId;

        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);

        ______TS("action: delete");

        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        ThreadHelper.waitFor(500);
        String studentName = testData.students.get("Student2Course2").name;
        String studentEmail = testData.students.get("Student2Course2").email;
        String courseId = testData.courses.get("course2").getId();

        viewPage.clickDeleteAndCancel(courseId, studentName);
        assertNotNull(BackDoor.getStudent(courseId, studentEmail));

        String expectedStatus = "The student has been removed from the course";
        viewPage.clickDeleteAndConfirm(courseId, studentName);
        InstructorCourseDetailsPage courseDetailsPage = viewPage.changePageType(InstructorCourseDetailsPage.class);
        courseDetailsPage.waitForTextsForAllStatusMessagesToUserEquals(expectedStatus);
    }

    private void testSearchScript() {
        // already covered under testContent()
        // ______TS("content: search active")
    }

    private void testDisplayArchive() throws Exception {
        String instructorId = testData.instructors.get("instructorOfCourse4").googleId;
        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE).withUserId(instructorId);
        viewPage = loginAdminToPage(viewPageUrl, InstructorStudentListPage.class);

        ______TS("action: display archive");

        viewPage.clickDisplayArchiveOptions();
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.checkCourse(2);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageDisplayArchivedCourses.html");

        ______TS("action: hide archive");

        viewPage.clickDisplayArchiveOptions();
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageHideArchivedCourses.html");

        ______TS("action: re-display archive");

        viewPage.clickDisplayArchiveOptions();
        viewPage.checkCourse(0);
        viewPage.checkCourse(1);
        viewPage.checkCourse(2);
        viewPage.verifyHtmlMainContent("/instructorStudentListPageDisplayArchivedCourses.html");
    }

}
