package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetRegkeyValidityAction}.
 */
public class GetRegkeyValidityActionTest extends BaseActionTest<GetRegkeyValidityAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH_REGKEY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {

        String[] params;

        String student1Key = StringHelper.encrypt(
                logic.getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt").key
        );

        ______TS("Failure Case: No intent parameter");

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
        };

        verifyHttpParameterFailure(params);

        ______TS("Failure Case: No regkey parameter");

        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        verifyHttpParameterFailure(params);

        ______TS("Normal case: No logged in user for a used regkey; should be valid/used/disallowed");

        gaeSimulation.logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        GetRegkeyValidityAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        RegkeyValidityData output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Wrong logged in user for a used regkey; should be valid/used/disallowed");

        loginAsStudent("student2InCourse1");

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Correct logged in user for a used regkey; should be valid/used/allowed");

        loginAsStudent("student1InCourse1");

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: No logged in user for an unused regkey; should be valid/unused/allowed");

        logic.resetStudentGoogleId("student1InCourse1@gmail.tmt", "idOfTypicalCourse1");

        gaeSimulation.logoutUser();

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: Any logged in user for an unused regkey; should be valid/unused/allowed");

        loginAsStudent("student2InCourse1");

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        loginAsStudent("student3InCourse1");

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: Invalid regkey; should be invalid/unused/disallowed");

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt("invalid-key"),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Invalid intent; should be invalid/unused/disallowed");

        gaeSimulation.logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());

        output = (RegkeyValidityData) r.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyUserCanAccess();
    }

}
