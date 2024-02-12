package teammates.it.ui.webapi;

import java.time.Duration;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionPublishedRemindersAction;
import teammates.ui.webapi.JsonResult;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}
 */
public class FeedbackSessionPublishedRemindersActionIT extends BaseActionIT<FeedbackSessionPublishedRemindersAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        ______TS("Typical Success Case 1: 1 published-email tasks queued for 1 session that was recently published");
        textExecute_typicalSuccess1();
    
        ______TS("Typical Success Case 2: No email tasks queued -- session results not published yet");
        textExecute_typicalSuccess2();

        ______TS("Typical Success Case 2: No email tasks queued -- send-published-emails not enabled");
        textExecute_typicalSuccess3();

        // test 4: results...Time = Const.TIME_REPRESENTS_LATER
        ______TS("Typical Success Case 2: No email tasks queued -- is recently published, but resultsVisibleTime is special time");
        textExecute_typicalSuccess4();
        // 
    }

    private void textExecute_typicalSuccess1() {
        long oneDay = 60 * 60 * 24;
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(now.minusSeconds(thirtyMin)); // recently published

        session.setStartTime(now.minusSeconds(oneDay * 3));
        session.setEndTime(now.minusSeconds(oneDay));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionPublishedRemindersAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifySpecifiedTasksAdded(Const.TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME, 1);
    }

    private void textExecute_typicalSuccess2() {
        long oneDay = 60 * 60 * 24;
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(now.plusSeconds(thirtyMin));

        session.setStartTime(now.minusSeconds(oneDay * 3));
        session.setEndTime(now.minusSeconds(oneDay));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionPublishedRemindersAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess3() {
        long oneDay = 60 * 60 * 24;
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setPublishedEmailEnabled(false);
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(now.minusSeconds(thirtyMin)); // recently published

        session.setStartTime(now.minusSeconds(oneDay * 3));
        session.setEndTime(now.minusSeconds(oneDay));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionPublishedRemindersAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess4() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER); // special time

        session.setStartTime(now.minusSeconds(oneDay * 3));
        session.setEndTime(now.minusSeconds(oneDay));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionPublishedRemindersAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }
}
