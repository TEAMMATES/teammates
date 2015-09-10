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

        // This is the full HTML verification for Student Home Page, the rest can all be verifyMainHtml
        studentHome.verifyHtml("/studentHomeHTMLEmpty.html");
        
        ______TS("persistence check");
        
        loginWithPersistenceProblem();
        studentHome.verifyHtmlMainContent("/studentHomeHTMLPersistenceCheck.html");
        
        ______TS("login");
        
        studentHome = HomePage.getNewInstance(browser)
                              .clickStudentLogin()
                              .loginAsStudent(TestProperties.inst().TEST_STUDENT1_ACCOUNT, 
                                              TestProperties.inst().TEST_STUDENT1_PASSWORD);
            
        ______TS("content: multiple courses");
        
        // this test uses the accounts from test.properties
        studentHome.verifyHtmlMainContent("/studentHomeHTML.html");
        
        Url detailsPageUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                             .withUserId(testData.students.get("SHomeUiT.charlie.d@SHomeUiT.CS2104").googleId);

        StudentHomePage studentHomePage = loginAdminToPage(browser, detailsPageUrl, StudentHomePage.class);
        
        studentHomePage.verifyHtmlMainContent("/studentHomeTypicalHTML.html");
           
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
        
        ______TS("link: link of published feedback");
        
        
        studentHomePage.getViewFeedbackButton("Closed Feedback Session").click();
        studentHomePage.reloadPage();
        String pageSource = browser.driver.getPageSource();
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
        
        
        ______TS("access the feedback session exactly after it is deleted");
        
        BackDoor.deleteFeedbackSession("First Feedback Session", "SHomeUiT.CS2104");     
        studentHomePage.getSubmitFeedbackButton("First Feedback Session").click();
        studentHomePage.waitForPageToLoad();
        studentHomePage.verifyHtmlMainContent("/studentHomeFeedbackDeletedHTML.html");
        
    }

    private void loginWithPersistenceProblem() {
        Url homeUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                    .withParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "SHomeUiT.CS2104")
                    .withUserId("unreg_user");
        
        studentHome = loginAdminToPage(browser, homeUrl, StudentHomePage.class);
        
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
}
