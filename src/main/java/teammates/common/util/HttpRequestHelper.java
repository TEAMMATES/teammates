package teammates.common.util;

import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;

public class HttpRequestHelper {

    /**
     * 
     * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
     * @param key
     * @return the first value for the key. Returns null if key not found.
     */
    public static String getValueFromParamMap(Map<String, String[]> paramMap, String key) {
        String[] values = paramMap.get(key);
        return values == null ? null : values[0];
    }

    /**
     * 
     * @param paramMap A parameter map (e.g., the kind found in HttpServletRequests)
     * @param key
     * @return all values for the key. Returns null if key not found.
     */
    public static String[] getValuesFromParamMap(Map<String, String[]> paramMap, String key) {
        String[] values = paramMap.get(key);
        return values == null ? null : values;
    }
    
    /**
     * Returns a HashMap object containing all the parameters key-value pairs
     * from a URLFetchRequest object
     */
    public static HashMap<String,String> getParamMap(URLFetchRequest request) {
        String requestBody = request.getPayload().toStringUtf8();
        String[] params = requestBody.split("&");
        HashMap<String,String> hashMap = new HashMap<String, String>();
        
        for (String param : params) {
            String[] pair = param.split("=");
            String name = pair[0];
            String value = pair[1];
            try {
                String decodedValue = URLDecoder.decode(value, "UTF8");
                
                hashMap.put(name, decodedValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        
        return hashMap;
    }
    
    /**
     * 
     * @param req An HttpServletRequest which contains the parameters map
     * @param key
     * @return the first value for the key. Returns null if key is not found
     */
    @SuppressWarnings("unchecked")
    public static String getValueFromRequestParameterMap(HttpServletRequest req, String key) {
        return getValueFromParamMap(req.getParameterMap(), key);
    }
    
    /**
     * 
     * @param req An HttpServletRequest which contains the parameters map
     * @param key
     * @return the values for the key. Returns null if key is not found
     */
    @SuppressWarnings("unchecked")
    public static String[] getValuesFromRequestParameterMap(HttpServletRequest req, String key) {
        return getValuesFromParamMap(req.getParameterMap(), key);
    }

    //TODO: rename to a better name
    public static String printRequestParameters(HttpServletRequest request) {
        String requestParameters = "{";
        for (Enumeration<?> f = request.getParameterNames(); f.hasMoreElements();) {
            String paramet = new String(f.nextElement().toString());
            requestParameters += paramet + "::";
            String[] parameterValues = request.getParameterValues(paramet);
            for (int j = 0; j < parameterValues.length; j++){
                requestParameters += parameterValues[j] + "//";
            }
            requestParameters = requestParameters.substring(0, requestParameters.length() - 2) + ", ";
        }
        if (!requestParameters.equals("{")) {
            requestParameters = requestParameters.substring(0, requestParameters.length() - 2);
        }
        requestParameters += "}";
        return requestParameters;
    }

    /**
     * @return  the URL used for the HTTP request but without the domain.
     * e.g. "/page/studentHome?user=james" 
     */
    public static String getRequestedURL(HttpServletRequest req) {
        String link = req.getRequestURI();
        String query = req.getQueryString();
        if (query != null && !query.trim().isEmpty()){
            link += "?" + query;
        }
        return link;
    }

}
