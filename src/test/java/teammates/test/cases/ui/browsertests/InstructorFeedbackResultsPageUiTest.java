package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.util.Priority;

/**
 * Tests 'Feedback Results' view of instructors.
 * SUT: {@link InstructorFeedbackResultsPage}.
 */
@Priority(-1)
public class InstructorFeedbackResultsPageUiTest extends BaseUiTestCase {
    protected static Logger log = Utils.getLogger();

    private static DataBundle testData;
    private static Browser browser;
    private InstructorFeedbackResultsPage resultsPage;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }

    @BeforeMethod
    public void refreshTestData() throws Exception {
        testData = loadDataBundle("/InstructorFeedbackResultsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
    }

    @Test
    public void testHtmlContent() throws Exception {
        testContent();
        testModerateResponsesButton();
        testLink();
    }

    @Test
    public void testFrontEndActions() throws Exception {
        testSortAction();
        testFilterAction();
        testPanelsCollapseExpand();
        testShowStats();
        testSearchScript();
    }

    @Test
    public void testBackEndActions() throws Exception {
        testFeedbackResponseCommentActions();
        testDownloadAction();
    }

    public void testContent() {

        ______TS("Typical case: standard session results");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.waitForPanelsToCollapse();
        // This is the full HTML verification for Instructor Feedback Results Page, the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/instructorFeedbackResultsPageOpen.html");

        ______TS("Typical case: standard session results: helper view");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.helper1", "Open Session");
        resultsPage.waitForPanelsToCollapse();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageOpenViewForHelperOne.html");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.helper2", "Open Session");
        resultsPage.waitForPanelsToCollapse();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageOpenViewForHelperTwo.html");

        ______TS("Typical case: empty session");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Empty Session");
        resultsPage.waitForPanelsToCollapse();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageEmpty.html");

    }

    public void testModerateResponsesButton() {

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByQuestion();
        ThreadHelper.waitFor(2000);

        ______TS("Typical case: test moderate responses button for individual response (including no response)");

        verifyModerateResponsesButton(2, "CFResultsUiT.alice.b@gmail.tmt", "CFResultsUiT.benny.c@gmail.tmt",
                                      "CFResultsUiT.charlie.d@gmail.tmt", "CFResultsUiT.danny.e@gmail.tmt",
                                      "drop.out@gmail.tmt", "extra.guy@gmail.tmt", "CFResultsUiT.emily.f@gmail.tmt");

        ______TS("Typical case: test moderate responses button for team response");

        verifyModerateResponsesButton(4, "CFResultsUiT.alice.b@gmail.tmt");

    }

    public void testSortAction() {

        ______TS("Typical case: test sort by giver > recipient > question");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByGiverRecipientQuestion();

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "giver-1-recipient-1"));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(8, "giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(8, "giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(8, "giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "giver-1-recipient-1"));
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestion.html");

        ______TS("test sort by recipient > giver > question");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestion.html");

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "giver-1-recipient-1"));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(8, "giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(8, "giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(8, "giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "giver-1-recipient-1"));

        ______TS("test sort by giver > question > recipient");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipient.html");

        ______TS("test sort by recipient > question > giver");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiver.html");

        // Do not sort by team
        resultsPage.clickGroupByTeam();

        ______TS("test order in giver > recipient > question team");

        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestionTeam.html");

        ______TS("test order in recipient > giver > question team");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestionTeam.html");

        ______TS("test order in giver > question > recipient team");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipientTeam.html");

        ______TS("test order in recipient > question > giver team");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiverTeam.html");

        ______TS("test sort by question");
        
        // By question
        resultsPage.displayByQuestion();
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestion.html");
        

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(7, ""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(7, ""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(7, ""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(7, ""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(7, ""));

        ______TS("Typical case: test in-table sort");

        verifySortingOrder(By.id("button_sortFeedback"), 
                           "1 Response to Danny.", 
                           "2 Response to Benny.", 
                           "3 Response to Emily.", 
                           "4 Response to Charlie.");

        verifySortingOrder(By.id("button_sortFromName"), 
                           "Alice Betsy",
                           "Benny Charles",
                           "Benny Charles",
                            "Charlie Dávis");
        
        verifySortingOrder(By.id("button_sortFromTeam"), 
                           "Team 1",
                           "Team 1",
                           "Team 2",
                           "Team 2");

        verifySortingOrder(By.id("button_sortToName"), 
                           "Benny Charles", 
                           "Charlie Dávis", 
                           "Danny Engrid",
                           "Emily");

        /*
         * Omitted as unable to check both forward and reverse order in one go
         * TODO: split up verifySortingOrder to enable this test
        verifySortingOrder(By.id("button_sortToTeam"), 
                "Team 2{*}Team 3",
                "Team 1{*}Team 2",
                "Team 1{*}Team 2",
                "Team 1{*}Team 1");
         */

    }
    
    @Test
    public void testVisibilityOptions() {
        ______TS("test sort by giver > recipient > question for second session");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Unpublished Session");
        resultsPage.displayByGiverRecipientQuestion();
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverRecipientQuestion.html");

        ______TS("test sort by recipient > giver > question for second session");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientGiverQuestion.html");

        ______TS("test sort by giver > question > recipient for second session");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverQuestionRecipient.html");

        ______TS("test sort by recipient > question > giver for second session");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientQuestionGiver.html");

        // Do not sort by team
        resultsPage.clickGroupByTeam();

        ______TS("test order in giver > recipient > question team for second session");

        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverRecipientQuestionTeam.html");

        ______TS("test order in recipient > giver > question team for second session");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientGiverQuestionTeam.html");

        ______TS("test order in giver > question > recipient team for second session");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverQuestionRecipientTeam.html");

        ______TS("test order in recipient > question > giver team for second session");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientQuestionGiverTeam.html");

        ______TS("test sort by question for second session");
        resultsPage.displayByQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionQuestion.html");
        
        
    }

    @Test
    public void testViewPhotoAndAjaxForLargeScaledSession() throws Exception {

        uploadPhotoForStudent(testData.students.get("Alice").googleId);

        ______TS("Typical case: ajax for view by questions");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr",
                                                                       "Open Session", true, "question");

        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByQuestion.html");

        ______TS("Typical case: test view photo for view by questions");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewGiverPhotoOnTableCell(0, 0, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverClickAndViewRecipientPhotoOnTableCell(0, 0, "profile_picture_default.png");

        ______TS("Typical case: ajax for view by question for helper 1");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.helper1",
                                                                       "Open Session", true, "question");

        resultsPage.clickAjaxPanel(0);

        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByQuestionViewForHelperOne.html");
        
        ______TS("Typical case: ajax for view by question for helper2");
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.helper2",
                                        "Open Session", true, "question");

        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByQuestionViewForHelperTwo.html");

        ______TS("Typical case: ajax for view by giver > recipient > question");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "giver-recipient-question");

        resultsPage.clickAjaxPanel(0);

        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByGRQ.html");

        ______TS("Typical case: test view photo for view by giver > recipient > question");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverAndViewStudentPhotoOnBody(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.click(By.id("panelHeading-5"));
        ThreadHelper.waitFor(1000);

        resultsPage.hoverClickAndViewStudentPhotoOnHeading(6, "profile_picture_default.png");
        
        ______TS("Typical case: ajax for view by giver > question > recipient");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "giver-question-recipient");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByGQR.html");
        
        ______TS("Typical case: test view photo for view by giver > question > recipient");
        
        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.clickViewPhotoLink(5, "profile_picture_default.png");

        ______TS("Typical case: ajax for view by recipient > question > giver");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "recipient-question-giver");

        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByRQG.html");

        ______TS("Typical case: test view photo for view by recipient > question > giver");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.clickViewPhotoLink(6, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        
        ______TS("Typical case: ajax for view by recipient > giver > question");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "recipient-giver-question");

        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByRGQ.html");

        ______TS("Typical case: test view photo for view by recipient > giver > question");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverAndViewStudentPhotoOnBody(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.click(By.id("panelHeading-5"));
        ThreadHelper.waitFor(1000);

        resultsPage.hoverClickAndViewStudentPhotoOnHeading(6, "profile_picture_default.png");
    }

    public void testFilterAction() {

        ______TS("Typical case: filter by section A");

        resultsPage.filterResponsesForSection("Section A");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionA.html");

        ______TS("Typical case: filter by section B, no responses");

        resultsPage.filterResponsesForSection("Section B");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionB.html");

        ______TS("Typical case: filter by 'Not in a section'");

        resultsPage.filterResponsesForSection("Not in a section");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredByNoSection.html");
        
        
        ______TS("Verify that 'Not in a section' has a section panel on a non-question view");
        resultsPage.displayByRecipientGiverQuestion();
        assertTrue(resultsPage.isSectionPanelExist("Not in a section"));
        assertFalse(resultsPage.isSectionPanelExist("Section A"));

        resultsPage.displayByQuestion();
        resultsPage.filterResponsesForAllSections();

    }

    public void testPanelsCollapseExpand() {
        
        ______TS("Test that 'Collapse Student' button is working");
        resultsPage.clickGroupByTeam();
        resultsPage.displayByGiverRecipientQuestion();
        assertEquals("Collapse Students", resultsPage.instructorPanelCollapseStudentsButton.getText());
        resultsPage.clickInstructorPanelCollapseStudentsButton();
        assertEquals("Expand Students", resultsPage.instructorPanelCollapseStudentsButton.getText());
        
        int numOfPanels = resultsPage.getNumOfPanelsInInstructorPanel();
        resultsPage.verifyParticipantPanelIsCollapsed(4, (numOfPanels * 50));

        resultsPage.clickGroupByTeam();
        resultsPage.displayByQuestion();

        ______TS("Typical case: panels expand/collapse");

        assertEquals("Collapse Questions", resultsPage.collapseExpandButton.getText());
        assertEquals("Collapse all panels. You can also click on the panel heading to toggle each one individually.",
                     resultsPage.collapseExpandButton.getAttribute("data-original-title"));
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(true));

        resultsPage.clickCollapseExpand();
        assertEquals("Expand Questions", resultsPage.collapseExpandButton.getText());
        assertEquals("Expand all panels. You can also click on the panel heading to toggle each one individually.",
                     resultsPage.collapseExpandButton.getAttribute("data-original-title"));
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(false));

        resultsPage.clickCollapseExpand();
        assertEquals("Collapse Questions", resultsPage.collapseExpandButton.getText());
        assertEquals("Collapse all panels. You can also click on the panel heading to toggle each one individually.",
                     resultsPage.collapseExpandButton.getAttribute("data-original-title"));
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(true));

    }

    public void testShowStats() {

        ______TS("Typical case: show stats");

        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"), "true");
        assertTrue(resultsPage.verifyAllStatsVisibility());

        resultsPage.clickShowStats();
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"), null);
        assertFalse(resultsPage.verifyAllStatsVisibility());

        resultsPage.clickShowStats();
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"), "true");
        assertTrue(resultsPage.verifyAllStatsVisibility());

    }

    public void testSearchScript() {

        ______TS("Typical case: test search/filter script");

        resultsPage.fillSearchBox("question 1");
        ThreadHelper.waitFor(1000);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestionSearch.html");

    }

    // TODO unnecessary coupling of FRComments test here. this should be tested separately.
    public void testFeedbackResponseCommentActions() {

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();

        ______TS("Failure case: add empty feedback response comment");

        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-1-1", "");
        resultsPage.verifyCommentFormErrorMessage("-0-1-1", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);

        ______TS("Typical case: add new feedback response comments");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-1-1", "test comment 1");
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-1-1", "test comment 2");
        resultsPage.verifyCommentRowContent("-0-1-1-1", "test comment 1", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyContains("id=\"frComment-visibility-options-trigger-0-1-1-1\"");
        resultsPage.verifyCommentRowContent("-0-1-1-2", "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyContains("id=\"visibility-options-0-1-1-2\"");
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAddComment.html");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1", "test comment 1", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyCommentRowContent("-0-1-1-2", "test comment 2", "CFResultsUiT.instr@gmail.tmt");

        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-1-1-1", "test comment 3");
        resultsPage.verifyCommentRowContent("-1-1-1-1", "test comment 3", "CFResultsUiT.instr@gmail.tmt");

        ______TS("Typical case: edit existing feedback response comment");

        resultsPage.editFeedbackResponseComment("-0-1-1-1", "edited test comment");
        resultsPage.verifyCommentRowContent("-0-1-1-1", "edited test comment", "CFResultsUiT.instr@gmail.tmt");

        ______TS("Typical case: delete existing feedback response comment");

        resultsPage.deleteFeedbackResponseComment("-0-1-1-1");
        resultsPage.verifyRowMissing("-0-1-1-1");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1", "test comment 2", "CFResultsUiT.instr@gmail.tmt");

        ______TS("Typical case: add edit and delete successively");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-1-1", "successive action comment");
        resultsPage.verifyCommentRowContent("-0-1-1-2", "successive action comment", "CFResultsUiT.instr@gmail.tmt");

        resultsPage.editFeedbackResponseComment("-0-1-1-2", "edited successive action comment");
        resultsPage.verifyCommentRowContent("-0-1-1-2", "edited successive action comment", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow-0-1-1-1", 1);

        resultsPage.deleteFeedbackResponseComment("-0-1-1-2");
        resultsPage.verifyRowMissing("-0-1-1-2");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1", "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyRowMissing("-0-1-1-2");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsDeleteComment.html");

    }

    private void testDownloadAction() {

        ______TS("Typical case: download report");

        Url reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
                                                  .withUserId("CFResultsUiT.instr")
                                                  .withCourseId("CFResultsUiT.CS2104")
                                                  .withSessionName("First Session");

        resultsPage.verifyDownloadLink(reportUrl);

        ______TS("Typical case: download report unsuccessfully");

        reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
                                              .withUserId("CFResultsUiT.instr");
        browser.driver.get(reportUrl.toString());
        String afterReportDownloadUrl = browser.driver.getCurrentUrl();
        assertFalse(reportUrl.equals(afterReportDownloadUrl));
        // Get an error page due to missing parameters in URL
        assertEquals(true, afterReportDownloadUrl.contains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE));

        // return to the previous page
        loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
    }

    public void testLink() {

        ______TS("action: edit");
        InstructorFeedbackEditPage editPage = resultsPage.clickEditLink();
        editPage.verifyContains("Edit Feedback Session");
        editPage.verifyContains("CFResultsUiT.CS2104");
        editPage.verifyContains("First Session");

    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private void uploadPhotoForStudent(String googleId) throws Exception {
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = Utils.getTeammatesGson().toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
        assertEquals("Unable to upload profile picture", "[BACKDOOR_STATUS_SUCCESS]", 
                     BackDoor.uploadAndUpdateStudentProfilePicture(googleId, pictureData));
    }

    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPage(String instructorName, String fsName) {
        Url resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                                .withUserId(testData.instructors.get(instructorName).googleId)
                                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        InstructorFeedbackResultsPage resultsPage = loginAdminToPage(browser, resultsUrl, InstructorFeedbackResultsPage.class);
        resultsPage.waitForPageToLoad();
        return resultsPage;
    }

    private InstructorFeedbackResultsPage
            loginToInstructorFeedbackResultsPageWithViewType(String instructorName, String fsName,
                                                             boolean needAjax, String viewType) {
        Url resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                                .withUserId(testData.instructors.get(instructorName).googleId)
                                .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                                .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);

        if (needAjax) {
            resultsUrl = resultsUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX,
                                        String.valueOf(needAjax));
        }

        if (viewType != null) {
            resultsUrl = resultsUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }

        InstructorFeedbackResultsPage resultsPage = loginAdminToPage(browser, resultsUrl, InstructorFeedbackResultsPage.class);
        if (needAjax) {
            resultsPage.waitForPageStructureToLoad();
        } else {
            resultsPage.waitForPageToLoad();
        }
        
        return resultsPage;
    }

    private void verifySortingOrder(By sortIcon, String... values) {
        // check if the rows match the given order of values
        resultsPage.click(sortIcon);
        String searchString = "";
        for (int i = 0; i < values.length; i++) {
            searchString += values[i] + "{*}";
        }
        resultsPage.verifyContains(searchString);

        // click the sort icon again and check for the reverse order
        resultsPage.click(sortIcon);
        searchString = "";
        for (int i = values.length; i > 0; i--) {
            searchString += values[i - 1] + "{*}";
        }
        resultsPage.verifyContains(searchString);
    }

    private void verifyModerateResponsesButton(int qnNumber, String... emails) {
        for (int i = 1; i <= emails.length; i++) {
            resultsPage.verifyModerateResponseButtonBelongsTo(resultsPage.getModerateResponseButtonInQuestionView(qnNumber, i),
                                                              emails[i - 1]);
        }
    }

}
