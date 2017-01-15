package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.GenericAppPage;
import teammates.test.util.Priority;

/** This is considered a UI test case because it uses a Browser */
@Priority(5)
public class AutomatedSessionRemindersTest extends BaseUiTestCase {
    
    private static Browser browser;
    private static DataBundle testData;
    
    @BeforeClass
    public void classSetup() {
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
        testData.feedbackSessions.get("closedSession").setCreatorEmail(Config.SUPPORT_EMAIL);
        testData.feedbackSessions.get("closingSession").setCreatorEmail(Config.SUPPORT_EMAIL);
        testData.feedbackSessions.get("openingSession").setCreatorEmail(Config.SUPPORT_EMAIL);
        testData.feedbackSessions.get("publishedSession").setCreatorEmail(Config.SUPPORT_EMAIL);
        testData.feedbackQuestions.get("questionForOpeningSession").creatorEmail = Config.SUPPORT_EMAIL;
        testData.feedbackQuestions.get("questionForClosingSession").creatorEmail = Config.SUPPORT_EMAIL;
        testData.feedbackQuestions.get("questionForPublishedSession").creatorEmail = Config.SUPPORT_EMAIL;
        
        // Set closing time of one feedback session to tomorrow
        FeedbackSessionAttributes closingFeedbackSession = testData.feedbackSessions.get("closingSession");
        closingFeedbackSession.setEndTime(TimeHelper.getDateOffsetToCurrentTime(1));

        // Set closing time of one feedback session to 30 mins ago
        FeedbackSessionAttributes closedFeedbackSession = testData.feedbackSessions.get("closedSession");
        closedFeedbackSession.setEndTime(TimeHelper.getMsOffsetToCurrentTime(-1000 * 60 * 30));

        // Set opening time for one feedback session to yesterday
        FeedbackSessionAttributes openingFeedbackSession = testData.feedbackSessions.get("openingSession");
        openingFeedbackSession.setStartTime(TimeHelper.getDateOffsetToCurrentTime(-1));
        
        //Published time for one feedback session already set to some time in the past.
        
        removeAndRestoreDataBundle(testData);
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testFeedbackSessionOpeningReminders() {
        AppUrl openingRemindersUrl = createUrl(Const.ActionURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS);
        loginAdminToPage(browser, openingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSessionClosingReminders() {
        AppUrl closingRemindersUrl = createUrl(Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS);
        loginAdminToPage(browser, closingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSessionClosedReminders() {
        AppUrl closedRemindersUrl = createUrl(Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS);
        loginAdminToPage(browser, closedRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSessionPublishedReminders() {
        AppUrl publishedRemindersUrl = createUrl(Const.ActionURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS);
        loginAdminToPage(browser, publishedRemindersUrl, GenericAppPage.class);
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

}
