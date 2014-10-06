package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorEvalResultsPage;
import teammates.test.pageobjects.InstructorEvalSubmissionViewPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.pageobjects.InstructorHelpPage;

/**
 * Tests 'Evaluation Results' view of Instructors.
 * SUT: {@link InstructorEvalResultsPage}.
 */
public class InstructorEvalResultsPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private InstructorEvalResultsPage resultsPage;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorEvalResultsPageUiTest.json");
        
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    
    @Test
    public void testOpenEval() throws Exception{
        
        ______TS("contents: summary view");
        
        String instructorId = testData.instructors.get("CEvalRUiT.instr").googleId;
        String courseId = testData.courses.get("CEvalRUiT.CS1101").id;
        String evalName = testData.evaluations.get("First Eval").name;
        resultsPage = loginToResultsPage(instructorId, courseId, evalName);
        
        resultsPage.verifyHtmlMainContent("/instructorEvalResultsOpenEval.html");
        
        //sort by name"
        
        resultsPage.sortByName()
            .verifyTablePattern(1, 1, "Alice Betsy{*}Benny Charles{*}Charlie Davis{*}Danny Engrid{*}Emily");
        resultsPage.sortByName()
            .verifyTablePattern(1, 1, "Emily{*}Danny Engrid{*}Charlie Davis{*}Benny Charles{*}Alice Betsy");
        
        //sort by claimed
        
        resultsPage.sortByClaimed()
            .verifyTablePattern(1, 2,  "E -5%{*}E +3%{*}E +5%{*}E +10%{*}E +10%");
        resultsPage.sortByClaimed()
            .verifyTablePattern(1, 2, "E +10%{*}E +10%{*}E +5%{*}E +3%{*}E -5%");
        
        //sort by perceived

        resultsPage.sortByPerceived()
            .verifyTablePattern(1, 3, "E -3%{*}E -1%{*}E{*}E{*}E +4%");
        resultsPage.sortByPerceived()
        .verifyTablePattern(1, 3, "E +4%{*}E{*}E{*}E -1%{*}E -3%");
        
        //sort by diff
        
        resultsPage.sortByDiff()
            .verifyTablePattern(1, 4, "-11%{*}-6%{*}-6%{*}-5%{*}+5%");
        resultsPage.sortByDiff()
            .verifyTablePattern(1, 4, "+5%{*}-5%{*}-6%{*}-6%{*}-11%");
        
        //sort by team name
        
        resultsPage.sortByTeam()
            .verifyTablePattern(1, 0, "Team 1{*}Team 1{*}Team 2{*}Team 2{*}Team 2");
        resultsPage.sortByTeam()
            .verifyTablePattern(1, 0, "Team 2{*}Team 2{*}Team 2{*}Team 1{*}Team 1");
        resultsPage.sortByTeam(); //set back to ascending
        
        ______TS("contents: detailed views");
        
        resultsPage.showDetailsByReviewer()
            .verifyHtmlMainContent("/instructorEvalResultsOpenEvalByReviewer.html");
        
        resultsPage.showDetailsByReviewee()
                .verifyHtmlMainContent("/instructorEvalResultsOpenEvalByReviewee.html");
        
        //TODO: check 'To Top' link
        
        //ensure we can go back to the summary view
        resultsPage.showSummary()
            .sortByName()
            .verifyTablePattern(1, 1,"Alice Betsy{*}Benny Charles{*}Charlie Davis{*}Danny Engrid{*}Emily");
        
        ______TS("link: edit submissions");
        
        /*this is tested in {@link InstructorEvalSubmissionEditPageUiTest} */
        
        ______TS("link: view submissions");
        
        InstructorEvalSubmissionViewPage viewPage = resultsPage.clickViewLinkForStudent("Alice Betsy");
        viewPage.verifyIsCorrectPage("Alice Betsy");
        viewPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("link: interpreting help");
        
        InstructorHelpPage helpPage = resultsPage.clickInterpretHelpLink();
        helpPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("action: download report");
        
        Url reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_DOWNLOAD)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withEvalName(evalName);
        
        resultsPage.verifyDownloadLink(reportUrl);

    }
    
    @Test 
    public void testPublishedEval() throws Exception{
        
        String courseId = testData.courses.get("CEvalRUiT.CS1101").id;
        String evalName = testData.evaluations.get("Second Eval").name;
        String instructorId = testData.instructors.get("CEvalRUiT.instr").googleId;
        resultsPage = loginToResultsPage(instructorId, courseId, evalName);
        
        ______TS("contents: summary view");
        
        resultsPage.verifyHtmlMainContent("/instructorEvalResultsPublishedEval.html");
        
        ______TS("action: download report");
        
        Url reportUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_DOWNLOAD)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withEvalName(evalName);
        
        resultsPage.verifyDownloadLink(reportUrl);
        
        ______TS("action: unpublish");
        
        resultsPage.unpublishAndCancel()
            .verifyHtmlMainContent("/instructorEvalResultsPublishedEval.html");
        assertEquals(true, BackDoor.getEvaluation(courseId, evalName).published);
        
        InstructorFeedbacksPage feedbacksPage = resultsPage.unpublishAndConfirm();
        feedbacksPage.verifyStatus(Const.StatusMessages.EVALUATION_UNPUBLISHED);
        assertEquals(false, BackDoor.getEvaluation(courseId, evalName).published);
        
        //Other content checking, link checking and action checking were 
        //  omitted because they were checked previously.

    }
    
    @Test
    public void testClosedEval() throws Exception{
        
        String instructorId = testData.instructors.get("CEvalRUiT.instr").googleId;
        String courseId = testData.courses.get("CEvalRUiT.CS1101").id;
        String evalName = testData.evaluations.get("Third Eval").name;
        resultsPage = loginToResultsPage(instructorId, courseId, evalName);
        
        ______TS("contents: summary view");
        
        resultsPage.verifyHtmlMainContent("/instructorEvalResultsClosedEval.html");
        
        ______TS("action: publishing");
        
        resultsPage.publishAndCancel()
            .verifyHtmlMainContent("/instructorEvalResultsClosedEval.html");
        assertEquals(false, BackDoor.getEvaluation(courseId, evalName).published);
        
        InstructorFeedbacksPage feedbacksPage = resultsPage.publishAndConfirm();
        feedbacksPage.verifyStatus(Const.StatusMessages.EVALUATION_PUBLISHED);
        assertEquals(true, BackDoor.getEvaluation(courseId, evalName).published);
        
        //other content checking, link checking and action checking were 
        //  omitted because they were checked previously.

    }
    
    @Test
    public void testP2PDisabledEval() throws Exception{
        
        String instructorId = testData.instructors.get("CEvalRUiT.instr").googleId;
        String courseId = testData.courses.get("CEvalRUiT.CS1101").id;
        String evalName = testData.evaluations.get("Fifth Eval").name;
        resultsPage = loginToResultsPage(instructorId, courseId, evalName);
        
        ______TS("contents: summary view");
        
        resultsPage.verifyHtmlMainContent("/instructorEvalResultsP2PDisabled.html");
        
        ______TS("contents: detailed views");
        
        resultsPage.showDetailsByReviewer()
            .verifyHtmlMainContent("/instructorEvalResultsP2PDisabledByReviewer.html");
        
        resultsPage.showDetailsByReviewee()
                .verifyHtmlMainContent("/instructorEvalResultsP2PDisabledByReviewee.html");
        
        //other content checking, link checking and action checking were 
        //  omitted because they were checked previously.
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

    private InstructorEvalResultsPage loginToResultsPage(String instructorId, String courseId, String evalName){
        Url resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE)
            .withUserId(instructorId)
            .withCourseId(courseId)
            .withEvalName(evalName);
        return loginAdminToPage(browser, resultsUrl , InstructorEvalResultsPage.class);
    }
}