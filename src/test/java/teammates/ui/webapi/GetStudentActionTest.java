package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.StudentData;

/**
 * Tests for {@link GetStudentAction}.
 */
public class GetStudentActionTest extends BaseActionTest<GetStudentAction, StudentData> {
    private static final String DUMMY_UUID = UUID.fromString("6b8aa718-7ec9-4cc1-8ba9-ec5ab8530e2e").toString();

    @Test(groups = GroupNames.ACTION)
    public void getStudentAction_instructorInCourse_returnsStudentData() {
        var requesterAccount = given.account("requester-account");
        var course = given.course("course");
        var section = given.section("section", s -> s.course(course.alias()));
        var targetStudent = given.student("student", s -> s.course(course.alias()).section(section.alias()));
        given.instructor("requester", i -> i.account(requesterAccount.alias()).course(course.alias()).coOwner());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, targetStudent.id().toString())
                .withAccountAuth(requesterAccount.id());

        StudentData result = execute(request);

        assertEquals(targetStudent.id(), result.getUserId());
        assertEquals(course.id(), result.getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentAction_adminBypass_returnsStudentData() {
        var targetStudent = given.student("student", s -> s.defaultCourse());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, targetStudent.id().toString())
                .withAdminAuth();

        StudentData result = execute(request);

        assertEquals(targetStudent.id(), result.getUserId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getStudentAction_nonAdminNonexistentTarget_throwsUnauthorizedAccessException() {
        var requesterAccount = given.account("requester-account");
        given.instructor("requester", i -> i.defaultCourse().account(requesterAccount.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, DUMMY_UUID)
                .withAccountAuth(requesterAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, request);
    }
}
