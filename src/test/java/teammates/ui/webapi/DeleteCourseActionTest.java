package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link DeleteCourseAction}.
 */
public class DeleteCourseActionTest extends BaseActionTest<DeleteCourseAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void deleteCourseAction_instructorWithModifyPrivilege_deletesCourse() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).coOwner());
        persistGivenData(given);

        verifyPresentInDatabase(Course.class, course.id());

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        execute(request);

        verifyAbsentInDatabase(Course.class, course.id());
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteCourseAction_instructorWithoutModifyPrivilege_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).noPrivileges());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void deleteCourseAction_instructorNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.course(otherCourse.alias()).account(account.alias()).coOwner());
        var course = given.course("course");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
