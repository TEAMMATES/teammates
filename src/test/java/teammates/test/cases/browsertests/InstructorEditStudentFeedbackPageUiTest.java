package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.InstructorEditStudentFeedbackPage;

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
        testEditResponse();
        testAddResponse();
        testDeleteResponse();
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

        assertEquals("Student 1 self feedback.", fr.getResponseDetails().getAnswerString());

        submitPage = loginToInstructorEditStudentFeedbackPage("IESFPTCourseinstr", "student1InIESFPTCourse@gmail.tmt",
                                                              "session1InIESFPTCourse");

        // Full HTML verification already done in InstructorFeedbackSubmitPageUiTest
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageOpen.html");

        submitPage.fillResponseRichTextEditor(1, 0, "Good design");

        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

        fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 1);

        fr = BackDoor.getFeedbackResponse(fq.getId(),
                                          "student1InIESFPTCourse@gmail.tmt",
                                          "student1InIESFPTCourse@gmail.tmt");

        assertEquals("<p>Good design</p>", fr.getResponseDetails().getAnswerString());
    }

    private void testAddResponse() throws Exception {
        ______TS("test new response");

        submitPage.fillResponseTextBox(2, 0, "4");
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

        FeedbackQuestionAttributes fq = BackDoor.getFeedbackQuestion("IESFPTCourse", "First feedback session", 2);
        FeedbackResponseAttributes fr = BackDoor.getFeedbackResponse(fq.getId(),
                                          "student1InIESFPTCourse@gmail.tmt",
                                          "student1InIESFPTCourse@gmail.tmt");

        assertEquals("4", fr.getResponseDetails().getAnswerString());

        // Full HTML verification already done in InstructorFeedbackSubmitPageUiTest
        submitPage.verifyHtmlMainContent("/instructorEditStudentFeedbackPageModified.html");
    }

    private void testDeleteResponse() {
        ______TS("test delete response");

        submitPage.fillResponseTextBox(2, 0, "");

        submitPage.fillResponseTextBox(1, 0, "");
        submitPage.clickSubmitButton();

        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

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
