package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.StudentHelpPage;
import teammates.test.pageobjects.StudentHomePage;

/**
 * Covers Homepage and Login page for students. Some part of it is using a 
 * real Google account alice.tmms. <br> 
 * SUT: {@link StudentHelpPage} and {@link LoginPage} for students.
 */
public class StudentHomePageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private StudentHomePage studentHome;
    private static FeedbackSessionAttributes gracedFeedbackSession;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentHomePageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        
        gracedFeedbackSession = BackDoor.getFeedbackSession("SHomeUiT.CS2104", "Graced Feedback Session");
        gracedFeedbackSession.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        BackDoor.editFeedbackSession(gracedFeedbackSession);

        browser = BrowserPool.getBrowser(true);
    }


    @Test    
    public void allTests() throws Exception{
        testContentAndLogin();        
        testLinks();
        testLinkAndContentAfterDelete();
    }


    private void testContentAndLogin() throws Exception {
        
        ______TS("content: no courses, 'welcome stranger' message");
        
        String unregUserId = TestProperties.inst().TEST_UNREG_ACCOUNT;
        String unregPassword = TestProperties.inst().TEST_UNREG_PASSWORD;
        BackDoor.deleteAccount(unregUserId); //delete account if it exists
        
        AppPage.logout(browser);
        studentHome = HomePage.getNewInstance(browser).clickStudentLogin()
                                                      .loginAsStudent(unregUserId, unregPassword);
        // this test uses the accounts from test.properties
        studentHome.verifyHtmlMainContent("/StudentHomeHTMLEmpty.html");
        
        ______TS("login");
        
        studentHome = HomePage.getNewInstance(browser)
                              .clickStudentLogin()
                              .loginAsStudent(TestProperties.inst().TEST_STUDENT1_ACCOUNT, 
                                              TestProperties.inst().TEST_STUDENT1_PASSWORD);
            
        ______TS("content: multiple courses");
        
        // this test uses the accounts from test.properties
        studentHome.verifyHtmlMainContent("/StudentHomeHTML.html");
        
        Url detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                             .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(browser, detailsPageUrl, StudentHomePage.class);
        
        studentHomePage.verifyHtmlMainContent("/StudentHomeTypicalHTML.html");
           
    }
    
    
    private void testLinks(){
        
        Url detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(browser, detailsPageUrl, StudentHomePage.class);
        
        
        ______TS("link: help page");
        
        StudentHelpPage helpPage = studentHomePage.clickHelpLink();
        helpPage.closeCurrentWindowAndSwitchToParentWindow();
        
        
        ______TS("link: view team link");
        
        studentHomePage.clickViewTeam();
        
        assertTrue(browser.driver.getCurrentUrl().contains("page/studentCourseDetailsPage?user=SHomeUiT.charlie.d&courseid=SHomeUiT.CS1101"));
        studentHomePage.clickHomeTab();
        
        ______TS("link: links of pending eval");
        
        
        studentHomePage.getSubmitEvalButton("Fifth Eval").click();
        studentHomePage.reloadPage();
        String pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("SHomeUiT.CS1101"));
        assertTrue(pageSource.contains("Fifth Eval"));
        assertTrue(pageSource.contains("Evaluation Submission"));
        studentHomePage.clickHomeTab();
               
        ______TS("link: links of closed eval");
        
        //results not visible yet
        assertTrue(studentHomePage.getViewEvalResultsButton("Third Eval").getAttribute("class").contains("disabled"));
        
        
        studentHomePage.getEditEvalButton("Third Eval").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("SHomeUiT.CS1101"));
        assertTrue(pageSource.contains("Evaluation Submission"));
        assertTrue(pageSource.contains("This evaluation is not open at this time. You are not allowed to edit your submission."));
        studentHomePage.clickHomeTab();
        
        ______TS("link: links of published eval");
        
        assertTrue(studentHomePage.getEditEvalButton("Second Eval").getAttribute("class").contains("disabled"));
        
        
        studentHomePage.getViewEvalResultsButton("Second Eval").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Evaluation Results"));
        assertTrue(pageSource.contains("Second Eval"));
        studentHomePage.clickHomeTab();
        
        ______TS("link: links of submitted eval");
        
        assertTrue(studentHomePage.getViewEvalResultsButton("First Eval").getAttribute("class").contains("disabled"));
        
        
        studentHomePage.getEditEvalButton("First Eval").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Evaluation Submission"));
        assertTrue(pageSource.contains("First Eval"));
        studentHomePage.clickHomeTab();
        
        ______TS("link: link of published feedback");
        
        
        studentHomePage.getViewFeedbackButton("Closed Feedback Session").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Feedback Results"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Closed Feedback Session"));
        studentHomePage.clickHomeTab();
        
        
        studentHomePage.getSubmitFeedbackButton("Closed Feedback Session").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Submit Feedback"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Closed Feedback Session"));
        assertTrue(pageSource.contains("You can view the questions and any submitted responses for this feedback session but cannot submit new responses as the session is not currently open for submission."));
        studentHomePage.clickHomeTab();
        
        
        ______TS("link: link of Grace period feedback");
        
        assertTrue(studentHomePage.getViewFeedbackButton("Graced Feedback Session").getAttribute("Class").contains("disabled"));
        
        
        studentHomePage.getSubmitFeedbackButton("Graced Feedback Session").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Submit Feedback"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("Graced Feedback Session"));
        assertTrue(pageSource.contains("You can view the questions and any submitted responses for this feedback session but cannot submit new responses as the session is not currently open for submission."));
        studentHomePage.clickHomeTab();
        
        
        ______TS("link: link of pending feedback");
        
        assertTrue(studentHomePage.getViewFeedbackButton("First Feedback Session").getAttribute("Class").contains("disabled"));
        
        studentHomePage.getSubmitFeedbackButton("First Feedback Session").click();
        studentHomePage.reloadPage();
        pageSource = browser.driver.getPageSource();
        assertTrue(pageSource.contains("Submit Feedback"));
        assertTrue(pageSource.contains("SHomeUiT.CS2104"));
        assertTrue(pageSource.contains("First Feedback Session"));
        studentHomePage.clickHomeTab();
    }
    
    
    private void testLinkAndContentAfterDelete(){
        
        Url detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                             .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(browser, detailsPageUrl, StudentHomePage.class);
        
        ______TS("access the eval exactly after it is deleted");
               
        BackDoor.deleteEvaluation("SHomeUiT.CS1101", "Third Eval");
        
        studentHomePage.getEditEvalButton("Third Eval").click();
        browser.selenium.waitForPageToLoad("15000");
        studentHomePage.verifyHtmlMainContent("/StudentHomeEvalDeletedHTML.html");
        studentHomePage.reloadPage();
        
        
        ______TS("access the feedback session exactly after it is deleted");
        
        BackDoor.deleteFeedbackSession("First Feedback Session", "SHomeUiT.CS2104");     
        studentHomePage.getSubmitFeedbackButton("First Feedback Session").click();
        browser.selenium.waitForPageToLoad("15000");
        studentHomePage.verifyHtmlMainContent("/StudentHomeFeedbackDeletedHTML.html");
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
