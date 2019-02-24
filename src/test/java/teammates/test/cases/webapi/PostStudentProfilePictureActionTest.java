package teammates.test.cases.webapi;

import java.io.File;
import java.net.URLConnection;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.PostStudentProfilePictureAction;
import teammates.ui.webapi.output.StudentProfilePictureResults;

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
        StudentProfileAttributes student1ProfileAttributes = typicalBundle.profiles.get("student1InCourse1");

        String oldPictureKey = student1ProfileAttributes.pictureKey;
        loginAsStudent(student1.googleId);

        ______TS("Typical case: upload profile picture operation successful");

        String filePath = "src/test/resources/images/profile_pic.png";
        String contentType = URLConnection.guessContentTypeFromName(new File(filePath).getName());

        PostStudentProfilePictureAction action = getActionWithParts("studentprofilephoto", contentType, filePath);
        JsonResult result = getJsonResult(action);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        StudentProfilePictureResults output = (StudentProfilePictureResults) result.getOutput();
        String newPictureKey = logic.getStudentProfile(student1.googleId).pictureKey;

        assertNotNull(output.getPictureKey());
        assertNotEquals(oldPictureKey, newPictureKey);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyInaccessibleWithoutLogin();
        verifyInaccessibleForUnregisteredUsers();
    }
}
