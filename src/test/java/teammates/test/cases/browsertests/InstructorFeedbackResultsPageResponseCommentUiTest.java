package teammates.test.cases.browsertests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_RESULTS_PAGE},
 *      specifically for feedback response comments.
 */
public class InstructorFeedbackResultsPageResponseCommentUiTest extends BaseUiTestCase {

    @Override
    protected void prepareTestData() {
        // see prepareData()
    }

    @BeforeMethod
    public void prepareData() {
        testData = loadDataBundle("/InstructorFeedbackResultsPageResponseCommentUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testFeedbackResponseCommentActionsForQuestionsView() {
        InstructorFeedbackResultsPage resultsPage;

        ______TS("Question view: Failure case: add an empty feedback response comment using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(1);

        addEmptyCommentToResponseAndCheckStatusMessage("-2-1-1", resultsPage);

        ______TS("Question view: Typical case: add new feedback response comments using comment modal");

        addCommentToValidResponseAndVerify("-2-1-1", resultsPage);
        addCommentToValidResponseAndVerify("-3-1-1", resultsPage);

        ______TS("Question view: Typical case: edit an existing feedback response comment using comment modal");

        editFirstCommentOnResponseAndVerify("-2-1-1", "edited test comment", resultsPage);

        ______TS("Question view: Typical case: edit comment created by different instructors using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr2", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(2);

        editFirstCommentAndVisibilityAndVerify("-1-1-2", "Comment edited by different instructor",
                resultsPage);

        ______TS("Question view: Typical case: delete existing feedback response comments using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(1);

        deleteFirstCommentAndVerify("-2-1-1", resultsPage);
        deleteFirstCommentAndVerify("-3-1-1", resultsPage);

        ______TS("Question view: Typical case: add edit and delete successively");

        addEditAndDeleteTypicalCommentAndVerify("-2-1-1", resultsPage);
    }

    @Test
    public void testFeedbackResponseCommentActionsForGqrView() {
        InstructorFeedbackResultsPage resultsPage;

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");

        ______TS("GQR view: Failure case: add empty feedback response comment using comment modal");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.loadResultSectionPanel(1, 2);
        addEmptyCommentToResponseAndCheckStatusMessage("-2-0-1", resultsPage);

        ______TS("GQR view: Typical case: add new feedback response comments using comment modal");
        addCommentToValidResponseAndVerify("-2-0-1", resultsPage);
        addCommentToValidResponseAndVerify("-9-0-1", resultsPage);

        ______TS("GQR view: Typical case: edit an existing feedback response comment using comment modal");
        editFirstCommentOnResponseAndVerify("-2-0-1", "edited test comment", resultsPage);

        ______TS("GQR view: Typical case: edit comment created by different instructors using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr2", "Open Session");
        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.loadResultSectionPanel(0, 1);

        editFirstCommentOnResponseAndVerify("-2-0-1", "Comment edited by different instructor",
                resultsPage);

        ______TS("GQR view: Typical case: delete existing feedback response comments using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.loadResultSectionPanel(1, 2);

        deleteFirstCommentAndVerify("-2-0-1", resultsPage);
        deleteFirstCommentAndVerify("-9-0-1", resultsPage);

        ______TS("GQR view: Typical case: add edit and delete successively");

        addEditAndDeleteTypicalCommentAndVerify("-2-0-1", resultsPage);
    }

    @Test
    public void testFeedbackResponseCommentActionsForRqgView() {
        InstructorFeedbackResultsPage resultsPage;

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");

        ______TS("RQG view: Failure case: add empty feedback response comment using comment modal");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.loadResultSectionPanel(1, 2);
        addEmptyCommentToResponseAndCheckStatusMessage("-0-8-1", resultsPage);

        ______TS("RQG view: Typical case: add new feedback response comments using comment modal");
        addCommentToValidResponseAndVerify("-0-8-1", resultsPage);
        addCommentToValidResponseAndVerify("-0-0-7", resultsPage);

        ______TS("RQG view: Typical case: edit an existing feedback response comment using comment modal");
        editFirstCommentOnResponseAndVerify("-0-8-1", "edited test comment", resultsPage);

        ______TS("RQG view: Typical case: edit comment created by different instructors using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr2", "Open Session");
        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.loadResultSectionPanel(0, 1);

        editFirstCommentOnResponseAndVerify("-0-8-1", "Comment edited by different instructor",
                resultsPage);

        ______TS("RQG view: Typical case: delete existing feedback response comments using comment modal");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.loadResultSectionPanel(1, 2);

        deleteFirstCommentAndVerify("-0-8-1", resultsPage);
        deleteFirstCommentAndVerify("-0-0-7", resultsPage);

        ______TS("RQG view: Typical case: add edit and delete successively");

        addEditAndDeleteTypicalCommentAndVerify("-0-8-1", resultsPage);
    }

    @Test
    public void testFeedbackResponseCommentActionsForRgqView() throws Exception {
        InstructorFeedbackResultsPage resultsPage;

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");

        ______TS("RGQ view: Failure case: add empty feedback response comment");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "");
        resultsPage.waitForCommentFormErrorMessageEquals("-0-0-1-1", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);

        ______TS("RGQ view: Typical case: add new feedback response comments");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "test comment 1");
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "test comment 2");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-1-0-1-1", "test comment 1", "Teammates Test");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-1-0-1-2", "test comment 2", "Teammates Test");

        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAddComment.html");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-0-1-1-1", "test comment 1", "Teammates Test");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-0-1-1-2", "test comment 2", "Teammates Test");

        resultsPage.loadResultSectionPanel(1, 2);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-1-1-1-1", "test comment 3");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-1-1-1-1-1", "test comment 3", "Teammates Test");

        ______TS("RGQ view: Typical case: edit existing feedback response comment");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-1-1-1-1-1", "edited test comment");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-1-1-1-1-1",
                "edited test comment", "Teammates Test");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsEditComment.html");

        ______TS("RGQ view: Typical case: edit comment created by different instructor");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-1-0-1-1-1", "Comment edited by different instructor");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-1-0-1-1-1",
                "Comment edited by different instructor", "Teammates Test");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsEditCommentByDifferentInstructor.html");

        ______TS("RGQ view: Typical case: delete existing feedback response comment");

        resultsPage.deleteFeedbackResponseCommentInline("-1-1-1-1-1");
        resultsPage.verifyRowMissing("-1-1-1-1-1");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-0-1-1-2", "test comment 2", "Teammates Test");

        ______TS("RGQ view: Typical case: add edit and delete successively");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "successive action comment");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-1-0-1-3",
                "successive action comment", "Teammates Test");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-0-1-0-1-3", "edited successive action comment");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-1-0-1-3", "edited successive action comment",
                "Teammates Test");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow-0-1-0-1-3", 1);

        resultsPage.deleteFeedbackResponseCommentInline("-0-1-0-1-3");
        resultsPage.verifyRowMissing("-0-0-1-1-3");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded("-0-0-1-1-2", "test comment 2", "Teammates Test");
        resultsPage.verifyRowMissing("-0-0-1-1-3");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsDeleteComment.html");
    }

    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPage(String instructorName, String fsName) {
        AppUrl resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                .withUserId(testData.instructors.get(instructorName).googleId)
                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);
    }

    private void addEmptyCommentToResponseAndCheckStatusMessage(String commentModelId,
            InstructorFeedbackResultsPage resultsPage) {
        // TODO: `clickCommentModalButton` should wait for modal to open before returning
        resultsPage.clickCommentModalButton(commentModelId);
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm" + commentModelId, "");
        // TODO: all instances of this should do an immediate check instead of waiting for the error message
        resultsPage.waitForCommentFormErrorMessageEquals(commentModelId,
                Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
        resultsPage.closeCommentModal(commentModelId);
    }

    // Add first comment to response
    private void addCommentToValidResponseAndVerify(String commentModelId, InstructorFeedbackResultsPage resultsPage) {
        String commentId = commentModelId + "-1";
        resultsPage.clickCommentModalButton(commentModelId);
        // TODO: Wait for comment to be added before returning
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm" + commentModelId,
                "test comment 1");
        // TODO: all instances of this should do an immediate check instead of waiting for the comment to be added.
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded(commentId, "test comment 1", "Teammates Test");
        resultsPage.closeCommentModal(commentModelId);
    }

    private void editFirstCommentOnResponseAndVerify(String commentModelId, String commentText,
            InstructorFeedbackResultsPage resultsPage) {
        String commentId = commentModelId + "-1";
        resultsPage.clickCommentModalButton(commentModelId);
        resultsPage.editFeedbackResponseCommentInOpenedCommentModal(commentId, commentText);
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded(commentId, commentText, "Teammates Test");
        resultsPage.closeCommentModal(commentModelId);
    }

    private void editFirstCommentAndVisibilityAndVerify(String commentModelId, String commentText,
            InstructorFeedbackResultsPage resultsPage) {
        String commentId = commentModelId + "-1";
        resultsPage.clickCommentModalButton(commentModelId);
        resultsPage.editFeedbackResponseCommentInOpenedCommentModal(commentId, commentText);
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded(commentId, commentText, "Teammates Test");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow" + commentId, 1);
        resultsPage.closeCommentModal(commentModelId);
    }

    private void deleteFirstCommentAndVerify(String commentModelId, InstructorFeedbackResultsPage resultsPage) {
        String commentId = commentModelId + "-1";
        resultsPage.clickCommentModalButton(commentModelId);
        resultsPage.deleteFeedbackResponseCommentInModal(commentId);
        resultsPage.verifyRowMissing(commentId);
        resultsPage.closeCommentModal(commentModelId);
    }

    private void addEditAndDeleteTypicalCommentAndVerify(String commentModelId,
            InstructorFeedbackResultsPage resultsPage) {
        String commentId = commentModelId + "-1";
        resultsPage.clickCommentModalButton(commentModelId);
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm" + commentModelId,
                "successive action comment");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded(commentId, "successive action comment", "Teammates Test");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal(commentId, "edited successive action comment");
        resultsPage.waitAndVerifyForFeedbackResponseCommentAdded(commentId, "edited successive action comment",
                "Teammates Test");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow" + commentId, 1);

        resultsPage.deleteFeedbackResponseCommentInModal(commentId);
        resultsPage.closeCommentModal(commentModelId);
    }

}
