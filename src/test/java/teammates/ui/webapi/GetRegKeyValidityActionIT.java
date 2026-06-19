package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetRegKeyValidityAction}.
 */
public class GetRegKeyValidityActionIT extends BaseActionIT<GetRegkeyValidityAction> {
    private DataBundle typicalBundle;

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH_REGKEY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testExecute() {
        Student student1 = typicalBundle.students.get("student1InCourse1");
        Instructor instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String student1Key = student1.getRegKey();
        String instructor1Key = instructor1.getRegKey();

        ______TS("Normal case: No logged in user for a used regkey; should be valid/used/disallowed");

        logoutUser();

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        GetRegkeyValidityAction getRegkeyValidityAction = getAction(params);
        JsonResult actionOutput = getJsonResult(getRegkeyValidityAction);

        RegkeyValidityData output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Wrong logged in user for a used regkey; should be valid/used/disallowed");

        loginAsInstructor(typicalBundle.instructors.get("instructor2OfCourse1"));

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Correct logged in user for a used regkey; should be valid/used/allowed");

        loginAsStudent(student1);

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertTrue(output.isAllowedAccess());

        loginAsInstructor(instructor1);

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertTrue(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: No logged in user for an unused regkey; should be valid/unused/allowed");

        inTransaction(() -> {
            logic.unlinkAccount(student1.getId());
            logic.unlinkAccount(instructor1.getId());
        });

        logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: Any logged in user for an unused regkey; should be valid/unused/allowed");

        loginAsInstructor(typicalBundle.instructors.get("instructor2OfCourse1"));

        params = new String[] {
                Const.ParamsNames.REGKEY, instructor1Key,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertTrue(output.isValid());
        assertFalse(output.isUsed());
        assertTrue(output.isAllowedAccess());

        ______TS("Normal case: Invalid regkey; should be invalid/unused/disallowed");

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt("invalid-key"),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());

        params = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt("invalid-key"),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());

        ______TS("Normal case: Invalid intent; should be invalid/unused/disallowed");

        logoutUser();

        params = new String[] {
                Const.ParamsNames.REGKEY, student1Key,
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
        };

        getRegkeyValidityAction = getAction(params);
        actionOutput = getJsonResult(getRegkeyValidityAction);

        output = (RegkeyValidityData) actionOutput.getOutput();
        assertFalse(output.isValid());
        assertFalse(output.isUsed());
        assertFalse(output.isAllowedAccess());

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
    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
