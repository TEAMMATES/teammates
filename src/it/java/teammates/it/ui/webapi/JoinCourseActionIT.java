package teammates.it.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.RegKeyRequest;
import teammates.ui.webapi.JoinCourseAction;

/**
 * SUT: {@link JoinCourseAction}.
 */
public class JoinCourseActionIT extends BaseActionIT<JoinCourseAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
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

        RegKeyRequest regKeyRequest = new RegKeyRequest();
        regKeyRequest.setKey(student1RegKey);

        JoinCourseAction joinCourseAction = getAction(regKeyRequest);
        getJsonResult(joinCourseAction);

        verifyNumberOfEmailsSent(1);
        EmailWrapper email = mockEmailSender.getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(), "Typical Course 4", "course-4"),
                email.getSubject());

        ______TS("failure: student is already registered");

        regKeyRequest.setKey(student1RegKey);

        InvalidOperationException ioe = verifyInvalidOperation(regKeyRequest);
        assertEquals("User has already joined course", ioe.getMessage());

        verifyNoEmailsSent();

        ______TS("success: instructor joins course");

        loginAsUnregistered(loggedInGoogleIdInst);

        regKeyRequest.setKey(instructor1RegKey);

        joinCourseAction = getAction(regKeyRequest);
        getJsonResult(joinCourseAction);

        verifyNumberOfEmailsSent(1);
        email = mockEmailSender.getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(), "Typical Course 4", "course-4"),
                email.getSubject());

        ______TS("failure: instructor is already registered");

        regKeyRequest.setKey(instructor1RegKey);

        ioe = verifyInvalidOperation(regKeyRequest);
        assertEquals("User has already joined course", ioe.getMessage());

        verifyNoEmailsSent();

        ______TS("failure: invalid regkey");

        regKeyRequest.setKey("ANXKJZNZXNJCZXKJDNKSDA");

        verifyEntityNotFound(regKeyRequest);

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
