package teammates.it.ui.webapi;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.FeedbackSessionPublishedRemindersAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionIT extends BaseActionIT<FeedbackSessionPublishedRemindersAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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
        List<FeedbackQuestion> qns = new ArrayList<>();
        for (String fqKey : fqKeys) {
            qns.add(typicalBundle.feedbackQuestions.get(fqKey));
        }

        FeedbackSession session = typicalBundle.feedbackSessions.get("session1InCourse1");
        session.setFeedbackQuestions(qns);
        session.setStartTime(now.minusSeconds(oneDay * 3));
        session.setEndTime(now.minusSeconds(oneDay));
        session.setGracePeriod(noGracePeriod);

        return session;
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

        FeedbackSession session = generatePreparedSession();

        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(now.minusSeconds(thirtyMin)); // recently publish

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

        FeedbackSession session = generatePreparedSession();
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(now.plusSeconds(thirtyMin));

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess3() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();

        FeedbackSession session = generatePreparedSession();

        session.setPublishedEmailEnabled(false);
        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(now.minusSeconds(thirtyMin)); // recently publish

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }

    private void textExecute_typicalSuccess4() {
        FeedbackSession session = generatePreparedSession();

        session.setPublishedEmailSent(false);
        session.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER); // special time

        FeedbackSessionPublishedRemindersAction action = getAction();
        JsonResult actionOutput = getJsonResult(action);
        MessageOutput response = (MessageOutput) actionOutput.getOutput();

        assertEquals("Successful", response.getMessage());

        verifyNoTasksAdded();
    }
}
