package teammates.test.cases.ui.browsertests;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.GenericAppPage;
import teammates.test.util.Priority;
import teammates.test.util.Url;

/** This is considered a UI test case because it uses a Browser */
@Priority(5)
public class AutomatedSessionRemindersTest extends BaseUiTestCase {
    
    private static Browser browser;
    private static DataBundle testData;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/AutomatedSessionRemindersTest.json");
        
        /* 
         * In this test, we set the email address of the accounts to be the same as the
         * support email address. When running the test against a production server,
         * email alerts will be sent to the specified support email address.
         * The tester should manually check the email box after running the test suite. 
         */
        
        testData.accounts.get("instructorWithEvals").email = Config.SUPPORT_EMAIL;
        testData.instructors.get("AutSessRem.instructor").email = Config.SUPPORT_EMAIL;
        testData.students.get("alice.tmms@AutSessRem.course").email = Config.SUPPORT_EMAIL;
        testData.feedbackSessions.get("closingSession").creatorEmail = Config.SUPPORT_EMAIL;
        testData.feedbackSessions.get("openingSession").creatorEmail = Config.SUPPORT_EMAIL;
        testData.feedbackSessions.get("publishedSession").creatorEmail = Config.SUPPORT_EMAIL;
        testData.feedbackQuestions.get("question").creatorEmail = Config.SUPPORT_EMAIL;
        
        //Set closing time of one feedback session in 23+ hours ahead of now.
        FeedbackSessionAttributes closingFeedbackSession = testData.feedbackSessions.get("closingSession");
        int _23hours59min_InMilliSeconds = (60*23+59)*60*1000;
        closingFeedbackSession.endTime = TimeHelper.getMsOffsetToCurrentTime(_23hours59min_InMilliSeconds);

        //Opening time for one feedback session already set to some time in the past.
        
        //Published time for one feedback session already set to some time in the past.
        
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testFeedbackSessionOpeningReminders(){
        Url openingRemindersUrl = new Url(Const.ActionURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS);
        loginAdminToPage(browser, openingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSesssionClosingReminders(){
        Url closingRemindersUrl = new Url(Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS);
        loginAdminToPage(browser, closingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSessionPublishedReminders(){
        Url publishedRemindersUrl = new Url(Const.ActionURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS);
        loginAdminToPage(browser, publishedRemindersUrl, GenericAppPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
