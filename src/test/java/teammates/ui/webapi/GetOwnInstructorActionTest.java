package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.InstructorData;

/**
 * Tests for {@link GetOwnInstructorAction}.
 */
public class GetOwnInstructorActionTest extends BaseActionTest<GetOwnInstructorAction, InstructorData> {

    @Test(groups = GroupNames.ACTION)
    public void getOwnInstructorAction_byAccount_returnsInstructorData() {
        var account = given.account("account");
        var course = given.course("course");
        var instructor = given.instructor("instructor", i -> i.account(account.alias()).course(course.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        InstructorData result = execute(request);

        assertEquals(instructor.id(), result.getUserId());
        assertEquals(course.id(), result.getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getOwnInstructorAction_requestWithoutInstructor_throwsEntityNotFoundException() {
        var account = given.account("account");
        var course = given.course("course");
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id());

        assertActionThrows(EntityNotFoundException.class, request);
    }
}
