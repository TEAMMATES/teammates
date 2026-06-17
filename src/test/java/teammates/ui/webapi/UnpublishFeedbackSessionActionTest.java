package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.testng.annotations.Test;

import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.FeedbackSessionData;

/**
 * Tests for {@link UnpublishFeedbackSessionAction}.
 */
public class UnpublishFeedbackSessionActionTest
        extends BaseActionTest<UnpublishFeedbackSessionAction, FeedbackSessionData> {

    @Test(groups = GroupNames.ACTION)
    public void unpublishFeedbackSessionAction_publishedEmailEnabled_queuesUnpublishedEmails() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().published().publishedEmailEnabled(true));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        FeedbackSessionData result = execute(new RequestContext()
                .withParam(teammates.common.util.Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id()));

        assertEquals(session.id(), result.getFeedbackSessionId());
        assertEquals(3, mockTaskQueuer.getTasksAdded().size());
        assertFalse(getEntityInTransaction(FeedbackSession.class, session.id()).isPublished());
    }

    @Test(groups = GroupNames.ACTION)
    public void unpublishFeedbackSessionAction_publishedEmailDisabled_doesNotQueueUnpublishedEmails() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().published().publishedEmailEnabled(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        execute(new RequestContext()
                .withParam(teammates.common.util.Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id()));

        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(getEntityInTransaction(FeedbackSession.class, session.id()).isPublished());
    }
}
