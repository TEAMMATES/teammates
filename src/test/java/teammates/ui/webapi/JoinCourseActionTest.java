package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.StringHelper;

/**
 * SUT: {@link JoinCourseAction}.
 */
public class JoinCourseActionTest extends BaseActionTest<JoinCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() {

        loginAsUnregistered("unreg.user");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(
                Const.ParamsNames.REGKEY, "regkey"
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT
        );

        ______TS("Failure case: regkey is not valid for student");

        String[] params = {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        JoinCourseAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        verifyNoEmailsSent(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());

        ______TS("Failure case: student is already registered");

        String registeredStudentKey =
                logic.getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(registeredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        a = getAction(params);
        r = getJsonResult(a);

        verifyNoEmailsSent(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        ______TS("Normal case: student is not registered");

        String unregisteredStudentKey =
                logic.getStudentForEmail("idOfUnregisteredCourse", "student1InUnregisteredCourse@gmail.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        a = getAction(params);
        r = getJsonResult(a);

        verifyNumberOfEmailsSent(a, 1);
        EmailWrapper email = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(), "Unregistered Course", "idOfUnregisteredCourse"),
                email.getSubject());

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        loginAsUnregistered("unreg.user0");

        ______TS("Failure case: regkey is not valid for instructor");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        r = getJsonResult(a);

        verifyNoEmailsSent(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());

        ______TS("Failure case: instructor is already registered");

        String registeredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(registeredInstructorKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        r = getJsonResult(a);

        verifyNoEmailsSent(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

        ______TS("Normal case: instructor is not registered");

        String unregisteredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructorNotYetJoinedCourse1@email.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredInstructorKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        r = getJsonResult(a);

        verifyNumberOfEmailsSent(a, 1);
        email = a.getEmailSender().getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                        "Typical Course 1 with 2 Evals", "idOfTypicalCourse1"),
                email.getSubject());

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        ______TS("Failure case: invalid entity type");

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, "unknown",
        };

        a = getAction(params);
        r = getJsonResult(a);

        verifyNoEmailsSent(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
