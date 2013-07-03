package teammates.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import teammates.test.driver.TestProperties;

public class Url {

	private String urlString;

	public Url(String url) {
		this.urlString = url;
		if(urlString.startsWith("/")){
			urlString = TestProperties.inst().TEAMMATES_URL + urlString;
		}
	}

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
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_USER_ID, userId);
		return this;
	}
	
	public Url withCourseId(String courseId) {
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_COURSE_ID, courseId);
		return this;
	}

	public Url withEvalName(String evaluationName) {
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_EVALUATION_NAME, evaluationName);
		return this;
	}
	
	public Url withSessionName(String feedbackSessionName) {
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_FEEDBACK_SESSION_NAME, feedbackSessionName);
		return this;
	}

	public Url withStudentEmail(String email) {
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_STUDENT_EMAIL, email);
		return this;
	}

	public Url withInstructorId(String instructorId) {
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_INSTRUCTOR_ID, instructorId);
		return this;
	}

	public Url withCourseName(String courseName) {
		this.urlString = Url.addParamToUrl(this.urlString, Constants.PARAM_COURSE_NAME, courseName);
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
			return URLEncoder.encode(str, Config.ENCODING);
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
	 * 
	 * @param url
	 * @param key
	 * @param value
	 * @return
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
		System.out.println(new Url("http://teammates.com/page/instructorHome?user=abc").get(Constants.PARAM_USER_ID));
		System.out.println(new Url("http://teammates.com/page/instructorHome?user=abc&course=course1").get(Constants.PARAM_USER_ID));
		System.out.println(new Url("http://teammates.com/page/instructorHome?error=true&user=abc&course=course1").get(Constants.PARAM_USER_ID));
		System.out.println(new Url("/page/instHome").withUserId("abc").toString());
		System.out.println(new Url("/page/instHome").withUserId("abc").withCourseId("course1").toString());
		System.out.println(new Url("http://google.com").withUserId("abc").withCourseId("course1").toString());
	}

}
