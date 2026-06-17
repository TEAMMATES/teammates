package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.logic.api.Logic;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningSoonRemindersAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_eligibleSession_queuesOwnerEmail() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().openingSoon().openingSoonEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isOpeningSoonEmailSent()));
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_alreadySent_doesNotQueueEmails() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().openingSoon().openingSoonEmailSent(true));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isOpeningSoonEmailSent()));
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_notOpeningSoon_doesNotQueueEmails() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().waitingToOpen().openingSoonEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isOpeningSoonEmailSent()));
    }
}
