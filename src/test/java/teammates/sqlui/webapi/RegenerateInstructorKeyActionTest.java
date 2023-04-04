package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.RegenerateInstructorKeyAction;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link RegenerateInstructorKeyAction}.
 */
public class RegenerateInstructorKeyActionTest extends BaseActionTest<RegenerateInstructorKeyAction> {

    String googleId = "user-googleId";

    Course course;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @BeforeMethod
    public void baseClassSetup() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");
        loginAsAdmin();
    }

    @Test
    protected void testExecute_notEnoughParameters_throwsInvalidHttpParameterException() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);

        ______TS("Invalid parameters");

        //no parameters
        verifyHttpParameterFailure();

        //null instructor email
        String[] invalidParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };
        verifyHttpParameterFailure(invalidParams);

        //null course id
        invalidParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
        };
        verifyHttpParameterFailure(invalidParams);
    }

    @Test
    protected void testExecute_invalidCourseId_throwsInvalidHttpParameterException() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);

        ______TS("course does not exist");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.COURSE_ID, "non-existent-course",
        };

        assertNull(mockLogic.getCourse("non-existent-course"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_nonExistentInstructorInCourse_shouldFail() {
        Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);

        ______TS("instructor with email address does not exist in course");

        String[] nonExistingParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent-instructor@abc.com",
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        assertNull(mockLogic.getInstructorForEmail(instructor.getCourseId(), "non-existent-instructor@abc.com"));

        verifyEntityNotFound(nonExistingParams);
    }

    @Test
    protected void testExecute_regenerateInstructorKey() {
        Course course = generateCourse1();
        Instructor instructor = generateInstructor1InCourse(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), instructor.getGoogleId())).thenReturn(instructor);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);

        // Instructor instructor = new Instructor(course, "name", "email@tm.tmt", false, "", null, null);
        // when(mockLogic.getCourse(course.getId())).thenReturn(course);
        // when(mockLogic.getInstructorByRegistrationKey(instructor.getRegKey())).thenReturn(instructor);

        ______TS("Successfully sent regenerated links email");

        String[] param = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        RegenerateInstructorKeyAction a = getAction(param);
        JsonResult result = getJsonResult(a);

        RegenerateKeyData output = (RegenerateKeyData) result.getOutput();

        assertEquals(RegenerateInstructorKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, output.getMessage());
        assertNotEquals(instructor.getRegKey(), output.getNewRegistrationKey());

        verifyNumberOfEmailsSent(1);

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
                                   course.getName(),
                                   instructor.getCourseId()),
                     emailSent.getSubject());
        assertEquals(instructor.getEmail(), emailSent.getRecipient());
    }

    private Course generateCourse1() {
        Course c = new Course("course-1", "Typical Course 1",
                "Africa/Johannesburg", "TEAMMATES Test Institute 0");
        c.setCreatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        c.setUpdatedAt(Instant.parse("2023-01-01T00:00:00Z"));
        return c;
    }

    private Instructor generateInstructor1InCourse(Course courseInstructorIsIn) {
        return new Instructor(courseInstructorIsIn, "instructor-1",
                "instructor-1@tm.tmt", false,
                "", null,
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER));
    }

    /* TODO
    verifyInaccessibleWithoutLogin(params);
    verifyInaccessibleForUnregisteredUsers(params);
    verifyInaccessibleForStudents(params);
    verifyInaccessibleForInstructors(params);
    verifyAccessibleForAdmin(params);
     */
}
