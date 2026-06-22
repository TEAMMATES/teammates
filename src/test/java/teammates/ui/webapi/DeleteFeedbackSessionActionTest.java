package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteFeedbackSessionAction}.
 */
public class DeleteFeedbackSessionActionTest extends BaseActionTest<DeleteFeedbackSessionAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteFeedbackSessionAction_instructorWithModifyPrivilege_deletesSession() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        persistGivenData(given);

        verifyPresentInDatabase(FeedbackSession.class, session.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(account.id());

        execute(request);

        verifyAbsentInDatabase(FeedbackSession.class, session.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteFeedbackSessionAction_instructorWithoutModifyPrivilege_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).noPrivileges());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteFeedbackSessionAction_instructorNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.course(otherCourse.alias()).account(account.alias()).coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
