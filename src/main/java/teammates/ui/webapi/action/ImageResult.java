package teammates.ui.webapi.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

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
        BlobKey bk = new BlobKey(blobKey);
        String contentType = new BlobInfoFactory().loadBlobInfo(bk).getContentType();

        resp.setContentType(contentType);

        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        blobstoreService.serve(bk, resp);
    }

}
