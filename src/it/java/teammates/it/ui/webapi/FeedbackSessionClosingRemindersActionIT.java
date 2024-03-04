package teammates.it.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionClosingRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionClosingRemindersAction}.
 */
public class FeedbackSessionClosingRemindersActionIT extends BaseActionIT<FeedbackSessionClosingRemindersAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
        prepareSession();
    }

    private void prepareSession() {
        // DEADLINE EXTENSIONS
        String[] deKeys = {"student1InCourse1Session1", "instructor1InCourse1Session1"};
        List<DeadlineExtension> exts = new ArrayList<>();
        for (String deKey : deKeys) {
            exts.add(typicalBundle.deadlineExtensions.get(deKey));
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
        List<FeedbackQuestion> qns = new ArrayList<>();
        for (String fqKey : fqKeys) {
            qns.add(typicalBundle.feedbackQuestions.get(fqKey));
        }

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setDeadlineExtensions(exts);
        session.setFeedbackQuestions(qns);
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSING_REMINDERS;
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

        ______TS("Typical Success Case 1: email tasks added for 1 all users of 1 session");
        textExecute_typicalSuccess1();

        ______TS("Typical Success Case 2: email tasks added for 1 all users of 1 session and 1 deadline extension");
        textExecute_typicalSuccess2();

        ______TS("Typical Success Case 3: Only 1 email task queued -- "
                + "0 for session: already sent, "
                + "1 for deadline extension: closing-soon not sent yet");
        textExecute_typicalSuccess3();

        ______TS("Typical Success Case 4: No tasks queued -- "
                + "both session and deadline extensions have already sent closing-soon emails");
        textExecute_typicalSuccess4();

        ______TS("Typical Success Case 5: No tasks queued -- session's closing-soon email disabled");
        textExecute_typicalSuccess5();
    }

    private void textExecute_typicalSuccess1() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setClosingSoonEmailSent(false);
        session.setEndTime(now.plusSeconds((oneHour * 23) + 60));
        session.setGracePeriod(noGracePeriod);

        String[] params = {};

        FeedbackSessionClosingRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isClosingSoonEmailSent());
        assertTrue(session.getDeadlineExtensions().stream().allMatch(de -> !de.isClosingSoonEmailSent()));

        // 7 email tasks queued:
        // 1 co-owner, 5 students and 3 instructors,
        // but 1 student and 1 instructor have deadline extensions (should not receive email)
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 7);
    }

    private void textExecute_typicalSuccess2() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setClosingSoonEmailSent(false);
        session.setEndTime(now.plusSeconds((oneHour * 23) + 60));
        session.setGracePeriod(noGracePeriod);

        DeadlineExtension de = session.getDeadlineExtensions().get(0);
        de.setEndTime(now.plusSeconds(oneHour * 16));

        String[] params = {};

        FeedbackSessionClosingRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isClosingSoonEmailSent());
        assertTrue(de.isClosingSoonEmailSent());

        // 8 email tasks queued:
        // - 7 emails: 1 co-owner, 5 students and 3 instructors,
        //             but 1 student and 1 instructor have deadline extensions (should not receive email)
        // - 1 email:  1 student deadline extension
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 8);
    }

    private void textExecute_typicalSuccess3() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setClosingSoonEmailSent(true);
        session.setEndTime(now.plusSeconds((oneHour * 23) + 60));
        session.setGracePeriod(noGracePeriod);

        DeadlineExtension de = session.getDeadlineExtensions().get(0);
        de.setEndTime(now.plusSeconds(oneHour * 16));
        de.setClosingSoonEmailSent(false);

        String[] params = {};

        FeedbackSessionClosingRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isClosingSoonEmailSent());
        assertTrue(de.isClosingSoonEmailSent());

        // 1 email tasks queued:
        // - 0 emails: session already sent closing-soon emails
        // - 1 email:  1 student deadline extension where closing-soon email not sent yet
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 1);
    }

    private void textExecute_typicalSuccess4() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setClosingSoonEmailSent(true);
        session.setEndTime(now.plusSeconds((oneHour * 23) + 60));
        session.setGracePeriod(noGracePeriod);

        DeadlineExtension de = session.getDeadlineExtensions().get(0);
        de.setEndTime(now.plusSeconds(oneHour * 16));
        de.setClosingSoonEmailSent(true);

        String[] params = {};

        FeedbackSessionClosingRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(session.isClosingSoonEmailSent());
        assertTrue(de.isClosingSoonEmailSent());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess5() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setClosingEmailEnabled(false);
        session.setClosingSoonEmailSent(false);
        session.setEndTime(now.plusSeconds((oneHour * 23) + 60));
        session.setGracePeriod(noGracePeriod);

        DeadlineExtension de = session.getDeadlineExtensions().get(0);
        de.setEndTime(now.plusSeconds(oneHour * 16));
        de.setClosingSoonEmailSent(false);

        String[] params = {};

        FeedbackSessionClosingRemindersAction action1 = getAction(params);
        JsonResult actionOutput1 = getJsonResult(action1);
        MessageOutput response1 = (MessageOutput) actionOutput1.getOutput();

        assertEquals("Successful", response1.getMessage());
        assertTrue(!session.isClosingSoonEmailSent());
        assertTrue(!de.isClosingSoonEmailSent());

        verifyNoTasksAdded();
    }
}
