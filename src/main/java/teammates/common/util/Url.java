package teammates.common.util;

public class Url {

    private String urlString;

    public Url(String url) {
        this.urlString = url;
    }

    /**
     * @return The value of the {@code parameterName} parameter. Null if no
     * such parameter.
     */
    public String get(String parameterName) {
        String startIndicator = "?" + parameterName + "=";

        int startIndicationLocation = urlString.indexOf(startIndicator);
        if (startIndicationLocation < 0) {
            startIndicator = "&" + parameterName + "=";
            startIndicationLocation = urlString.indexOf(startIndicator);
        }

        if (startIndicationLocation < 0) {
            return null;
        }

        int startIndex = startIndicationLocation + parameterName.length() + 2;
        String prefixStripped = urlString.substring(startIndex);
        int endIndex = prefixStripped.indexOf('&');
        if (endIndex > 0) {
            return prefixStripped.substring(0, endIndex);
        } else {
            return prefixStripped;
        }
    }

    public Url withUserId(String userId) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.USER_ID, userId);
        return this;
    }

    public Url withRegistrationKey(String key) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.REGKEY, key);
        return this;
    }

    public Url withCourseId(String courseId) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.COURSE_ID, courseId);
        return this;
    }

    public Url withSessionName(String feedbackSessionName) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        return this;
    }

    public Url withStudentEmail(String email) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.STUDENT_EMAIL, email);
        return this;
    }

    public Url withInstructorId(String instructorId) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        return this;
    }

    public Url withCourseName(String courseName) {
        this.urlString = addParamToUrl(this.urlString, Const.ParamsNames.COURSE_NAME, courseName);
        return this;
    }

    public Url withParam(String paramName, String paramValue) {
        this.urlString = addParamToUrl(this.urlString, paramName, paramValue);
        return this;
    }

    /**
     * Returns the URL with the specified key-value pair parameter added.
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
        if (key == null || value == null) {
            // return the url if any of the key or the value is null
            return url;
        }
        if (url.contains("?" + key + "=") || url.contains("&" + key + "=")) {
            // return the url if the key is already included in the url
            return url;
        }
        url += url.indexOf('?') >= 0 ? '&' : '?';
        url += key + "=" + Sanitizer.sanitizeForUri(value);
        return url;
    }

    public static String trimTrailingSlash(String url) {
        return url.trim().replaceAll("/(?=$)", "");
    }

    @Override
    public String toString() {
        return urlString;
    }

}
