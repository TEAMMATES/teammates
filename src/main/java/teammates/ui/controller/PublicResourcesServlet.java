package teammates.ui.controller;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;

@SuppressWarnings("serial")
public class PublicResourcesServlet extends HttpServlet {
    protected static final Logger log = Utils.getLogger();
    
    /** Parameters received with the request */
    protected Map<String, String[]> requestParameters;

    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        this.doPost(req, resp);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        
        requestParameters = req.getParameterMap();
        
        String blobKey = getBlobKeyFromRequest();

        if (blobKey != "") {
            resp.setContentType("image/png");
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            blobstoreService.serve(new BlobKey(blobKey), resp);
        } else {
            resp.sendError(1, "No image found");;
        }
    }

    private String getBlobKeyFromRequest() {
        String blobKey = getRequestParamValue(Const.ParamsNames.BLOB_KEY);
        Assumption.assertPostParamNotNull(Const.ParamsNames.BLOB_KEY, blobKey);
        return blobKey;
    }
    
    public String getRequestParamValue(String paramName) {
        return HttpRequestHelper.getValueFromParamMap(requestParameters, paramName);
    }
}
