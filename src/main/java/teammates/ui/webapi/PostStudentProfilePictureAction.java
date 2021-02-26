package teammates.ui.webapi;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.apache.http.HttpStatus;

import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.UnauthorizedAccessException;

/**
 * Action: saves the file information of the profile picture that was just uploaded.
 */
class PostStudentProfilePictureAction extends Action {

    private static final int MAX_PROFILE_PIC_SIZE = 5000000;

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isStudent) {
            throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
        }
    }

    @Override
    JsonResult execute() {
        try {
            Part image = req.getPart("studentprofilephoto");
            if (image == null) {
                throw new InvalidHttpRequestBodyException("Please specify a file to be uploaded.");
            }
            if (image.getSize() > MAX_PROFILE_PIC_SIZE) {
                throw new InvalidHttpRequestBodyException("The uploaded profile picture was too large. "
                        + "Please try again with a smaller picture.");
            }
            if (!image.getContentType().startsWith("image/")) {
                throw new InvalidHttpRequestBodyException("The file that you have uploaded is not a picture. "
                        + "Please upload a picture (usually it ends with .jpg or .png)");
            }
            byte[] imageData = new byte[(int) image.getSize()];
            try (InputStream is = image.getInputStream()) {
                is.read(imageData);
            }
            fileStorage.create(userInfo.id, imageData, image.getContentType());
            return new JsonResult("Your profile picture is updated successfully.");
        } catch (ServletException | IOException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
