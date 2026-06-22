package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.testng.annotations.Test;

import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.output.FeedbackSessionData;

/**
 * Tests for {@link PublishFeedbackSessionAction}.
 */
public class PublishFeedbackSessionActionTest
        extends BaseActionTest<PublishFeedbackSessionAction, FeedbackSessionData> {

    @Test(groups = GroupNames.ACTION)
    public void publishFeedbackSessionAction_publishedEmailEnabled_queuesPublishedEmails() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().closed().resultsVisibleFromTime(Instant.now().plusSeconds(3600))
                .publishedEmailEnabled(true).publishedEmailSent(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        FeedbackSessionData result = execute(new RequestContext()
                .withParam(teammates.common.util.Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id()));

        assertEquals(session.id(), result.getFeedbackSessionId());
        assertEquals(3, mockTaskQueuer.getTasksAdded().size());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isPublishedEmailSent());
    }

    @Test(groups = GroupNames.ACTION)
    public void publishFeedbackSessionAction_publishedEmailDisabled_doesNotQueuePublishedEmails() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("requester", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse()
                .noCreator().closed().resultsVisibleFromTime(Instant.now().plusSeconds(3600))
                .publishedEmailEnabled(false).publishedEmailSent(false));
        given.feedbackQuestion("qn", fq -> fq.feedbackSession(session.alias()).studentsToSelf());
        persistGivenData(given);

        execute(new RequestContext()
                .withParam(teammates.common.util.Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(instructorAccount.id()));

        assertEquals(0, mockTaskQueuer.getTasksAdded().size());
        assertFalse(getEntityInTransaction(FeedbackSession.class, session.id()).isPublishedEmailSent());
        assertTrue(getEntityInTransaction(FeedbackSession.class, session.id()).isPublished());
    }
}
