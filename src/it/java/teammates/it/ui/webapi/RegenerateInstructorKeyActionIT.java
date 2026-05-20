package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RegenerateInstructorKeyAction;

/**
 * SUT: {@link RegenerateInstructorKeyAction}.
 */
public class RegenerateInstructorKeyActionIT extends BaseActionIT<RegenerateInstructorKeyAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        String oldRegKey = instructor.getRegKey();
        loginAsAdmin();

        ______TS("Typical Success Case");

        String[] param = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };

        RegenerateInstructorKeyAction regenerateInstructorKeyAction = getAction(param);
        JsonResult actionOutput = getJsonResult(regenerateInstructorKeyAction);

        RegenerateKeyData response = (RegenerateKeyData) actionOutput.getOutput();

        assertEquals(RegenerateInstructorKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, response.getMessage());
        assertNotEquals(oldRegKey, response.getNewRegistrationKey());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.INSTRUCTOR_COURSE_LINKS_REGENERATED.getSubject(),
                                   course.getName(),
                                   instructor.getCourseId()),
                     emailSent.getSubject());
        assertEquals(instructor.getEmail(), emailSent.getRecipient());

        ______TS("No parameters");
        verifyHttpParameterFailure();

        ______TS("Instructor not found");

        String[] invalidInstructorIdParams = new String[] {
                Const.ParamsNames.USER_ID, "00000000-0000-0000-0000-000000000000",
        };

        verifyEntityNotFound(invalidInstructorIdParams);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("only instructors of the same course with correct privilege can access");
        loginAsAdmin();

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, instructor.getId().toString(),
        };
        verifyOnlyAdminCanAccess(course, submissionParams);

        ______TS("Instructors cannot access");
        loginAsInstructor(instructor.getAccount().getGoogleId());

        verifyInaccessibleForInstructors(course, submissionParams);
    }
}
