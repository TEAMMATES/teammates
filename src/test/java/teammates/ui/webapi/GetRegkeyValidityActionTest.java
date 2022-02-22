package teammates.ui.webapi;

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

        String courseId = "idOfTypicalCourse1";

        String student1Key = logic.getStudentForEmail(courseId, "student1InCourse1@gmail.tmt").getKey();
        String instructor1Key = logic.getInstructorForEmail(courseId, "instructor1@course1.tmt").getKey();

        ______TS("Failure Case: No intent parameter");

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
        };

        verifyHttpParameterFailure(params);

        ______TS("Failure Case: No regkey parameter");

        params = new String[] {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        verifyHttpParameterFailure(params);

        ______TS("Normal case: No logged in user for a used regkey; should be valid/used/disallowed");

        logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        GetRegkeyValidityAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        RegkeyValidityData output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
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

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        loginAsInstructor("idOfInstructor2OfCourse1");

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Correct logged in user for a used regkey; should be valid/used/allowed");

        loginAsStudent("student1InCourse1");

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertTrue(output.isAllowedAccess());

        loginAsInstructor("idOfInstructor1OfCourse1");

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: No logged in user for an unused regkey; should be valid/unused/allowed");

        logic.resetStudentGoogleId("student1InCourse1@gmail.tmt", courseId);
        logic.resetInstructorGoogleId("instructor1@course1.tmt", courseId);

        logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: Any logged in user for an unused regkey; should be valid/unused/allowed");

        loginAsStudent("student2InCourse1");

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        loginAsInstructor("idOfInstructor5");

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

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

        output = (RegkeyValidityData) r.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt("invalid-key"),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

        output = (RegkeyValidityData) r.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Invalid intent; should be invalid/unused/disallowed");

        logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
        };

        a = getAction(params);
        r = getJsonResult(a);

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
