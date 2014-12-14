package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentEvalResultsPage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Covers 'Evaluation Results' page for students.
 * SUT: {@link StudentEvalResultsPage}
 */
public class StudentEvalResultsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private StudentEvalResultsPage resultsPage;
    

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentEvalResultsPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test    
    public void testAll() throws Exception{
       
        testContent();
        testLink();   
    }
    
    
    private void testContent(){
        
        ______TS("content");
        
        verifyResultContent("Third Eval", "SEvalRUiT.charlie.d", "/studentEvalResultsTypicalHTML.html");

        //typical case: two members
        
        verifyResultContent("Third Eval", "SEvalRUiT.alice.b", "/studentEvalResultsTwoMembersTypicalHTML.html");
        
        //extreme case: 1
        //My view:         of me: E +100%  of others: E , 0%
        //Team's view:    of me: E +2%    of others: E +48% , E -50%
        verifyResultContent("Second Eval", "SEvalRUiT.charlie.d", "/studentEvalResultsExtreme1HTML.html");

        //extreme case: 2
        //My view:         of me: E        of others: E +10% , E
        //Team's view:    of me: E -48%   of others: E +53% , E +6%
        verifyResultContent("Second Eval", "SEvalRUiT.danny.e", "/studentEvalResultsExtreme2HTML.html");

        //extreme case: 3
        //My view:         of me: E        of others: E , E
        //Team's view:    of me: E +48%   of others: E +2% , E -50%
        verifyResultContent("Second Eval", "SEvalRUiT.emily.f", "/studentEvalResultsExtreme3HTML.html");

        //student did not submit
        
        verifyResultContent("Second Eval", "SEvalRUiT.alice.b", "/studentEvalResultsNotSubmittedHTML.html");

        //team mates did not submit
        
        verifyResultContent("Second Eval", "SEvalRUiT.benny.c", "/studentEvalResultsTheOtherDidn'tSubmitHTML.html");
        
        //with p2pFeedback disabled
        
        verifyResultContent("P2P Disabled Eval", "SEvalRUiT.benny.c", "/studentEvalResultsP2PDisabled.html");
        
    }
    
    
    private void testLink(){
        
        ______TS("Click Result Interpret Link");
        
        resultsPage = getResultsPage("Third Eval", "SEvalRUiT.charlie.d");
        resultsPage.clickResultInterpretLink();  
        resultsPage.verifyHtmlMainContent("/studentEvalResultsTypicalHTML.html");
 
        Url expectedUrl = createUrl(Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE)
                          .withUserId(testData.students.get("SEvalRUiT.charlie.d").googleId)
                          .withCourseId(testData.evaluations.get("Third Eval").courseId)
                          .withEvalName(testData.evaluations.get("Third Eval").name);
        
        String expectedUrlString = expectedUrl.toString() + "#interpret";
        String actualUrlString = browser.driver.getCurrentUrl();      
        assertEquals(expectedUrlString, actualUrlString);
        
        
        ______TS("Click Calculation Detalis Link");     
                
        resultsPage.clickcalculationDetaislLink();
        
        actualUrlString = browser.driver.getCurrentUrl();
        String stringShouldAppear = "/dev/spec.html#supplementaryrequirements-pointcalculationscheme";
        
        assertTrue(actualUrlString.contains(stringShouldAppear));
    
    }

    private void verifyResultContent(String evalObjectId, String studentObjectId, String filePath) {
        
        StudentEvalResultsPage actualResultsPage = getResultsPage(evalObjectId, studentObjectId);
        actualResultsPage.verifyHtmlMainContent(filePath);     
    }
    
    
    private StudentEvalResultsPage getResultsPage(String evalObjectId, String studentObjectId){
        
        Url resultsUrl = createUrl(Const.ActionURIs.STUDENT_EVAL_RESULTS_PAGE)
                         .withUserId(testData.students.get(studentObjectId).googleId)
                         .withCourseId(testData.evaluations.get(evalObjectId).courseId)
                         .withEvalName(testData.evaluations.get(evalObjectId).name);
        
        resultsPage = loginAdminToPage(browser, resultsUrl, StudentEvalResultsPage.class); 
        return resultsPage;      
    }
    
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}