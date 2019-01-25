package teammates.test.cases.newaction;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.newcontroller.JsonResult;
import teammates.ui.newcontroller.PutStudentProfilePictureAction;

/**
 * SUT: {@link PutStudentProfilePictureAction}.
 */
public class PutStudentProfilePictureActionTest extends BaseActionTest<PutStudentProfilePictureAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    public void testExecute() {
        AccountAttributes student = typicalBundle.accounts.get("student2InCourse1");
        loginAsStudent(student.googleId);

        testActionForEmptyLeftX();
        testActionForEmptyRightY();
        testActionForEmptyTopY();
        testActionForEmptyBottomY();
        testActionForEmptyHeight();
        testActionForEmptyWidth();
        testActionForZeroHeight();
        testActionForZeroWidth();
        testActionForNonExistentBlobKey();
    }

    private void testActionForEmptyLeftX() {
        ______TS("Failure case: empty parameter - leftx");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[1] = "";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForEmptyRightY() {
        String[] submissionParams;
        ______TS("Failure case: empty parameter - rightx");

        submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[3] = "";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForEmptyTopY() {
        ______TS("Failure case: empty parameter - topy");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[5] = "";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForEmptyBottomY() {
        ______TS("Failure case: empty parameter - bottomy");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[7] = "";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForEmptyHeight() {
        ______TS("Failure case: empty parameter - height");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[9] = "";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForEmptyWidth() {
        ______TS("Failure case: empty parameter - width");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[11] = "";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForZeroHeight() {
        ______TS("Failure case: zero height");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[9] = "0";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForZeroWidth() {
        ______TS("Failure case: zero width");

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[11] = "0";

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private void testActionForNonExistentBlobKey() {
        ______TS("Failure case: non-existent blobKey");

        String[] submissionParams = createValidParamsForProfilePictureEdit();

        PutStudentProfilePictureAction action = getAction(submissionParams);
        JsonResult result = getJsonResult(action);

        assertEquals(result.getStatusCode(), HttpStatus.SC_BAD_REQUEST);
    }

    private String[] createValidParamsForProfilePictureEdit() {
        return new String[] {
                Const.ParamsNames.PROFILE_PICTURE_LEFTX, "0",
                Const.ParamsNames.PROFILE_PICTURE_RIGHTX, "100",
                Const.ParamsNames.PROFILE_PICTURE_TOPY, "0",
                Const.ParamsNames.PROFILE_PICTURE_BOTTOMY, "100",
                Const.ParamsNames.PROFILE_PICTURE_HEIGHT, "500",
                Const.ParamsNames.PROFILE_PICTURE_WIDTH, "300",
                Const.ParamsNames.PROFILE_PICTURE_ROTATE, "90",
                Const.ParamsNames.BLOB_KEY, "random-blobKey"
        };
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {
                Const.ParamsNames.PROFILE_PICTURE_LEFTX, "0",
                Const.ParamsNames.PROFILE_PICTURE_RIGHTX, "100",
                Const.ParamsNames.PROFILE_PICTURE_TOPY, "0",
                Const.ParamsNames.PROFILE_PICTURE_BOTTOMY, "100",
                Const.ParamsNames.PROFILE_PICTURE_HEIGHT, "500",
                Const.ParamsNames.PROFILE_PICTURE_WIDTH, "300",
                Const.ParamsNames.PROFILE_PICTURE_ROTATE, "180",
                Const.ParamsNames.BLOB_KEY, "random-blobKey"
        };
        verifyAnyLoggedInUserCanAccess(submissionParams);
    }
}
