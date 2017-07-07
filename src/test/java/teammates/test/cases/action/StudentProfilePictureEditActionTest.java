package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfilePictureEditAction;

/**
 * SUT: {@link StudentProfilePictureEditAction}.
 */
public class StudentProfilePictureEditActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {

        AccountAttributes student = typicalBundle.accounts.get("student2InCourse1");
        gaeSimulation.loginAsStudent(student.googleId);

        testActionForEmptyLeftX(student);
        testActionForEmptyRightY(student);
        testActionForEmptyTopY(student);
        testActionForEmptyBottomY(student);
        testActionForEmptyHeight(student);
        testActionForEmptyWidth(student);
        testActionForZeroHeight(student);
        testActionForZeroWidth(student);
        testActionForNonExistentBlobKey(student);
    }

    private void testActionForEmptyLeftX(AccountAttributes student) {
        ______TS("Failure case: empty parameter - leftx");
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);
        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[1] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyRightY(AccountAttributes student) {
        String[] submissionParams;
        ______TS("Failure case: empty parameter - rightx");
        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[3] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyTopY(AccountAttributes student) {
        ______TS("Failure case: empty parameter - topy");
        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[5] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyBottomY(AccountAttributes student) {
        ______TS("Failure case: empty parameter - bottomy");
        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[7] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyHeight(AccountAttributes student) {
        ______TS("Failure case: empty parameter - height");
        String expectedLogMessage = getExpectedLogMessageEmptyDimensions(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[9] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyWidth(AccountAttributes student) {
        ______TS("Failure case: empty parameter - width");
        String expectedLogMessage = getExpectedLogMessageEmptyDimensions(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[11] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForZeroHeight(AccountAttributes student) {
        ______TS("Failure case: zero height");
        String expectedLogMessage = getExpectedLogMessageZeroDimensions(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[9] = "0";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForZeroWidth(AccountAttributes student) {
        ______TS("Failure case: zero width");
        String expectedLogMessage = getExpectedLogMessageZeroDimensions(student);
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[11] = "0";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForNonExistentBlobKey(AccountAttributes student) {
        ______TS("Failure case: non-existent blobKey");
        String expectedUrl = getPageResultDestination(Const.ActionURIs.STUDENT_PROFILE_PAGE, true, student.googleId);
        String[] submissionParams = createValidParamsForProfilePictureEdit();

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = getRedirectResult(action);

        String expectedLogMessage = getExpectedLogMessageNonExistentBlob(student);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private String getExpectedLogMessageNonExistentBlob(AccountAttributes student) {
        return "TEAMMATESLOG|||studentProfilePictureEdit|||"
                + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                + student.googleId + "|||student2InCourse1@gmail.tmt|||"
                + "Servlet Action Failure : Reading and transforming image failed.Could not read blob."
                + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
    }

    private String getExpectedLogMessageZeroDimensions(AccountAttributes student) {
        return "TEAMMATESLOG|||studentProfilePictureEdit|||"
                + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                + student.googleId + "|||" + student.email + "|||"
                + "Servlet Action Failure : One or both of the image dimensions were zero."
                + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
    }

    private String getExpectedLogMessageEmptyDimensions(AccountAttributes student) {
        return "TEAMMATESLOG|||studentProfilePictureEdit|||"
                + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                + student.googleId + "|||" + student.email + "|||"
                + "Servlet Action Failure : One or both of the image dimensions were empty."
                + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
    }

    private String getExpectedLogMessageEmptyCoords(AccountAttributes student) {
        return "TEAMMATESLOG|||studentProfilePictureEdit|||"
                + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                + student.googleId + "|||" + student.email + "|||"
                + "Servlet Action Failure : One or more of the given coords were empty."
                + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
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

    @Override
    protected StudentProfilePictureEditAction getAction(String... params) {
        return (StudentProfilePictureEditAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
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
        verifyAnyRegisteredUserCanAccess(submissionParams);
    }

}
