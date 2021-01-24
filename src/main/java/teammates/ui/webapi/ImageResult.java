package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import teammates.logic.api.FileStorage;

/**
 * Action result in form of an image.
 */
class ImageResult extends ActionResult {

    // TODO figure out how to inject fileStorage to this class without explicit initialization
    private FileStorage fileStorage = new FileStorage();

    /** The blob key for the image. */
    private String blobKey;

    ImageResult() {
        super(HttpStatus.SC_NO_CONTENT);
    }

    ImageResult(String blobKey) {
        super(HttpStatus.SC_OK);
        this.blobKey = blobKey;
    }

    String getBlobKey() {
        return blobKey;
    }

    @Override
    void send(HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        if (blobKey != null) {
            fileStorage.serve(resp, blobKey);
        }
    }

}
