package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.JsonResult;
import teammates.ui.newcontroller.PostStudentProfileFormUrlAction;

/**
 * SUT: {@link PostStudentProfileFormUrlAction}.
 */
public class PostStudentProfileFormUrlActionTest extends BaseActionTest<PostStudentProfileFormUrlAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_FORM_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        AccountAttributes student = typicalBundle.accounts.get("student1InCourse1");

        testGenerateUploadUrlSuccessTypical(student);
        testGenerateUploadUrlSuccessMasqueradeMode(student);
    }

    private void testGenerateUploadUrlSuccessTypical(AccountAttributes student) {
        ______TS("Typical case");

        String[] submissionParams = new String[] {};
        loginAsStudent(student.googleId);
        PostStudentProfileFormUrlAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        assertEquals(result.getStatusCode(), HttpStatus.SC_OK);
    }

    private void testGenerateUploadUrlSuccessMasqueradeMode(AccountAttributes student) {
        ______TS("Typical case: masquerade mode");

        gaeSimulation.loginAsAdmin("admin.user");

        String[] submissionParams = new String[] {
                Const.ParamsNames.USER_ID, student.googleId
        };

        PostStudentProfileFormUrlAction action = getAction(addUserIdToParams(student.googleId,
                submissionParams));
        JsonResult result = getJsonResult(action);
        assertEquals(result.getStatusCode(), HttpStatus.SC_OK);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}
