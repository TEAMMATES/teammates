package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorUpdateRequest;

/**
 * Tests for {@link UpdateInstructorAction}.
 */
public class UpdateInstructorActionTest extends BaseActionTest<UpdateInstructorAction, InstructorData> {

    @Test(groups = GroupNames.ACTION)
    public void updateInstructorAction_coOwnerUpdatesAnotherInstructor_success() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("requester", i -> i.course(course.alias()).account(requesterAccount.alias()).coOwner());
        var target = given.instructor("target", i -> i.course(course.alias())
                .name("Old Name").email("old@example.com").coOwner());
        persistGivenData(given);

        InstructorUpdateRequest reqBody = new InstructorUpdateRequest(
                "New Name", "new@example.com",
                Const.InstructorPermissionRoleNames.COOWNER, "Display Name", true, null);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, target.id().toString())
                .withCookie(getAuthCookie(requesterAccount.id()))
                .withRequest(reqBody);

        InstructorData result = execute(request);

        assertEquals("New Name", result.getName());
        assertEquals("new@example.com", result.getEmail());

        Instructor updated = getEntity(Instructor.class, target.id());
        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateInstructorAction_invalidEmail_throwsInvalidHttpRequestBodyException() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("requester", i -> i.course(course.alias()).account(requesterAccount.alias()).coOwner());
        var target = given.instructor("target", i -> i.course(course.alias()).coOwner());
        persistGivenData(given);

        InstructorUpdateRequest reqBody = new InstructorUpdateRequest(
                "Name", "not-a-valid-email",
                Const.InstructorPermissionRoleNames.COOWNER, "Display Name", true, null);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, target.id().toString())
                .withCookie(getAuthCookie(requesterAccount.id()))
                .withRequest(reqBody);

        assertActionThrows(InvalidHttpRequestBodyException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateInstructorAction_makeLastDisplayedInstructorHidden_throwsInvalidOperationException() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        // Co-owner is the only displayed instructor; trying to hide themselves should fail
        var instructor = given.instructor("instructor", i -> i.course(course.alias())
                .account(requesterAccount.alias()).coOwner().isDisplayedToStudents(true));
        persistGivenData(given);

        InstructorUpdateRequest reqBody = new InstructorUpdateRequest(
                "Name", "instructor@example.com",
                Const.InstructorPermissionRoleNames.COOWNER, null, false, null);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, instructor.id().toString())
                .withCookie(getAuthCookie(requesterAccount.id()))
                .withRequest(reqBody);

        assertActionThrows(InvalidOperationException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateInstructorAction_instructorWithoutModifyPrivilege_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        given.instructor("requester", i -> i.course(course.alias()).account(requesterAccount.alias()).noPrivileges());
        var target = given.instructor("target", i -> i.course(course.alias()).coOwner());
        persistGivenData(given);

        InstructorUpdateRequest reqBody = new InstructorUpdateRequest(
                "Name", "target@example.com",
                Const.InstructorPermissionRoleNames.COOWNER, "Display Name", true, null);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, target.id().toString())
                .withCookie(getAuthCookie(requesterAccount.id()))
                .withRequest(reqBody);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

}
