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
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionClosingSoonRemindersAction}.
 */
public class FeedbackSessionClosingSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosingSoonRemindersAction, MessageOutput> {

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
            FeedbackSession session = Logic.inst().getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            session.setFeedbackQuestions(questions);
            session.setDeadlineExtensions(Set.of(
                    Logic.inst().getDeadlineExtension(typicalBundle.deadlineExtensions.get("student1InCourse1Session1")
                            .getId()),
                    Logic.inst().getDeadlineExtension(typicalBundle.deadlineExtensions.get("instructor1InCourse1Session1")
                            .getId())));
        });
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosingSoonRemindersAction_eligibleSessionWithoutExtensions_queuesSessionEmails() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);
        FeedbackSession session = inTransaction(() -> {
            FeedbackSession loaded = Logic.inst().getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            loaded.setClosingSoonEmailEnabled(true);
            loaded.setClosingSoonEmailSent(false);
            loaded.setStartTime(now.minusSeconds(oneHour * 24));
            loaded.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            loaded.setGracePeriod(noGracePeriod);
            loaded.getDeadlineExtensions().forEach(deadlineExtension -> deadlineExtension.setClosingSoonEmailSent(true));
            return loaded;
        });

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(session.getId()).isClosingSoonEmailSent()));
        assertEquals(7, mockTaskQueuer.getTasksAdded().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosingSoonRemindersAction_eligibleSessionWithExtension_queuesBothSlices() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);
        FeedbackSession session = inTransaction(() -> {
            FeedbackSession loaded = Logic.inst().getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            loaded.setClosingSoonEmailEnabled(true);
            loaded.setClosingSoonEmailSent(false);
            loaded.setStartTime(now.minusSeconds(oneHour * 24));
            loaded.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            loaded.setGracePeriod(noGracePeriod);
            return loaded;
        });
        DeadlineExtension studentExtension = inTransaction(() -> {
            DeadlineExtension loaded = Logic.inst().getDeadlineExtension(
                    typicalBundle.deadlineExtensions.get("student1InCourse1Session1").getId());
            loaded.setEndTime(now.plusSeconds(oneHour * 16));
            loaded.setClosingSoonEmailSent(false);
            return loaded;
        });
        inTransaction(() -> {
            DeadlineExtension loaded = Logic.inst().getDeadlineExtension(
                    typicalBundle.deadlineExtensions.get("instructor1InCourse1Session1").getId());
            loaded.setClosingSoonEmailSent(true);
            return loaded;
        });

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(session.getId()).isClosingSoonEmailSent()));
        assertTrue(inTransaction(() -> Logic.inst().getDeadlineExtension(studentExtension.getId()).isClosingSoonEmailSent()));
        assertEquals(8, mockTaskQueuer.getTasksAdded().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosingSoonRemindersAction_closingSoonDisabled_doesNotQueueEmails() {
        long oneHour = 60 * 60;
        Instant now = Instant.now();
        Duration noGracePeriod = Duration.between(now, now);
        FeedbackSession session = inTransaction(() -> {
            FeedbackSession loaded = Logic.inst().getFeedbackSession(
                    typicalBundle.feedbackSessions.get("session1InCourse1").getId());
            loaded.setClosingSoonEmailEnabled(false);
            loaded.setClosingSoonEmailSent(false);
            loaded.setStartTime(now.minusSeconds(oneHour * 24));
            loaded.setEndTime(now.plusSeconds((oneHour * 23) + 60));
            loaded.setGracePeriod(noGracePeriod);
            return loaded;
        });
        DeadlineExtension studentExtension = inTransaction(() -> {
            DeadlineExtension loaded = Logic.inst().getDeadlineExtension(
                    typicalBundle.deadlineExtensions.get("student1InCourse1Session1").getId());
            loaded.setEndTime(now.plusSeconds(oneHour * 16));
            loaded.setClosingSoonEmailSent(false);
            return loaded;
        });

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertFalse(inTransaction(() -> Logic.inst().getFeedbackSession(session.getId()).isClosingSoonEmailSent()));
        assertFalse(inTransaction(() -> Logic.inst().getDeadlineExtension(studentExtension.getId()).isClosingSoonEmailSent()));
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
    }
}
