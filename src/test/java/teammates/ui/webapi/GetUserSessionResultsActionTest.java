package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.UserSessionResultsData;

/**
 * Tests for {@link GetUserSessionResultsAction}.
 */
public class GetUserSessionResultsActionTest extends BaseActionTest<GetUserSessionResultsAction, UserSessionResultsData> {

    @Test(groups = GroupNames.ACTION)
    public void getUserSessionResultsAction_previewByInstructor_returnsUserSessionResultsData() {
        var instructorAccount = given.account("instructor-account");
        given.instructor("instructor", i -> i.defaultCourse().account(instructorAccount.alias()).coOwner());
        var studentAccount = given.account("student-account");
        var student = given.student("student", s -> s.defaultCourse().account(studentAccount.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().published());
        given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).studentsToSelf().text());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withParam(Const.ParamsNames.IS_PREVIEW, "true")
                .withAccountAuth(instructorAccount.id());

        UserSessionResultsData result = execute(request);

        assertNotNull(result);
        assertEquals(1, result.getQuestions().size());
        assertFalse(result.getQuestions().isEmpty());
    }

    @Test(groups = GroupNames.ACTION)
    public void getUserSessionResultsAction_byAccount_returnsUserSessionResultsData() {
        var account = given.account("account");
        var student = given.student("student", s -> s.defaultCourse().account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().published());
        given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).studentsToSelf().text());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withParam(Const.ParamsNames.IS_PREVIEW, "false")
                .withAccountAuth(account.id());

        UserSessionResultsData result = execute(request);

        assertNotNull(result);
        assertEquals(1, result.getQuestions().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void getUserSessionResultsAction_byRegKey_returnsUserSessionResultsData() {
        var student = given.student("student", s -> s.defaultCourse());
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().published());
        given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).studentsToSelf().text());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withParam(Const.ParamsNames.IS_PREVIEW, "false")
                .withRegKey(student.regKey());

        UserSessionResultsData result = execute(request);

        assertNotNull(result);
        assertEquals(1, result.getQuestions().size());
    }

    @Test(groups = GroupNames.ACTION)
    public void getUserSessionResultsAction_otherAccount_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var student = given.student("student", s -> s.defaultCourse().account(account.alias()));
        var otherAccount = given.account("other-account");
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().published());
        given.feedbackQuestion("question", q -> q.feedbackSession(session.alias()).studentsToSelf().text());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withParam(Const.ParamsNames.IS_PREVIEW, "false")
                .withAccountAuth(otherAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
