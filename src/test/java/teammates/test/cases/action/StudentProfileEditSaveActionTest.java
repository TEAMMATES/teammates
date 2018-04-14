package teammates.test.cases.action;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfileEditSaveAction;

/**
 * SUT: {@link StudentProfileEditSaveAction}.
 */
public class StudentProfileEditSaveActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        AccountAttributes student = typicalBundle.accounts.get("student1InCourse1");

        testActionWithInvalidParameters(student);
        testActionSuccess(student, "Typical Case");
        testActionInMasqueradeMode(student);

        student = typicalBundle.accounts.get("student1InTestingSanitizationCourse");
        // simulate sanitization that occurs before persistence
        student.sanitizeForSaving();
        testActionSuccess(student, "Typical case: attempted script injection");
    }

    private void testActionWithInvalidParameters(AccountAttributes student) throws Exception {
        gaeSimulation.loginAsStudent(student.googleId);
        ______TS("Failure case: invalid parameters");

        String[] submissionParams = createInvalidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(student.googleId, submissionParams);
        expectedProfile.googleId = student.googleId;

        StudentProfileEditSaveAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        assertTrue(result.isError);
        AssertHelper.assertContains(
                getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId),
                result.getDestinationWithParams());
        List<String> expectedErrorMessages = new ArrayList<>();

        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE, submissionParams[1],
                                         FieldValidator.PERSON_NAME_FIELD_NAME,
                                         FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                                         FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE, submissionParams[3],
                                         FieldValidator.EMAIL_FIELD_NAME,
                                         FieldValidator.REASON_INCORRECT_FORMAT,
                                         FieldValidator.EMAIL_MAX_LENGTH));

        AssertHelper.assertContains(expectedErrorMessages, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||studentProfileEditSave|||studentProfileEditSave"
                                  + "|||true|||Student|||" + student.name + "|||" + student.googleId
                                  + "|||" + student.email + "|||" + Const.ACTION_RESULT_FAILURE
                                  + " : " + result.getStatusMessage() + "|||/page/studentProfileEditSave";
        AssertHelper.assertContainsRegex(expectedLogMessage, action.getLogMessage());

        ______TS("Failure case: invalid parameters with attempted script injection");

        submissionParams = createInvalidParamsForProfileWithScriptInjection();
        expectedProfile = getProfileAttributesFrom(student.googleId, submissionParams);
        expectedProfile.googleId = student.googleId;

        action = getAction(submissionParams);
        result = getRedirectResult(action);

        assertTrue(result.isError);
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE
                        + "?error=true&user=" + student.googleId,
                result.getDestinationWithParams());
        expectedErrorMessages = new ArrayList<>();

        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[1]),
                        FieldValidator.PERSON_NAME_FIELD_NAME,
                        FieldValidator.REASON_CONTAINS_INVALID_CHAR,
                        FieldValidator.PERSON_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.EMAIL_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[3]),
                        FieldValidator.EMAIL_FIELD_NAME,
                        FieldValidator.REASON_INCORRECT_FORMAT,
                        FieldValidator.EMAIL_MAX_LENGTH));
        expectedErrorMessages.add(
                getPopulatedErrorMessage(FieldValidator.INVALID_NAME_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[5]),
                        FieldValidator.INSTITUTE_NAME_FIELD_NAME,
                        FieldValidator.REASON_START_WITH_NON_ALPHANUMERIC_CHAR,
                        FieldValidator.INSTITUTE_NAME_MAX_LENGTH));
        expectedErrorMessages.add(
                String.format(FieldValidator.NATIONALITY_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[7])));
        expectedErrorMessages.add(
                String.format(FieldValidator.GENDER_ERROR_MESSAGE,
                        SanitizationHelper.sanitizeForHtml(submissionParams[9])));

        AssertHelper.assertContains(expectedErrorMessages, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||studentProfileEditSave|||studentProfileEditSave"
                + "|||true|||Student|||" + student.name + "|||" + student.googleId
                + "|||" + student.email + "|||" + Const.ACTION_RESULT_FAILURE
                + " : " + result.getStatusMessage() + "|||/page/studentProfileEditSave";
        AssertHelper.assertContainsRegex(expectedLogMessage, action.getLogMessage());
    }

    private void testActionSuccess(AccountAttributes student, String caseDescription) {
        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(student.googleId, submissionParams);
        gaeSimulation.loginAsStudent(student.googleId);

        ______TS(caseDescription);

        StudentProfileEditSaveAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);
        expectedProfile.googleId = student.googleId;

        assertFalse(result.isError);
        AssertHelper.assertContains(
                getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, false, student.googleId),
                result.getDestinationWithParams());
        assertEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED, result.getStatusMessage());

        verifyLogMessage(student, action, expectedProfile, false);
    }

    private void testActionInMasqueradeMode(AccountAttributes student) {

        ______TS("Typical case: masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(student.googleId, submissionParams);
        expectedProfile.googleId = student.googleId;

        StudentProfileEditSaveAction action = getAction(addUserIdToParams(student.googleId, submissionParams));
        RedirectResult result = getRedirectResult(action);

        assertFalse(result.isError);
        assertEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED, result.getStatusMessage());
        AssertHelper.assertContains(
                getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, false, student.googleId),
                result.getDestinationWithParams());
        verifyLogMessage(student, action, expectedProfile, true);
    }

    //-------------------------------------------------------------------------------------------------------
    //-------------------------------------- Helper Functions -----------------------------------------------
    //-------------------------------------------------------------------------------------------------------

    private void verifyLogMessage(AccountAttributes student, StudentProfileEditSaveAction action,
                                  StudentProfileAttributes expectedProfile, boolean isMasquerade) {
        expectedProfile.modifiedDate = action.account.studentProfile.modifiedDate;
        String expectedLogMessage = "TEAMMATESLOG|||studentProfileEditSave|||studentProfileEditSave"
                                  + "|||true|||Student" + (isMasquerade ? "(M)" : "") + "|||"
                                  + student.name + "|||" + student.googleId + "|||" + student.email
                                  + "|||Student Profile for <span class=\"bold\">(" + student.googleId
                                  + ")</span> edited.<br>"
                                  + SanitizationHelper.sanitizeForHtmlTag(expectedProfile.toString())
                                  + "|||/page/studentProfileEditSave";
        AssertHelper.assertContainsRegex(expectedLogMessage, action.getLogMessage());
    }

    private StudentProfileAttributes getProfileAttributesFrom(
            String googleId, String[] submissionParams) {
        StudentProfileAttributes spa = StudentProfileAttributes.builder(googleId).build();

        spa.shortName = StringHelper.trimIfNotNull(submissionParams[1]);
        spa.email = StringHelper.trimIfNotNull(submissionParams[3]);
        spa.institute = StringHelper.trimIfNotNull(submissionParams[5]);
        spa.nationality = StringHelper.trimIfNotNull(submissionParams[7]);
        spa.gender = StringHelper.trimIfNotNull(submissionParams[9]);
        spa.moreInfo = StringHelper.trimIfNotNull(submissionParams[11]);
        spa.modifiedDate = null;

        return spa;
    }

    @Override
    protected StudentProfileEditSaveAction getAction(String... params) {
        return (StudentProfileEditSaveAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    private String[] createInvalidParamsForProfileWithScriptInjection() {
        return new String[] {
                Const.ParamsNames.STUDENT_SHORT_NAME, "short%<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_PROFILE_EMAIL, "<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_PROFILE_INSTITUTION, "<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_NATIONALITY, "USA<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_GENDER, "female<script>alert(\"was here\");</script>",
                Const.ParamsNames.STUDENT_PROFILE_MOREINFO, "This is more info on me<script>alert(\"was here\");</script>"
        };
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = createValidParamsForProfile();
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }

}
