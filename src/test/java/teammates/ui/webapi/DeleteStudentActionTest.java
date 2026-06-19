package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteStudentAction}.
 */
public class DeleteStudentActionTest extends BaseActionTest<DeleteStudentAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteStudentAction_instructorWithModifyPrivilege_deletesStudent() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(requesterAccount.alias()).coOwner());
        var student = given.student("student", s -> s.course(course.alias()));
        persistGivenData(given);

        verifyPresentInDatabase(Student.class, student.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(requesterAccount.id());

        execute(request);

        verifyAbsentInDatabase(Student.class, student.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteStudentAction_instructorNotInCourse_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.course(otherCourse.alias()).account(requesterAccount.alias()).coOwner());
        var student = given.student("student", s -> s.defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteStudentAction_adminDeletesStudent_deletesStudent() {
        var student = given.student("student", s -> s.defaultCourse());
        persistGivenData(given);

        verifyPresentInDatabase(Student.class, student.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAdminAuth();

        execute(request);

        verifyAbsentInDatabase(Student.class, student.id());
    }
}
