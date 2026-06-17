package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link FeedbackSessionPublishedRemindersAction}.
 */
public class FeedbackSessionPublishedRemindersActionTest
        extends BaseActionTest<FeedbackSessionPublishedRemindersAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionPublishedRemindersAction_publishedSession_queuesParticipantAndPreviewEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().published().publishedEmailSent(false).publishedEmailEnabled(true));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(3, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isPublishedEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionPublishedRemindersAction_alreadySent_doesNotQueueEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().published().publishedEmailSent(true).publishedEmailEnabled(true));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isPublishedEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionPublishedRemindersAction_publishedEmailDisabled_doesNotQueueEmails() {
        given.student("student", s -> s.defaultCourse());
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().published().publishedEmailSent(false).publishedEmailEnabled(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(getEntityInTransaction(FeedbackSession.class, session.id()).isPublishedEmailSent());
    }
}
