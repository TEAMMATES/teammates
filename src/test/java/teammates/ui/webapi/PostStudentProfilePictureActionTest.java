package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.request.InvalidHttpRequestBodyException;

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
        loginAsStudent(student1.getGoogleId());

        deleteFile(student1.getGoogleId());

        ______TS("Typical case: upload profile picture operation successful");

        String filePath = "src/test/resources/images/profile_pic.png";
        PostStudentProfilePictureAction action = getActionWithParts("studentprofilephoto", filePath);
        getJsonResult(action);

        assertTrue(doesFileExist(student1.getGoogleId()));

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

        deleteFile(student1.getGoogleId());
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
