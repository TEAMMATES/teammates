package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link SendJoinReminderEmailAction}.
 */
public class SendJoinReminderEmailActionTest extends BaseActionTest<SendJoinReminderEmailAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void sendJoinReminderEmailAction_unregisteredStudent_queuesStudentJoinEmail() {
        var instructorAccount = given.account("instructor-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.course(course.alias()).email("student@test.tmt"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(instructorAccount.id());

        MessageOutput result = execute(request);

        assertEquals("An email has been sent to " + student.email(), result.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void sendJoinReminderEmailAction_unregisteredInstructor_returnsSuccessMessage() {
        var actingInstructorAccount = given.account("acting-instructor-account");
        var course = given.course("course");
        given.instructor("acting-instructor", i -> i.course(course.alias())
                .account(actingInstructorAccount.alias()).coOwner());
        var invitedInstructor = given.instructor("invited-instructor",
                i -> i.course(course.alias()).email("invited-instructor@test.tmt").noAccount());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, invitedInstructor.id().toString())
                .withAccountAuth(actingInstructorAccount.id());

        MessageOutput result = execute(request);

        assertEquals("An email has been sent to " + invitedInstructor.email(), result.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void sendJoinReminderEmailAction_bothUserIdAndCourseIdProvided_throwsInvalidHttpParameterException() {
        var instructorAccount = given.account("instructor-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.course(course.alias()).email("student@test.tmt"));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(instructorAccount.id());

        InvalidHttpParameterException exception = assertActionThrows(InvalidHttpParameterException.class, request);

        assertEquals("Exactly one of userId or courseId must be provided.", exception.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void sendJoinReminderEmailAction_neitherUserIdNorCourseIdProvided_throwsInvalidHttpParameterException() {
        var instructorAccount = given.account("instructor-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(instructorAccount.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withAccountAuth(instructorAccount.id());

        InvalidHttpParameterException exception = assertActionThrows(InvalidHttpParameterException.class, request);

        assertEquals("Exactly one of userId or courseId must be provided.", exception.getMessage());
    }
}
