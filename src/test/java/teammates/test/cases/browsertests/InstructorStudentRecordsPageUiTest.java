package teammates.test.cases.browsertests;

import java.io.IOException;

import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_STUDENT_RECORDS_PAGE}.
 */
public class InstructorStudentRecordsPageUiTest extends BaseUiTestCase {
    private InstructorStudentRecordsPage viewPage;

    private String instructorId;
    private String courseId;
    private String studentEmail;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentRecordsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testFeedbackResponseCommentEditAndDeleteAction();
        testLinks();
        testPanelsCollapseExpand();
    }

    private void testContent() throws Exception {
        InstructorAttributes instructor;
        StudentAttributes student;

        ______TS("content: typical case, normal student records with comments");

        instructor = testData.instructors.get("teammates.test.CS2104");
        student = testData.students.get("benny.c.tmms@ISR.CS2104");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        // This is the full HTML verification for Instructor Student Records Page, the rest can all be verifyMainHtml
        viewPage.verifyHtml("/instructorStudentRecords.html");

        ______TS("content: typical case, normal student records with comments, helper view");

        instructor = testData.instructors.get("teammates.test.CS2104.Helper");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsWithHelperView.html");

        ______TS("content: normal student records with other instructor's comments, private feedback session");

        instructor = testData.instructors.get("teammates.test.CS1101");
        student = testData.students.get("teammates.test@ISR.CS1101");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageWithPrivateFeedback.html");

        ______TS("content: no student records, no profiles");

        instructor = testData.instructors.get("teammates.noeval");
        student = testData.students.get("alice.b.tmms@ISR.NoEval");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageNoRecords.html");

        ______TS("content: multiple feedback session type student record");

        DataBundle testDataQuestionType = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDataBundle(testDataQuestionType);

        instructor = testDataQuestionType.instructors.get("instructor1OfCourse1");
        student = testDataQuestionType.students.get("student1InCourse1");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageMixedQuestionType.html");

        ______TS("content: profile with attemoted script and html injection with comment");

        instructor = testData.instructors.get("instructor1OfTestingSanitizationCourse");
        student = testData.students.get("student1InTestingSanitizationCourse");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageWithScriptInjectionProfile.html");

    }

    private void testLinks() {
        // TODO add link to a feedback session
    }

    private void testFeedbackResponseCommentEditAndDeleteAction() throws IOException {
        InstructorAttributes instructor;
        StudentAttributes student;

        instructor = testData.instructors.get("teammates.test.CS2104");
        student = testData.students.get("benny.c.tmms@ISR.CS2104");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();

        ______TS("Typical Case: Edit comment created by different instructor");

        viewPage.editFeedbackResponseComment("-RGQ-1-1-1-1",
                "First edited comment to Alice about feedback to Benny from different Instructor");
        viewPage.verifyCommentRowContent("-RGQ-1-1-1-1", "First edited comment to Alice about feedback to Benny from "
                + "different Instructor",
                "Teammates Test");
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageEditedCommentOfDifferentInstructor.html");

        ______TS("Typical Case: Edit comment");

        viewPage.editFeedbackResponseComment("-RGQ-1-1-1-2",
                "Instructor second edited comment to Alice about feedback to Benny");
        viewPage.verifyCommentRowContent("-RGQ-1-1-1-2", "Instructor second edited comment to Alice about feedback to Benny",
                "Teammates Test");
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageEditedComment.html");

        ______TS("Typical Case: Edit and add empty comment");

        viewPage.editFeedbackResponseComment("-RGQ-1-1-1-2", "");
        viewPage.verifyCommentFormErrorMessage(Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
        viewPage.closeEditFeedbackResponseCommentForm("-RGQ-1-1-1-2");

        ______TS("Typical Case: Delete comment");

        viewPage.deleteFeedbackResponseComment("-RGQ-1-1-1-3");
        viewPage.verifyRowMissing("-RGQ-1-1-1-3");
    }

    private InstructorStudentRecordsPage getStudentRecordsPage() {
        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                           .withUserId(instructorId)
                           .withCourseId(courseId)
                           .withStudentEmail(studentEmail);
        return loginAdminToPage(viewPageUrl, InstructorStudentRecordsPage.class);
    }

    private void testPanelsCollapseExpand() {

        DataBundle testDataQuestionType = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        InstructorAttributes instructor = testDataQuestionType.instructors.get("instructor1OfCourse1");
        StudentAttributes student = testDataQuestionType.students.get("student1InCourse1");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();

        ______TS("Typical case: panels expand/collapse");

        viewPage.clickAllRecordPanelHeadings();
        viewPage.waitForPanelsToCollapse();
        assertTrue(viewPage.areRecordsHidden());

        viewPage.clickAllRecordPanelHeadings();
        viewPage.waitForPanelsToExpand();
        assertTrue(viewPage.areRecordsVisible());
    }

}
