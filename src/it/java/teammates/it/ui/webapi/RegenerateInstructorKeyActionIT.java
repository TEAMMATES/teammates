package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.webapi.EntityNotFoundException;
import teammates.ui.webapi.RegenerateInstructorKeyAction;
import teammates.ui.webapi.JsonResult;
import teammates.ui.output.RegenerateKeyData;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;

/**
 * SUT: {@link RegenerateInstructorKeyAction}.
 */
public class RegenerateInstructorKeyActionIT extends BaseActionIT<RegenerateInstructorKeyAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
        loginAsAdmin();
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
            Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
            Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
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

        // ______TS("Invalid parameters");

        // //no parameters
        // verifyHttpParameterFailure();

        // //null instructor email
        // String[] invalidParams = new String[] {
        //         Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        // };
        // verifyHttpParameterFailure(invalidParams);

        // //null course id
        // invalidParams = new String[] {
        //         Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
        // };
        // verifyHttpParameterFailure(invalidParams);

        ______TS("Course ID given but course is non existent");

        String[] invalidCourseParams = new String[] {
            Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
            Const.ParamsNames.COURSE_ID, "does-not-exist-id",
        };

        //     EntityNotFoundException enfe = verifyEntityNotFound(invalidCourseParams);
        //     assertEquals("Instructor could not be found for this course", enfe.getMessage());
        verifyEntityNotFound(invalidCourseParams);

        ______TS("Instructor not found in course");

        String[] invalidEmailParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "non-existent-instructor@abc.com",
                Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };

        //     enfe = verifyEntityNotFound(invalidCourseParams);
        //     assertEquals("Instructor could not be found for this course", enfe.getMessage());

        verifyEntityNotFound(invalidEmailParams);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Instructor instructor = typicalBundle.instructors.get("instructor1OfCourse1");
        //String oldRegKey = instructor.getRegKey();
        //loginAsAdmin();

        ______TS("only instructors of the same course with correct privilege can access");
        loginAsAdmin();

        String[] submissionParams = new String[] {
            Const.ParamsNames.INSTRUCTOR_EMAIL, instructor.getEmail(),
            Const.ParamsNames.COURSE_ID, instructor.getCourseId(),
        };
        verifyOnlyAdminCanAccess(course, submissionParams);

        // ______TS("Instructors cannot access");
        // loginAsInstructor(instructor.getAccount().getGoogleId());

        // verifyInaccessibleForInstructors(course, submissionParams);
        // verifyInaccessibleWithoutLogin(submissionParams);
    }
}
