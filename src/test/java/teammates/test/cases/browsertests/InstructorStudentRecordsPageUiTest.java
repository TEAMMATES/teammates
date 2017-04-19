package teammates.test.cases.browsertests;

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
        testLinks();
        testScript();
        testVisibilityCheckboxScript();
        testAction();
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

    }

    private void testLinks() {
        // TODO add link to a feedback session
    }

    private void testScript() {
        InstructorAttributes instructor = testData.instructors.get("teammates.test.CS2104");
        StudentAttributes student = testData.students.get("benny.c.tmms@ISR.CS2104");

        instructorId = instructor.googleId;
        courseId = instructor.courseId;
        studentEmail = student.email;

        viewPage = getStudentRecordsPage();

        ______TS("add comment button");
        viewPage.verifyAddCommentButtonClick();

        ______TS("edit comment button");
        viewPage.verifyEditCommentButtonClick(1);
    }

    private void testVisibilityCheckboxScript() {
        viewPage.clickVisibilityOptionsButton(1);

        ______TS("check giver when answer is unchecked");

        viewPage.clickGiverCheckboxForCourse(1);
        assertTrue(viewPage.isAnswerCheckboxForCourseSelected(1));
        assertTrue(viewPage.isGiverCheckboxForCourseSelected(1));
        assertFalse(viewPage.isRecipientCheckboxForCourseSelected(1));

        ______TS("uncheck answer when giver is checked");

        viewPage.clickAnswerCheckboxForCourse(1);
        assertFalse(viewPage.isAnswerCheckboxForCourseSelected(1));
        assertFalse(viewPage.isGiverCheckboxForCourseSelected(1));
        assertFalse(viewPage.isRecipientCheckboxForCourseSelected(1));

        ______TS("check recipient when answer is unchecked");

        viewPage.clickRecipientCheckboxForCourse(1);
        assertTrue(viewPage.isAnswerCheckboxForCourseSelected(1));
        assertFalse(viewPage.isGiverCheckboxForCourseSelected(1));
        assertTrue(viewPage.isRecipientCheckboxForCourseSelected(1));

        ______TS("uncheck answer when recipient is checked");

        viewPage.clickAnswerCheckboxForCourse(1);
        assertFalse(viewPage.isAnswerCheckboxForCourseSelected(1));
        assertFalse(viewPage.isGiverCheckboxForCourseSelected(1));
        assertFalse(viewPage.isRecipientCheckboxForCourseSelected(1));

        viewPage.clickVisibilityOptionsButton(1);
    }

    private void testAction() throws Exception {

        ______TS("add comment: failure (empty comment)");

        viewPage.addComment("").verifyStatus("Please enter a valid comment. The comment can't be empty.");

        ______TS("add comment: success");

        viewPage.addComment("New comment from teammates.test for Benny C")
                .verifyStatus(String.format(Const.StatusMessages.COMMENT_ADDED,
                                            "New comment from teammates.test for Benny C"));

        ______TS("add comment with custom visibility: success");

        viewPage.addCommentWithVisibility("New comment from teammates.test for Benny C, viewable by everyone", 4);
        viewPage.verifyHtmlMainContent("/instructorStudentRecordsPageAddComment.html");

        ______TS("delete comment: cancel");

        viewPage.clickDeleteCommentAndCancel(1);

        ______TS("delete comment: success");

        viewPage.clickDeleteCommentAndConfirm(1).verifyStatus("Comment deleted");

        ______TS("edit comment then cancel: success");

        viewPage.clickEditCommentAndCancel(1);
        viewPage.verifyCommentEditBoxNotVisible(1);

        ______TS("edit comment: success");

        viewPage.editComment(2, "Edited comment 2 from CS2104 teammates.test Instructor to Benny")
                .verifyStatus("Comment edited");

        // Edit back so that restoreDataBundle can identify and delete the comment.
        viewPage.editComment(2, "Comment 2 from ISR.CS2104 teammates.test Instructor to Benny");

        ______TS("edit other instructor's comment: success");

        viewPage.editComment(5, "Edited comment 2 from CS2104 teammates.test.Helper Instructor to Benny, "
                                + "viewable by instructors")
                .verifyStatus("Comment edited");

        ______TS("delete other instructor's comment: success");

        viewPage.clickDeleteCommentAndConfirm(5).verifyStatus("Comment deleted");

    }

    private InstructorStudentRecordsPage getStudentRecordsPage() {
        AppUrl viewPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                           .withUserId(instructorId)
                           .withCourseId(courseId)
                           .withStudentEmail(studentEmail);
        return loginAdminToPage(viewPageUrl, InstructorStudentRecordsPage.class);
    }

    private void testPanelsCollapseExpand() {

        ______TS("Typical case: panels expand/collapse");

        viewPage.clickAllRecordPanelHeadings();
        viewPage.waitForPanelsToCollapse();
        assertTrue(viewPage.areRecordsHidden());

        viewPage.clickAllRecordPanelHeadings();
        viewPage.waitForPanelsToExpand();
        assertTrue(viewPage.areRecordsVisible());
    }

}
