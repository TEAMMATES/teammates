package teammates.common.util;

/**
 * The Url class represents a URL string.
 * It provides methods to manipulate the URL string and extract values from it.
 */
public class Url {

    private String relativeUrl;

    public Url(String url) {
        this.relativeUrl = url == null ? "" : url.replace(getAppUrl(), ""); // force the URL to be relative
    }

    protected String getAppUrl() {
        return Config.APP_URL;
    }
    
    /**
     * @return The value of the {@code parameterName} parameter. Null if no
     * such parameter.
     */
    public String get(String parameterName) {
        String startIndicator = "?" + parameterName + "=";

        int startIndicationLocation = relativeUrl.indexOf(startIndicator);
        if (startIndicationLocation < 0) {
            startIndicator = "&" + parameterName + "=";
            startIndicationLocation = relativeUrl.indexOf(startIndicator);
        }

        if (startIndicationLocation < 0) {
            return null;
        }

        int startIndex = startIndicationLocation + parameterName.length() + 2;
        String prefixStripped = relativeUrl.substring(startIndex);
        int endIndex = prefixStripped.indexOf('&');
        if (endIndex > 0) {
            return prefixStripped.substring(0, endIndex);
        } else {
            return prefixStripped;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withUserId(String userId) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.USER_ID, userId);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withRegistrationKey(String key) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.REGKEY, key);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withInstructorInstitution(String institute) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withCourseId(String courseId) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.COURSE_ID, courseId);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withSessionName(String feedbackSessionName) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withStudentEmail(String email) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.STUDENT_EMAIL, email);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withInstructorId(String instructorId) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withCourseName(String courseName) {
        relativeUrl = addParamToUrl(relativeUrl, Const.ParamsNames.COURSE_NAME, courseName);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Url> T withParam(String paramName, String paramValue) {
        relativeUrl = addParamToUrl(relativeUrl, paramName, paramValue);
        return (T) this;
    }

    /**
     * Returns the URL with the specified key-value pair parameter added.
     * The parameter will also be sanitized according to URL specification.
     * Unchanged if either the key or value is null, or the key already exists<br />
     * Example:
     * <ul>
     * <li><code>addParam("index.jsp","action","add")</code> returns
     * <code>index.jsp?action=add</code></li>
     * <li><code>addParam("index.jsp?action=add","courseid","cs1101")</code>
     * returns <code>index.jsp?action=add&courseid=cs1101</code></li>
     * <li><code>addParam("index.jsp","message",null)</code> returns
     * <code>index.jsp</code></li>
     * </ul>
     */
    public static String addParamToUrl(String url, String key, String value) {
        if (key == null || key.isEmpty() || value == null || value.isEmpty()
             || url.contains("?" + key + "=") || url.contains("&" + key + "=")) {
            // return the url if any of the key or the value is null or empty
            // or if the key is already included in the url
            return url;
        }
        return url + (url.contains("?") ? "&" : "?") + key + "=" + Sanitizer.sanitizeForUri(value);
    }

    public static String trimTrailingSlash(String url) {
        return url.trim().replaceAll("/(?=$)", "");
    }

    @Override
    public String toString() {
        return relativeUrl;
    }

    /**
     * Returns the absolute version of the URL by appending the application's URL
     * to the URL input.
     */
    public String toAbsoluteString() {
        return getAppUrl() + relativeUrl;
    }
    
}
