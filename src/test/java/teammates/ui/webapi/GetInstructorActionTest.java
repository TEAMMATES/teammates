package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;

/**
 * Tests for {@link GetInstructorAction}.
 */
public class GetInstructorActionTest extends BaseActionTest<GetInstructorAction, InstructorData> {
    private static final String DUMMY_UUID = UUID.fromString("5d17a2a8-3e2a-40a9-b9e2-3e4a3f6a8680").toString();

    @Test(groups = GroupNames.ACTION)
    public void getInstructorAction_sameCourseInstructor_returnsInstructorData() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(course.alias()));
        var targetInstructor = given.instructor("target", i -> i.course(course.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, targetInstructor.id().toString())
                .withAccountAuth(requesterAccount.id());

        InstructorData result = execute(request);

        assertEquals(targetInstructor.id(), result.getUserId());
        assertEquals(course.id(), result.getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorAction_adminBypass_returnsInstructorData() {
        var targetInstructor = given.instructor("target", i -> i.defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, targetInstructor.id().toString())
                .withAdminAuth();

        InstructorData result = execute(request);

        assertEquals(targetInstructor.id(), result.getUserId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorAction_differentCourseInstructor_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        var requesterCourse = given.course("requester-course");
        var targetCourse = given.course("target-course");
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(requesterCourse.alias()));
        var targetInstructor = given.instructor("target", i -> i.course(targetCourse.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, targetInstructor.id().toString())
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getInstructorAction_nonAdminNonexistentTarget_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        given.instructor("requester", i -> i.defaultCourse().account(requesterAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, DUMMY_UUID)
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
