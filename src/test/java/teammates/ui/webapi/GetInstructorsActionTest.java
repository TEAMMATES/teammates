package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorsData;

/**
 * Tests for {@link GetInstructorsAction}.
 */
public class GetInstructorsActionTest extends BaseActionTest<GetInstructorsAction, InstructorsData> {

    @Test(groups = GroupNames.ACTION)
    public void getInstructorsAction_adminCanSearchSystemWide_returnsLimitedMatches() {
        var course1 = given.course("course-1", c -> c.name("Shared Course 1"));
        var course2 = given.course("course-2", c -> c.name("Shared Course 2"));
        var firstMatch = given.instructor("first-match", i -> i.course(course1.alias()).name("Shared Alice"));
        given.instructor("second-match", i -> i.course(course2.alias()).name("Shared Bob"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAdminAuth()
                .withParam(Const.ParamsNames.SEARCH_KEY, "shared")
                .withParam(Const.ParamsNames.LIMIT, "1");

        InstructorsData result = execute(request);

        assertEquals(1, result.getInstructors().size());
        assertEquals(firstMatch.id(), result.getInstructors().get(0).getUserId());
        assertEquals(course1.id(), result.getInstructors().get(0).getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorsAction_courseInstructorCanSearchWithinCourse_returnsMatchingInstructor() {
        var requesterAccount = given.account("requester-account");
        var requesterCourse = given.course("requester-course");
        var otherCourse = given.course("other-course");
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(requesterCourse.alias()));
        var matchingInstructor = given.instructor("matching", i -> i.course(requesterCourse.alias()).name("Search Target"));
        given.instructor("other-course-match", i -> i.course(otherCourse.alias()).name("Search Target"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, requesterCourse.id())
                .withParam(Const.ParamsNames.SEARCH_KEY, "target")
                .withAccountAuth(requesterAccount.id());

        InstructorsData result = execute(request);

        assertEquals(1, result.getInstructors().size());
        assertEquals(matchingInstructor.id(), result.getInstructors().get(0).getUserId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorsAction_withoutCourseId_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        given.instructor("requester", i -> i.account(requesterAccount.alias()).defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(requesterAccount.id())
                .withParam(Const.ParamsNames.SEARCH_KEY, "target");

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
