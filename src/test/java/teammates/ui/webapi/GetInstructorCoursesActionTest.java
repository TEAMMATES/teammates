package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.InstructorCoursesData;

/**
 * Tests for {@link GetInstructorCoursesAction}.
 */
public class GetInstructorCoursesActionTest extends BaseActionTest<GetInstructorCoursesAction, InstructorCoursesData> {

    @Test(groups = GroupNames.ACTION)
    public void getInstructorCoursesAction_activeCourseStatus_returnsActiveCoursesWithPermissions() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.ACTIVE)
                .withAccountAuth(account.id());

        InstructorCoursesData result = execute(request);

        assertEquals(1, result.getCourses().size());
        assertEquals(course.id(), result.getCourses().get(0).getCourseId());
        assertNotNull(result.getInstructorPermissions().get(course.id()));
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorCoursesAction_softDeletedCourseStatus_returnsSoftDeletedCourses() {
        var account = given.account("account");
        var course = given.course("course", c -> c.softDeleted());
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_STATUS, Const.CourseStatus.SOFT_DELETED)
                .withAccountAuth(account.id());

        InstructorCoursesData result = execute(request);

        assertEquals(1, result.getCourses().size());
        assertEquals(course.id(), result.getCourses().get(0).getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorCoursesAction_invalidCourseStatus_throwsInvalidHttpParameterException() {
        var account = given.account("account");
        given.instructor("instructor", i -> i.defaultCourse().account(account.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_STATUS, "invalid")
                .withAccountAuth(account.id());

        assertActionThrows(InvalidHttpParameterException.class, request);
    }
}
