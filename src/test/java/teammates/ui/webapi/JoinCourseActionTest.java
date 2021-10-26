package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;

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

        verifyEntityNotFound(params);

        verifyNoEmailsSent();

        ______TS("Failure case: student is already registered");

        String registeredStudentKey =
                logic.getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt").getKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, registeredStudentKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        InvalidOperationException ioe = verifyInvalidOperation(params);
        assertEquals("Student has already joined course", ioe.getMessage());

        verifyNoEmailsSent();

        ______TS("Normal case: student is not registered");

        String unregisteredStudentKey =
                logic.getStudentForEmail("idOfUnregisteredCourse", "student1InUnregisteredCourse@gmail.tmt").getKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredStudentKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        JoinCourseAction a = getAction(params);
        getJsonResult(a);

        verifyNumberOfEmailsSent(1);
        EmailWrapper email = mockEmailSender.getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(), "Unregistered Course", "idOfUnregisteredCourse"),
                email.getSubject());

        loginAsUnregistered("unreg.user0");

        ______TS("Failure case: regkey is not valid for instructor");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyEntityNotFound(params);

        verifyNoEmailsSent();

        ______TS("Failure case: instructor is already registered");

        String registeredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt").getKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, registeredInstructorKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        ioe = verifyInvalidOperation(params);
        assertEquals("Instructor has already joined course", ioe.getMessage());

        verifyNoEmailsSent();

        ______TS("Normal case: instructor is not registered");

        String unregisteredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructorNotYetJoinedCourse1@email.tmt").getKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredInstructorKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        getJsonResult(a);

        verifyNumberOfEmailsSent(1);
        email = mockEmailSender.getEmailsSent().get(0);
        assertEquals(
                String.format(EmailType.USER_COURSE_REGISTER.getSubject(),
                        "Typical Course 1 with 2 Evals", "idOfTypicalCourse1"),
                email.getSubject());

        ______TS("Failure case: invalid entity type");

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredStudentKey,
                Const.ParamsNames.ENTITY_TYPE, "unknown",
        };

        verifyHttpParameterFailure(params);

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
