package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorEvalResultsPage;
import teammates.test.pageobjects.InstructorEvalSubmissionEditPage;
import teammates.test.pageobjects.InstructorEvalSubmissionViewPage;

/**
 * Covers viewing of evaluation submissions by the instructor.
 * SUT: {@link InstructorEvalSubmissionViewPage}.
 */
public class InstructorEvalSubmissionViewPageUiTest extends BaseUiTestCase{
    private static Browser browser;
    private static InstructorEvalSubmissionViewPage viewPage;
    private static DataBundle testData;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorEvalSubmissionViewPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    
    @Test
    public void testAll() {
        
        EvaluationAttributes eval = testData.evaluations.get("Second Eval");
        
        //TODO: test content for student without team and 1-person team
        
        ______TS("content: p2p disabled eval");
        
        Url resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE)
            .withUserId(testData.instructors.get("CESubViewUiT.instructor").googleId)
            .withCourseId(eval.courseId)
            .withEvalName(eval.name);
        InstructorEvalResultsPage resultsPage = loginAdminToPage(browser, resultsUrl, InstructorEvalResultsPage.class);
        viewPage = resultsPage.clickViewLinkForStudent("Charlie");
        viewPage.verifyHtmlMainContent("/instructorEvalSubmissionViewP2pDisabled.html");
        
        viewPage.closeCurrentWindowAndSwitchToParentWindow();
        
        ______TS("content: p2p enabled eval");
        
        eval = testData.evaluations.get("First Eval");
        
        resultsUrl = createUrl(Const.ActionURIs.INSTRUCTOR_EVAL_RESULTS_PAGE)
            .withUserId(testData.instructors.get("CESubViewUiT.instructor").googleId)
            .withCourseId(eval.courseId)
            .withEvalName(eval.name);
        resultsPage = loginAdminToPage(browser, resultsUrl, InstructorEvalResultsPage.class);
        viewPage = resultsPage.clickViewLinkForStudent("Charlie");
        viewPage.verifyHtmlMainContent("/instructorEvalSubmissionView.html");
        
        ______TS("link: edit");
        
        InstructorEvalSubmissionEditPage editPage = viewPage.clickEditButton();
        editPage.verifyIsCorrectPage(eval.courseId, eval.name, testData.students.get("Charlie").name);
        editPage.closeCurrentWindowAndSwitchToParentWindow();
        
    }


    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}