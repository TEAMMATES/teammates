package teammates.common.datatransfer.logs;

import java.util.Map;

import jakarta.annotation.Nullable;

/**
 * Contains specific structure and processing logic for HTTP request log.
 */
public class RequestLogDetails extends LogDetails {

    private int responseStatus;
    private long responseTime;
    private String requestMethod;
    private String requestUrl;
    private String userAgent;
    private String instanceId;
    private String webVersion;
    private String referrer;
    @Nullable
    private Map<String, Object> requestParams;
    @Nullable
    private Map<String, Object> requestHeaders;
    @Nullable // TODO remove nullable annotation 30 days after release of V8.2.1
    private String requestBody;
    @Nullable
    private String actionClass;
    @Nullable
    private RequestLogUser userInfo;

    public RequestLogDetails() {
        super(LogEvent.REQUEST_LOG);
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getWebVersion() {
        return webVersion;
    }

    public void setWebVersion(String webVersion) {
        this.webVersion = webVersion;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, Object> requestParams) {
        this.requestParams = requestParams;
    }

    public Map<String, Object> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, Object> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getActionClass() {
        return actionClass;
    }

    public void setActionClass(String actionClass) {
        this.actionClass = actionClass;
    }

    public RequestLogUser getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(RequestLogUser userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public void hideSensitiveInformation() {
        requestHeaders = null;
        requestParams = null;
        requestBody = null;
        userInfo = null;

        if (referrer != null) {
            // Hide only the query parameters as the URL on its own is not sensitive
            referrer = referrer.split("\\?", 2)[0];
        }
    }

}
