package teammates.test.cases.ui.browsertests;

import java.io.File;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbackResultsPage;
import teammates.test.util.FileHelper;
import teammates.test.util.Priority;

/**
 * Tests 'Feedback Results' view of instructors.
 * SUT: {@link InstructorFeedbackResultsPage}.
 */
@Priority(-1)
public class InstructorFeedbackResultsPageUiTest extends BaseUiTestCase {

    private static DataBundle testData;
    private static Browser browser;
    private InstructorFeedbackResultsPage resultsPage;

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }

    @BeforeMethod
    public void refreshTestData() {
        testData = loadDataBundle("/InstructorFeedbackResultsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
    }

    @Test
    public void testHtmlContent() throws Exception {
        testContent();
        testModerateResponsesButton();
    }

    @Test
    public void testFrontEndActions() throws Exception {
        testDefaultSort();
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

    public void testContent() throws Exception {

        ______TS("Typical case: large session with no sections");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr",
                "Session with no sections", true, "question");
        resultsPage.verifyStatus(Const.StatusMessages.FEEDBACK_RESULTS_QUESTIONVIEWWARNING);
        
        ______TS("Typical case: standard session results");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.waitForPanelsToExpand();
        // This is the full HTML verification for Instructor Feedback Results Page, the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/instructorFeedbackResultsPageOpen.html");

        ______TS("Typical case: standard session results: helper view");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.helper1", "Open Session");
        resultsPage.waitForPanelsToExpand();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageOpenViewForHelperOne.html");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.helper2", "Open Session");
        resultsPage.waitForPanelsToExpand();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageOpenViewForHelperTwo.html");

        ______TS("Typical case: empty session");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Empty Session");
        resultsPage.waitForPanelsToExpand();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageEmpty.html");
        
    }
    
    @Test
    public void testExceptionalCases() throws Exception {
        ______TS("Case where more than 1 question with same question number");
        // results page should be able to load incorrect data and still display it gracefully
        
        FeedbackQuestionAttributes firstQuestion = testData.feedbackQuestions.get("qn1InSession4");
        assertEquals(1, firstQuestion.questionNumber);
        FeedbackQuestionAttributes firstQuestionFromDatastore =
                                        BackDoor.getFeedbackQuestion(firstQuestion.courseId,
                                                                     firstQuestion.feedbackSessionName,
                                                                     firstQuestion.questionNumber);
        
        FeedbackQuestionAttributes secondQuestion = testData.feedbackQuestions.get("qn2InSession4");
        assertEquals(2, secondQuestion.questionNumber);
        // need to retrieve question from datastore to get its questionId
        FeedbackQuestionAttributes secondQuestionFromDatastore =
                                        BackDoor.getFeedbackQuestion(secondQuestion.courseId,
                                                                     secondQuestion.feedbackSessionName,
                                                                     secondQuestion.questionNumber);
        assertEquals(secondQuestion, secondQuestionFromDatastore);
        // make both questions have the same question number
        secondQuestionFromDatastore.questionNumber = 1;
        BackDoor.editFeedbackQuestion(secondQuestionFromDatastore);

        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Session with errors");
        resultsPage.waitForPanelsToExpand();
        
        // compare html for each question panel
        // to verify that the right responses are showing for each question
        By firstQuestionPanelResponses = By.xpath("//div[contains(@class,'panel')][.//input[@name='questionid'][@value='"
                                             + firstQuestionFromDatastore.getId() + "']]"
                                             + "//div[contains(@class, 'table-responsive')]");
        resultsPage.verifyHtmlPart(firstQuestionPanelResponses,
                                   "/instructorFeedbackResultsDuplicateQuestionNumberPanel1.html");
        
        By secondQuestionPanelResponses = By.xpath("//div[contains(@class,'panel')][.//input[@name='questionid'][@value='"
                                              + secondQuestionFromDatastore.getId() + "']]"
                                              + "//div[contains(@class, 'table-responsive')]");
        resultsPage.verifyHtmlPart(secondQuestionPanelResponses,
                                   "/instructorFeedbackResultsDuplicateQuestionNumberPanel2.html");
        
        
        ______TS("Results with sanitized data");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.SanitizedTeam.instr",
                                                           "Session with sanitized data");
        resultsPage.waitForPanelsToExpand();
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageWithSanitizedData.html");
    }

    public void testModerateResponsesButton() {

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByQuestion();

        ______TS("Typical case: test moderate responses button for individual response (including no response)");

        verifyModerateResponsesButton(2, "CFResultsUiT.alice.b@gmail.tmt", "CFResultsUiT.benny.c@gmail.tmt",
                "CFResultsUiT.fred.g@gmail.tmt", "CFResultsUiT.charlie.d@gmail.tmt",
                "CFResultsUiT.danny.e@gmail.tmt", "drop.out@gmail.tmt",
                "extra.guy@gmail.tmt", "CFResultsUiT.emily.f@gmail.tmt");

        ______TS("Typical case: test moderate responses button for team response");

        verifyModerateResponsesButton(4, "Team 1</td></div>'\"");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Session with Instructors as Givers");
        resultsPage.displayByQuestion();
        resultsPage.waitForPageToLoad();
        
        ______TS("Typical case: test moderate responses button for instructors as givers");
        verifyModerateResponsesButton(1, "CFResultsUiT.instr@gmail.tmt", "CFResultsUiT.instr@gmail.tmt",
                                      "CFResultsUiT.instr@gmail.tmt");
        
    }

    public void testSortAction() throws Exception {

        ______TS("Typical case: test sort by giver > recipient > question");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByGiverRecipientQuestion();

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "section-1-giver-1-recipient-1"));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(8, "section-1-giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(8, "section-1-giver-1-recipient-1"));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(8, "section-1-giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "section-1-giver-1-recipient-1"));
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestionTeam.html");

        ______TS("test sort by recipient > giver > question");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestionTeam.html");

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "section-1-giver-1-recipient-0"));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(8, "section-1-giver-1-recipient-0"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(8, "section-1-giver-1-recipient-0"));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(8, "section-1-giver-1-recipient-0"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(8, "section-1-giver-1-recipient-0"));

        ______TS("test sort by giver > question > recipient");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipientTeam.html");

        ______TS("test sort by recipient > question > giver");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiverTeam.html");

        // Do not sort by team
        resultsPage.clickGroupByTeam();

        ______TS("test order in giver > recipient > question team");

        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestion.html");

        ______TS("test order in recipient > giver > question team");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestion.html");

        ______TS("test order in giver > question > recipient team");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipient.html");

        ______TS("test order in recipient > question > giver team");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiver.html");

        ______TS("test sort by question");
        
        // By question
        resultsPage.displayByQuestion();
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestionTeam.html");
        

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(7, ""));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(7, ""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(7, ""));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(7, ""));
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
                "Team 1</td></div>'\"{*}Team 2",
                "Team 1</td></div>'\"{*}Team 2",
                "Team 1</td></div>'\"{*}Team 1</td></div>'\"");
         */

    }
    
    @Test
    public void testVisibilityOptions() throws Exception {
        ______TS("test sort by giver > recipient > question for second session");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Unpublished Session");
        resultsPage.displayByGiverRecipientQuestion();
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverRecipientQuestionTeam.html");

        ______TS("test sort by recipient > giver > question for second session");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientGiverQuestionTeam.html");

        ______TS("test sort by giver > question > recipient for second session");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverQuestionRecipientTeam.html");

        ______TS("test sort by recipient > question > giver for second session");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientQuestionGiverTeam.html");

        // Do not sort by team
        resultsPage.clickGroupByTeam();

        ______TS("test order in giver > recipient > question team for second session");

        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverRecipientQuestion.html");

        ______TS("test order in recipient > giver > question team for second session");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientGiverQuestion.html");

        ______TS("test order in giver > question > recipient team for second session");

        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionGiverQuestionRecipient.html");

        ______TS("test order in recipient > question > giver team for second session");

        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionRecipientQuestionGiver.html");

        ______TS("test sort by question for second session");
        resultsPage.displayByQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionQuestionTeam.html");
        
        ______TS("filter by section A");

        resultsPage.filterResponsesForSection("Section A");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortSecondSessionFilteredBySectionATeam.html");
        
    }

    @Test
    public void testViewPhotoAndAjaxForLargeScaledSession() throws Exception {

        // Mouseover actions do not work on Selenium-Chrome
        if ("chrome".equals(TestProperties.BROWSER)) {
            return;
        }
        
        uploadPhotoForStudent(testData.students.get("Alice").googleId);

        ______TS("Typical case: ajax for view by questions");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr",
                                                                       "Open Session", true, "question");

        resultsPage.clickAjaxLoadResponsesPanel(0);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByQuestion.html");
        
        ______TS("Failure case: Ajax error");
        
        // Change fs name so that the ajax request will fail
        resultsPage.changeFsNameInAjaxLoadResponsesForm(1, "invalidFsName");
        resultsPage.clickAjaxLoadResponsesPanel(1);
        resultsPage.waitForAjaxError(1);
        
        resultsPage.changeFsNameInNoResponsePanelForm("InvalidFsName");
        resultsPage.clickAjaxNoResponsePanel();
        resultsPage.waitForAjaxErrorOnNoResponsePanel();

        ______TS("Typical case: test view photo for view by questions");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewGiverPhotoOnTableCell(
                0, 0, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverClickAndViewRecipientPhotoOnTableCell(0, 0, "profile_picture_default.png");

        ______TS("Typical case: ajax for view by question for helper 1");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.helper1",
                                                                       "Open Session", true, "question");

        resultsPage.clickAjaxLoadResponsesPanel(0);

        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByQuestionViewForHelperOne.html");
        
        ______TS("Typical case: ajax for view by question for helper2");
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.helper2",
                                        "Open Session", true, "question");

        resultsPage.clickAjaxLoadResponsesPanel(0);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByQuestionViewForHelperTwo.html");

        ______TS("Typical case: ajax for view by giver > recipient > question");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "giver-recipient-question");

        resultsPage.waitForPageToLoad();

        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByGRQ.html");

        ______TS("Typical case: test view photo for view by giver > recipient > question");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading("1-1",
                "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverAndViewStudentPhotoOnBody("1-1",
                "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverClickAndViewStudentPhotoOnHeading("1-2", "profile_picture_default.png");
        
        ______TS("Typical case: ajax for view by giver > question > recipient");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "giver-question-recipient");
        
        resultsPage.waitForPageToLoad();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByGQR.html");
                
        ______TS("Typical case: test view photo for view by giver > question > recipient");
        
        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading("1-1",
                "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.clickViewPhotoLink("1-2", "profile_picture_default.png");

        ______TS("Typical case: ajax for view by recipient > question > giver");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "recipient-question-giver");

        resultsPage.waitForPageToLoad();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByRQG.html");

        ______TS("Typical case: test view photo for view by recipient > question > giver");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading("1-1",
                "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.clickViewPhotoLink("1-2", "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        
        ______TS("Typical case: ajax for view by recipient > giver > question");

        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true,
                                                                       "recipient-giver-question");

        resultsPage.waitForPageToLoad();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAjaxByRGQ.html");

        ______TS("Typical case: test view photo for view by recipient > giver > question");

        resultsPage.removeNavBar();
        resultsPage.hoverClickAndViewStudentPhotoOnHeading("1-1",
                "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverAndViewStudentPhotoOnBody("1-1",
                "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverClickAndViewStudentPhotoOnHeading("1-2", "profile_picture_default.png");
    }

    public void testFilterAction() throws Exception {

        ______TS("Typical case: filter by section A");

        resultsPage.filterResponsesForSection("Section A");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionA.html");

        ______TS("Typical case: filter by section B, no responses");

        resultsPage.filterResponsesForSection("Section B");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionB.html");

        ______TS("Typical case: filter by 'No specific recipient'");

        resultsPage.filterResponsesForSection(Const.NO_SPECIFIC_RECIPIENT);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredByNoSection.html");
        
        
        ______TS("Verify that 'No specific recipient' has a section panel on a non-question view");
        resultsPage.displayByRecipientGiverQuestion();
        assertTrue(resultsPage.isSectionPanelExist(Const.NO_SPECIFIC_RECIPIENT));
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
        resultsPage.waitForInstructorPanelStudentPanelsToCollapse();
        assertEquals("Expand Students", resultsPage.instructorPanelCollapseStudentsButton.getText());
        resultsPage.verifySpecifiedPanelIdsAreCollapsed(new String[] { "0-2", "0-3", "0-4" });

        resultsPage.clickGroupByTeam();

        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.clickSectionCollapseStudentsButton();
        resultsPage.waitForSectionStudentPanelsToCollapse();

        resultsPage.displayByQuestion();

        ______TS("Typical case: panels expand/collapse");

        assertEquals("Collapse Questions", resultsPage.collapseExpandButton.getText());
        assertEquals("Collapse all panels. You can also click on the panel heading to toggle each one individually.",
                     resultsPage.collapseExpandButton.getAttribute("data-original-title"));
        resultsPage.verifyResultsVisible();

        resultsPage.clickCollapseExpand();
        resultsPage.waitForPanelsToCollapse();
        assertEquals("Expand Questions", resultsPage.collapseExpandButton.getText());
        assertEquals("Expand all panels. You can also click on the panel heading to toggle each one individually.",
                     resultsPage.collapseExpandButton.getAttribute("data-original-title"));
        resultsPage.verifyResultsHidden();

        resultsPage.clickCollapseExpand();
        resultsPage.waitForPanelsToExpand();
        assertEquals("Collapse Questions", resultsPage.collapseExpandButton.getText());
        assertEquals("Collapse all panels. You can also click on the panel heading to toggle each one individually.",
                     resultsPage.collapseExpandButton.getAttribute("data-original-title"));
        resultsPage.verifyResultsVisible();

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

    @Test
    public void testIndicateMissingResponses() {

        ______TS("Typical case: Hide Missing Responses");

        assertTrue(resultsPage.indicateMissingResponsesCheckbox.isSelected());
        assertFalse(resultsPage.verifyMissingResponsesVisibility());

        resultsPage.clickIndicateMissingResponses();
        resultsPage.waitForPageToLoad();
        assertFalse(resultsPage.indicateMissingResponsesCheckbox.isSelected());
        assertTrue(resultsPage.verifyMissingResponsesVisibility());

        resultsPage.clickIndicateMissingResponses();
        resultsPage.waitForPageToLoad();
        assertTrue(resultsPage.indicateMissingResponsesCheckbox.isSelected());
        assertFalse(resultsPage.verifyMissingResponsesVisibility());

    }
    
    public void testSearchScript() throws Exception {

        ______TS("Typical case: test search/filter script");

        resultsPage.fillSearchBox("question 1");
        ThreadHelper.waitFor(1000);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestionSearch.html");

        ______TS("Verify that search works on RGQ view");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.clickGroupByTeam();
        resultsPage.fillSearchBox("team 2");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRGQSearch.html");
        
        resultsPage.fillSearchBox("alice");
        resultsPage.verifyPanelForParticipantIsDisplayed("CFResultsUiT.alice.b@gmail.tmt");
        
        resultsPage.fillSearchBox("Alice");
        resultsPage.verifyPanelForParticipantIsDisplayed("CFResultsUiT.alice.b@gmail.tmt");
        
        resultsPage.displayByQuestion();
    }

    public void testDefaultSort() throws Exception {

        ______TS("Typical case: test default sort Team Name-->Giver display name");

        resultsPage.fillSearchBox("default sort");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultDefaultSort.html");
    }

    
    // TODO unnecessary coupling of FRComments test here. this should be tested separately.
    public void testFeedbackResponseCommentActions() throws Exception {

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();

        ______TS("Failure case: add empty feedback response comment");

        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "");
        resultsPage.verifyCommentFormErrorMessage("-0-0-1-1", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);

        ______TS("Typical case: add new feedback response comments");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "test comment 1");
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "test comment 2");
        resultsPage.verifyCommentRowContent("-0-1-0-1-1", "test comment 1", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyContains("id=\"frComment-visibility-options-trigger-0-1-0-1-1\"");
        resultsPage.verifyCommentRowContent("-0-1-0-1-2", "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyContains("id=\"visibility-options-0-1-0-1-2\"");
        
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsAddComment.html");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-0-1-1-1", "test comment 1", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyCommentRowContent("-0-0-1-1-2", "test comment 2", "CFResultsUiT.instr@gmail.tmt");

        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-1-1-1-1", "test comment 3");
        resultsPage.verifyCommentRowContent("-1-1-1-1-1", "test comment 3", "CFResultsUiT.instr@gmail.tmt");

        ______TS("Typical case: edit existing feedback response comment");

        resultsPage.editFeedbackResponseComment("-1-1-1-1-1", "edited test comment");
        resultsPage.verifyCommentRowContent("-1-1-1-1-1", "edited test comment", "CFResultsUiT.instr@gmail.tmt");

        ______TS("Typical case: delete existing feedback response comment");

        resultsPage.deleteFeedbackResponseComment("-1-1-1-1-1");
        resultsPage.verifyRowMissing("-1-1-1-1-1");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-0-1-1-2", "test comment 2", "CFResultsUiT.instr@gmail.tmt");

        ______TS("Typical case: add edit and delete successively");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("showResponseCommentAddForm-0-0-1-1", "successive action comment");
        resultsPage.verifyCommentRowContent("-0-1-0-1-3", "successive action comment", "CFResultsUiT.instr@gmail.tmt");

        resultsPage.editFeedbackResponseComment("-0-1-0-1-3", "edited successive action comment");
        resultsPage.verifyCommentRowContent("-0-1-0-1-3", "edited successive action comment",
                "CFResultsUiT.instr@gmail.tmt");
        resultsPage.clickVisibilityOptionForResponseCommentAndSave("responseCommentRow-0-1-0-1-3", 1);

        resultsPage.deleteFeedbackResponseComment("-0-1-0-1-3");
        resultsPage.verifyRowMissing("-0-0-1-1-3");

        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-0-1-1-2", "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyRowMissing("-0-0-1-1-3");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsDeleteComment.html");

    }

    private void testDownloadAction() {

        ______TS("Typical case: download report");

        AppUrl reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
                                                  .withUserId("CFResultsUiT.instr")
                                                  .withCourseId("CFResultsUiT.CS2104")
                                                  .withSessionName("First Session");

        resultsPage.verifyDownloadLink(reportUrl);

        ______TS("Typical case: download report unsuccessfully");

        reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
                                              .withUserId("CFResultsUiT.instr");
        browser.driver.get(reportUrl.toAbsoluteString());
        String afterReportDownloadUrl = browser.driver.getCurrentUrl();
        assertFalse(reportUrl.equals(afterReportDownloadUrl));
        // Get an error page due to missing parameters in URL
        // If admin is an instructor, expected url is InstructorHomePage
        //                 otherwise, expected url is unauthorised.jsp
        assertTrue("Expected url is InstructorHomePage or Unauthorised page, but is " + afterReportDownloadUrl,
                   afterReportDownloadUrl.contains(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                   || afterReportDownloadUrl.contains(Const.ViewURIs.UNAUTHORIZED));

        // return to the previous page
        loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
    }

    @Test
    public void testLink() {
        ______TS("action: test that edit link leads to correct edit page");
        
        InstructorFeedbackEditPage editPage = resultsPage.clickEditLink();
        editPage.verifyContains("Edit Feedback Session");
        assertEquals("CFResultsUiT.CS2104", editPage.getCourseId());
        assertEquals("First Session", editPage.getFeedbackSessionName());
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

    private void uploadPhotoForStudent(String googleId) throws Exception {
        File picture = new File("src/test/resources/images/profile_pic_updated.png");
        String pictureData = JsonUtils.toJson(FileHelper.readFileAsBytes(picture.getAbsolutePath()));
        assertEquals("Unable to upload profile picture", "[BACKDOOR_STATUS_SUCCESS]",
                     BackDoor.uploadAndUpdateStudentProfilePicture(googleId, pictureData));
    }

    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPage(String instructorName, String fsName) {
        AppUrl resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                                .withUserId(testData.instructors.get(instructorName).googleId)
                                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        InstructorFeedbackResultsPage resultsPage =
                loginAdminToPage(browser, resultsUrl, InstructorFeedbackResultsPage.class);
        resultsPage.waitForPageToLoad();
        return resultsPage;
    }

    private InstructorFeedbackResultsPage
            loginToInstructorFeedbackResultsPageWithViewType(String instructorName, String fsName,
                                                             boolean needAjax, String viewType) {
        AppUrl resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                                .withUserId(testData.instructors.get(instructorName).googleId)
                                .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                                .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());

        if (needAjax) {
            resultsUrl = resultsUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX,
                                        String.valueOf(needAjax));
        }

        if (viewType != null) {
            resultsUrl = resultsUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }

        InstructorFeedbackResultsPage resultsPage =
                loginAdminToPage(browser, resultsUrl, InstructorFeedbackResultsPage.class);
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
        StringBuilder searchString = new StringBuilder();
        for (String value : values) {
            searchString.append(value).append("{*}");
        }
        resultsPage.verifyContains(searchString.toString());

        // click the sort icon again and check for the reverse order
        resultsPage.click(sortIcon);
        searchString.setLength(0);
        for (int i = values.length; i > 0; i--) {
            searchString.append(values[i - 1]).append("{*}");
        }
        resultsPage.verifyContains(searchString.toString());
    }

    private void verifyModerateResponsesButton(int qnNumber, String... emails) {
        for (int i = 1; i <= emails.length; i++) {
            resultsPage.verifyModerateResponseButtonBelongsTo(
                    resultsPage.getModerateResponseButtonInQuestionView(qnNumber, i), emails[i - 1]);
        }
    }

}
