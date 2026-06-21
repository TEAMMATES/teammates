package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackQuestionsData;

/**
 * Tests for {@link GetFeedbackQuestionsAction}.
 */
public class GetFeedbackQuestionsActionTest extends BaseActionTest<GetFeedbackQuestionsAction, FeedbackQuestionsData> {

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackQuestionsAction_instructorReturnsFullDetailQuestions() {
        var account = given.account("instructor-account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        var question = given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).number(3)
                .description("question description")
                .text("question brief"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(account.id());

        FeedbackQuestionsData result = execute(request);

        assertNotNull(result);
        assertEquals(1, result.getQuestions().size());
        assertEquals(question.id(), result.getQuestions().get(0).getFeedbackQuestionId());
        assertEquals(1, result.getQuestions().get(0).getQuestionNumber());
        assertEquals("question brief", result.getQuestions().get(0).getQuestionBrief());
        assertEquals("question description", result.getQuestions().get(0).getQuestionDescription());
    }

    @Test(groups = GroupNames.ACTION)
    public void getFeedbackQuestionsAction_studentThrowsUnauthorizedAccessException() {
        var account = given.account("student-account");
        given.student("student", s -> s.defaultCourse().account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).text("question brief"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
