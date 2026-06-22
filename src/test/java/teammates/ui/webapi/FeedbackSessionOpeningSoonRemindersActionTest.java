package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.SendEmailRequest;

/**
 * Tests for {@link FeedbackSessionOpeningSoonRemindersAction}.
 */
public class FeedbackSessionOpeningSoonRemindersActionTest
        extends BaseActionTest<FeedbackSessionOpeningSoonRemindersAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_eligibleSession_queuesOwnerEmail() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().openingSoon().openingSoonEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isOpeningSoonEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_alreadySent_doesNotQueueEmails() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().openingSoon().openingSoonEmailSent(true));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isOpeningSoonEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_notOpeningSoon_doesNotQueueEmails() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().waitingToOpen().openingSoonEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(getEntityInTransaction(FeedbackSession.class, session.id()).isOpeningSoonEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void feedbackSessionOpeningSoonRemindersAction_unjoinedCoOwner_queuesJoinVariantEmail() {
        given.instructor("coOwner", i -> i.defaultCourse().coOwner().noAccount());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().openingSoon().openingSoonEmailSent(false));
        persistGivenData(given);

        MessageOutput result = execute(new RequestContext().withWorkerAuth());

        assertEquals("Successful", result.getMessage());
        assertEquals(1, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isOpeningSoonEmailSent());

        SendEmailRequest emailRequest = (SendEmailRequest) mockTaskQueuer.getTasksAdded().get(0).getRequestBody();
        assertTrue(emailRequest.getEmail().getContent().contains("/web/join?key="));
    }
}
