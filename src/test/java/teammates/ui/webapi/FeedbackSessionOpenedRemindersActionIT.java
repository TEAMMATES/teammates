package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.TaskWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;

/**
 * SUT: {@link FeedbackSessionOpenedRemindersAction}.
 */
public class FeedbackSessionOpenedRemindersActionIT extends BaseActionIT<FeedbackSessionOpenedRemindersAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        prepareSession();
    }

    private void prepareSession() {
        // FEEDBACK QUESTIONS
        String[] fqKeys = {
                "qn1InSession1InCourse1",
                "qn2InSession1InCourse1",
                "qn3InSession1InCourse1",
                "qn4InSession1InCourse1",
                "qn5InSession1InCourse1",
                "qn6InSession1InCourse1NoResponses",
        };
        inTransaction(() -> {
            Set<FeedbackQuestion> qns = new HashSet<>();
            for (String fqKey : fqKeys) {
                qns.add(logic.getFeedbackQuestion(typicalBundle.feedbackQuestions.get(fqKey).getId()));
            }
            logic.getFeedbackSession(typicalBundle.feedbackSessions.get("session1InCourse1").getId())
                    .setFeedbackQuestions(qns);
        });
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_OPENED_REMINDERS;
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

        ______TS("Typical Success Case 2: No email tasks added for session -- already sent opened emails");
        testExecute_typicalSuccess2();

        ______TS("Typical Success Case 3: No email tasks added for session -- session not visible yet");
        testExecute_typicalSuccess3();

        ______TS("Typical Success Case 4: No email tasks added for session -- session visible but not open yet");
        testExecute_typicalSuccess4();
    }

    private void testExecute_typicalSuccess1() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setOpenedEmailSent(false);
            s.setStartTime(now.minusSeconds(thirtyMin));
            s.setSessionVisibleFromTime(now.minusSeconds(thirtyMin));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionOpenedRemindersAction action1 = getAction();
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isOpenedEmailSent(session));

        // # of email to send =
        //    # emails sent to instructorsToNotify (ie co-owner), 1 +
        //    # emails sent to students, 5 +
        //    # emails sent to instructors, 3 (including instructorsToNotify)
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 9);

        List<TaskWrapper> tasksAdded = mockTaskQueuer.getTasksAdded();
        for (TaskWrapper task : tasksAdded) {
            SendEmailRequest requestBody = (SendEmailRequest) task.getRequestBody();
            EmailWrapper email = requestBody.getEmail();

            String expectedSubject = (email.getIsCopy() ? EmailWrapper.EMAIL_COPY_SUBJECT_PREFIX : "")
                    + String.format(EmailType.FEEDBACK_OPENED.getSubject(),
                    session.getCourse().getName(), session.getName());
            assertEquals(expectedSubject, email.getSubject());
        }
    }

    private void testExecute_typicalSuccess2() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setOpenedEmailSent(true);
            s.setStartTime(now.minusSeconds(thirtyMin));
            s.setSessionVisibleFromTime(now.minusSeconds(thirtyMin));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionOpenedRemindersAction action1 = getAction();
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isOpenedEmailSent(session));

        verifyNoTasksAdded();
    }

    private void testExecute_typicalSuccess3() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setOpenedEmailSent(false);
            s.setStartTime(now.plusSeconds(thirtyMin * 2));
            s.setSessionVisibleFromTime(now.plusSeconds(thirtyMin));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionOpenedRemindersAction action1 = getAction();
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertFalse(isOpenedEmailSent(session));

        verifyNoTasksAdded();
    }

    private void testExecute_typicalSuccess4() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setOpenedEmailSent(false);
            s.setStartTime(now.plusSeconds(oneDay));
            s.setEndTime(now.plusSeconds(oneDay * 3));
            s.setSessionVisibleFromTime(now.minusSeconds(oneDay * 3));
            s.setGracePeriod(noGracePeriod);
        });

        FeedbackSessionOpenedRemindersAction action1 = getAction();
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertFalse(isOpenedEmailSent(session));

        verifyNoTasksAdded();
    }

    private FeedbackSession updateSession(Consumer<FeedbackSession> updater) {
        return inTransaction(() -> {
            FeedbackSession session = logic.getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            updater.accept(session);
            return session;
        });
    }

    private boolean isOpenedEmailSent(FeedbackSession session) {
        return inTransaction(() -> logic.getFeedbackSession(session.getId()).isOpenedEmailSent());
    }
}
