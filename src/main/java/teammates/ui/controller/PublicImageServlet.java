package teammates.ui.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.UserType;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogMessageGenerator;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

/**
 * Serves the public image stored in google cloud storage using the blobkey.<br>
 * Correct blobkey is required for image serving.
 */
@SuppressWarnings("serial")
public class PublicImageServlet extends PublicResourcesServlet {

    @SuppressWarnings("unchecked")
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {

        servletName = Const.PublicActionNames.PUBLIC_IMAGE_SERVE_ACTION;
        action = Const.PublicActionNames.PUBLIC_IMAGE_SERVE_ACTION;

        requestParameters = req.getParameterMap();
        String blobKey = getBlobKeyFromRequest();

        try {
            if (blobKey.isEmpty()) {
                String message = "Failed to serve image with URL : blobKey is missing";
                logMessage(req, message);
                resp.sendError(1, "No image found");
            } else {
                resp.setContentType("image/png");
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                blobstoreService.serve(new BlobKey(blobKey), resp);
                // TODO : restrict image request to those "public" files only

                String url = req.getRequestURL().toString() + "?blob-key=" + blobKey;

                String message = "Public image request with URL: <br>"
                               + "<a href=\"" + url + "\" target=\"_blank\" rel=\"noopener noreferrer\" >"
                               + url + "</a>";
                logMessage(req, message);
            }

        } catch (IOException e) {
            UserType userType = new GateKeeper().getCurrentUser();
            String url = HttpRequestHelper.getRequestedUrl(req);
            Map<String, String[]> params = HttpRequestHelper.getParameterMap(req);
            log.warning(new LogMessageGenerator()
                                .generateActionFailureLogMessage(url, params, e, userType));
        }
    }

}
