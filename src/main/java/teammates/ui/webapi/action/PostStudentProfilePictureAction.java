package teammates.ui.webapi.action;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.ui.webapi.output.StudentProfilePictureResults;

/**
 * Action: saves the file information of the profile picture that was just uploaded.
 */
public class PostStudentProfilePictureAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        try {
            Part image = extractProfilePicture();
            byte[] imageData = new byte[(int) image.getSize()];
            try (InputStream is = image.getInputStream()) {
                is.read(imageData);
            }
            String pictureKey = GoogleCloudStorageHelper.writeImageDataToGcs(userInfo.id, imageData);
            logic.updateOrCreateStudentProfile(
                    StudentProfileAttributes.updateOptionsBuilder(userInfo.id)
                            .withPictureKey(pictureKey)
                            .build());
            StudentProfilePictureResults dataFormat =
                    new StudentProfilePictureResults(Const.StatusMessages.STUDENT_PROFILE_PICTURE_SAVED, pictureKey);
            return new JsonResult(dataFormat);
        } catch (InvalidParametersException | ServletException | IOException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
    }

    private Part extractProfilePicture() throws IOException, ServletException, InvalidParametersException {
        Part image = req.getPart("studentprofilephoto");
        if (image == null) {
            throw new InvalidParametersException(Const.StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN);
        }
        return validateProfilePicture(image);
    }

    private Part validateProfilePicture(Part image) throws InvalidParameterException {
        if (image.getSize() > Const.SystemParams.MAX_PROFILE_PIC_SIZE) {
            throw new InvalidParameterException(Const.StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE);
        } else if (!image.getContentType().startsWith("image/")) {
            throw new InvalidParameterException(Const.StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE);
        }
        return image;
    }
}
