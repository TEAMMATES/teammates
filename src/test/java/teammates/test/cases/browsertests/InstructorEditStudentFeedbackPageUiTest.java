package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorEditStudentFeedbackPage;

import java.io.IOException;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE}.
 */
public class InstructorEditStudentFeedbackPageUiTest extends BaseUiTestCase {

    private InstructorEditStudentFeedbackPage submitPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorEditStudentFeedbackPageTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() throws Exception {
        testModerationHint();
        testAddCommentsWithoutResponse();
        testEditResponse();
        testAddResponse();
        testAddCommentsToResponses();
        testEditCommentsActionAfterAddingComments();
        testDeleteCommentsActionAfterEditingComments();
        testDeleteResponse();
    }

    private void testAddCommentsToResponses() throws IOException {
        ______TS("add new comments on questions with responses and verify add comments without responses action");

        submitPage = loginToInstructorEditStudentFeedbackPage(
                "IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt", "session1InIESFPTCourse");
        submitPage.waitForPageToLoad();
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageModifiedNoComments.html");

        submitPage.addFeedbackResponseComment("-0-1-3", "New MCQ Comment 1");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.verifyAndCloseSuccessfulSubmissionModal();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
    }

    private void testAddCommentsWithoutResponse() {
        ______TS("add comments on questions without responses: no effect");

        logout();
        submitPage = loginToInstructorEditStudentFeedbackPage(
                "IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt", "session1InIESFPTCourse");
        submitPage.waitForPageToLoad();

        submitPage.addFeedbackResponseComment("-0-1-3", "Comment without response");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.verifyAndCloseSuccessfulSubmissionModal();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
    }

    private void testModerationHint() throws Exception {
        ______TS("verify moderation hint");

        submitPage = loginToInstructorEditStudentFeedbackPage("IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt",
                "session1InIESFPTCourse");

        submitPage.verifyModerationHeaderHtml("/instructorEditStudentFeedbackHint.html");

        submitPage.clickModerationHintButton();
        assertTrue(submitPage.isModerationHintVisible());
        assertEquals("[Less]", submitPage.getModerationHintButtonText());

        submitPage.clickModerationHintButton();
        assertFalse(submitPage.isModerationHintVisible());
        assertEquals("[More]", submitPage.getModerationHintButtonText());
    }

    private void testEditResponse() throws Exception {
        ______TS("edit responses");

        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 1);
        FeedbackResponseAttributes fr = BackDoor.getFeedbackResponse(fq.getId(),
                                                  "student1InIESFPTCourse@gmail.tmt",
                                                  "student1InIESFPTCourse@gmail.tmt");

        assertEquals("<p>Student 1 self feedback.</p>", fr.getResponseDetails().getAnswerString());

        submitPage = loginToInstructorEditStudentFeedbackPage("IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt",
                                                              "session1InIESFPTCourse");

        // Full HTML verification already done in InstructorFeedbackSubmitPageUiTest
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageOpen.html");

        submitPage.fillResponseRichTextEditor(1, 0, "Good design");

        submitPage.clickSubmitButton();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        submitPage.verifyAndCloseSuccessfulSubmissionModal();

        fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 1);

        fr = BackDoor.getFeedbackResponse(fq.getId(),
                                          "student1InIESFPTCourse@gmail.tmt",
                                          "student1InIESFPTCourse@gmail.tmt");

        assertEquals("<p>Good design</p>", fr.getResponseDetails().getAnswerString());
    }

    private void testAddResponse() throws Exception {
        ______TS("test new response");

        submitPage.fillResponseTextBox(2, 0, "4");
        submitPage.chooseMcqOption(3, 0, "It's good");
        submitPage.clickSubmitButton();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 2);
        FeedbackQuestionAttributes fqMcq =
                BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 6);
        FeedbackResponseAttributes fr = BackDoor.getFeedbackResponse(fq.getId(),
                                          "student1InIESFPTCourse@gmail.tmt",
                                          "student1InIESFPTCourse@gmail.tmt");
        FeedbackResponseAttributes frMcq = BackDoor.getFeedbackResponse(fqMcq.getId(),
                "student1InIESFPTCourse@gmail.tmt", "student1InIESFPTCourse@gmail.tmt");

        assertEquals("4", fr.getResponseDetails().getAnswerString());
        assertNotNull(frMcq);
        // Full HTML verification already done in InstructorFeedbackSubmitPageUiTest
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageModified.html");
        submitPage.verifyAndCloseSuccessfulSubmissionModal();
    }

    private void testEditCommentsActionAfterAddingComments() throws IOException {
        ______TS("edit comments on responses and verify added comments action");

        submitPage = loginToInstructorEditStudentFeedbackPage(
                "IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt", "session1InIESFPTCourse");
        submitPage.waitForPageToLoad();
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageModifiedAddedCommentOnResponses.html");

        submitPage.editFeedbackResponseComment("-0-1-3-1", "Edited MCQ Comment 1");

        submitPage.submitWithoutConfirmationEmail();
        submitPage.verifyAndCloseSuccessfulSubmissionModal();
        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
    }

    private void testDeleteCommentsActionAfterEditingComments() throws IOException {
        ______TS("delete comments on responses and verify edited comments action");

        submitPage = loginToInstructorEditStudentFeedbackPage(
                "IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt", "session1InIESFPTCourse");
        submitPage.waitForPageToLoad();
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageModifiedEditedComments.html");

        submitPage.deleteFeedbackResponseComment("-0-1-3-1");
        submitPage.verifyRowMissing("-0-1-3-1");
    }

    private void testDeleteResponse() {
        ______TS("test delete response");

        submitPage.fillResponseTextBox(2, 0, "");

        submitPage.fillResponseTextBox(1, 0, "");
        submitPage.clickSubmitButton();

        submitPage.waitForTextsForAllStatusMessagesToUserEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);
        submitPage.verifyAndCloseSuccessfulSubmissionModal();

        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 1);
        FeedbackResponseAttributes fr = BackDoor.getFeedbackResponse(fq.getId(),
                                          "student1InIESFPTCourse@gmail.tmt",
                                          "student1InIESFPTCourse@gmail.tmt");
        assertNull(fr);
        fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 2);
        fr = BackDoor.getFeedbackResponse(fq.getId(),
                                          "student1InIESFPTCourse@gmail.tmt",
                                          "student1InIESFPTCourse@gmail.tmt");
        assertNull(fr);
    }

    private InstructorEditStudentFeedbackPage loginToInstructorEditStudentFeedbackPage(
            String instructorName, String moderatedStudentEmail, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE)
                .withUserId(testData.instructors.get(instructorName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName())
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail);

        return loginAdminToPage(editUrl, InstructorEditStudentFeedbackPage.class);
    }

}
