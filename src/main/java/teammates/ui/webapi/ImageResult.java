package teammates.ui.webapi;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

/**
 * Action result in form of an image.
 */
class ImageResult extends ActionResult {

    private byte[] bytes;

    ImageResult() {
        super(HttpStatus.SC_NO_CONTENT);
        this.bytes = new byte[0];
    }

    ImageResult(byte[] bytes) {
        super(HttpStatus.SC_OK);
        this.bytes = bytes;
    }

    byte[] getBytes() {
        return this.bytes;
    }

    @Override
    void send(HttpServletResponse resp) throws IOException {
        resp.setContentType("image/png");
        resp.getOutputStream().write(bytes);
    }

}
