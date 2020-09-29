package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.output.StudentProfilePictureResults;

/**
 * SUT: {@link PostStudentProfilePictureAction}.
 */
public class PostStudentProfilePictureActionTest extends BaseActionTest<PostStudentProfilePictureAction> {
    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.STUDENT_PROFILE_PICTURE;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        AccountAttributes student1 = typicalBundle.accounts.get("student1InCourse1");
        loginAsStudent(student1.googleId);

        ______TS("Typical case: upload profile picture operation successful");

        String filePath = "src/test/resources/images/profile_pic.png";
        PostStudentProfilePictureAction action = getActionWithParts("studentprofilephoto", filePath);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        StudentProfilePictureResults output = (StudentProfilePictureResults) result.getOutput();
        String resultPictureKey = output.getPictureKey();
        String newPictureKey = logic.getStudentProfile(student1.googleId).pictureKey;

        assertNotNull(resultPictureKey);
        assertNotEquals(resultPictureKey, "");
        assertEquals(resultPictureKey, newPictureKey);

        ______TS("Typical case: profile picture is null");

        PostStudentProfilePictureAction nullProfilePicAction = getAction();

        assertThrows(InvalidHttpRequestBodyException.class, () -> nullProfilePicAction.execute());

        ______TS("Typical case: profile picture is too large");

        String largeProfilePicFilePath = "src/test/resources/images/profile_pic_too_large.jpg";
        PostStudentProfilePictureAction largeProfilePicAction =
                getActionWithParts("studentprofilephoto", largeProfilePicFilePath);

        assertThrows(InvalidHttpRequestBodyException.class, () -> largeProfilePicAction.execute());

        ______TS("Typical case: not a profile picture");

        String invalidProfilePicFilePath = "src/test/resources/images/not_a_picture.txt";
        PostStudentProfilePictureAction invalidProfilePicAction =
                getActionWithParts("studentprofilephoto", invalidProfilePicFilePath);

        assertThrows(InvalidHttpRequestBodyException.class, () -> invalidProfilePicAction.execute());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
