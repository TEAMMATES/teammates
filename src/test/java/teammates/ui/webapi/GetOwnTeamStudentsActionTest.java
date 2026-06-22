package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentsData;

/**
 * Tests for {@link GetOwnTeamStudentsAction}.
 */
public class GetOwnTeamStudentsActionTest extends BaseActionTest<GetOwnTeamStudentsAction, StudentsData> {

    @Test(groups = GroupNames.ACTION)
    public void getOwnTeamStudentsAction_studentWithTeammates_returnsTeamStudentsWithHiddenInfo() {
        var account = given.account("account");
        var course = given.course("course");
        var team = given.team("team", t -> t.course(course.alias()));
        var student = given.student("student", s -> s
                .account(account.alias()).course(course.alias()).team(team.alias()).comments("some comments"));
        var teammate = given.student("teammate", s -> s
                .course(course.alias()).team(team.alias()).comments("teammate comments"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        StudentsData result = execute(request);

        assertTrue(result.getStudents().stream().anyMatch(s -> s.getUserId().equals(student.id())));
        assertTrue(result.getStudents().stream().anyMatch(s -> s.getUserId().equals(teammate.id())));
        // comments and join state should be hidden for all returned students
        result.getStudents().forEach(s -> {
            assertNull(s.getComments());
            assertNull(s.getJoinState());
        });
    }

    @Test(groups = GroupNames.ACTION)
    public void getOwnTeamStudentsAction_studentAloneInTeam_returnsSingleStudent() {
        var account = given.account("account");
        var course = given.course("course");
        var team = given.team("team", t -> t.course(course.alias()));
        var student = given.student("student", s -> s
                .account(account.alias()).course(course.alias()).team(team.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        StudentsData result = execute(request);

        assertTrue(result.getStudents().stream().anyMatch(s -> s.getUserId().equals(student.id())));
        result.getStudents().forEach(s -> {
            assertNull(s.getComments());
            assertNull(s.getJoinState());
        });
    }

    @Test(groups = GroupNames.ACTION)
    public void getOwnTeamStudentsAction_studentNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var course = given.course("course");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
