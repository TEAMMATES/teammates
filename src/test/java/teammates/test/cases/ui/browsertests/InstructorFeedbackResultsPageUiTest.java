package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
        testData = loadDataBundle("/InstructorFeedbackResultsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() throws Exception {
        testContent();
        testViewPhotoAndAjaxForLargeScaledSession();
        testSortAction();
        testFilterAction();
        testPanelsCollapseExpand();
        testShowStats();
        testSearchScript();
        testFeedbackResponseCommentActions();
        testDownloadAction();
        testLink();
    }

    public void testContent(){

        ______TS("standard session results");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.verifyHtml("/instructorFeedbackResultsPageOpen.html");
        
        ______TS("standard session results with helper view");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.helper", "Open Session");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageOpenWithHelperView.html");
        
        ______TS("empty session");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Empty Session");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsPageEmpty.html");
    
    }
    
    public void testSortAction(){
        
        ______TS("test sort types");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByGiverRecipientQuestion();
        
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));

        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestion.html");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestion.html");

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,"giver-1-recipient-1"));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,"giver-1-recipient-1"));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,"giver-1-recipient-1"));
        
        
        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipient.html");
        
        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiver.html");
        
        //Sorted by team
        resultsPage.clickGroupByTeam();
        
        resultsPage.displayByGiverRecipientQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverRecipientQuestionTeam.html");

        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientGiverQuestionTeam.html");
        
        resultsPage.displayByGiverQuestionRecipient();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortGiverQuestionRecipientTeam.html");
        
        resultsPage.displayByRecipientQuestionGiver();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortRecipientQuestionGiverTeam.html");
        
        
        //By question
        resultsPage.displayByQuestion();
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestion.html");
        
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(9,""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(9,""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(9,""));
        
        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(10,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(10,""));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(11,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(11,""));

        assertEquals(true, resultsPage.clickQuestionAdditionalInfoButton(12,""));
        assertEquals(false, resultsPage.clickQuestionAdditionalInfoButton(12,""));

        
        ______TS("test in-table sort");
        
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

        /*Omitted as unable to check both forward and reverse order in one go
         * //TODO: split up verifySortingOrder to enable this test
        verifySortingOrder(By.id("button_sortToTeam"), 
                "Team 2{*}Team 3",
                "Team 1{*}Team 2",
                "Team 1{*}Team 2",
                "Team 1{*}Team 1");
        */
        
    }
    
    public void testViewPhotoAndAjaxForLargeScaledSession() throws Exception {
        
        uploadPhotoForStudent(testData.students.get("Alice").googleId);
        
        ______TS("Ajax for view by questions");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "question");
        
        resultsPage.clickAjaxPanel(0);
       
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByQuestion.html");
        
        ______TS("Ajax for view by question for helper");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.helper", "Open Session", true, "question");
        
        resultsPage.clickAjaxPanel(0);
        
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByQuestionHelperView.html");
        
        ______TS("Ajax for view by giver > recipient > question");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "giver-recipient-question");
        
        resultsPage.clickAjaxPanel(0);
        
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByGRQ.html");
        
        ______TS("test view photo for view by giver > recipient > question");
        
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverAndViewStudentPhotoOnBody(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.click(By.id("panelHeading-5"));
        ThreadHelper.waitFor(1000);
        
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(6, "profile_picture_default.png");
        
        ______TS("Ajax for view by giver > question > recipient");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "giver-question-recipient");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByGQR.html");
        
        ______TS("test view photo for view by giver > question > recipient");
        
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.clickViewPhotoLink(5, "profile_picture_default.png");
        
        ______TS("Ajax for view by recipient > question > giver");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "recipient-question-giver");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByRQG.html");
        
        ______TS("test view photo for view by recipient > question > giver");
        
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.clickViewPhotoLink(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        
        ______TS("Ajax for view by recipient > giver > question");
        
        resultsPage = loginToInstructorFeedbackResultsPageWithViewType("CFResultsUiT.instr", "Open Session", true, "recipient-giver-question");
        
        resultsPage.clickAjaxPanel(0);
        resultsPage.verifyHtmlAjax("/instructorFeedbackResultsAjaxByRGQ.html");
        
        ______TS("test view photo for view by recipient > giver > question");
        
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.hoverAndViewStudentPhotoOnBody(5, "studentProfilePic?studentemail={*}&courseid={*}&user=CFResultsUiT.instr");
        resultsPage.click(By.id("panelHeading-5"));
        ThreadHelper.waitFor(1000);
        
        resultsPage.hoverClickAndViewStudentPhotoOnHeading(6, "profile_picture_default.png");
    }

    public void testFilterAction() {

        ______TS("filter by section A");

        resultsPage.filterResponsesForSection("Section A");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionA.html");
        
        ______TS("filter by section B, no responses");
        
        resultsPage.filterResponsesForSection("Section B");
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsFilteredBySectionB.html");
        
        resultsPage.filterResponsesForAllSections();
        
    }
    
    public void testPanelsCollapseExpand(){
        ______TS("panels expand/collapse");
        
        assertEquals(resultsPage.collapseExpandButton.getText(),"Collapse Questions");
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(true));
        
        resultsPage.clickCollapseExpand();
        ThreadHelper.waitFor(1000);
        assertEquals(resultsPage.collapseExpandButton.getText(),"Expand Questions");
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(false));
        

        resultsPage.clickCollapseExpand();
        ThreadHelper.waitFor(1000);
        assertEquals(resultsPage.collapseExpandButton.getText(),"Collapse Questions");
        assertTrue(resultsPage.verifyAllResultsPanelBodyVisibility(true));
        
    }
    
    public void testShowStats(){
        ______TS("show stats");
        
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"),"true");
        assertTrue(resultsPage.verifyAllStatsVisibility());
        
        resultsPage.clickShowStats();
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"),null);
        assertFalse(resultsPage.verifyAllStatsVisibility());
        

        resultsPage.clickShowStats();
        assertEquals(resultsPage.showStatsCheckbox.getAttribute("checked"),"true");
        assertTrue(resultsPage.verifyAllStatsVisibility());
        
    }
    
    public void testSearchScript(){
        ______TS("test search/filter script");
        
        resultsPage.fillSearchBox("question 1");
        ThreadHelper.waitFor(1000);
        resultsPage.verifyHtmlMainContent("/instructorFeedbackResultsSortQuestionSearch.html");
    }
    
    public void testFeedbackResponseCommentActions() {
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        
        ______TS("failure: add empty feedback response comment");
        
        resultsPage.addFeedbackResponseComment("");
        resultsPage.verifyCommentFormErrorMessage("-0-1-1", Const.StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY);
        
        ______TS("action: add new feedback response comments");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("test comment 1");
        resultsPage.addFeedbackResponseComment("test comment 2");
        resultsPage.verifyCommentRowContent("-0",
                "test comment 1", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyCommentRowContent("-1",
                "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "test comment 1", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyCommentRowContent("-0-1-1-2",
                "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        
        ______TS("action: edit existing feedback response comment");

        resultsPage.editFeedbackResponseComment("-0-1-1-1",
                "edited test comment");
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "edited test comment", "CFResultsUiT.instr@gmail.tmt");
        
        ______TS("action: delete existing feedback response comment");

        resultsPage.deleteFeedbackResponseComment("-0-1-1-1");
        resultsPage.verifyRowMissing("-0-1-1-1");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        
        ______TS("action: add edit and delete successively");
        
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.addFeedbackResponseComment("successive action comment");
        resultsPage.verifyCommentRowContent("-0",
                "successive action comment", "CFResultsUiT.instr@gmail.tmt");
        
        resultsPage.editFeedbackResponseComment("-0",
                "edited successive action comment");
        resultsPage.verifyCommentRowContent("-0",
                "edited successive action comment", "CFResultsUiT.instr@gmail.tmt");
        
        resultsPage.deleteFeedbackResponseComment("-0");
        resultsPage.verifyRowMissing("-0");
        
        resultsPage = loginToInstructorFeedbackResultsPage("CFResultsUiT.instr", "Open Session");
        resultsPage.displayByRecipientGiverQuestion();
        resultsPage.verifyCommentRowContent("-0-1-1-1",
                "test comment 2", "CFResultsUiT.instr@gmail.tmt");
        resultsPage.verifyRowMissing("-0-1-1-2");
        
    }
    
    private void testDownloadAction() {
        ______TS("action: download report");
        
        Url reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
            .withUserId("CFResultsUiT.instr")
            .withCourseId("CFResultsUiT.CS2104")
            .withSessionName("First Session");
        
        resultsPage.verifyDownloadLink(reportUrl);
        
        ______TS("action: download report unsuccessfully");
        
        reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_DOWNLOAD)
            .withUserId("CFResultsUiT.instr");
        browser.driver.get(reportUrl.toString());
        String afterReportDownloadUrl = browser.driver.getCurrentUrl();
        assertFalse(reportUrl.equals(afterReportDownloadUrl));
        //Get an error page due to missing parameters in URL
        assertEquals(true, afterReportDownloadUrl.contains("errorPage.jsp"));
        
        //return to the previous page
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
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                    .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        
        return loginAdminToPage(browser, editUrl, InstructorFeedbackResultsPage.class);
    }
    
    private InstructorFeedbackResultsPage loginToInstructorFeedbackResultsPageWithViewType(
            String instructorName, String fsName, boolean needAjax, String viewType) {
        Url editUrl = createUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE)
                    .withUserId(testData.instructors.get(instructorName).googleId)
                    .withCourseId(testData.feedbackSessions.get(fsName).courseId)
                    .withSessionName(testData.feedbackSessions.get(fsName).feedbackSessionName);
        
        if(needAjax){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_NEED_AJAX, String.valueOf(needAjax));
        }
        
        if(viewType != null){
            editUrl = editUrl.withParam(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, viewType);
        }
        
        return loginAdminToPage(browser, editUrl,
                InstructorFeedbackResultsPage.class);
    }
    
    private void verifySortingOrder(By sortIcon, String... values) {
        //check if the rows match the given order of values
        resultsPage.click(sortIcon);
        String searchString = "";
        for (int i = 0; i < values.length; i++) {
            searchString += values[i]+"{*}";
        }
        resultsPage.verifyContains(searchString);
        
        //click the sort icon again and check for the reverse order
        resultsPage.click(sortIcon);
        searchString = "";
        for (int i = values.length; i > 0; i--) {
            searchString += values[i-1]+"{*}";
        }
        resultsPage.verifyContains(searchString);
    }

}