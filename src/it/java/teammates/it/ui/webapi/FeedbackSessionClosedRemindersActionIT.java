package teammates.it.ui.webapi;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosedRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionIT extends BaseActionIT<FeedbackSessionClosedRemindersAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        loginAsAdmin();

        ______TS("Typical Success Case: email task added for 1 owner of session");

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setClosedEmailSent(false);
        session.setEndTime(now.minusSeconds(thirtyMin));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionClosedRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isClosedEmailSent());

        verifySpecifiedTasksAdded("send-email-queue", 1);

        ______TS("Success Case: no sessions to consider (`session` already sent closed email)");
        session.setClosedEmailSent(true);
        session.setEndTime(now.minusSeconds(thirtyMin));
        session.setGracePeriod(noGracePeriod);

        FeedbackSessionClosedRemindersAction action2 = getAction(params);
        JsonResult actionOutput2 = getJsonResult(action2);
        MessageOutput response2 = (MessageOutput) actionOutput2.getOutput();

        assertEquals("Successful", response2.getMessage());
        verifyNoTasksAdded();

        ______TS("Success Case: no sessions to consider (`session` closed more than 1 hour ago)");
        session.setClosedEmailSent(false);
        session.setEndTime(now.minusSeconds(thirtyMin * 3));
        session.setGracePeriod(noGracePeriod);

        FeedbackSessionClosedRemindersAction action3 = getAction(params);
        JsonResult actionOutput3 = getJsonResult(action3);
        MessageOutput response3 = (MessageOutput) actionOutput3.getOutput();

        assertEquals("Successful", response3.getMessage());
        verifyNoTasksAdded();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

}
