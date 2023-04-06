package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.RegenerateKeyData;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.RegenerateStudentKeyAction;

/**
 * SUT: {@link RegenerateStudentKeyAction}.
 */
public class RegenerateStudentKeyActionIT extends BaseActionIT<RegenerateStudentKeyAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_KEY;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() {
        Course course = typicalBundle.courses.get("course1");
        Student student = typicalBundle.students.get("student1InCourse1");
        String oldRegKey = student.getRegKey();
        loginAsAdmin();

        ______TS("Typical Success Case");

        String[] param = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        RegenerateStudentKeyAction regenerateStudentKeyAction = getAction(param);
        JsonResult actionOutput = getJsonResult(regenerateStudentKeyAction);

        RegenerateKeyData response = (RegenerateKeyData) actionOutput.getOutput();

        assertEquals(RegenerateStudentKeyAction.SUCCESSFUL_REGENERATION_WITH_EMAIL_SENT, response.getMessage());
        assertNotEquals(oldRegKey, response.getNewRegistrationKey());

        verifyNumberOfEmailsSent(1);
        EmailWrapper emailSent = mockEmailSender.getEmailsSent().get(0);
        assertEquals(String.format(EmailType.STUDENT_COURSE_LINKS_REGENERATED.getSubject(),
                                   course.getName(),
                                   student.getCourseId()),
                     emailSent.getSubject());
        assertEquals(student.getEmail(), emailSent.getRecipient());

        ______TS("No parameters");
        verifyHttpParameterFailure();

        ______TS("No student email");
        String[] noEmailParams = new String[] {
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };
        verifyHttpParameterFailure(noEmailParams);

        ______TS("No course ID");
        String[] noCourseParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };
        verifyHttpParameterFailure(noCourseParams);

        ______TS("Course ID given but course is non existent");

        String[] invalidCourseParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.COURSE_ID, "does-not-exist-id",
        };

        verifyEntityNotFound(invalidCourseParams);

        ______TS("Student not found in course");

        String[] invalidEmailParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, "non-existent-student@abc.com",
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };

        verifyEntityNotFound(invalidEmailParams);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        Course course = typicalBundle.courses.get("course1");
        Student student = typicalBundle.students.get("student1InCourse1");

        ______TS("Only admin can access");
        loginAsAdmin();

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_EMAIL, student.getEmail(),
                Const.ParamsNames.COURSE_ID, student.getCourseId(),
        };
        verifyOnlyAdminCanAccess(course, submissionParams);

        ______TS("Students cannot access");
        loginAsStudent(student.getAccount().getGoogleId());

        verifyInaccessibleForStudents(course, submissionParams);
    }
}
