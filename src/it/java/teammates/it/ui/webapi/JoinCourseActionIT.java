package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.InvalidOperationException;
import teammates.ui.webapi.JoinCourseAction;

/**
 * SUT: {@link JoinCourseAction}.
 */
public class JoinCourseActionIT extends BaseActionIT<JoinCourseAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        Student studentYetToJoinCourse = typicalBundle.students.get("student2YetToJoinCourse4");
        String student1RegKey =
                getRegKeyForStudent(studentYetToJoinCourse.getCourseId(), studentYetToJoinCourse.getEmail());
        String loggedInGoogleIdStu = "AccLogicT.student.id";

        Instructor instructorYetToJoinCourse = typicalBundle.instructors.get("instructor2YetToJoinCourse4");
        String instructor1RegKey =
                getRegKeyForInstructor(instructorYetToJoinCourse.getCourseId(), instructorYetToJoinCourse.getEmail());

        String loggedInGoogleIdInst = "AccLogicT.instr.id";

        ______TS("success: student joins course");

        loginAsUnregistered(loggedInGoogleIdStu);

        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, student1RegKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        JoinCourseAction joinCourseAction = getAction(submissionParams);
        getJsonResult(joinCourseAction);

        verifyNumberOfEmailsSent(1);
        EmailWrapper email = mockEmailSender.getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(), "Typical Course 4", "course-4"),
                email.getSubject());

        ______TS("failure: student is already registered");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, student1RegKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        InvalidOperationException ioe = verifyInvalidOperation(submissionParams);
        assertEquals("Student has already joined course", ioe.getMessage());

        verifyNoEmailsSent();

        ______TS("success: instructor joins course");

        loginAsUnregistered(loggedInGoogleIdInst);

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, instructor1RegKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        joinCourseAction = getAction(submissionParams);
        getJsonResult(joinCourseAction);

        verifyNumberOfEmailsSent(1);
        email = mockEmailSender.getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(), "Typical Course 4", "course-4"),
                email.getSubject());

        ______TS("failure: instructor is already registered");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, instructor1RegKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        ioe = verifyInvalidOperation(submissionParams);
        assertEquals("Instructor has already joined course", ioe.getMessage());

        verifyNoEmailsSent();

        ______TS("failure: invalid regkey");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyEntityNotFound(submissionParams);

        verifyNoEmailsSent();

        ______TS("failure: invalid entity type");

        submissionParams = new String[] {
                Const.ParamsNames.REGKEY, student1RegKey,
                Const.ParamsNames.ENTITY_TYPE, "invalid_entity_type",
        };

        verifyHttpParameterFailure(submissionParams);

        verifyNoEmailsSent();
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }

    private String getRegKeyForStudent(String courseId, String email) {
        return logic.getStudentForEmail(courseId, email).getRegKey();
    }

    private String getRegKeyForInstructor(String courseId, String email) {
        return logic.getInstructorForEmail(courseId, email).getRegKey();
    }
}
