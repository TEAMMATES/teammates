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
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * Covers the 'student records' view for instructors.
 */
public class InstructorStudentRecordsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static InstructorStudentRecordsPage viewPage;
    private static DataBundle testDataNormal, testDataQuestionType;

    private static String instructorId;
    private static String courseId;
    private static String studentEmail;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testDataNormal = loadDataBundle("/InstructorStudentRecordsPageUiTest.json");
        testDataQuestionType = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");

        browser = BrowserPool.getBrowser();
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testLinks();
        testScript();
        testAction();
    }

    private void testContent() throws Exception {
        // TODO: Add a full HTML verification check

        InstructorAttributes instructor;
        StudentAttributes student;

        ______TS("content: typical case, normal student records with comments");

        removeAndRestoreTestDataOnServer(testDataNormal);

        instructor = testDataNormal.instructors.get("teammates.test.CS2104");
        student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlAjax("/instructorStudentRecords.html");

        ______TS("content: typical case, normal student records with comments, helper view");

        instructor = testDataNormal.instructors.get("teammates.test.CS2104.Helper");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlAjax("/instructorStudentRecordsWithHelperView.html");

        ______TS("content: normal student records with private feedback session");

        instructor = testDataNormal.instructors.get("teammates.test.CS1101");
        student = testDataNormal.students.get("teammates.test@ISR.CS1101");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlAjax("/instructorStudentRecordsPageWithPrivateFeedback.html");

        ______TS("content: no student records, no profiles");

        instructor = testDataNormal.instructors.get("teammates.noeval");
        student = testDataNormal.students.get("alice.b.tmms@ISR.NoEval");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlAjax("/instructorStudentRecordsPageNoRecords.html");

        ______TS("content: multiple feedback session type student record");

        removeAndRestoreTestDataOnServer(testDataQuestionType);

        instructor = testDataQuestionType.instructors.get("instructor1OfCourse1");
        student = testDataQuestionType.students.get("student1InCourse1");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlAjax("/instructorStudentRecordsPageMixedQuestionType.html");

    }

    private void testLinks() throws Exception {
        // TODO add link to a feedback session
    }

    private void testScript() throws Exception {
        InstructorAttributes instructor = testDataNormal.instructors.get("teammates.test.CS2104");
        StudentAttributes student = testDataNormal.students.get("benny.c.tmms@ISR.CS2104");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();

        ______TS("add comment button");
        viewPage.verifyAddCommentButtonClick();

        ______TS("edit comment button");
        viewPage.verifyEditCommentButtonClick(1);
    }

    private void testAction() throws Exception {

        ______TS("add comment: success");

        viewPage.addComment("New comment from teammates.test for Benny C")
                .verifyStatus("New comment has been added");

        ______TS("delete comment: cancel");

        viewPage.clickDeleteCommentAndCancel(1);

        ______TS("delete comment: success");

        viewPage.clickDeleteCommentAndConfirm(1).verifyStatus("Comment deleted");
        
        ______TS("edit comment then cancel: success");
        
        viewPage.clickEditCommentAndCancel(1);
        viewPage.verifyCommentEditBoxNotVisible(1);

        ______TS("edit comment: success");

        viewPage.editComment(1, "Edited comment 2 from CS2104 teammates.test Instructor to Benny")
                .verifyStatus("Comment edited");

        // Edit back so that restoreDataBundle can identify and delete the comment.
        viewPage.editComment(1, "Comment 2 from ISR.CS2104 teammates.test Instructor to Benny");

    }

    private InstructorStudentRecordsPage getStudentRecordsPage() {
        Url viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                           .withUserId(instructorId)
                           .withCourseId(courseId)
                           .withStudentEmail(studentEmail);
        return loginAdminToPage(browser, viewPageUrl, InstructorStudentRecordsPage.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
