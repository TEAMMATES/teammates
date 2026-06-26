package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionKeyAccessDecision;
import teammates.common.datatransfer.SessionKeyType;
import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.output.SessionKeyAccessData;
import teammates.ui.request.SessionKeyAccessRequest;

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
                .withRequest(sessionKeyRequest(session.id(), student.regKey(), SessionKeyType.SUBMISSION));
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
                .withRequest(sessionKeyRequest(session.id(), student.regKey(), SessionKeyType.SUBMISSION));
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
                .withRequest(sessionKeyRequest(session.id(), student.regKey(), SessionKeyType.SUBMISSION))
                .withAccountAuth(account.id());
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
                .withRequest(sessionKeyRequest(session.id(), student.regKey(), SessionKeyType.SUBMISSION))
                .withAccountAuth(otherAccount.id());
        request.uri = Const.ResourceURIs.SESSION_KEY_ACCESS;

        SessionKeyAccessData result = execute(request);

        assertEquals(SessionKeyAccessDecision.SIGN_IN_WITH_ANOTHER_ACCOUNT, result.getDecision());
    }

    private SessionKeyAccessRequest sessionKeyRequest(java.util.UUID feedbackSessionId, String regKey,
            SessionKeyType type) {
        SessionKeyAccessRequest requestBody = new SessionKeyAccessRequest();
        requestBody.setFeedbackSessionId(feedbackSessionId);
        requestBody.setKey(regKey);
        requestBody.setType(type);
        return requestBody;
    }
}
