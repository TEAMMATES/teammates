package teammates.test.cases.ui.browsertests;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.test.driver.TestProperties;
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
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/AutomatedSessionRemindersTest.json");
        
        //Set opening time of one evaluation within last hour
        EvaluationAttributes openingEval = testData.evaluations.get("openingEval");
        openingEval.startTime = TimeHelper.getMsOffsetToCurrentTime(-1);
        
        //Set closing time of one evaluation in 23+ hours ahead of now.
        EvaluationAttributes closingEval = testData.evaluations.get("closingEval");
        int _23hours59min_InMilliSeconds = (60*23+59)*60*1000;
        closingEval.endTime = TimeHelper.getMsOffsetToCurrentTime(_23hours59min_InMilliSeconds);
        
        //Set closing time of one feedback session in 23+ hours ahead of now.
        FeedbackSessionAttributes closingFeedbackSession = testData.feedbackSessions.get("closingSession");
        closingFeedbackSession.endTime = TimeHelper.getMsOffsetToCurrentTime(_23hours59min_InMilliSeconds);

        //Opening time for one feedback session already set to some time in the past.
        
        //Published time for one feedback session already set to some time in the past.
        
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
    }
    
    /* In these tests, we set the email address of a student to be the same as the
     * support email address and trigger email alerts. When running these tests
     * against a production server, these alerts will appear in the admin's
     * email box. The admin should manually check the email box after running 
     * the test suite. 
     */
    
    @Test
    public void testEvaluationOpeningReminders(){
        Url openingRemindersUrl = new Url(
                TestProperties.inst().TEAMMATES_URL+ 
                Const.ActionURIs.AUTOMATED_EVAL_OPENING_REMINDERS);
        loginAdminToPage(browser, openingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testEvaluationClosingReminders(){
        Url closingRemindersUrl = new Url(TestProperties.inst().TEAMMATES_URL+
                Const.ActionURIs.AUTOMATED_EVAL_CLOSING_REMINDERS);
        loginAdminToPage(browser, closingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSessionOpeningReminders(){
        Url openingRemindersUrl = new Url(
                TestProperties.inst().TEAMMATES_URL+ 
                Const.ActionURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS);
        loginAdminToPage(browser, openingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSesssionClosingReminders(){
        Url closingRemindersUrl = new Url(TestProperties.inst().TEAMMATES_URL+
                Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS);
        loginAdminToPage(browser, closingRemindersUrl, GenericAppPage.class);
    }
    
    @Test
    public void testFeedbackSessionPublishedReminders(){
        Url publishedRemindersUrl = new Url(
                TestProperties.inst().TEAMMATES_URL+ 
                Const.ActionURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS);
        loginAdminToPage(browser, publishedRemindersUrl, GenericAppPage.class);
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }

}
