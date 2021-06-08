package teammates.ui.webapi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

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
        String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes));

        // Replaces "application/xml" with "image/svg+xml" as guessContentTypeFromStream only guesses the former.
        // We can do this conversion because "image/svg+xml" is the only MIME type based around XML for
        // image files we accept.
        if ("application/xml".equals(contentType)) {
            contentType = "image/svg+xml";
        }
        resp.setContentType(contentType);
        resp.getOutputStream().write(bytes);
    }

}
