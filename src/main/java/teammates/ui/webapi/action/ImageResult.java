package teammates.ui.webapi.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.common.util.GoogleCloudStorageHelper;

/**
 * Action result in form of an image.
 */
public class ImageResult extends ActionResult {

    /** The blob key for the image. */
    public String blobKey;

    public ImageResult(String blobKey) {
        super(HttpStatus.SC_OK);
        this.blobKey = blobKey;
    }

    @Override
    public void send(HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        GoogleCloudStorageHelper.serve(resp, blobKey);
    }

}
