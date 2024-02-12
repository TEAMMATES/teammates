package teammates.it.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.common.util.TaskWrapper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;
import teammates.ui.webapi.FeedbackSessionOpeningSoonRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionIT extends BaseActionIT<FeedbackSessionOpeningSoonRemindersAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_SOON_REMINDERS;
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
        loginAsAdmin();

        ______TS("Typical Success Case 1: Add 1 email task for 1 opening-soon session with only 1 co-owner");
        textExecute_typicalSuccess1();

        ______TS("Typical Success Case 2: No email task queued -- opening soon email already sent");
        textExecute_typicalSuccess2();

        ______TS("Typical Success Case 3: No email task queued -- feedback session not opening soon");
        textExecute_typicalSuccess3();
    }

    private void textExecute_typicalSuccess1() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setOpeningSoonEmailSent(false);
        session.setStartTime(now.plusSeconds(oneDay));
        session.setEndTime(now.plusSeconds(oneDay * 3));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionOpeningSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isOpeningSoonEmailSent());

        // Notify only co-owner (1 instructor only for session1InCourse1)
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        String emailSubjectFormat = EmailType.FEEDBACK_OPENING_SOON.getSubject();
        String courseName = session.getCourse().getName();
        String sessionName = session.getName();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();
            assertEquals(
                    String.format(emailSubjectFormat, courseName, sessionName),
                    email.getSubject());

        }
    }

    private void textExecute_typicalSuccess2() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setOpeningSoonEmailSent(true);
        session.setStartTime(now.plusSeconds(oneDay));
        session.setEndTime(now.plusSeconds(oneDay * 3));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionOpeningSoonRemindersAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());
        assertTrue(session.isOpeningSoonEmailSent());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess3() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setOpeningSoonEmailSent(false);
        session.setStartTime(now.plusSeconds(oneDay + 60));
        session.setEndTime(now.plusSeconds(oneDay * 3));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionOpeningSoonRemindersAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());
        assertFalse(session.isOpeningSoonEmailSent());

        verifyNoTasksAdded();
    }
}
