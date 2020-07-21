package teammates.test.cases.browsertests;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.e2e.util.Priority;
import teammates.test.driver.TimeHelperExtension;
import teammates.test.pageobjects.GenericAppPage;

/**
 * SUT: {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_OPENING_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_CLOSING_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_CLOSED_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS}.
 */
@Priority(5)
public class AutomatedSessionRemindersTest extends BaseLegacyUiTestCase {

    @Override
    protected void prepareTestData() {
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

        // Set closing time of one feedback session to tomorrow
        FeedbackSessionAttributes closingFeedbackSession = testData.feedbackSessions.get("closingSession");
        closingFeedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));

        // Set closing time of one feedback session to 30 mins ago
        FeedbackSessionAttributes closedFeedbackSession = testData.feedbackSessions.get("closedSession");
        closedFeedbackSession.setEndTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-30));

        // Set opening time for one feedback session to yesterday
        FeedbackSessionAttributes openingFeedbackSession = testData.feedbackSessions.get("openingSession");
        openingFeedbackSession.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));

        //Published time for one feedback session already set to some time in the past.

        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testFeedbackSessionOpeningReminders() {
        AppUrl openingRemindersUrl = createUrl(Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS);
        loginAdminToPageOld(openingRemindersUrl, GenericAppPage.class);
    }

    @Test
    public void testFeedbackSessionClosingReminders() {
        AppUrl closingRemindersUrl = createUrl(Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS);
        loginAdminToPageOld(closingRemindersUrl, GenericAppPage.class);
    }

    @Test
    public void testFeedbackSessionClosedReminders() {
        AppUrl closedRemindersUrl = createUrl(Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS);
        loginAdminToPageOld(closedRemindersUrl, GenericAppPage.class);
    }

    @Test
    public void testFeedbackSessionPublishedReminders() {
        AppUrl publishedRemindersUrl = createUrl(Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS);
        loginAdminToPageOld(publishedRemindersUrl, GenericAppPage.class);
    }

}
