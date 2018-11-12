package teammates.ui.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.datatransfer.UserInfo;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.LogMessageGenerator;
import teammates.common.util.Logger;
import teammates.logic.api.GateKeeper;

/**
 * Serves the public image stored in google cloud storage using the blobkey.<br>
 * Correct blobkey is required for image serving.
 */
@SuppressWarnings("serial")
public class PublicImageServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String url = HttpRequestHelper.getRequestedUrl(req);

        UserInfo userInfo = new GateKeeper().getCurrentUser();
        String blobKey = req.getParameter(Const.ParamsNames.BLOB_KEY);
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY, blobKey);

        try {
            if (blobKey.isEmpty()) {
                String message = "Failed to serve image with URL : blobKey is missing";
                Map<String, String[]> params = req.getParameterMap();
                log.info(new LogMessageGenerator().generateBasicActivityLogMessage(url, params, message, userInfo));
                resp.sendError(1, "No image found");
            } else {
                resp.setContentType("image/png");
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                blobstoreService.serve(new BlobKey(blobKey), resp);
                // TODO : restrict image request to those "public" files only

                String message = "Public image request with URL: <br>"
                               + "<a href=\"" + url + "\" target=\"_blank\" rel=\"noopener noreferrer\" >"
                               + url + "</a>";

                Map<String, String[]> params = req.getParameterMap();
                log.info(new LogMessageGenerator().generateBasicActivityLogMessage(url, params, message, userInfo));
            }
        } catch (IOException ioe) {
            Map<String, String[]> params = req.getParameterMap();
            log.warning(new LogMessageGenerator().generateActionFailureLogMessage(url, params, ioe, userInfo));
        } catch (Exception e) {
            log.severe("Exception occured while performing publicImageServeAction:"
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
