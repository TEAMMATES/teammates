package teammates.test.driver;

import teammates.common.Common;

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
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_USER_ID, userId);
		return this;
	}
	
	public Url withCourseId(String courseId) {
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_COURSE_ID, courseId);
		return this;
	}

	public Url withEvalName(String evaluationName) {
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_EVALUATION_NAME, evaluationName);
		return this;
	}
	
	public Url withSessionName(String feedbackSessionName) {
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_FEEDBACK_SESSION_NAME, feedbackSessionName);
		return this;
	}

	public Url withStudentEmail(String email) {
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_STUDENT_EMAIL, email);
		return this;
	}

	public Url withInstructorId(String instructorId) {
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_INSTRUCTOR_ID, instructorId);
		return this;
	}

	public Url withCourseName(String courseName) {
		this.urlString = Common.addParamToUrl(this.urlString, Common.PARAM_COURSE_NAME, courseName);
		return this;
	}
	
	public Url withParam(String paramName, String paramValue) {
		this.urlString = Common.addParamToUrl(this.urlString, paramName, paramValue);
		return this;
	}

	@Override
	public String toString(){
		return urlString;
	}

	public static void main(String[] args) {
		System.out.println(new Url("http://teammates.com/page/instructorHome?user=abc").get(Common.PARAM_USER_ID));
		System.out.println(new Url("http://teammates.com/page/instructorHome?user=abc&course=course1").get(Common.PARAM_USER_ID));
		System.out.println(new Url("http://teammates.com/page/instructorHome?error=true&user=abc&course=course1").get(Common.PARAM_USER_ID));
		System.out.println(new Url("/page/instHome").withUserId("abc").toString());
		System.out.println(new Url("/page/instHome").withUserId("abc").withCourseId("course1").toString());
		System.out.println(new Url("http://google.com").withUserId("abc").withCourseId("course1").toString());
	}

}
