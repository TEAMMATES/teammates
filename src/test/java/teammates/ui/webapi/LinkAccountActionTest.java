package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionKeyType;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.LinkAccountRequest;

/**
 * Tests for {@link LinkAccountAction}.
 */
public class LinkAccountActionTest extends BaseActionTest<LinkAccountAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void linkAccountAction_matchingAccountAndStudent_linksAccount() {
        var account = given.account("account");
        var student = given.student("student");
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        LinkAccountRequest requestBody = new LinkAccountRequest();
        requestBody.setAccountId(account.id());
        requestBody.setUserId(student.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.linkVersion(), session.id())
                .withRequest(requestBody);

        MessageOutput result = execute(request);

        assertEquals("Account linked successfully.", result.getMessage());
        assertEquals(account.id(), getEntityInTransaction(teammates.storage.entity.Student.class, student.id())
                .getAccount().getId());
    }

    @Test(groups = GroupNames.ACTION)
    public void linkAccountAction_mismatchedAccount_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var otherAccount = given.account("other-account");
        var student = given.student("student");
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        LinkAccountRequest requestBody = new LinkAccountRequest();
        requestBody.setAccountId(otherAccount.id());
        requestBody.setUserId(student.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.linkVersion(), session.id())
                .withRequest(requestBody);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void linkAccountAction_mismatchedUserId_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var student = given.student("student");
        var otherStudent = given.student("other-student");
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        LinkAccountRequest requestBody = new LinkAccountRequest();
        requestBody.setAccountId(account.id());
        requestBody.setUserId(otherStudent.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.linkVersion(), session.id())
                .withRequest(requestBody);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void linkAccountAction_alreadyJoined_throwsInvalidOperationException() {
        var account = given.account("account");
        var student = given.student("student", s -> s.account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.defaultCourse().opened());
        persistGivenData(given);

        LinkAccountRequest requestBody = new LinkAccountRequest();
        requestBody.setAccountId(account.id());
        requestBody.setUserId(student.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withStudentSessionKey(student.id(), SessionKeyType.SUBMISSION, student.linkVersion(), session.id())
                .withRequest(requestBody);

        assertActionThrows(InvalidOperationException.class, request);
    }
}
