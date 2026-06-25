package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionKeyAccessDecision;
import teammates.common.datatransfer.SessionKeyType;
import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.output.SessionKeyAccessData;

/**
 * Tests for {@link GetSessionKeyAccessAction}.
 */
public class GetSessionKeyAccessActionTest extends BaseActionTest<GetSessionKeyAccessAction, SessionKeyAccessData> {

    @Test(groups = GroupNames.ACTION)
    public void getSessionKeyAccessAction_studentWithoutAccount_allowsAccess() {
        var student = given.student("student");
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.regKey(), session.id());
        request.uri = Const.ResourceURIs.SESSION_KEY_ACCESS;

        SessionKeyAccessData result = execute(request);

        assertEquals(SessionKeyAccessDecision.ALLOW_UNREGISTERED, result.getDecision());
        assertEquals(null, result.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void getSessionKeyAccessAction_studentWithLinkedAccount_requiresSignIn() {
        var account = given.account("account");
        var student = given.student("student", s -> s.account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.regKey(), session.id());
        request.uri = Const.ResourceURIs.SESSION_KEY_ACCESS;

        SessionKeyAccessData result = execute(request);

        assertEquals(SessionKeyAccessDecision.SIGN_IN_REQUIRED, result.getDecision());
    }

    @Test(groups = GroupNames.ACTION)
    public void getSessionKeyAccessAction_studentWithLinkedAccount_allowsSignedInAccess() {
        var account = given.account("account");
        var student = given.student("student", s -> s.account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(account.id())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.regKey(), session.id());
        request.uri = Const.ResourceURIs.SESSION_KEY_ACCESS;

        SessionKeyAccessData result = execute(request);

        assertEquals(SessionKeyAccessDecision.ALLOW_SIGNED_IN, result.getDecision());
    }

    @Test(groups = GroupNames.ACTION)
    public void getSessionKeyAccessAction_studentWithWrongAccount_requiresAnotherAccount() {
        var account = given.account("account");
        var otherAccount = given.account("other-account");
        var student = given.student("student", s -> s.account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withAccountAuth(otherAccount.id())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.regKey(), session.id());
        request.uri = Const.ResourceURIs.SESSION_KEY_ACCESS;

        SessionKeyAccessData result = execute(request);

        assertEquals(SessionKeyAccessDecision.SIGN_IN_WITH_ANOTHER_ACCOUNT, result.getDecision());
    }
}
