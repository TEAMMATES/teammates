package teammates.test.cases.webapi;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PutStudentProfileAction;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link PutStudentProfileAction}.
 */
public class PutStudentProfileActionTest extends BaseActionTest<PutStudentProfileAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        AccountAttributes student1 = typicalBundle.accounts.get("student1InCourse1");
        AccountAttributes student2 = typicalBundle.accounts.get("student2InCourse1");

        testActionWithInvalidParameters(student1);
        testActionSuccess(student1, "Typical Case");
        testActionForbidden(student1, student2, "Forbidden Case");
        testActionInMasqueradeMode(student1);

        student1 = typicalBundle.accounts.get("student1InTestingSanitizationCourse");
        // simulate sanitization that occurs before persistence
        student1.sanitizeForSaving();
        testActionSuccess(student1, "Typical case: attempted script injection");
    }

    private void testActionWithInvalidParameters(AccountAttributes student) throws Exception {
        loginAsStudent(student.googleId);
        ______TS("Failure case: invalid parameters");

        String[] submissionParams = createInvalidParamsForProfile(student.googleId);

        PutStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        List<String> expectedErrorMessages = new ArrayList<>();
        MessageOutput invalidOutput = (MessageOutput) result.getOutput();

        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, submissionParams[3],
                        FieldValidator.PERSON_NAME_FIELD_NAME,
                        FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                        FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, submissionParams[5],
                        FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH));
        expectedErrorMessages.add(
                String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[9])));

        assertEquals(String.join(System.lineSeparator(), expectedErrorMessages), invalidOutput.getMessage());

        ______TS("Failure case: invalid parameters with attempted script injection");

        submissionParams = createInvalidParamsForProfileWithScriptInjection(student.googleId);

        action = getAction(submissionParams);
        result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);

        expectedErrorMessages = new ArrayList<>();
        invalidOutput = (MessageOutput) result.getOutput();

        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[3]),
                        FieldValidator.PERSON_NAME_FIELD_NAME,
                        FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                        FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[5]),
                        FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[7]),
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME,
                        FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                        FieldValidator.INSTITUTE_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[9])));

        assertEquals(String.join(System.lineSeparator(), expectedErrorMessages), invalidOutput.getMessage());
    }

    private void testActionSuccess(AccountAttributes student, String caseDescription) {
        String[] submissionParams = createValidParamsForProfile(student.googleId);
        loginAsStudent(student.googleId);

        ______TS(caseDescription);

        PutStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_ACCEPTED);
    }

    private void testActionForbidden(AccountAttributes student1, AccountAttributes student2,
                                     String caseDescription) {
        String[] submissionParams = createValidParamsForProfile(student1.googleId);
        loginAsStudent(student2.googleId);

        ______TS(caseDescription);

        PutStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_FORBIDDEN, result.getStatusCode());
    }

    private void testActionInMasqueradeMode(AccountAttributes student) {

        ______TS("Typical case: masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = createValidParamsForMasqueradeMode(student.googleId);

        PutStudentProfileAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_ACCEPTED);
    }

    private String[] createValidParamsForMasqueradeMode(String googleId) {
        return new String[] {
                Const.ParamsNames.STUDENT_ID, googleId,
                Const.ParamsNames.USER_ID, googleId,
                Const.ParamsNames.STUDENT_SHORT_NAME, "short ",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "e@email.com  ",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, " TEAMMATES Test Institute 5   ",
                Const.ParamsNames.STUDENT_NATIONALITY, "American",
                Const.ParamsNames.STUDENT_GENDER, "  other   ",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "   This is more info on me   ",
        };
    }

    private String[] createValidParamsForProfile(String googleId) {
        return new String[] {
                Const.ParamsNames.STUDENT_ID, googleId,
                Const.ParamsNames.STUDENT_SHORT_NAME, "short ",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "e@email.com  ",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, " TEAMMATES Test Institute 5   ",
                Const.ParamsNames.STUDENT_NATIONALITY, "American",
                Const.ParamsNames.STUDENT_GENDER, "  other   ",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "   This is more info on me   ",
        };
    }

    private String[] createInvalidParamsForProfile(String googleId) {
        return new String[] {
                Const.ParamsNames.STUDENT_ID, googleId,
                Const.ParamsNames.STUDENT_SHORT_NAME, "$$short",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "invalid.email",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, "institute",
                Const.ParamsNames.STUDENT_NATIONALITY, "USA",
                Const.ParamsNames.STUDENT_GENDER, "female",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "This is more info on me",
        };
    }

    private String[] createInvalidParamsForProfileWithScriptInjection(String googleId) {
        return new String[] {
                Const.ParamsNames.STUDENT_ID, googleId,
                Const.ParamsNames.STUDENT_SHORT_NAME, "short%<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, "<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_NATIONALITY, "USA<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_GENDER, "female<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "This is more info on me<script>alert(\"was here\");</script>",
        };
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
