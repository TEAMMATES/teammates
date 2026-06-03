package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosedRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionIT extends BaseActionIT<FeedbackSessionClosedRemindersAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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

        FeedbackSession session = updateSession(s -> {
            s.setClosedEmailSent(false);
            s.setEndTime(now.minusSeconds(thirtyMin));
            s.setGracePeriod(noGracePeriod);
        });

        String[] params = {};

        FeedbackSessionClosedRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isClosedEmailSent(session));

        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);

        ______TS("Success Case: no sessions to consider (`session` already sent closed email)");
        updateSession(s -> {
            s.setClosedEmailSent(true);
            s.setEndTime(now.minusSeconds(thirtyMin));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionClosedRemindersAction action2 = getAction(params);
        JsonResult actionOutput2 = getJsonResult(action2);
        MessageOutput response2 = (MessageOutput) actionOutput2.getOutput();

        assertEquals("Successful", response2.getMessage());
        verifyNoTasksAdded();

        ______TS("Success Case: email task added (`session` closed more than 1 hour ago but within redundancy window)");
        session = updateSession(s -> {
            s.setClosedEmailSent(false);
            s.setEndTime(now.minusSeconds(thirtyMin * 3));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionClosedRemindersAction action3 = getAction(params);
        JsonResult actionOutput3 = getJsonResult(action3);
        MessageOutput response3 = (MessageOutput) actionOutput3.getOutput();

        assertEquals("Successful", response3.getMessage());
        assertTrue(isClosedEmailSent(session));
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);

        ______TS("Success Case: no sessions to consider (`session` closed outside redundancy window)");
        updateSession(s -> {
            s.setClosedEmailSent(false);
            s.setEndTime(now.minusSeconds(thirtyMin * 5));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionClosedRemindersAction action4 = getAction(params);
        JsonResult actionOutput4 = getJsonResult(action4);
        MessageOutput response4 = (MessageOutput) actionOutput4.getOutput();

        assertEquals("Successful", response4.getMessage());
        verifyNoTasksAdded();
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }

    private FeedbackSession updateSession(Consumer<FeedbackSession> updater) {
        return inTransaction(() -> {
            FeedbackSession session = logic.getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            updater.accept(session);
            return session;
        });
    }

    private boolean isClosedEmailSent(FeedbackSession session) {
        return inTransaction(() -> logic.getFeedbackSession(session.getId()).isClosedEmailSent());
    }

}
