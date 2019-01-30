package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.action.GetCourseJoinStatusAction;
import teammates.ui.webapi.action.GetCourseJoinStatusAction.JoinStatus;
import teammates.ui.webapi.action.JsonResult;

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

        GetCourseJoinStatusAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        JoinStatus output = (JoinStatus) r.getOutput();
        assertTrue(output.isHasJoined());
        assertNull(output.getUserId());

        ______TS("Normal case: student is not registered");

        String unregisteredStudentKey =
                logic.getStudentForEmail("idOfUnregisteredCourse", "student1InUnregisteredCourse@gmail.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (JoinStatus) r.getOutput();
        assertFalse(output.isHasJoined());
        assertEquals("unreg.user", output.getUserId());

        ______TS("Failure case: regkey is not valid for student");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());

        ______TS("Normal case: instructor is already registered");

        String registeredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(registeredInstructorKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (JoinStatus) r.getOutput();
        assertTrue(output.isHasJoined());
        assertNull(output.getUserId());

        ______TS("Normal case: instructor is not registered");

        String unregisteredInstructorKey =
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructorNotYetJoinedCourse1@email.tmt").key;

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredInstructorKey),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (JoinStatus) r.getOutput();
        assertFalse(output.isHasJoined());
        assertEquals("unreg.user", output.getUserId());

        ______TS("Failure case: regkey is not valid for instructor");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_NOT_FOUND, r.getStatusCode());

        ______TS("Failure case: invalid entity type");

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unregisteredStudentKey),
                Const.ParamsNames.ENTITY_TYPE, "unknown",
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_BAD_REQUEST, r.getStatusCode());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
