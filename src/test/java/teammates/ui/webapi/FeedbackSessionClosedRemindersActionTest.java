package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.testng.annotations.Test;

import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionClosedRemindersAction}.
 */
public class FeedbackSessionClosedRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosedRemindersAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosedRemindersAction_recentlyClosedSession_queuesOwnerEmail() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator()
                .startTime(Instant.now().minus(3, ChronoUnit.HOURS))
                .endTime(Instant.now().minus(30, ChronoUnit.MINUTES))
                .sessionVisibleFromTime(Instant.now().minus(4, ChronoUnit.HOURS))
                .resultsVisibleFromTime(Instant.now().plus(1, ChronoUnit.HOURS))
                .closedEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isClosedEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosedRemindersAction_alreadySent_doesNotQueueEmails() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator()
                .startTime(Instant.now().minus(3, ChronoUnit.HOURS))
                .endTime(Instant.now().minus(30, ChronoUnit.MINUTES))
                .sessionVisibleFromTime(Instant.now().minus(4, ChronoUnit.HOURS))
                .resultsVisibleFromTime(Instant.now().plus(1, ChronoUnit.HOURS))
                .closedEmailSent(true));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isClosedEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosedRemindersAction_closedOutsideWindow_doesNotQueueEmails() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().noCreator()
                .startTime(Instant.now().minus(7, ChronoUnit.HOURS))
                .endTime(Instant.now().minus(3, ChronoUnit.HOURS))
                .sessionVisibleFromTime(Instant.now().minus(8, ChronoUnit.HOURS))
                .resultsVisibleFromTime(Instant.now().plus(1, ChronoUnit.HOURS))
                .closedEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(getEntityInTransaction(FeedbackSession.class, session.id()).isClosedEmailSent());
    }
}
