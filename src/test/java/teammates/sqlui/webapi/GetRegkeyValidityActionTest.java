package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.RegkeyValidityData;
import teammates.ui.request.Intent;
import teammates.ui.webapi.GetRegkeyValidityAction;
import teammates.ui.webapi.JsonResult;

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
    void testStudentIntentNotLoggedInUsedKey_validUsedDisallowed() {
        when(mockLogic.getStudentByRegistrationKey(stubRegkey)).thenReturn(stubStudentWithAccount);

        executeAndAssert(stubRegkey, Intent.STUDENT_SUBMISSION, true, true, false);
        executeAndAssert(stubRegkey, Intent.STUDENT_RESULT, true, true, false);
    }



    @Test
    void testExecute_instructorIntentNotLoggedInUsedKey_validUsedDisallowedKey() {
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_SUBMISSION, true, true, false);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_RESULT, true, true, false);
    }

    @Test
    void testExecute_studentIntentLoggedInUsedKey_validUsedAllowedKey() {
        executeAndAssert(stubRegkey, Intent.STUDENT_SUBMISSION, true, true, true);
        executeAndAssert(stubRegkey, Intent.STUDENT_RESULT, true, true, true);
    }

    @Test
    void testExecute_instructorIntentLoggedInUsedKey_validUsedAllowedKey() {
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_SUBMISSION, true, true, true);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_RESULT, true, true, true);
    }

    @Test
    void testExecute_studentIntentWrongUserLoggedInUsedKey_validUsedDisallowedKey() {
        executeAndAssert(stubRegkey, Intent.STUDENT_SUBMISSION, true, true, false);
        executeAndAssert(stubRegkey, Intent.STUDENT_RESULT, true, true, false);
    }

    @Test
    void testExecute_instructorIntentWrongUserLoggedInUsedKey_validUsedDisallowedKey() {
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_SUBMISSION, true, true, false);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_RESULT, true, true, false);
    }

    @Test
    void testExecute_studentIntentNotLoggedInUnusedKey_validUnusedAllowed() {
        executeAndAssert(stubRegkey, Intent.STUDENT_SUBMISSION, true, false, true);
        executeAndAssert(stubRegkey, Intent.STUDENT_RESULT, true, false, true);
    }

    @Test
    void testExecute_instructorIntentNotLoggedInUnusedKey_validUnusedAllowed() {
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_SUBMISSION, true, false, true);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_RESULT, true, false, true);
    }

    @Test
    void testExecute_studentIntentLoggedInUnusedKey_validUnusedAllowed() {
        executeAndAssert(stubRegkey, Intent.STUDENT_SUBMISSION, true, false, true);
        executeAndAssert(stubRegkey, Intent.STUDENT_RESULT, true, false, true);
    }

    @Test
    void testExecute_instructorIntentLoggedInUnusedKey_validUnusedAllowed() {
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_SUBMISSION, true, false, true);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_RESULT, true, false, true);
    }

    @Test
    void testExecute_invalidRegkey_invalidUnusedDisallowed() {
        executeAndAssert(stubRegkey, Intent.STUDENT_SUBMISSION, false, false, false);
        executeAndAssert(stubRegkey, Intent.STUDENT_RESULT, false, false, false);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_SUBMISSION, false, false, false);
        executeAndAssert(stubRegkey, Intent.INSTRUCTOR_RESULT, false, false, false);
    }

    @Test
    void testExecute_invalidIntent_invalidUnusedDisallowed() {
        executeAndAssert(stubRegkey, Intent.FULL_DETAIL, false, false, false);
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

    private RegkeyValidityData executeAction(String regkey, Intent intent) {
        String[] params = {
                Const.ParamsNames.REGKEY, regkey,
                Const.ParamsNames.INTENT, intent.name()
        };
        GetRegkeyValidityAction action = getAction(params);
        JsonResult jsonResult = action.execute();
        return (RegkeyValidityData) jsonResult.getOutput();
    }

    private void assertRegkeyData(RegkeyValidityData data, boolean valid, boolean used, boolean allowed) {
        assertEquals(valid, data.isValid());
        assertEquals(used, data.isUsed());
        assertEquals(allowed, data.isAllowedAccess());
    }

    private void executeAndAssert(String regkey, Intent intent, boolean valid, boolean used, boolean allowed) {
        RegkeyValidityData data = executeAction(regkey, intent);
        assertRegkeyData(data, valid, used, allowed);
    }


}
