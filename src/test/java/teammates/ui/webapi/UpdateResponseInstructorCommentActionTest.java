package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.ResponseInstructorCommentData;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;

/**
 * Tests for {@link UpdateResponseInstructorCommentAction}.
 */
public class UpdateResponseInstructorCommentActionTest
        extends BaseActionTest<UpdateResponseInstructorCommentAction, ResponseInstructorCommentData> {

    @Test(groups = GroupNames.ACTION)
    public void updateResponseInstructorCommentAction_giverUpdatesOwnComment_success() {
        var giverAccount = given.account("giver-account");
        var giver = given.instructor("giver", i -> i.defaultCourse().account(giverAccount.alias()).coOwner());
        var comment = given.responseInstructorComment("comment", c -> c.giver(giver.alias()).commentText("Old text"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.id().toString())
                .withAccountAuth(giverAccount.id())
                .withRequest(new ResponseInstructorCommentUpdateRequest("Updated text"));

        ResponseInstructorCommentData result = execute(request);

        assertEquals(comment.id(), result.getResponseInstructorCommentId());
        assertEquals(giver.id(), result.getGiverId());
        assertEquals("Updated text", result.getCommentText());

        ResponseInstructorComment updatedComment = getEntityInTransaction(ResponseInstructorComment.class, comment.id());
        assertEquals("Updated text", updatedComment.getCommentText());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateResponseInstructorCommentAction_nonGiverInstructor_throwsUnauthorizedAccessException() {
        var giverAccount = given.account("giver-account");
        var otherInstructorAccount = given.account("other-instructor-account");
        var giver = given.instructor("giver", i -> i.defaultCourse().account(giverAccount.alias()).coOwner());
        given.instructor("other-instructor", i -> i.defaultCourse().account(otherInstructorAccount.alias()).coOwner());
        var comment = given.responseInstructorComment("comment", c -> c.giver(giver.alias()).commentText("Old text"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_RESPONSE_COMMENT_ID, comment.id().toString())
                .withAccountAuth(otherInstructorAccount.id())
                .withRequest(new ResponseInstructorCommentUpdateRequest("Updated text"));

        assertActionThrows(UnauthorizedAccessException.class, request);

        ResponseInstructorComment updatedComment = getEntityInTransaction(ResponseInstructorComment.class, comment.id());
        assertEquals("Old text", updatedComment.getCommentText());
    }
}
