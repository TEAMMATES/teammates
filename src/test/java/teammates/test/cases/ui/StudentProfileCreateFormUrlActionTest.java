package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.StudentProfileCreateFormUrlAction;
import teammates.ui.controller.StudentProfileCreateFormUrlAjaxPageData;

public class StudentProfileCreateFormUrlActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.STUDENT_PROFILE_CREATEUPLOADFORMURL;
    }

    @Test
    public void testExecuteAndPostProcess() {
        AccountAttributes student = dataBundle.accounts.get("student1InCourse1");

        testGenerateUploadUrlSuccessTypical(student);
        testGenerateUploadUrlSuccessMasqueradeMode(student);
    }

    private void testGenerateUploadUrlSuccessTypical(AccountAttributes student) {
        ______TS("Typical case");

        String[] submissionParams = new String[] {};
        gaeSimulation.loginAsStudent(student.googleId);
        StudentProfileCreateFormUrlAction action = getAction(submissionParams);
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();

        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());
        verifyLogMessage(student, action, result, false);
    }

    private void testGenerateUploadUrlSuccessMasqueradeMode(AccountAttributes student) {
        ______TS("Typical case: masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student.googleId
        };

        StudentProfileCreateFormUrlAction action = getAction(addUserIdToParams(student.googleId,
                                                                               submissionParams));
        AjaxResult result = (AjaxResult) action.executeAndPostProcess();

        assertFalse(result.isError);
        assertEquals("", result.getStatusMessage());
        verifyLogMessage(student, action, result, true);
    }

    private void verifyLogMessage(AccountAttributes student, StudentProfileCreateFormUrlAction action,
                                  AjaxResult result, boolean isMasquerade) {
        StudentProfileCreateFormUrlAjaxPageData data = (StudentProfileCreateFormUrlAjaxPageData) result.data;
        String expectedLogMessage = "TEAMMATESLOG|||studentProfileCreateFormUrl|||studentProfileCreateFormUrl"
                                  + "|||true|||Student" + (isMasquerade ? "(M)" : "") + "|||" + student.name
                                  + "|||" + student.googleId + "|||" + student.email + "|||Created Url successfully: "
                                  + data.formUrl + "|||/page/studentProfileCreateFormUrl";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    private StudentProfileCreateFormUrlAction getAction(String... params) {
        return (StudentProfileCreateFormUrlAction) gaeSimulation.getActionObject(uri, params);
    }

}
