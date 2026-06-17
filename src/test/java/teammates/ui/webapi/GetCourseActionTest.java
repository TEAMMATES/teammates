package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseViewData;

/**
 * Tests for {@link GetCourseAction}.
 */
public class GetCourseActionTest extends BaseActionTest<GetCourseAction, CourseViewData> {
    @Test(groups = GroupNames.ACTION)
    public void getCourseAction_instructorEntityType_shouldReturnCourseViewData() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()));
        persistGivenData(given);

        RequestContext testRequest = getInstructorRequest(course.id(), account.id());

        CourseViewData result = execute(testRequest);

        assertNotNull(result);
        assertEquals(course.id(), result.getCourse().getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getCourseAction_studentEntityType_shouldReturnCourseViewData() {
        var account = given.account("account");
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias()).account(account.alias()));
        persistGivenData(given);

        RequestContext testRequest = getStudentRequest(course.id(), account.id());

        CourseViewData result = execute(testRequest);

        assertNotNull(result);
        assertEquals(course.id(), result.getCourse().getCourseId());
    }

    @Test(groups = GroupNames.ACTION)
    public void getCourseAction_invalidAccount_shouldThrowUnauthorizedAccessException() {
        var invalidAccount = given.account("invalid-account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.account(invalidAccount.alias()).course(otherCourse.alias()));
        var course = given.course("course");
        persistGivenData(given);

        RequestContext testRequest = getInstructorRequest(course.id(), invalidAccount.id());

        assertActionThrows(UnauthorizedAccessException.class, testRequest);
    }

    private RequestContext getInstructorRequest(String courseId, UUID accountId) {
        return new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, courseId)
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                .withAccountAuth(accountId);
    }

    private RequestContext getStudentRequest(String courseId, UUID accountId) {
        return new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, courseId)
                .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT)
                .withAccountAuth(accountId);
    }
}
