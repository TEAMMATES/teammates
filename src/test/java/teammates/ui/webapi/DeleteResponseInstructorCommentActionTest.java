package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteResponseInstructorCommentAction}.
 */
public class DeleteResponseInstructorCommentActionTest
        extends BaseActionTest<DeleteResponseInstructorCommentAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteResponseInstructorCommentAction_giverDeletesOwnComment_success() {
        var giverAccount = given.account("giver-account");
        var giver = given.instructor("giver", i -> i.defaultCourse().account(giverAccount.alias()).coOwner());
        var comment = given.responseInstructorComment("comment", c -> c.giver(giver.alias()));
        persistGivenData(given);

        verifyPresentInDatabase(ResponseInstructorComment.class, comment.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.id().toString())
                .withAccountAuth(giverAccount.id());

        execute(request);

        verifyAbsentInDatabase(ResponseInstructorComment.class, comment.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteResponseInstructorCommentAction_nonGiverInstructor_throwsUnauthorizedAccessException() {
        var giverAccount = given.account("giver-account");
        var otherInstructorAccount = given.account("other-instructor-account");
        var giver = given.instructor("giver", i -> i.defaultCourse().account(giverAccount.alias()).coOwner());
        given.instructor("other-instructor", i -> i.defaultCourse().account(otherInstructorAccount.alias()).coOwner());
        var comment = given.responseInstructorComment("comment", c -> c.giver(giver.alias()));
        persistGivenData(given);

        verifyPresentInDatabase(ResponseInstructorComment.class, comment.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.id().toString())
                .withAccountAuth(otherInstructorAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);

        verifyPresentInDatabase(ResponseInstructorComment.class, comment.id());
    }
}
