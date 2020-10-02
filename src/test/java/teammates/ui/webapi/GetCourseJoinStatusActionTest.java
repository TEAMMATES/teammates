package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.JoinStatus;

/**
 * SUT: {@link GetCourseJoinStatusAction}.
 */
public class GetCourseJoinStatusActionTest extends BaseActionTest<GetCourseJoinStatusAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
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

        ______TS("Normal case: student is already registered");

        String registeredStudentKey =
                logic.getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt").key;

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(registeredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetCourseJoinStatusAction getCourseJoinStatusAction = getAction(params);
        JsonResult result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        JoinStatus output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());
        assertEquals("unreg.user", output.getUserId());

        ______TS("Normal case: student is not registered");

        String unregisteredStudentKey =
                logic.getStudentForEmail("idOfUnregisteredCourse", "student1InUnregisteredCourse@gmail.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());
        assertEquals("unreg.user", output.getUserId());

        ______TS("Failure case: regkey is not valid for student");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        ______TS("Normal case: instructor is already registered");

        String registeredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(registeredInstructorKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());
        assertEquals("unreg.user", output.getUserId());

        ______TS("Normal case: instructor is not registered");

        String unregisteredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructorNotYetJoinedCourse1@email.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredInstructorKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());
        assertEquals("unreg.user", output.getUserId());

        ______TS("Failure case: regkey is not valid for instructor");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        ______TS("Failure case: invalid entity type");

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, "unknown",
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        assertEquals(HttpStatus.SC_BAD_REQUEST, result.getStatusCode());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
