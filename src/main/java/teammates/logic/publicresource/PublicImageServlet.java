package teammates.logic.publicresource;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;

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
        
        try{      
            if (blobKey != "") {
                resp.setContentType("image/png");
                BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
                blobstoreService.serve(new BlobKey(blobKey), resp);
                // TODO : restrict image request to those "public" files only
                
                String url = req.getRequestURL().toString() + 
                             "?blob-key=" + blobKey;
          
                
                String message = "Public image request with URL: <br>" + 
                                 "<a href=\"" + url + "\" target=blank>" +
                                 url + "</a>";
                logMessage(req, message);
            } else {               
                String message = "Failed to serve image with URL : blobKey is missing";
                logMessage(req, message);
                resp.sendError(1, "No image found");
            }
            
        } catch (IOException e){
            log.warning(ActivityLogEntry.generateServletActionFailureLogMessage(req, e));
        }
    }

}
