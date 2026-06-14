package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.StudentData;

/**
 * Tests for {@link GetOwnStudentAction}.
 */
public class GetOwnStudentActionTest extends BaseActionTest<GetOwnStudentAction, StudentData> {

    @Test(groups = GroupNames.ACTION)
    public void getOwnStudentAction_byAccount_returnsStudentData() {
        var account = given.account("account");
        var course = given.course("course");
        var student = given.student("student", s -> s.account(account.alias()).course(course.alias())
                .comments("student-comments"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withCookie(getAuthCookie(account.id()));

        StudentData result = execute(request);

        assertEquals(student.id(), result.getUserId());
        assertEquals(course.id(), result.getCourseId());
        // Comments and join state should be hidden from the student
        assertNull(result.getComments());
        assertNull(result.getJoinState());
    }

    @Test(groups = GroupNames.ACTION)
    public void getOwnStudentAction_byRegKey_returnsStudentData() {
        var course = given.course("course");
        var student = given.student("student", s ->
                s.course(course.alias()).comments("student-comments"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withRegKey(student.regKey());

        StudentData result = execute(request);

        assertEquals(student.id(), result.getUserId());
        assertEquals(course.id(), result.getCourseId());
        // Comments and join state should be hidden from the student
        assertNull(result.getComments());
        assertNull(result.getJoinState());
    }

    @Test(groups = GroupNames.ACTION)
    public void getOwnStudentAction_requestWithoutStudent_throwsEntityNotFoundException() {
        var account = given.account("account");
        var course = given.course("course");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withCookie(getAuthCookie(account.id()));

        assertActionThrows(EntityNotFoundException.class, request);
    }
}
