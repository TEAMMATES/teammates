package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.KeyUtil;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.CourseJoinKeyRequest;

/**
 * Tests for {@link JoinCourseAction}.
 */
public class JoinCourseActionTest extends BaseActionTest<JoinCourseAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void joinCourseAction_unregisteredStudent_success() {
        var account = given.account("account");
        var student = given.student("student");
        persistGivenData(given);

        Student persistedStudent = getEntityInTransaction(Student.class, student.id());
        CourseJoinKeyRequest requestBody = new CourseJoinKeyRequest();
        requestBody.setKey(KeyUtil.encryptCourseJoinKey(persistedStudent.getId(), persistedStudent.getLinkVersion()));

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(requestBody);

        MessageOutput result = execute(request);

        assertEquals("User successfully joined course", result.getMessage());

        Student joinedStudent = getEntityInTransaction(Student.class, student.id());
        assertEquals(account.id(), joinedStudent.getAccount().getId());
    }

    @Test(groups = GroupNames.ACTION)
    public void joinCourseAction_userAlreadyJoined_throwsInvalidOperationException() {
        var account = given.account("account");
        var student = given.student("student", s -> s.account(account.alias()));
        persistGivenData(given);

        Student persistedStudent = getEntityInTransaction(Student.class, student.id());
        CourseJoinKeyRequest requestBody = new CourseJoinKeyRequest();
        requestBody.setKey(KeyUtil.encryptCourseJoinKey(persistedStudent.getId(), persistedStudent.getLinkVersion()));

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(requestBody);

        InvalidOperationException exception = assertActionThrows(InvalidOperationException.class, request);

        assertEquals("User has already joined course", exception.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void joinCourseAction_invalidKey_throwsInvalidHttpRequestBodyException() {
        var account = given.account("account");
        persistGivenData(given);

        CourseJoinKeyRequest requestBody = new CourseJoinKeyRequest();
        requestBody.setKey("invalid-encrypted-key");

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(requestBody);

        assertActionThrows(InvalidHttpRequestBodyException.class, request);
    }
}
