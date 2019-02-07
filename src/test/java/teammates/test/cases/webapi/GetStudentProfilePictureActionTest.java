package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetStudentProfilePictureAction;
import teammates.ui.webapi.action.ImageResult;

/**
 * SUT: {@link GetStudentProfilePictureAction}.
 */
public class GetStudentProfilePictureActionTest extends BaseActionTest<GetStudentProfilePictureAction> {

    private AccountAttributes account;
    private StudentProfileAttributes studentProfileAttributes;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
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

        GetStudentProfilePictureAction action = getAction(submissionParams);
        assertThrows(NullHttpParameterException.class, () -> action.execute());
    }

    /**
     * Tests the branch of the Action handling a request from the Student
     * directly, where the parameters are simply the blobKey of the picture
     * itself.
     */
    private void testActionWithBlobKey() {
        testActionWithBlobKeySuccess();
    }

    private void testActionWithBlobKeySuccess() {
        ______TS("Typical case: using blobkey");
        loginAsStudent(account.googleId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.BLOB_KEY, studentProfileAttributes.pictureKey,
        };
        GetStudentProfilePictureAction action = getAction(submissionParams);
        ImageResult result = getImageResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals(studentProfileAttributes.pictureKey, result.blobKey);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }

}
