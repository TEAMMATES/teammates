package teammates.ui.webapi;

import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;

/**
 * SUT: {@link GetRegkeyValidityAction}.
 */
public class GetRegkeyValidityActionTest extends BaseActionTest<GetRegkeyValidityAction> {
    private Student stubStudentWithAccount;
    private Instructor stubInstructorWithAccount;
    private Student stubStudentWithoutAccount;
    private Instructor stubInstructorWithoutAccount;
    private String stubRegkey = "key";

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.AUTH_REGKEY;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        logoutUser();
        stubInstructorWithAccount = getTypicalInstructor();
        stubInstructorWithAccount.setAccount(getTypicalAccount());
        stubStudentWithAccount = getTypicalStudent();
        stubStudentWithAccount.setAccount(getTypicalAccount());

        stubInstructorWithoutAccount = getTypicalInstructor();
        stubStudentWithoutAccount = getTypicalStudent();
    }

    @Test
    void testExecute_invalidParams_throwsInvalidHttpParameterException() {
        String[] params1 = {
                Const.ParamsNames.REGKEY, stubRegkey,
        };
        verifyHttpParameterFailure(params1);

        String[] params2 = {
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        verifyHttpParameterFailure(params2);

        String[] params3 = {};
        verifyHttpParameterFailure(params3);

        loginAsStudent(stubStudentWithAccount.getGoogleId());
        verifyHttpParameterFailure(params1);
        verifyHttpParameterFailure(params2);
        verifyHttpParameterFailure(params3);

        logoutUser();
        loginAsInstructor(stubInstructorWithAccount.getGoogleId());
        verifyHttpParameterFailure(params1);
        verifyHttpParameterFailure(params2);
        verifyHttpParameterFailure(params3);
    }

    @Test
    void testExecute_studentIntentNotLoggedInUsedKey_validUsedDisallowed() {
        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        when(mockLogic.getStudentByRegistrationKey(stubRegkey)).thenReturn(stubStudentWithAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertTrue(data.isUsed());
        Assertions.assertFalse(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };
        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertTrue(data2.isUsed());
        Assertions.assertFalse(data2.isAllowedAccess());
    }

    @Test
    void testExecute_instructorIntentNotLoggedInUsedKey_validUsedDisallowedKey() {
        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getInstructorByRegistrationKey(stubRegkey)).thenReturn(stubInstructorWithAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertTrue(data.isUsed());
        Assertions.assertFalse(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };
        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertTrue(data2.isUsed());
        Assertions.assertFalse(data2.isAllowedAccess());
    }

    @Test
    void testExecute_studentIntentLoggedInUsedKey_validUsedAllowedKey() {
        loginAsStudent(stubStudentWithAccount.getGoogleId());

        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        when(mockLogic.getStudentByRegistrationKey(stubRegkey)).thenReturn(stubStudentWithAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertTrue(data.isUsed());
        Assertions.assertTrue(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertTrue(data2.isUsed());
        Assertions.assertTrue(data2.isAllowedAccess());
    }

    @Test
    void testExecute_instructorIntentLoggedInUsedKey_validUsedAllowedKey() {
        loginAsInstructor(stubInstructorWithAccount.getGoogleId());

        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getInstructorByRegistrationKey(stubRegkey)).thenReturn(stubInstructorWithAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertTrue(data.isUsed());
        Assertions.assertTrue(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertTrue(data2.isUsed());
        Assertions.assertTrue(data2.isAllowedAccess());
    }

    @Test
    void testExecute_studentIntentWrongUserLoggedInUsedKey_validUsedDisallowedKey() {
        loginAsStudent("another-id");

        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        when(mockLogic.getStudentByRegistrationKey(stubRegkey)).thenReturn(stubStudentWithAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertTrue(data.isUsed());
        Assertions.assertFalse(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertTrue(data2.isUsed());
        Assertions.assertFalse(data2.isAllowedAccess());
    }

    @Test
    void testExecute_instructorIntentWrongUserLoggedInUsedKey_validUsedDisallowedKey() {
        loginAsInstructor("another-id");

        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getInstructorByRegistrationKey(stubRegkey)).thenReturn(stubInstructorWithAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertTrue(data.isUsed());
        Assertions.assertFalse(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertTrue(data2.isUsed());
        Assertions.assertFalse(data2.isAllowedAccess());
    }

    @Test
    void testExecute_studentIntentNotLoggedInUnusedKey_validUnusedAllowed() {
        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        when(mockLogic.getStudentByRegistrationKey(stubRegkey)).thenReturn(stubStudentWithoutAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertFalse(data.isUsed());
        Assertions.assertTrue(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertFalse(data2.isUsed());
        Assertions.assertTrue(data2.isAllowedAccess());
    }

    @Test
    void testExecute_instructorIntentNotLoggedInUnusedKey_validUnusedAllowed() {
        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getInstructorByRegistrationKey(stubRegkey)).thenReturn(stubInstructorWithoutAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertFalse(data.isUsed());
        Assertions.assertTrue(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertFalse(data2.isUsed());
        Assertions.assertTrue(data2.isAllowedAccess());
    }

    @Test
    void testExecute_studentIntentLoggedInUnusedKey_validUnusedAllowed() {
        loginAsStudent(stubStudentWithAccount.getGoogleId());

        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        when(mockLogic.getStudentByRegistrationKey(stubRegkey)).thenReturn(stubStudentWithoutAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertFalse(data.isUsed());
        Assertions.assertTrue(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertFalse(data2.isUsed());
        Assertions.assertTrue(data2.isAllowedAccess());
    }

    @Test
    void testExecute_instructorIntentLoggedInUnusedKey_validUnusedAllowed() {
        loginAsInstructor(stubInstructorWithAccount.getGoogleId());

        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getInstructorByRegistrationKey(stubRegkey)).thenReturn(stubInstructorWithoutAccount);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertTrue(data.isValid());
        Assertions.assertFalse(data.isUsed());
        Assertions.assertTrue(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertTrue(data2.isValid());
        Assertions.assertFalse(data2.isUsed());
        Assertions.assertTrue(data2.isAllowedAccess());
    }

    @Test
    void testExecute_invalidRegkey_invalidUnusedDisallowed() {
        String[] params = {
                Const.ParamsNames.REGKEY, "invalid-regkey",
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.name(),
        };
        when(mockLogic.getStudentByRegistrationKey("invalid-regkey")).thenReturn(null);

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertFalse(data.isValid());
        Assertions.assertFalse(data.isUsed());
        Assertions.assertFalse(data.isAllowedAccess());

        String[] params2 = {
                Const.ParamsNames.REGKEY, "invalid-regkey",
                Const.ParamsNames.INTENT, Intent.STUDENT_RESULT.name(),
        };

        GetRegkeyValidityAction action2 = getAction(params2);
        JsonResult jsonResult2 = action2.execute();
        RegkeyValidityData data2 = (RegkeyValidityData) jsonResult2.getOutput();

        Assertions.assertFalse(data2.isValid());
        Assertions.assertFalse(data2.isUsed());
        Assertions.assertFalse(data2.isAllowedAccess());

        String[] params3 = {
                Const.ParamsNames.REGKEY, "invalid-regkey",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.name(),
        };
        when(mockLogic.getInstructorByRegistrationKey("invalid-regkey")).thenReturn(null);

        GetRegkeyValidityAction action3 = getAction(params3);
        JsonResult jsonResult3 = action3.execute();
        RegkeyValidityData data3 = (RegkeyValidityData) jsonResult3.getOutput();

        Assertions.assertFalse(data3.isValid());
        Assertions.assertFalse(data3.isUsed());
        Assertions.assertFalse(data3.isAllowedAccess());

        String[] params4 = {
                Const.ParamsNames.REGKEY, "invalid-regkey",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.name(),
        };

        GetRegkeyValidityAction action4 = getAction(params4);
        JsonResult jsonResult4 = action4.execute();
        RegkeyValidityData data4 = (RegkeyValidityData) jsonResult4.getOutput();

        Assertions.assertFalse(data4.isValid());
        Assertions.assertFalse(data4.isUsed());
        Assertions.assertFalse(data4.isAllowedAccess());
    }

    @Test
    void testExecute_invalidIntent_invalidUnusedDisallowed() {
        String[] params = {
                Const.ParamsNames.REGKEY, stubRegkey,
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.name(),
        };

        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        RegkeyValidityData data = (RegkeyValidityData) jsonResult.getOutput();

        Assertions.assertFalse(data.isValid());
        Assertions.assertFalse(data.isUsed());
        Assertions.assertFalse(data.isAllowedAccess());
    }

    @Test
    void testSpecificAccessControl_anyUser_canAccess() {
        verifyCanAccess();

        loginAsAdmin();
        verifyCanAccess();

        logoutUser();
        loginAsStudent(stubStudentWithAccount.getGoogleId());
        verifyCanAccess();

        logoutUser();
        loginAsInstructor(stubInstructorWithAccount.getGoogleId());
        verifyCanAccess();

        logoutUser();
        loginAsMaintainer();
        verifyCanAccess();

        logoutUser();
        loginAsUnregistered(stubStudentWithAccount.getGoogleId());
        verifyCanAccess();

        logoutUser();
        loginAsStudentInstructor(stubStudentWithAccount.getGoogleId());
        verifyCanAccess();
    }
}
