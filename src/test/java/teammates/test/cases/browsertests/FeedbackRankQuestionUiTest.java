package teammates.test.cases.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackRankOptionsQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_FEEDBACK_EDIT_PAGE},
 *      specifically for rank questions.
 */
public class FeedbackRankQuestionUiTest extends FeedbackQuestionUiTest {
    private InstructorFeedbackEditPage feedbackEditPage;

    private String instructorCourseId;
    private String instructorEditFsName;
    private String instructorId;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackRankQuestionUiTest.json");
        removeAndRestoreDataBundle(testData);

        instructorId = testData.accounts.get("instructor1").googleId;
        instructorCourseId = testData.courses.get("course").getId();
        instructorEditFsName = testData.feedbackSessions.get("edit").getFeedbackSessionName();
    }

    @Test
    public void testStudentSubmitAndResultsPages() throws Exception {
        ______TS("Rank submission: input disabled for closed session");

        FeedbackSubmitPage submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankUiT.CS4221", "closed");
        submitPage.waitForAndDismissAlertModal();
        assertTrue(submitPage.isElementVisible(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0-0"));
        assertFalse(submitPage.isElementEnabled(Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0-0"));
        ______TS("Rank submission");

        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankUiT.CS4221", "student");

        // verifies that question submit form without existing responses loads correctly
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageRank.html");

        submitPage.selectResponseTextDropdown(1, 0, 0, "1");
        submitPage.selectResponseTextDropdown(1, 0, 1, "2");

        // to check if deleting responses work
        submitPage.selectResponseTextDropdown(1, 1, 1, "");
        assertEquals("Please rank the above options.", submitPage.getRankMessage(1, 1));

        submitPage.selectResponseTextDropdown(2, 0, 0, "1");
        submitPage.selectResponseTextDropdown(2, 1, 0, "2");

        // test rank messages
        assertEquals("Please rank the above recipients.", submitPage.getRankMessage(5, 3));
        submitPage.selectResponseTextDropdown(5, 0, 0, "2");
        assertTrue(submitPage.getRankMessage(5, 0).isEmpty());
        submitPage.selectResponseTextDropdown(5, 1, 0, "2");
        assertEquals("The same rank should not be given multiple times.", submitPage.getRankMessage(5, 3));

        // try to submit with error
        submitPage.clickSubmitButton();
        submitPage.verifyStatus("Please fix the error(s) for rank question(s) 5. "
                                + "To skip a rank question, leave all the boxes blank.");

        submitPage.selectResponseTextDropdown(5, 1, 0, "1");
        assertEquals("Please rank the above recipients.", submitPage.getRankMessage(5, 3));

        // Submit
        submitPage.clickSubmitButton();
        submitPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED);

        FeedbackQuestionAttributes fq1 = BackDoor.getFeedbackQuestion("FRankUiT.CS4221", "Student Session", 1);
        assertNotNull(BackDoor.getFeedbackResponse(fq1.getId(),
                                        "alice.b.tmms@gmail.tmt",
                                        "tmms.helper1@gmail.tmt"));
        assertNull(BackDoor.getFeedbackResponse(fq1.getId(),
                                        "alice.b.tmms@gmail.tmt",
                                        "tmms.test@gmail.tmt"));

        FeedbackQuestionAttributes fq2 = BackDoor.getFeedbackQuestion("FRankUiT.CS4221", "Student Session", 2);
        assertNotNull(BackDoor.getFeedbackResponse(fq2.getId(),
                                        "alice.b.tmms@gmail.tmt",
                                        "tmms.helper1@gmail.tmt"));
        assertNotNull(BackDoor.getFeedbackResponse(fq2.getId(),
                                        "alice.b.tmms@gmail.tmt",
                                        "tmms.test@gmail.tmt"));

        // Go back to submission page and verify html
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankUiT.CS4221", "student");

        // try to submit duplicate ranks for question that permits it
        submitPage.selectResponseTextDropdown(6, 0, 0, "1");
        submitPage.selectResponseTextDropdown(6, 1, 0, "1");
        assertTrue("No error message expected", submitPage.getRankMessage(6, 0).isEmpty());

        submitPage.selectResponseTextDropdown(1, 0, 0, "3");
        assertEquals("The display message indicating this is a rank question is displayed until all options are ranked",
                      "Please rank the above options.", submitPage.getRankMessage(1, 0));
        submitPage.selectResponseTextDropdown(1, 0, 1, "3");
        submitPage.selectResponseTextDropdown(1, 0, 2, "1");
        submitPage.selectResponseTextDropdown(1, 0, 3, "4");
        assertTrue("No error message expected", submitPage.getRankMessage(1, 0).isEmpty());

        submitPage.selectResponseTextDropdown(7, 0, 0, "4");
        submitPage.selectResponseTextDropdown(7, 0, 1, "3");
        submitPage.selectResponseTextDropdown(7, 0, 2, "2");
        submitPage.selectResponseTextDropdown(7, 0, 3, "1");
        assertTrue("No error message expected", submitPage.getRankMessage(7, 0).isEmpty());

        submitPage.clickSubmitButton();
        // Go back to submission page
        submitPage = loginToStudentFeedbackSubmitPage("alice.tmms@FRankUiT.CS4221", "student");
        // to verify that the question submit form with existing response works correctly
        submitPage.verifyHtmlMainContent("/studentFeedbackSubmitPageSuccessRank.html");

        ______TS("Rank : student results");

        StudentFeedbackResultsPage studentResultsPage =
                loginToStudentFeedbackResultsPage("alice.tmms@FRankUiT.CS4221", "student");
        studentResultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageRank.html");

    }

    @Test
    public void testInstructorSubmitAndResultsPage() throws Exception {

        ______TS("Rank submission: input disabled for closed session");

        FeedbackSubmitPage submitPage = loginToInstructorFeedbackSubmitPage("instructor1", "closed");
        int qnNumber = 1;
        int responseNumber = 0;
        int rowNumber = 0;
        assertFalse(submitPage.isNamedElementEnabled(Const.ParamsNames.FEEDBACK_QUESTION_RANKOPTION + "-"
                                                     + qnNumber + "-" + responseNumber + "-" + rowNumber));

        ______TS("Rank submission: test submission page if some students are not visible to the instructor");
        submitPage = loginToInstructorFeedbackSubmitPage("instructorhelper", "instructor");
        submitPage.verifyHtmlMainContent("/instructorFeedbackSubmitPageRankHelper.html");

        ______TS("Rank standard submission");

        submitPage = loginToInstructorFeedbackSubmitPage("instructor1", "instructor");
        submitPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankSubmission.html");

        submitPage.selectResponseTextDropdown(1, 0, 2, "2");
        submitPage.selectResponseTextDropdown(1, 0, 1, "1");
        submitPage.selectResponseTextDropdown(1, 0, 0, "3");
        assertTrue(submitPage.getRankMessage(1, 0).isEmpty());

        submitPage.selectRecipient(2, 0, "Emily F.");
        submitPage.selectResponseTextDropdown(2, 0, 13, "1");
        submitPage.selectResponseTextDropdown(2, 0, 1, "2");

        submitPage.selectRecipient(2, 1, "Alice Betsy</option></td></div>'\"");
        submitPage.selectResponseTextDropdown(2, 1, 11, "1");
        submitPage.selectResponseTextDropdown(2, 1, 1, "1");
        assertEquals("Testing duplicate rank for rank options",
                     "The same rank should not be given multiple times.", submitPage.getRankMessage(2, 1));
        submitPage.selectResponseTextDropdown(2, 1, 1, "2");

        submitPage.selectResponseTextDropdown(3, 0, 0, "1");
        submitPage.selectResponseTextDropdown(3, 3, 0, "2");

        submitPage.clickSubmitButton();

        ______TS("Rank instructor results : question");

        InstructorFeedbackResultsPage instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("instructor1", "instructor", false, "question");
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-1", "ajax_auto");
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankQuestionView.html");

        ______TS("Rank instructor results : Giver > Recipient > Question");
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("instructor1", "instructor", false,
                                                                 "giver-recipient-question");
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-section-1-2", "ajax_auto");
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankGRQView.html");

        ______TS("Rank instructor results : Giver > Question > Recipient");
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("instructor1", "instructor", false,
                                                                 "giver-question-recipient");
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-section-1-2", "ajax_auto");
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankGQRView.html");

        ______TS("Rank instructor results : Recipient > Question > Giver ");
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("instructor1", "instructor", false,
                                                                 "recipient-question-giver");
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-section-1-2", "ajax_auto");
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankRQGView.html");

        ______TS("Rank instructor results : Recipient > Giver > Question");
        instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("instructor1", "instructor", false,
                                                                 "recipient-giver-question");
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-section-1-2", "ajax_auto");
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankRGQView.html");
    }

    @Test
    public void testInstructorResultsPageForRankRecipientQuestion() throws Exception {
        ______TS("Rank recipient instructor results : question");

        InstructorFeedbackResultsPage instructorResultsPage =
                loginToInstructorFeedbackResultsPageWithViewType("instructor1", "student", false, null);
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-3", "ajax_auto");
        clickAjaxLoadedPanelAndWaitForExpansion(instructorResultsPage, "panelHeading-9", "ajax_auto");
        instructorResultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageRankRecipient.html");
    }

    @Test
    public void testEditPage() throws Exception {
        feedbackEditPage = getFeedbackEditPage();
        testNewQuestionFrame();
        testInputValidation();
        testCustomizeOptions();
        testAddQuestionAction();
        testEditQuestionAction();
        testDeleteQuestionAction();
    }

    @Override
    public void testNewQuestionFrame() {
        testNewRankRecipientsQuestionFrame();
        feedbackEditPage.reloadPage();
        testNewRankOptionsQuestionFrame();
    }

    private void testNewRankRecipientsQuestionFrame() {
        ______TS("Rank recipients: new question (frame)");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RANK_RECIPIENTS");
        assertTrue(feedbackEditPage.verifyNewRankRecipientsQuestionFormIsDisplayed());
    }

    private void testNewRankOptionsQuestionFrame() {
        ______TS("Rank options: new question (frame)");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RANK_OPTIONS");
        assertTrue(feedbackEditPage.verifyNewRankOptionsQuestionFormIsDisplayed());
    }

    @Override
    public void testInputValidation() {

        ______TS("Rank edit: empty question text");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_TEXTINVALID);
    }

    @Override
    public void testCustomizeOptions() {
        // todo
    }

    @Override
    public void testAddQuestionAction() throws Exception {
        ______TS("Rank edit: add rank option question action success");

        assertNull(BackDoor.getFeedbackQuestion(instructorCourseId, instructorEditFsName, 1));

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Rank qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");
        feedbackEditPage.fillRankOptionForNewQuestion(0, "Option 1 <>");

        assertEquals(2, feedbackEditPage.getNumOfOptionsInRankOptionsForNewQuestion());
        // try to submit with insufficient non-blank option
        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(FeedbackRankOptionsQuestionDetails.ERROR_NOT_ENOUGH_OPTIONS
                                      + FeedbackRankOptionsQuestionDetails.MIN_NUM_OF_OPTIONS + ".");

        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RANK_OPTIONS");

        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Rank qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        // blank option at the start and end, to check they are removed
        feedbackEditPage.clickAddMoreRankOptionLinkForNewQuestion();
        feedbackEditPage.fillRankOptionForNewQuestion(1, "Option 1 <>");
        feedbackEditPage.fillRankOptionForNewQuestion(2, "  Option 2  ");
        feedbackEditPage.clickAddMoreRankOptionLinkForNewQuestion();
        assertEquals(4, feedbackEditPage.getNumOfOptionsInRankOptionsForNewQuestion());

        feedbackEditPage.tickDuplicatesAllowedCheckboxForNewQuestion();

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(instructorCourseId, instructorEditFsName, 1));

        assertEquals("Blank options should have been removed", 2, feedbackEditPage.getNumOfOptionsInRankOptions(1));

        ______TS("Rank edit: add rank recipient question action success");
        feedbackEditPage.clickNewQuestionButton();
        feedbackEditPage.selectNewQuestionType("RANK_RECIPIENTS");

        assertNull(BackDoor.getFeedbackQuestion(instructorCourseId, instructorEditFsName, 2));

        feedbackEditPage.verifyRankOptionIsHiddenForNewQuestion(0);
        feedbackEditPage.verifyRankOptionIsHiddenForNewQuestion(1);
        feedbackEditPage.fillQuestionTextBoxForNewQuestion("Rank recipients qn");
        feedbackEditPage.fillQuestionDescriptionForNewQuestion("more details");

        feedbackEditPage.clickAddQuestionButton();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_ADDED);
        assertNotNull(BackDoor.getFeedbackQuestion(instructorCourseId, instructorEditFsName, 2));

        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRankQuestionAddSuccess.html");
    }

    @Override
    public void testEditQuestionAction() throws Exception {
        ______TS("rank edit: edit rank options question success");
        feedbackEditPage.clickEditQuestionButton(1);

        // Verify that fields are editable
        feedbackEditPage.verifyHtmlPart(By.id("questionTable-1"),
                                        "/instructorFeedbackRankQuestionEdit.html");

        feedbackEditPage.fillQuestionTextBox("edited Rank qn text", 1);
        feedbackEditPage.fillQuestionDescription("more details", 1);

        feedbackEditPage.clickRemoveRankOptionLink(1, 0);
        assertEquals("Should still remain with 2 options,"
                         + "less than 2 options should not be permitted",
                     2, feedbackEditPage.getNumOfOptionsInRankOptions(1));

        feedbackEditPage.fillRankOption(1, 1, " (Edited) Option 2 ");

        // Should end up with 4 choices, including (1) and (2)
        feedbackEditPage.clickAddMoreRankOptionLink(1);
        feedbackEditPage.clickAddMoreRankOptionLink(1);
        feedbackEditPage.fillRankOption(1, 2, "  <New> Option 3 ");
        feedbackEditPage.fillRankOption(1, 3, "Option 4 (slightly longer text for this one)");

        feedbackEditPage.untickDuplicatesAllowedCheckbox(1);

        feedbackEditPage.clickSaveExistingQuestionButton(1);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        feedbackEditPage.verifyHtmlMainContent("/instructorFeedbackRankQuestionEditSuccess.html");

        ______TS("rank edit: edit rank recipients question success");
        feedbackEditPage.clickEditQuestionButton(2);

        feedbackEditPage.tickDuplicatesAllowedCheckbox(2);
        feedbackEditPage.clickSaveExistingQuestionButton(2);
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_EDITED);
        assertTrue(feedbackEditPage.isRankDuplicatesAllowedChecked(2));
    }

    @Override
    public void testDeleteQuestionAction() {
        ______TS("rank: qn delete");

        feedbackEditPage.clickDeleteQuestionLink(2);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(instructorCourseId, instructorEditFsName, 2));

        feedbackEditPage.clickDeleteQuestionLink(1);
        feedbackEditPage.waitForConfirmationModalAndClickOk();
        feedbackEditPage.verifyStatus(Const.StatusMessages.FEEDBACK_QUESTION_DELETED);
        assertNull(BackDoor.getFeedbackQuestion(instructorCourseId, instructorEditFsName, 1));
    }

    private InstructorFeedbackEditPage getFeedbackEditPage() {
        AppUrl feedbackPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE)
                        .withUserId(instructorId)
                        .withCourseId(instructorCourseId)
                        .withSessionName(instructorEditFsName)
                        .withEnableSessionEditDetails(true);
        return loginAdminToPage(feedbackPageLink, InstructorFeedbackEditPage.class);
    }

    private FeedbackSubmitPage loginToInstructorFeedbackSubmitPage(
            String instructorName, String fsName) {
        AppUrl submitPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE)
                        .withUserId(testData.instructors.get(instructorName).googleId)
                        .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                        .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(submitPageUrl, FeedbackSubmitPage.class);
    }

    private FeedbackSubmitPage loginToStudentFeedbackSubmitPage(
            String studentName, String fsName) {
        AppUrl submitPageUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                        .withUserId(testData.students.get(studentName).googleId)
                        .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                        .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(submitPageUrl, FeedbackSubmitPage.class);
    }

    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(
            String studentName, String fsName) {
        AppUrl resultsPageUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                        .withUserId(testData.students.get(studentName).googleId)
                        .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                        .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(resultsPageUrl, StudentFeedbackResultsPage.class);
    }

    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPageWithViewType(
            String instructorName, String fsName, boolean needAjax, String viewType) {
        AppUrl resultsPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                    .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());

        if (needAjax) {
            resultsPageUrl = resultsPageUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX,
                                                      String.valueOf(needAjax));
        }

        if (viewType != null) {
            resultsPageUrl = resultsPageUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }

        return loginAdminToPage(resultsPageUrl, InstructorFeedbackResultsPage.class);
    }
}
