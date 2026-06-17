package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.logic.api.Logic;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionClosingSoonRemindersAction}.
 */
public class FeedbackSessionClosingSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionClosingSoonRemindersAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosingSoonRemindersAction_eligibleSession_queuesParticipantAndPreviewEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().closingSoon().closingSoonEmailEnabled(true).closingSoonEmailSent(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias())
                .studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(2, mockTaskQueuer.getTasksAdded().size());
        assertTrue(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isClosingSoonEmailSent()));
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosingSoonRemindersAction_deadlineExtensionClosingSoon_queuesExtensionEmail() {
        var student = given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().opened().closingSoonEmailEnabled(true));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias())
                .studentsToSelf());
        given.deadlineExtension("ext", de -> de.feedbackSession(session.alias())
                .student(student.alias()).closingSoon().closingSoonEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertTrue(inTransaction(() -> Logic.inst().getDeadlineExtension(given.uuid("ext")).isClosingSoonEmailSent()));
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionClosingSoonRemindersAction_emailDisabled_doesNotQueueEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().closingSoon().closingSoonEmailEnabled(false).closingSoonEmailSent(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias())
                .studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(inTransaction(() -> Logic.inst().getFeedbackSession(given.uuid("session")).isClosingSoonEmailSent()));
    }
}
