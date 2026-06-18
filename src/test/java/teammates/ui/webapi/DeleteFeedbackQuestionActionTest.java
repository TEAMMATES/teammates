package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionTest extends BaseActionTest<DeleteFeedbackQuestionAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteFeedbackQuestionAction_instructorWithModifyPrivilege_deletesQuestion() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        var question = given.feedbackQuestion("question", fq -> fq.feedbackSession(session.alias()));
        persistGivenData(given);

        verifyPresentInDatabase(FeedbackQuestion.class, question.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString())
                .withAccountAuth(account.id());

        execute(request);

        verifyAbsentInDatabase(FeedbackQuestion.class, question.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteFeedbackQuestionAction_instructorWithoutModifyPrivilege_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).noPrivileges());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse());
        var question = given.feedbackQuestion("question", fq -> fq.feedbackSession(session.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_QUESTION_ID, question.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
