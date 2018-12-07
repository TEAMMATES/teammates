package teammates.test.cases.browsertests;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.test.driver.BackDoor;
import teammates.test.driver.FileHelper;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsEditPage;
import teammates.test.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.test.pageobjects.InstructorSearchPage;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_SEARCH_PAGE}.
 */
public class InstructorSearchPageUiTest extends BaseUiTestCase {
    private InstructorSearchPage searchPage;

    @Override
    protected void prepareTestData() throws Exception {
        testData = loadDataBundle("/InstructorSearchPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);

        // upload a profile picture for one of the students
        StudentAttributes student = testData.students.get("student2InCourse1");
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = JsonUtils.toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
        assertEquals("Unable to upload profile picture", "[BACKDOOR_STATUS_SUCCESS]",
                BackDoor.uploadAndUpdateStudentProfilePicture(student.googleId, pictureData));
    }

    @Test
    public void allTests() throws Exception {

        testContent();
        testSearch();

        testViewAction();
        testEditAction();
        testAllRecordsAction();
        testDeleteAction();

        testSanitization();

    }

    private void testContent() throws Exception {

        ______TS("content: default search page");

        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorId);

        // This is the full HTML verification for Instructor Search Page, the rest can all be verifyMainHtml
        searchPage.verifyHtml("/instructorSearchPageDefault.html");

    }

    private void testSearch() throws Exception {

        ______TS("search for nothing");

        String instructorId = testData.accounts.get("instructor1OfCourse1").googleId;
        String searchContent = "comment";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchNone.html");

        ______TS("search for feedback response comments");

        searchContent = "response comment";
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchFeedbackResponseComments.html");

        ______TS("search for feedback response comments as helper");

        String instructorHelperId = testData.accounts.get("helperOfCourse1").googleId;
        searchPage = getInstructorSearchPage(instructorHelperId);
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickStudentCheckBox();
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchCommentsAsHelper.html");

        searchPage = getInstructorSearchPage(instructorId);

        ______TS("search exact string for students");

        searchPage.clearSearchBox();
        searchContent = "\"student2 2 In Course1\"";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentsForStudent2WithExactString.html");

        ______TS("search for students");

        searchPage.clearSearchBox();
        searchContent = "Course1 In student1";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentsForStudent1.html");
        searchPage.clearSearchBox();
        searchContent = "In student2";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();
        searchPage.clickAndHoverPicture("studentphoto-c0.1");
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchStudentsForStudent2.html");
    }

    private void testViewAction() {

        ______TS("action: view");

        searchPage.clearSearchBox();
        String searchContent = "\"student2 2 In Course1\"";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        String studentName = testData.students.get("student2.2InCourse1").name;
        String studentEmail = testData.students.get("student2.2InCourse1").email;
        String courseId = testData.courses.get("typicalCourse1").getId();

        searchPage.clickViewStudent(courseId, studentName);
        InstructorCourseStudentDetailsViewPage studentDetailsViewPage = searchPage
                .changePageType(InstructorCourseStudentDetailsViewPage.class);
        studentDetailsViewPage.verifyIsCorrectPage(studentEmail);
        studentDetailsViewPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testEditAction() {

        ______TS("action: edit");

        searchPage.clearSearchBox();
        String searchContent = "\"student2 2 In Course1\"";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        String studentName = testData.students.get("student2.2InCourse1").name;
        String studentEmail = testData.students.get("student2.2InCourse1").email;
        String courseId = testData.courses.get("typicalCourse1").getId();

        searchPage.clickEditStudent(courseId, studentName);
        InstructorCourseStudentDetailsEditPage studentDetailsEditPage = searchPage
                .changePageType(InstructorCourseStudentDetailsEditPage.class);
        studentDetailsEditPage.verifyIsCorrectPage(studentEmail);
        studentDetailsEditPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testAllRecordsAction() {

        ______TS("action: all records");

        searchPage.clearSearchBox();
        String searchContent = "\"student2 2 In Course1\"";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickSearchButton();

        String studentName = testData.students.get("student2.2InCourse1").name;
        String courseId = testData.courses.get("typicalCourse1").getId();

        searchPage.clickAllRecordsLink(courseId, studentName);
        InstructorStudentRecordsPage studentRecordsPage = searchPage
                .changePageType(InstructorStudentRecordsPage.class);
        studentRecordsPage.verifyIsCorrectPage(studentName);
        studentRecordsPage.closeCurrentWindowAndSwitchToParentWindow();
    }

    private void testDeleteAction() {

        ______TS("action: delete");

        String studentName = testData.students.get("student2.2InCourse1").name;
        String studentEmail = testData.students.get("student2.2InCourse1").email;
        String courseId = testData.courses.get("typicalCourse1").getId();

        searchPage.clickDeleteAndCancel(courseId, studentName);
        assertNotNull(BackDoor.getStudent(courseId, studentEmail));

        searchPage.clickDeleteAndConfirm(courseId, studentName);
        InstructorCourseDetailsPage courseDetailsPage = searchPage.changePageType(InstructorCourseDetailsPage.class);
        courseDetailsPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.STUDENT_DELETED);
        assertNull(BackDoor.getStudent(courseId, studentEmail));
    }

    private void testSanitization() throws IOException {

        ______TS("action: test sanitization");

        String instructorId = testData.accounts.get("instructor1OfTestingSanitizationCourse").googleId;
        searchPage = getInstructorSearchPage(instructorId);

        String searchContent = "Normal feedback session name";
        searchPage.inputSearchContent(searchContent);
        searchPage.clickFeedbackResponseCommentCheckBox();
        searchPage.clickSearchButton();
        searchPage.verifyHtmlMainContent("/instructorSearchPageSearchTestingSanitization.html");
    }

    private InstructorSearchPage getInstructorSearchPage(String instructorId) {
        AppUrl searchPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE)
                .withUserId(instructorId);

        return loginAdminToPage(searchPageUrl, InstructorSearchPage.class);
    }

}
