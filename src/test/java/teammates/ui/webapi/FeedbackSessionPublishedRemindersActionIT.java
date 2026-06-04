package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionIT extends BaseActionIT<FeedbackSessionPublishedRemindersAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    String getActionUri() {
        return Const.CronJobURIs.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    private FeedbackSession generatePreparedSession() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);

        // FEEDBACK QUESTIONS
        String[] fqKeys = {
                "qn1InSession1InCourse1",
                "qn2InSession1InCourse1",
                "qn3InSession1InCourse1",
                "qn4InSession1InCourse1",
                "qn5InSession1InCourse1",
                "qn6InSession1InCourse1NoResponses",
        };
        return updateSession(session -> {
            Set<FeedbackQuestion> qns = new HashSet<>();
            for (String fqKey : fqKeys) {
                qns.add(logic.getFeedbackQuestion(typicalBundle.feedbackQuestions.get(fqKey).getId()));
            }
            session.setFeedbackQuestions(qns);
            session.setStartTime(now.minusSeconds(oneDay * 3));
            session.setEndTime(now.minusSeconds(oneDay));
            session.setGracePeriod(noGracePeriod);
        });
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
        ______TS("Typical Success Case 1: 9 published-email tasks queued for 1 session that was recently published");
        textExecute_typicalSuccess1();

        ______TS("Typical Success Case 2: No email tasks queued -- session results not published yet");
        textExecute_typicalSuccess2();

        ______TS("Typical Success Case 3: No email tasks queued -- send-published-emails not enabled");
        textExecute_typicalSuccess3();

        ______TS("Typical Success Case 4: No email tasks queued -- resultsVisibleTime is special time");
        textExecute_typicalSuccess4();
    }

    private void textExecute_typicalSuccess1() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();

        generatePreparedSession();

        updateSession(s -> {
            s.setPublishedEmailSent(false);
            s.setResultsVisibleFromTime(now.minusSeconds(thirtyMin)); // recently publish
        });

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        // 1 co-owner, 5 students and 3 instructors,
        verifySpecifiedTasksAdded(Const.TaskQueue.SEND_EMAIL_QUEUE_NAME, 9);
    }

    private void textExecute_typicalSuccess2() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();

        generatePreparedSession();
        updateSession(s -> {
            s.setPublishedEmailSent(false);
            s.setResultsVisibleFromTime(now.plusSeconds(thirtyMin));
        });

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess3() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();

        generatePreparedSession();

        updateSession(s -> {
            s.setPublishedEmailEnabled(false);
            s.setPublishedEmailSent(false);
            s.setResultsVisibleFromTime(now.minusSeconds(thirtyMin)); // recently publish
        });

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess4() {
        generatePreparedSession();

        updateSession(s -> {
            s.setPublishedEmailSent(false);
            s.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER); // special time
        });

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

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
}
