package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.logic.api.Logic;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionOpenedRemindersAction}.
 */
public class FeedbackSessionOpenedRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpenedRemindersAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpenedRemindersAction_openedSession_queuesParticipantAndPreviewEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().opened().openedEmailSent(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias())
                .studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(2, mockTaskQueuer.getTasksAdded().size());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isOpenedEmailSent()));
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpenedRemindersAction_notYetOpenedSession_doesNotQueueEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .waitingToOpen().openedEmailSent(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias())
                .studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isOpenedEmailSent()));
    }
}
