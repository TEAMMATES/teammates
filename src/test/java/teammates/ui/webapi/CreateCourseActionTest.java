package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.testng.annotations.Test;

import teammates.storage.entity.Course;
import teammates.test.GroupNames;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.request.CourseCreateRequest;

/**
 * Tests for {@link CreateCourseAction}.
 */
public class CreateCourseActionTest extends BaseActionTest<CreateCourseAction, CourseData> {

    @Test(groups = GroupNames.ACTION)
    public void createCourseAction_verifiedAccountWithApprovedRequest_createsCourse() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("avr", avr -> avr
                .account(account.alias())
                .institute(institute.alias())
                .approved());
        persistGivenData(given);

        CourseCreateRequest createRequest = new CourseCreateRequest();
        createRequest.setCourseId("test-course-id");
        createRequest.setCourseName("Test Course");
        createRequest.setTimeZone("UTC");
        createRequest.setInstituteId(institute.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(createRequest);

        CourseData result = execute(request);

        assertNotNull(result);
        assertEquals("test-course-id", result.getCourseId());
        assertEquals("Test Course", result.getCourseName());
        assertEquals("UTC", result.getTimeZone());

        verifyPresentInDatabase(Course.class, "test-course-id");
    }

    @Test(groups = GroupNames.ACTION)
    public void createCourseAction_unverifiedAccount_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("avr", avr -> avr
                .account(account.alias())
                .institute(institute.alias())
                .pending());
        persistGivenData(given);

        CourseCreateRequest createRequest = new CourseCreateRequest();
        createRequest.setCourseId("test-course-id");
        createRequest.setCourseName("Test Course");
        createRequest.setTimeZone("UTC");
        createRequest.setInstituteId(institute.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(createRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void createCourseAction_accountVerifiedForDifferentInstitute_throwsUnauthorizedAccessException() {
        var account = given.account("account");
        var approvedInstitute = given.institute("approved-institute");
        var otherInstitute = given.institute("other-institute");
        given.accountVerificationRequest("avr", avr -> avr
                .account(account.alias())
                .institute(approvedInstitute.alias())
                .approved());
        persistGivenData(given);

        CourseCreateRequest createRequest = new CourseCreateRequest();
        createRequest.setCourseId("test-course-id");
        createRequest.setCourseName("Test Course");
        createRequest.setTimeZone("UTC");
        createRequest.setInstituteId(otherInstitute.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(createRequest);

        assertActionThrows(UnauthorizedAccessException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void createCourseAction_duplicateCourseId_throwsInvalidOperationException() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("avr", avr -> avr
                .account(account.alias())
                .institute(institute.alias())
                .approved());
        var existingCourse = given.course("existing-course", c -> c.institute(institute.alias()));
        persistGivenData(given);

        CourseCreateRequest createRequest = new CourseCreateRequest();
        createRequest.setCourseId(existingCourse.id());
        createRequest.setCourseName("Test Course");
        createRequest.setTimeZone("UTC");
        createRequest.setInstituteId(institute.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(createRequest);

        assertActionThrows(InvalidOperationException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void createCourseAction_invalidTimeZone_throwsInvalidHttpRequestBodyException() {
        var account = given.account("account");
        var institute = given.institute("institute");
        given.accountVerificationRequest("avr", avr -> avr
                .account(account.alias())
                .institute(institute.alias())
                .approved());
        persistGivenData(given);

        CourseCreateRequest createRequest = new CourseCreateRequest();
        createRequest.setCourseId("test-course-id");
        createRequest.setCourseName("Test Course");
        createRequest.setTimeZone("Invalid/TimeZone");
        createRequest.setInstituteId(institute.id());

        RequestContext request = new RequestContext()
                .withAccountAuth(account.id())
                .withRequest(createRequest);

        assertActionThrows(InvalidHttpRequestBodyException.class, request);
    }
}
