package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.DeadlineExtensionData;

/**
 * Tests for {@link GetDeadlineExtensionAction}.
 */
public class GetDeadlineExtensionActionTest extends BaseActionTest<GetDeadlineExtensionAction, DeadlineExtensionData> {

    @Test(groups = GroupNames.ACTION)
    public void getDeadlineExtensionAction_ownStudentWithExtension_returnsDeadlineExtensionData() {
        var account = given.account("account");
        var course = given.course("course");
        var student = given.student("student", s -> s.course(course.alias()).account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.course(course.alias()));
        given.deadlineExtension("de", d -> d.student(student.alias()).feedbackSession(session.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(account.id());

        DeadlineExtensionData result = execute(request);

        assertNotNull(result);
        assertEquals(session.id(), result.getFeedbackSessionId());
        assertEquals(student.id(), result.getUserId());
        assertNotNull(result.getUserDeadlineExtension());
    }

    @Test(groups = GroupNames.ACTION)
    public void getDeadlineExtensionAction_instructorWithCanModifySession_returnsDeadlineExtensionData() {
        var instructorAccount = given.account("instructor-account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(instructorAccount.alias()).coOwner());
        var student = given.student("student", s -> s.course(course.alias()));
        var session = given.feedbackSession("session", fs -> fs.course(course.alias()));
        given.deadlineExtension("de", d -> d.student(student.alias()).feedbackSession(session.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(instructorAccount.id());

        DeadlineExtensionData result = execute(request);

        assertNotNull(result);
        assertEquals(student.id(), result.getUserId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getDeadlineExtensionAction_unauthorizedUser_throwsUnauthorizedAccessException() {
        var otherAccount = given.account("other-account");
        var course = given.course("course");
        var student = given.student("student", s -> s.course(course.alias()));
        given.student("other-student", s -> s.course(course.alias()).account(otherAccount.alias()));
        var session = given.feedbackSession("session", fs -> fs.course(course.alias()));
        given.deadlineExtension("de", d -> d.student(student.alias()).feedbackSession(session.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(otherAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void getDeadlineExtensionAction_noExtensionExists_throwsEntityNotFoundException() {
        var account = given.account("account");
        var course = given.course("course");
        var student = given.student("student", s -> s.course(course.alias()).account(account.alias()));
        var session = given.feedbackSession("session", fs -> fs.course(course.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.FEEDBACK_SESSION_ID, session.id().toString())
                .withParam(Const.ParamsNames.USER_ID, student.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(EntityNotFoundException.class, request);
    }
}
