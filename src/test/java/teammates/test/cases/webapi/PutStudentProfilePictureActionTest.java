package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PutStudentProfilePictureAction;

/**
 * SUT: {@link PutStudentProfilePictureAction}.
 */
public class PutStudentProfilePictureActionTest extends BaseActionTest<PutStudentProfilePictureAction> {

    private AccountAttributes account;
    private StudentProfileAttributes studentProfileAttributes;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
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

        PutStudentProfilePictureAction action = getAction(submissionParams);
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
        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }

}
