package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.DeleteStudentProfilePictureAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.MessageOutput;

/**
 * SUT: {@link DeleteStudentProfilePictureAction}.
 */
public class DeleteStudentProfilePictureActionTest extends BaseActionTest<DeleteStudentProfilePictureAction> {

    private AccountAttributes account;
    private StudentProfileAttributes studentProfileAttributes;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @BeforeClass
    public void classSetup() {
        account = typicalBundle.accounts.get("student1InCourse1");
        studentProfileAttributes = typicalBundle.profiles.get("student1InCourse1");
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        testActionWithNoParams();
        testActionWithBlobKey();
    }

    private void testActionWithNoParams() {

        ______TS("Failure case: no parameters given");
        loginAsStudent(account.googleId);

        String[] submissionParams = new String[] {};

        DeleteStudentProfilePictureAction action = getAction(submissionParams);
        assertThrows(NullHttpParameterException.class, () -> action.execute());
    }

    private void testActionWithBlobKey() {
        testActionWithBlobKeySuccess();
    }

    private void testActionWithBlobKeySuccess() {
        ______TS("Typical case: Success scenario");
        loginAsStudent(account.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.STUDENT_ID, studentProfileAttributes.googleId,
                Const.ParamsNames.BLOB_KEY, studentProfileAttributes.pictureKey,
        };
        DeleteStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();
        String newPictureKey = logic.getStudentProfile(account.googleId).pictureKey;

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals(messageOutput.getMessage(), "Your profile picture has been deleted successfully");
        assertEquals("", newPictureKey);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }

}
