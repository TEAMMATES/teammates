package teammates.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
        String returnValue = null;
        String startIndicator = "?"+parameterName+"=";
        
        int startIndicationLoaction = urlString.indexOf(startIndicator);
        if(startIndicationLoaction<0){
            startIndicator = "&"+parameterName+"=";
            startIndicationLoaction = urlString.indexOf(startIndicator);
        }
        
        if(startIndicationLoaction<0){
            return null;
        }
        
        int startIndex = startIndicationLoaction+parameterName.length()+2;
        String prefixStripped = urlString.substring(startIndex);
        int endIndex = prefixStripped.indexOf('&');
        if(endIndex>0){
            returnValue = prefixStripped.substring(0, endIndex);
        }else{
            returnValue = prefixStripped;
        }
        return returnValue;
    }
    
    public Url withUserId(String userId) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.USER_ID, userId);
        return this;
    }
    
    public Url withRegistrationKey(String key) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.REGKEY, key);
        return this;
    }
    
    public Url withCourseId(String courseId) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.COURSE_ID, courseId);
        return this;
    }

    public Url withEvalName(String evaluationName) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.EVALUATION_NAME, evaluationName);
        return this;
    }
    
    public Url withSessionName(String feedbackSessionName) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        return this;
    }

    public Url withStudentEmail(String email) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.STUDENT_EMAIL, email);
        return this;
    }

    public Url withInstructorId(String instructorId) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        return this;
    }

    public Url withCourseName(String courseName) {
        this.urlString = Url.addParamToUrl(this.urlString, Const.ParamsNames.COURSE_NAME, courseName);
        return this;
    }
    
    public Url withParam(String paramName, String paramValue) {
        this.urlString = Url.addParamToUrl(this.urlString, paramName, paramValue);
        return this;
    }

    /**
     * Converts a string to be put in URL (replaces some characters)
     */
    public static String convertForURL(String str){
        try {
            return URLEncoder.encode(str, Const.SystemParams.ENCODING);
        } catch (UnsupportedEncodingException e){
            return str;
        }
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
        if (key == null || value == null)
            return url;
        if (url.contains("?" + key + "=") || url.contains("&" + key + "="))
            return url;
        url += url.indexOf('?') >= 0 ? '&' : '?';
        url += key + "=" + Url.convertForURL(value);
        return url;
    }

    public static String trimTrailingSlash(String url) {
        return url.trim().replaceAll("/(?=$)", "");
    }

    @Override
    public String toString(){
        return urlString;
    }

    public static void main(String[] args) {
        System.out.println(new Url("http://teammates.com/page/instructorHome?user=abc").get(Const.ParamsNames.USER_ID));
        System.out.println(new Url("http://teammates.com/page/instructorHome?user=abc&course=course1").get(Const.ParamsNames.USER_ID));
        System.out.println(new Url ("http://teammates.com/page/instructorHome?error=true&user=abc&course=course1").get(Const.ParamsNames.USER_ID));
    }

}
