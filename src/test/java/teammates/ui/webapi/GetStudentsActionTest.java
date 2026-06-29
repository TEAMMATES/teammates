package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentsData;

/**
 * Tests for {@link GetStudentsAction}.
 */
public class GetStudentsActionTest extends BaseActionTest<GetStudentsAction, StudentsData> {

    @Test(groups = GroupNames.ACTION)
    public void getStudentsAction_adminCanSearchAcrossCourses_returnsLimitedStudentsWithAdminInfo() {
        var studentAccount = given.account("student-account");
        var course1 = given.course("course-1", c -> c.name("Shared Course 1"));
        var course2 = given.course("course-2", c -> c.name("Shared Course 2"));
        var firstMatch = given.student("first-match",
                s -> s.course(course1.alias()).account(studentAccount.alias()).name("Shared Alice"));
        given.student("second-match", s -> s.course(course2.alias()).name("Shared Bob"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.COURSE_ID, course1.id())
                .withParam(Const.ParamsNames.COURSE_ID, course2.id())
                .withParam(Const.ParamsNames.SEARCH_KEY, "shared")
                .withParam(Const.ParamsNames.LIMIT, "1");

        StudentsData result = execute(request);

        assertEquals(1, result.getStudents().size());
        assertEquals(firstMatch.id(), result.getStudents().get(0).getUserId());
        assertEquals(course1.id(), result.getStudents().get(0).getCourseId());

        assertEquals(studentAccount.id(), result.getStudents().get(0).getAccountId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentsAction_instructorSearchWithAuthorizedCourseId_returnsStudents() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("visible-course", c -> c.name("Visible Course"));
        var match = given.student("match", s -> s.course(course.alias()).name("Shared Alice"));
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(course.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withParam(Const.ParamsNames.SEARCH_KEY, "shared")
                .withAccountAuth(requesterAccount.id());

        StudentsData result = execute(request);

        assertEquals(1, result.getStudents().size());
        assertEquals(match.id(), result.getStudents().get(0).getUserId());
        assertEquals(course.id(), result.getStudents().get(0).getCourseId());
        assertNull(result.getStudents().get(0).getAccountId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentsAction_instructorSearchWithUnauthorizedCourseId_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("visible-course", c -> c.name("Visible Course"));
        var unauthorizedCourse = given.course("unauthorized-course", c -> c.name("Unauthorized Course"));
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(course.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, unauthorizedCourse.id())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentsAction_invalidLimit_throwsInvalidHttpParameterException() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(course.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.LIMIT, "0")
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(InvalidHttpParameterException.class, request);
    }
}
