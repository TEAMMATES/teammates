package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosingSoonRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionClosingSoonRemindersAction}.
 */
public class FeedbackSessionClosingSoonRemindersActionIT extends BaseActionIT<FeedbackSessionClosingSoonRemindersAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        prepareSession();
    }

    private void prepareSession() {
        // DEADLINE EXTENSIONS
        String[] deKeys = {"student1InCourse1Session1", "instructor1InCourse1Session1"};
        inTransaction(() -> {
            Set<DeadlineExtension> exts = new HashSet<>();
            for (String deKey : deKeys) {
                exts.add(logic.getDeadlineExtension(typicalBundle.deadlineExtensions.get(deKey).getId()));
            }

            // FEEDBACK QUESTIONS
            String[] fqKeys = {
                    "qn1InSession1InCourse1",
                    "qn2InSession1InCourse1",
                    "qn3InSession1InCourse1",
                    "qn4InSession1InCourse1",
                    "qn5InSession1InCourse1",
                    "qn6InSession1InCourse1NoResponses",
            };
            Set<FeedbackQuestion> qns = new HashSet<>();
            for (String fqKey : fqKeys) {
                qns.add(logic.getFeedbackQuestion(typicalBundle.feedbackQuestions.get(fqKey).getId()));
            }

            FeedbackSession session = logic.getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            session.setDeadlineExtensions(exts);
            session.setFeedbackQuestions(qns);
        });
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_SOON_REMINDERS;
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

    @Override
    protected void testExecute() throws Exception {
        // covered by individual test cases
    }

    @Test
    public void textExecute_typicalSuccess1() {
        ______TS("Typical Success Case 1: email tasks added for 1 all users of 1 session");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setClosingSoonEmailSent(false);
            s.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            s.setGracePeriod(noGracePeriod);
        });

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isClosingSoonEmailSent(session));
        assertTrue(allDeadlineExtensionsMatch(session, de -> !de.isClosingSoonEmailSent()));

        // 7 email tasks queued:
        // 1 co-owner, 5 students and 3 instructors,
        // but 1 student and 1 instructor have deadline extensions (should not receive email)
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 7);
    }

    @Test
    public void textExecute_typicalSuccess2() {
        ______TS("Typical Success Case 2: email tasks added for 1 all users of 1 session and 1 deadline extension");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setClosingSoonEmailSent(false);
            s.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            s.setGracePeriod(noGracePeriod);
        });

        DeadlineExtension de = updateFirstDeadlineExtension(d -> d.setEndTime(now.plusSeconds(oneHour * 16)));

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isClosingSoonEmailSent(session));
        assertTrue(isDeadlineExtensionClosingSoonEmailSent(de));

        // 8 email tasks queued:
        // - 7 emails: 1 co-owner, 5 students and 3 instructors,
        //             but 1 student and 1 instructor have deadline extensions (should not receive email)
        // - 1 email:  1 student deadline extension
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 8);
    }

    @Test
    public void textExecute_typicalSuccess3() {
        ______TS("Typical Success Case 3: Only 1 email task queued -- "
                + "0 for session: already sent, "
                + "1 for deadline extension: closing-soon not sent yet");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setClosingSoonEmailSent(true);
            s.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            s.setGracePeriod(noGracePeriod);
        });

        DeadlineExtension de = updateFirstDeadlineExtension(d -> {
            d.setEndTime(now.plusSeconds(oneHour * 16));
            d.setClosingSoonEmailSent(false);
        });

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isClosingSoonEmailSent(session));
        assertTrue(isDeadlineExtensionClosingSoonEmailSent(de));

        // 1 email tasks queued:
        // - 0 emails: session already sent closing-soon emails
        // - 1 email:  1 student deadline extension where closing-soon email not sent yet
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    public void textExecute_typicalSuccess4() {
        ______TS("Typical Success Case 4: No tasks queued -- "
                + "both session and deadline extensions have already sent closing-soon emails");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setClosingSoonEmailSent(true);
            s.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            s.setGracePeriod(noGracePeriod);
        });

        DeadlineExtension de = updateFirstDeadlineExtension(d -> {
            d.setEndTime(now.plusSeconds(oneHour * 16));
            d.setClosingSoonEmailSent(true);
        });

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(isClosingSoonEmailSent(session));
        assertTrue(isDeadlineExtensionClosingSoonEmailSent(de));

        verifyNoTasksAdded();
    }

    @Test
    public void textExecute_typicalSuccess5() {
        ______TS("Typical Success Case 5: No tasks queued -- session's closing-soon email disabled");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setClosingSoonEmailEnabled(false);
            s.setClosingSoonEmailSent(false);
            s.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            s.setGracePeriod(noGracePeriod);
        });

        DeadlineExtension de = updateFirstDeadlineExtension(d -> {
            d.setEndTime(now.plusSeconds(oneHour * 16));
            d.setClosingSoonEmailSent(false);
        });

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertFalse(isClosingSoonEmailSent(session));
        assertFalse(isDeadlineExtensionClosingSoonEmailSent(de));

        verifyNoTasksAdded();
    }

    @Test
    public void textExecute_typicalSuccess6() {
        ______TS("Typical Success Case 6: No tasks queued -- "
                + "session's closed email already sent, but closing-soon email not yet sent and still within time window");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            // Session has closing-soon email enabled (possibly just recently enabled)
            s.setClosingSoonEmailEnabled(true);
            // Closing-soon email was never sent (because it was disabled before)
            s.setClosingSoonEmailSent(false);
            // Session has already closed and sent closed emails
            s.setClosedEmailSent(true);
            // End time is still within the 2-day window checked by the query
            s.setEndTime(now.minusSeconds(oneHour * 12));
            s.setGracePeriod(noGracePeriod);
        });

        DeadlineExtension de = updateFirstDeadlineExtension(d -> {
            d.setEndTime(now.plusSeconds(oneHour * 16));
            d.setClosingSoonEmailSent(false);
        });

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        // Should still be false as no closing-soon email should be sent for already closed sessions
        assertFalse(isClosingSoonEmailSent(session));
        // Closing-soon email for deadline extension should still be sent
        assertTrue(isDeadlineExtensionClosingSoonEmailSent(de));

        // Only 1 email task should be added for the deadline extension
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
    }

    @Test
    public void textExecute_typicalSuccess7() {
        ______TS("Typical Success Case 7: No tasks queued -- session is shorter than the closing-soon lead time");
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = updateSession(s -> {
            s.setClosingSoonEmailSent(false);
            s.setStartTime(now.minusSeconds(oneHour / 2));
            s.setEndTime(now.plusSeconds(oneHour * 23));
            s.setGracePeriod(noGracePeriod);
            s.getDeadlineExtensions().forEach(de -> de.setClosingSoonEmailSent(true));
        });

        String[] params = {};

        FeedbackSessionClosingSoonRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertFalse(isClosingSoonEmailSent(session));

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

    private DeadlineExtension updateFirstDeadlineExtension(Consumer<DeadlineExtension> updater) {
        return inTransaction(() -> {
            DeadlineExtension deadlineExtension = logic.getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId())
                    .getDeadlineExtensions().iterator().next();
            updater.accept(deadlineExtension);
            return deadlineExtension;
        });
    }

    private boolean isClosingSoonEmailSent(FeedbackSession session) {
        return inTransaction(() -> logic.getFeedbackSession(session.getId()).isClosingSoonEmailSent());
    }

    private boolean isDeadlineExtensionClosingSoonEmailSent(DeadlineExtension deadlineExtension) {
        return inTransaction(() -> logic.getDeadlineExtension(deadlineExtension.getId()).isClosingSoonEmailSent());
    }

    private boolean allDeadlineExtensionsMatch(FeedbackSession session,
                                               java.util.function.Predicate<DeadlineExtension> predicate) {
        return inTransaction(() -> logic.getFeedbackSession(session.getId()).getDeadlineExtensions().stream()
                .allMatch(predicate));
    }
}
