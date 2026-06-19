package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseUpdateRequest;

/**
 * Tests for {@link UpdateCourseAction}.
 */
public class UpdateCourseActionTest extends BaseActionTest<UpdateCourseAction, CourseData> {

    @Test(groups = GroupNames.ACTION)
    public void updateCourseAction_instructorWithModifyPrivilege_updatesCourse() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).coOwner());
        persistGivenData(given);

        CourseUpdateRequest updateRequest = new CourseUpdateRequest();
        updateRequest.setCourseName("Updated Course Name");
        updateRequest.setTimeZone("Asia/Tokyo");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id())
                .withRequest(updateRequest);

        CourseData result = execute(request);

        assertNotNull(result);
        assertEquals(course.id(), result.getCourseId());
        assertEquals("Updated Course Name", result.getCourseName());
        assertEquals("Asia/Tokyo", result.getTimeZone());

        Course updatedCourse = getEntityInTransaction(Course.class, course.id());
        assertEquals("Updated Course Name", updatedCourse.getName());
        assertEquals("Asia/Tokyo", updatedCourse.getTimeZone());
    }

    @Test(groups = GroupNames.ACTION)
    public void updateCourseAction_instructorWithoutModifyPrivilege_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).noPrivileges());
        persistGivenData(given);

        CourseUpdateRequest updateRequest = new CourseUpdateRequest();
        updateRequest.setCourseName("Updated Course Name");
        updateRequest.setTimeZone("UTC");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id())
                .withRequest(updateRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateCourseAction_instructorNotInCourse_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var otherCourse = given.course("other-course");
        given.instructor("instructor", i -> i.course(otherCourse.alias()).account(account.alias()).coOwner());
        var course = given.course("course");
        persistGivenData(given);

        CourseUpdateRequest updateRequest = new CourseUpdateRequest();
        updateRequest.setCourseName("Updated Course Name");
        updateRequest.setTimeZone("UTC");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id())
                .withRequest(updateRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateCourseAction_nonExistentCourse_throwsEntityNotFoundException() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).coOwner());
        persistGivenData(given);

        CourseUpdateRequest updateRequest = new CourseUpdateRequest();
        updateRequest.setCourseName("Updated Course Name");
        updateRequest.setTimeZone("UTC");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, "non-existent-course-id")
                .withAccountAuth(account.id())
                .withRequest(updateRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void updateCourseAction_invalidTimeZone_throwsInvalidHttpRequestBodyException() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()).account(account.alias()).coOwner());
        persistGivenData(given);

        CourseUpdateRequest updateRequest = new CourseUpdateRequest();
        updateRequest.setCourseName("Updated Course Name");
        updateRequest.setTimeZone("Invalid/TimeZone");

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.COURSE_ID, course.id())
                .withAccountAuth(account.id())
                .withRequest(updateRequest);

        assertActionThrows(InvalidHttpRequestBodyException.class, request);
    }
}
