package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.logic.api.Logic;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionOpenedRemindersAction}.
 */
public class FeedbackSessionOpenedRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpenedRemindersAction, MessageOutput> {

    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    public void setUpData() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        inTransaction(() -> {
            Set<FeedbackQuestion> questions = new HashSet<>();
            for (String key : new String[] {
                    "qn1InSession1InCourse1",
                    "qn2InSession1InCourse1",
                    "qn3InSession1InCourse1",
                    "qn4InSession1InCourse1",
                    "qn5InSession1InCourse1",
                    "qn6InSession1InCourse1NoResponses",
            }) {
                questions.add(Logic.inst().getFeedbackQuestion(typicalBundle.feedbackQuestions.get(key).getId()));
            }
            Logic.inst().getFeedbackSession(typicalBundle.feedbackSessions.get("session1InCourse1").getId())
                    .setFeedbackQuestions(questions);
        });
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpenedRemindersAction_eligibleSession_queuesEmailsAndMarksSent() {
        long thirtyMin = 60 * 30;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);
        FeedbackSession session = inTransaction(() -> {
            FeedbackSession loaded = Logic.inst().getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            loaded.setOpenedEmailSent(false);
            loaded.setStartTime(now.minusSeconds(thirtyMin));
            loaded.setSessionVisibleFromTime(now.minusSeconds(thirtyMin));
            loaded.setGracePeriod(noGracePeriod);
            return loaded;
        });

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(session.getId()).isOpenedEmailSent()));
        assertEquals(9, mockTaskQueuer.getTasksAdded().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpenedRemindersAction_ineligibleSession_doesNotQueueEmails() {
        long oneDay = 60 * 60 * 24;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);
        FeedbackSession session = inTransaction(() -> {
            FeedbackSession loaded = Logic.inst().getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            loaded.setOpenedEmailSent(false);
            loaded.setStartTime(now.plusSeconds(oneDay));
            loaded.setEndTime(now.plusSeconds(oneDay * 3));
            loaded.setSessionVisibleFromTime(now.minusSeconds(oneDay * 3));
            loaded.setGracePeriod(noGracePeriod);
            return loaded;
        });

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertFalse(inTransaction(() -> Logic.inst().getFeedbackSession(session.getId()).isOpenedEmailSent()));
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }
}
