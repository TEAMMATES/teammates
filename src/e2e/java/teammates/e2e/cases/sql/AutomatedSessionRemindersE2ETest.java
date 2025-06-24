package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.TimeHelperExtension;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_OPENED_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_CLOSING_SOON_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_CLOSED_REMINDERS},
 *      {@link Const.CronJobURIs#AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS}.
 */
public class AutomatedSessionRemindersE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/AutomatedSessionRemindersE2ETestSql.json");

        // When running the test against a production server, email alerts will be sent
        // to the specified email address
        // The tester should manually check the email box after running the test suite
        // TODO check if we can automate this checking process

        String testEmail = TestProperties.TEST_EMAIL;
        testData.accounts.get("instructorWithEvals").setEmail(testEmail);
        testData.instructors.get("AutSesRem.instructor").setEmail(testEmail);
        // TODO: separate test email for instructor and student
        testData.students.get("alice.tmms@AutSesRem.course").setEmail("student." + testEmail);
        testData.feedbackSessions.get("openedSession").setCreatorEmail(testEmail);
        testData.feedbackSessions.get("closingSoonSession").setCreatorEmail(testEmail);
        testData.feedbackSessions.get("closedSession").setCreatorEmail(testEmail);
        testData.feedbackSessions.get("publishedSession").setCreatorEmail(testEmail);

        // Set opening time for one feedback session to yesterday
        FeedbackSession openedFeedbackSession = testData.feedbackSessions.get("openedSession");
        openedFeedbackSession.setStartTime(TimeHelper.getInstantDaysOffsetFromNow(-1));

        // Set closing time of one feedback session to tomorrow
        FeedbackSession closingSoonFeedbackSession = testData.feedbackSessions.get("closingSoonSession");
        closingSoonFeedbackSession.setEndTime(TimeHelper.getInstantDaysOffsetFromNow(1));

        // Set closing time of one feedback session to 30 mins ago
        FeedbackSession closedFeedbackSession = testData.feedbackSessions.get("closedSession");
        closedFeedbackSession.setEndTime(TimeHelperExtension.getInstantMinutesOffsetFromNow(-30));

        // Published time for one feedback session already set to some time in the past.

        removeAndRestoreDataBundle(testData);
    }

    @Override
    protected void prepareBrowser() {
        // this test does not require any browser
    }

    @Test
    @Override
    public void testAll() {
        testFeedbackSessionOpeningSoonReminders();
        testFeedbackSessionOpenedReminders();
        testFeedbackSessionClosingSoonReminders();
        testFeedbackSessionClosedReminders();
        testFeedbackSessionPublishedReminders();
    }

    private void testFeedbackSessionOpeningSoonReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS, null);
    }

    private void testFeedbackSessionOpenedReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENED_REMINDERS, null);
    }

    private void testFeedbackSessionClosingSoonReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_SOON_REMINDERS, null);
    }

    private void testFeedbackSessionClosedReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS, null);
    }

    private void testFeedbackSessionPublishedReminders() {
        BACKDOOR.executeGetRequest(Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS, null);
    }

}
