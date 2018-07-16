package teammates.test.cases.browsertests;

import java.io.IOException;

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
        testData = loadDataBundle("/InstructorFeedbackResultsPageResponseCommentUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void feedbackResponseCommentActions() throws IOException {

        InstructorFeedbackResultsPage resultsPage;

        ______TS("Failure case: add empty feedback response comment using comment modal in question view");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(1);

        // TODO: `clickCommentModalButton` should wait for modal to open before returning
        resultsPage.clickCommentModalButton("-2-1-0");
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm-2-1-0", "");
        // TODO: all instances of this should do an immediate check instead of waiting for the error message
        resultsPage.waitForCommentFormErrorMessageEquals("-2-1-0", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
        resultsPage.closeCommentModal("-2-1-0");

        ______TS("Typical case: add new feedback response comments using comment modal in questions's view");

        resultsPage.clickCommentModalButton("-2-1-0");
        // TODO: Wait for comment to be added before returning
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm-2-1-0", "test comment 1");
        // TODO: all instances of this should do an immediate check instead of waiting for the comment to be added.
        resultsPage.waitForFeedbackResponseCommentAdded("-2-1-0-1", "test comment 1", "Teammates Test");
        resultsPage.closeCommentModal("-2-1-0");

        resultsPage.clickCommentModalButton("-3-1-0");
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm-3-1-0", "test comment 2");
        resultsPage.waitForFeedbackResponseCommentAdded("-3-1-0-1", "test comment 2", "Teammates Test");
        resultsPage.closeCommentModal("-3-1-0");

        ______TS("Typical case: edit existing feedback response comment using comment modal in questions's view");

        resultsPage.clickCommentModalButton("-2-1-0");
        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-2-1-0-1", "edited test comment");
        resultsPage.waitForFeedbackResponseCommentAdded("-2-1-0-1", "edited test comment", "Teammates Test");
        resultsPage.closeCommentModal("-2-1-0");

        ______TS("Typical case: edit comment created by different instructor using comment modal in questions's view");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(2);

        resultsPage.clickCommentModalButton("-1-1-0");
        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-1-1-0-1", "Comment edited by different instructor");
        resultsPage.waitForFeedbackResponseCommentAdded("-1-1-0-1",
                "Comment edited by different instructor", "Teammates Test");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow-1-1-0-1", 1);
        resultsPage.closeCommentModal("-1-1-0");

        ______TS("Typical case: delete existing feedback response comments using comment modal in questions's view");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(1);

        resultsPage.clickCommentModalButton("-3-1-0");
        resultsPage.deleteFeedbackResponseCommentInModal("-3-1-0-1");
        resultsPage.verifyRowMissing("-3-1-0-1");
        resultsPage.closeCommentModal("-3-1-0");

        resultsPage.clickCommentModalButton("-2-1-0");
        resultsPage.deleteFeedbackResponseCommentInModal("-2-1-0-1");
        resultsPage.verifyRowMissing("-2-1-0-1");
        resultsPage.closeCommentModal("-2-1-0");

        ______TS("Typical case: add edit and delete successively");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        resultsPage.loadResultQuestionPanel(1);

        resultsPage.clickCommentModalButton("-2-1-0");
        resultsPage.addFeedbackResponseCommentInCommentModal("showResponseCommentAddForm-2-1-0",
                "successive action comment");
        resultsPage.waitForFeedbackResponseCommentAdded("-2-1-0-1", "successive action comment", "Teammates Test");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-2-1-0-1", "edited successive action comment");
        resultsPage.waitForFeedbackResponseCommentAdded("-2-1-0-1", "edited successive action comment",
                "Teammates Test");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow-2-1-0-1", 1);

        resultsPage.deleteFeedbackResponseCommentInModal("-2-1-0-1");
        resultsPage.closeCommentModal("-2-1-0");

        ______TS("Failure case: add empty feedback response comment");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "");
        resultsPage.waitForCommentFormErrorMessageEquals("-0-0-1-1", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);

        ______TS("Typical case: add new feedback response comments");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "test comment 1");
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "test comment 2");
        resultsPage.waitForFeedbackResponseCommentAdded("-0-1-0-1-1", "test comment 1", "Teammates Test");
        resultsPage.waitForFeedbackResponseCommentAdded("-0-1-0-1-2", "test comment 2", "Teammates Test");

        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAddComment.html");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.waitForFeedbackResponseCommentAdded("-0-0-1-1-1", "test comment 1", "Teammates Test");
        resultsPage.waitForFeedbackResponseCommentAdded("-0-0-1-1-2", "test comment 2", "Teammates Test");

        resultsPage.loadResultSectionPanel(1, 2);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-1-1-1-1", "test comment 3");
        resultsPage.waitForFeedbackResponseCommentAdded("-1-1-1-1-1", "test comment 3", "Teammates Test");

        ______TS("Typical case: edit existing feedback response comment");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-1-1-1-1-1", "edited test comment");
        resultsPage.waitForFeedbackResponseCommentAdded("-1-1-1-1-1", "edited test comment", "Teammates Test");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsEditComment.html");

        ______TS("Typical case: edit comment created by different instructor");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-1-0-1-1-1", "Comment edited by different instructor");
        resultsPage.waitForFeedbackResponseCommentAdded("-1-0-1-1-1",
                "Comment edited by different instructor", "Teammates Test");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsEditCommentByDifferentInstructor.html");

        ______TS("Typical case: delete existing feedback response comment");

        resultsPage.deleteFeedbackResponseCommentInline("-1-1-1-1-1");
        resultsPage.verifyRowMissing("-1-1-1-1-1");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.waitForFeedbackResponseCommentAdded("-0-0-1-1-2", "test comment 2", "Teammates Test");

        ______TS("Typical case: add edit and delete successively");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "successive action comment");
        resultsPage.waitForFeedbackResponseCommentAdded("-0-1-0-1-3", "successive action comment", "Teammates Test");

        resultsPage.editFeedbackResponseCommentInOpenedCommentModal("-0-1-0-1-3", "edited successive action comment");
        resultsPage.waitForFeedbackResponseCommentAdded("-0-1-0-1-3", "edited successive action comment",
                "Teammates Test");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow-0-1-0-1-3", 1);

        resultsPage.deleteFeedbackResponseCommentInline("-0-1-0-1-3");
        resultsPage.verifyRowMissing("-0-0-1-1-3");

        resultsPage = loginToInstructorFeedbackResultsPage("IFRResponseCommentUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.loadResultSectionPanel(0, 1);
        resultsPage.waitForFeedbackResponseCommentAdded("-0-0-1-1-2", "test comment 2", "Teammates Test");
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
}
