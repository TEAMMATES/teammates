package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.User;
import teammates.ui.output.RegenerateKeyData;

/**
 * SUT: {@link RegenerateUserKeyAction}.
 */
public class RegenerateUserKeyActionIT extends BaseActionIT<RegenerateUserKeyAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.USER_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        Course course = typicalBundle.courses.get("course1");
        User student = typicalBundle.students.get("student1InCourse1");
        User instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        loginAsAdmin();

        ______TS("Typical success case: student");
        verifySuccessfulRegeneration(course, student, EmailType.STUDENT_COURSE_LINKS_REGENERATED);

        ______TS("Typical success case: instructor");
        verifySuccessfulRegeneration(course, instructor, EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED);

        ______TS("No parameters");
        verifyHttpParameterFailure();

        ______TS("User not found");

        String[] invalidUserIdParams = new String[] {
                Const.ParamsNames.USER_ID, "00000000-0000-0000-0000-000000000000",
        };

        verifyEntityNotFound(invalidUserIdParams);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        User student = typicalBundle.students.get("student1InCourse1");
        User instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("Only admin can access");
        loginAsAdmin();

        String[] studentParams = new String[] {
                Const.ParamsNames.USER_ID, student.getId().toString(),
        };
        verifyOnlyAdminCanAccess(course, studentParams);

        ______TS("Students cannot access");
        loginAsStudent(student.getAccount().getGoogleId());

        verifyInaccessibleForStudents(course, studentParams);

        String[] instructorParams = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        ______TS("Instructors cannot access");
        loginAsInstructor(instructor.getAccount().getGoogleId());

        verifyInaccessibleForInstructors(course, instructorParams);
    }

    private void verifySuccessfulRegeneration(Course course, User user, EmailType emailType) {
        String oldRegKey = user.getRegKey();

        String[] params = new String[] {
                Const.ParamsNames.USER_ID, user.getId().toString(),
        };

        RegenerateUserKeyAction action = getAction(params);
        JsonResult actionOutput = getJsonResult(action);

        RegenerateKeyData response = (RegenerateKeyData) actionOutput.getOutput();

        assertEquals(RegenerateUserKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, response.getMessage());
        assertNotEquals(oldRegKey, response.getNewRegistrationKey());

        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(mockEmailSender.getEmailsSent().size() - 1);
        assertEquals(String.format(emailType.getSubject(), course.getName(), user.getCourseId()),
                emailSent.getSubject());
        assertEquals(user.getEmail(), emailSent.getRecipient());
    }
}
