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
import teammates.ui.webapi.FeedbackSessionOpeningRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionOpeningRemindersAction}.
 */
public class FeedbackSessionOpeningRemindersActionIT extends BaseActionIT<FeedbackSessionOpeningRemindersAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENING_REMINDERS;
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

        ______TS("Typical Success Case 1: Email tasks added for co-owner, students and instructors of 1 session");
        testExecute_typicalSuccess1();

        ______TS("Typical Success Case 2: No email tasks added for session -- already sent opening emails");
        testExecute_typicalSuccess2();

        ______TS("Typical Success Case 3: No email tasks added for session -- session not visible yet");
        testExecute_typicalSuccess3();
    }

    private void testExecute_typicalSuccess1() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setOpenEmailSent(false);
        session.setStartTime(now.minusSeconds(thirtyMin));
        session.setSessionVisibleFromTime(now.minusSeconds(thirtyMin));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionOpeningRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isOpenEmailSent());

        // # of email to send =
        //    # emails sent to instructorsToNotify (ie co-owner), 1 +
        //    # emails sent to students, 4 +
        //    # emails sent to instructors, 3 (including instructorsToNotify)
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 8);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();

            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_OPENING.getSubject(),
                    session.getCourse().getName(), session.getName());
            assertEquals(expectedSubject, email.getSubject());
        }
    }

    private void testExecute_typicalSuccess2() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setOpenEmailSent(true);
        session.setStartTime(now.minusSeconds(thirtyMin));
        session.setSessionVisibleFromTime(now.minusSeconds(thirtyMin));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionOpeningRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isOpenEmailSent());

        verifyNoTasksAdded();
    }

    private void testExecute_typicalSuccess3() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setOpenEmailSent(false);
        session.setStartTime(now.plusSeconds(thirtyMin * 2));
        session.setSessionVisibleFromTime(now.plusSeconds(thirtyMin));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionOpeningRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertFalse(session.isOpenEmailSent());

        verifyNoTasksAdded();
    }
}
