package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteStudentsAction}.
 */
public class DeleteStudentsActionTest extends BaseActionTest<DeleteStudentsAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteStudentsAction_instructorWithModifyPrivilege_deletesAllStudentsInCourse() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(requesterAccount.alias()).coOwner());
        var student1 = given.student("student1", s -> s.course(course.alias()));
        var student2 = given.student("student2", s -> s.course(course.alias()));
        persistGivenData(given);

        verifyPresentInDatabase(Student.class, student1.id());
        verifyPresentInDatabase(Student.class, student2.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(requesterAccount.id());

        execute(request);

        verifyAbsentInDatabase(Student.class, student1.id());
        verifyAbsentInDatabase(Student.class, student2.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteStudentsAction_instructorWithModifyPrivilege_doesNotDeleteStudentsInOtherCourse() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(requesterAccount.alias()).coOwner());
        given.student("student-in-course", s -> s.course(course.alias()));
        var otherCourse = given.course("other-course");
        var studentInOtherCourse = given.student("student-in-other-course", s -> s.course(otherCourse.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(requesterAccount.id());

        execute(request);

        verifyPresentInDatabase(Student.class, studentInOtherCourse.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteStudentsAction_instructorNotInCourse_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.course(otherCourse.alias()).account(requesterAccount.alias()).coOwner());
        var course = given.course("course");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
