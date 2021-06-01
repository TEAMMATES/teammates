package teammates.ui.webapi;

import static java.net.URLConnection.guessContentTypeFromStream;

import java.io.ByteArrayInputStream;
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
        String contentType = guessContentTypeFromStream(new ByteArrayInputStream(bytes));
        if ("application/xml".equals(contentType)) {
            contentType = "image/svg+xml";
        }
        resp.setContentType(contentType);
        resp.getOutputStream().write(bytes);
    }

}
