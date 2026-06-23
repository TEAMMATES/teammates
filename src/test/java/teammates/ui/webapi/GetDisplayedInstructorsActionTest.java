package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorsData;

/**
 * Tests for {@link GetDisplayedInstructorsAction}.
 */
public class GetDisplayedInstructorsActionTest
        extends BaseActionTest<GetDisplayedInstructorsAction, InstructorsData> {

    @Test(groups = GroupNames.ACTION)
    public void getDisplayedInstructorsAction_studentInCourse_returnsOnlyDisplayedInstructors() {
        var course = given.course("course");
        var studentAccount = given.account("student-account");
        var displayedInstructor = given.instructor("displayed-instructor",
                i -> i.course(course.alias()).name("Displayed Instructor").isDisplayedToStudents(true));
        given.instructor("hidden-instructor",
                i -> i.course(course.alias()).name("Hidden Instructor").isDisplayedToStudents(false));
        given.student("student", s -> s.course(course.alias()).account(studentAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(studentAccount.id());

        InstructorsData result = execute(request);

        assertEquals(1, result.getInstructors().size());
        assertEquals(displayedInstructor.id(), result.getInstructors().get(0).getUserId());
        assertEquals(course.id(), result.getInstructors().get(0).getCourseId());
        assertNull(result.getInstructors().get(0).getIsDisplayedToStudents());
        assertNull(result.getInstructors().get(0).getRole());
        assertNull(result.getInstructors().get(0).getJoinState());
        assertNull(result.getInstructors().get(0).getAccountId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getDisplayedInstructorsAction_adminCanAccessCourse_returnsDisplayedInstructors() {
        var course = given.course("course");
        var displayedInstructor = given.instructor("displayed-instructor",
                i -> i.course(course.alias()).name("Displayed Instructor").isDisplayedToStudents(true));
        given.instructor("hidden-instructor",
                i -> i.course(course.alias()).name("Hidden Instructor").isDisplayedToStudents(false));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAdminAuth();

        InstructorsData result = execute(request);

        assertEquals(1, result.getInstructors().size());
        assertEquals(displayedInstructor.id(), result.getInstructors().get(0).getUserId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getDisplayedInstructorsAction_nonMember_throwsUnauthorizedAccessException() {
        var course = given.course("course");
        given.instructor("displayed-instructor",
                i -> i.course(course.alias()).name("Displayed Instructor").isDisplayedToStudents(true));
        var requesterAccount = given.account("requester-account");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
