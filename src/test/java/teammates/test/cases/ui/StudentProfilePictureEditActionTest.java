package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentProfilePictureEditAction;

public class StudentProfilePictureEditActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
    }

    @Test
    public void testExecuteAndPostProcess() {

        AccountAttributes student = dataBundle.accounts.get("student2InCourse1");
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
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;
        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[1] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyRightY(AccountAttributes student) {
        String[] submissionParams;
        ______TS("Failure case: empty parameter - rightx");
        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[3] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyTopY(AccountAttributes student) {
        ______TS("Failure case: empty parameter - topy");
        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[5] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyBottomY(AccountAttributes student) {
        ______TS("Failure case: empty parameter - bottomy");
        String expectedLogMessage = getExpectedLogMessageEmptyCoords(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[7] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyHeight(AccountAttributes student) {
        ______TS("Failure case: empty parameter - height");
        String expectedLogMessage = getExpectedLogMessageEmptyDimensions(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[9] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForEmptyWidth(AccountAttributes student) {
        ______TS("Failure case: empty parameter - width");
        String expectedLogMessage = getExpectedLogMessageEmptyDimensions(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[11] = "";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForZeroHeight(AccountAttributes student) {
        ______TS("Failure case: zero height");
        String expectedLogMessage = getExpectedLogMessageZeroDimensions(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[9] = "0";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForZeroWidth(AccountAttributes student) {
        ______TS("Failure case: zero width");
        String expectedLogMessage = getExpectedLogMessageZeroDimensions(student);
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;

        String[] submissionParams = createValidParamsForProfilePictureEdit();
        submissionParams[11] = "0";

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private void testActionForNonExistentBlobKey(AccountAttributes student) {
        ______TS("Failure case: non-existent blobKey");
        String expectedUrl = Const.ActionURIs.STUDENT_PROFILE_PAGE + "?error=true&user=" + student.googleId;
        String[] submissionParams = createValidParamsForProfilePictureEdit();

        StudentProfilePictureEditAction action = getAction(submissionParams);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();

        String expectedLogMessage = getExpectedLogMessageNonExistentBlob(student);

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
        assertEquals(expectedUrl, result.getDestinationWithParams());
    }

    private String getExpectedLogMessageNonExistentBlob(AccountAttributes student) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePictureEdit|||"
                           + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                           + student.googleId + "|||student2InCourse1@gmail.tmt|||"
                           + "Servlet Action Failure : Reading and transforming image failed.Could not read blob."
                           + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        return expectedLogMessage;
    }

    private String getExpectedLogMessageZeroDimensions(AccountAttributes student) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePictureEdit|||"
                                  + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                                  + student.googleId + "|||" + student.email + "|||"
                                  + "Servlet Action Failure : One or both of the image dimensions were zero."
                                  + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        return expectedLogMessage;
    }

    private String getExpectedLogMessageEmptyDimensions(AccountAttributes student) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePictureEdit|||"
                                  + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                                  + student.googleId + "|||" + student.email + "|||"
                                  + "Servlet Action Failure : One or both of the image dimensions were empty."
                                  + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        return expectedLogMessage;
    }

    private String getExpectedLogMessageEmptyCoords(AccountAttributes student) {
        String expectedLogMessage = "TEAMMATESLOG|||studentProfilePictureEdit|||"
                                  + "studentProfilePictureEdit|||true|||Student|||Student in two courses|||"
                                  + student.googleId + "|||" + student.email + "|||"
                                  + "Servlet Action Failure : One or more of the given coords were empty."
                                  + "|||" + Const.ActionURIs.STUDENT_PROFILE_PICTURE_EDIT;
        return expectedLogMessage;
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

    private StudentProfilePictureEditAction getAction(String... params) {
        return (StudentProfilePictureEditAction) gaeSimulation.getActionObject(uri, params);
    }

}
