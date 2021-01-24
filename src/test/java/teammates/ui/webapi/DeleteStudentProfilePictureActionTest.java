package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.MessageOutput;

/**
 * SUT: {@link DeleteStudentProfilePictureAction}.
 */
public class DeleteStudentProfilePictureActionTest extends BaseActionTest<DeleteStudentProfilePictureAction> {

    private AccountAttributes account;

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
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        testValidAction();
        testInvalidProfileAction();
    }

    private void testValidAction() throws Exception {
        ______TS("Typical case: success scenario");

        loginAsStudent(account.googleId);

        writeFileToStorage(account.googleId, "src/test/resources/images/profile_pic.png");
        assertTrue(doesFileExist(account.googleId));

        String[] submissionParams = {
                Const.ParamsNames.STUDENT_ID, account.googleId,
        };
        DeleteStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        assertEquals(messageOutput.getMessage(), "Your profile picture has been deleted successfully");

        assertFalse(doesFileExist(account.googleId));
    }

    private void testInvalidProfileAction() {
        ______TS("Typical case: invalid student profile");

        loginAsStudent(account.googleId);
        String[] submissionParams = {
                Const.ParamsNames.STUDENT_ID, "invalidGoogleId",
        };
        DeleteStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);
        MessageOutput messageOutput = (MessageOutput) result.getOutput();

        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());
        assertEquals(messageOutput.getMessage(), "Invalid student profile");
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }

}
