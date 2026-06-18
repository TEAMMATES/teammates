package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;

/**
 * Tests for {@link CreateInstructorAction}.
 */
public class CreateInstructorActionTest extends BaseActionTest<CreateInstructorAction, InstructorData> {

    @Test(groups = GroupNames.ACTION)
    public void createInstructorAction_validRequest_createsInstructor() {
        var actingInstructorAccount = given.account("acting-instructor-account");
        var course = given.course("course");
        given.instructor("acting-instructor", i -> i.course(course.alias())
                .account(actingInstructorAccount.alias()).coOwner());
        persistGivenData(given);

        InstructorCreateRequest requestBody = new InstructorCreateRequest(
                "New Instructor",
                "new-instructor@test.tmt",
                Const.InstructorPermissionRoleNames.COOWNER,
                "New Instructor",
                false,
                null);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(actingInstructorAccount.id())
                .withRequest(requestBody);

        InstructorData result = execute(request);

        assertEquals("New Instructor", result.getName());
        assertEquals("new-instructor@test.tmt", result.getEmail());

        Instructor createdInstructor = inTransaction(() ->
                Logic.inst().getInstructorForEmail(course.id(), "new-instructor@test.tmt"));
        assertEquals("New Instructor", createdInstructor.getName());
        assertEquals("new-instructor@test.tmt", createdInstructor.getEmail());
    }
}
