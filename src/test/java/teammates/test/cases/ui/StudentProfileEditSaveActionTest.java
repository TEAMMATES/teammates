package teammates.test.cases.ui;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfileEditSaveAction;

public class StudentProfileEditSaveActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.STUDENT_PROFILE_EDIT_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");
        
        testActionWithInvalidParameters(student);
        testActionTypicalSuccess(student);
        testActionInMasqueradeMode(student);
    }

    private void testActionWithInvalidParameters(AccountAttributes student) throws Exception {
        gaeSimulation.loginAsStudent(student.googleId);
        ______TS("Failure case: invalid parameters");
        
        String[] submissionParams = createInvalidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(submissionParams);
        expectedProfile.googleId = student.googleId;
        
        StudentProfileEditSaveAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertTrue(result.isError);
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE
                                    + "?error=true&user=" + student.googleId,
                                    result.getDestinationWithParams());
        List<String> expectedErrorMessages = new ArrayList<String>();
        
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
    }

    private void testActionTypicalSuccess(AccountAttributes student) {
        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(submissionParams);
        gaeSimulation.loginAsStudent(student.googleId);
        
        ______TS("Typical case");
        
        StudentProfileEditSaveAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        expectedProfile.googleId = student.googleId;
        
        assertFalse(result.isError);
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=false&user=" + student.googleId,
                                    result.getDestinationWithParams());
        assertEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED, result.getStatusMessage());
        
        verifyLogMessage(student, action, expectedProfile, false);
    }

    private void testActionInMasqueradeMode(AccountAttributes student) {

        ______TS("Typical case: masquerade mode");
        gaeSimulation.loginAsAdmin("admin.user");
        
        String[] submissionParams = createValidParamsForProfile();
        StudentProfileAttributes expectedProfile = getProfileAttributesFrom(submissionParams);
        expectedProfile.googleId = student.googleId;
        
        StudentProfileEditSaveAction action = getAction(addUserIdToParams(student.googleId, submissionParams));
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertFalse(result.isError);
        assertEquals(Const.StatusMessages.STUDENT_PROFILE_EDITED, result.getStatusMessage());
        AssertHelper.assertContains(Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=false&user=" + student.googleId,
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
                                  + ")</span> edited.<br>" + expectedProfile.toString()
                                  + "|||/page/studentProfileEditSave";
        AssertHelper.assertContainsRegex(expectedLogMessage, action.getLogMessage());
    }

    private StudentProfileAttributes getProfileAttributesFrom(
            String[] submissionParams) {
        StudentProfileAttributes spa = new StudentProfileAttributes();
        
        spa.shortName = StringHelper.trimIfNotNull(submissionParams[1]);
        spa.email = StringHelper.trimIfNotNull(submissionParams[3]);
        spa.institute = StringHelper.trimIfNotNull(submissionParams[5]);
        spa.nationality = StringHelper.trimIfNotNull(submissionParams[7]);
        spa.gender = StringHelper.trimIfNotNull(submissionParams[9]);
        spa.moreInfo = StringHelper.trimIfNotNull(submissionParams[11]);
        spa.modifiedDate = null;
        
        return spa;
    }

    private StudentProfileEditSaveAction getAction(String[] submissionParams) {
        return (StudentProfileEditSaveAction) gaeSimulation.getActionObject(uri, submissionParams);
    }

}
