package teammates.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.exception.TeammatesException;

import com.google.appengine.api.log.AppLogLine;

/** A log entry to describe an action carried out by the app */
public class ActivityLogEntry {
	private long time;
	private String servletName;
	private String action; //TODO: remove if not needed (and rename servletName to action)
	private String role;
	private String name;
	private String googleId;
	private String email;
	private boolean toShow;
	private String message;
	private String url;
	
	
	/**
	 * Constructor that creates a empty ActivityLog
	 */
	public ActivityLogEntry(String servlet, String params, String link){
		time = System.currentTimeMillis();
		servletName = servlet;
		action = "Unknown";
		role = "Unknown";
		name = "Unknown";
		googleId = "Unknown";
		email = "Unknown";
		toShow = true;
		message = "<span class=\"color_red\">Error. ActivityLogEntry object is not created for this servlet action.</span><br>"
				+ params;
		url = link;
	}
	
	
	/**
	 * Constructor that creates an ActivityLog object from a app log on the server.
	 * Used in AdminActivityLogServlet.
	 */
	public ActivityLogEntry(AppLogLine appLog){
		time = appLog.getTimeUsec() / 1000;
		String[] tokens = appLog.getLogMessage().split("\\|\\|\\|", -1);
		
		//TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL
		try{
			servletName = tokens[1];
			action = tokens[2];
			toShow = (tokens[3].equals("true") ? true : false);
			role = tokens[4];
			name = tokens[5];
			googleId = tokens[6];
			email = tokens[7];
			message = tokens[8];
			url = tokens[9];
		} catch (ArrayIndexOutOfBoundsException e){
			
			servletName = "Unknown";
			action = "Unknown";
			role = "Unknown";
			name = "Unknown";
			googleId = "Unknown";
			email = "Unknown";
			toShow = true;
			message = "<span class=\"color_red\">Error. Problem parsing log message from the server.</span><br>"
					+ "System Error: " + e.getMessage() + "<br>" + appLog.getLogMessage();
			url = "Unknown";
		}
	}
	
	
	/**
	 * Constructor that creates an ActivityLog object from scratch
	 * Used in the various servlets in the application
	 */
	public ActivityLogEntry(String servlet, String act, AccountAttributes acc, String params,  String link){
		time = System.currentTimeMillis();
		servletName = servlet;
		action = act;
		toShow = true;
		message = params;
		url = link;
		
		if (acc == null){
			role = "Unknown";
			name = "Unknown";
			googleId = "Unknown";
			email = "Unknown";
		} else {
			role = acc.isInstructor ? "Instructor" : "Student"; 
			name = acc.name;
			googleId = acc.googleId;
			email = acc.email;
		}
	}
	
	public ActivityLogEntry(AccountAttributes userAccount, boolean isMasquerade, String logMessage,  String requestUrl){
		time = System.currentTimeMillis();
		servletName = getActionName(requestUrl);
		action = servletName; //TODO: remove this?
		toShow = true;
		message = logMessage;
		url = requestUrl;
		
		if (userAccount == null){
			role = "Unknown";
			name = "Unknown";
			googleId = "Unknown";
			email = "Unknown";
		} else {
			role = userAccount.isInstructor ? "Instructor" : "Student"; 
			role = role + (isMasquerade? "(M)" : "");
			name = userAccount.name;
			googleId = userAccount.googleId;
			email = userAccount.email;
		}
	}
	
	/**
	 * Assumption: the {@code requestUrl} is in the format "/something/actionName" 
	 *   possibly followed by "?something" e.g., "/page/studentHome?user=abc"
	 * @return action name in the URL e.g., "studentHome" in the above example.
	 */
	public static String getActionName(String requestUrl) {
		try {
			return requestUrl.split("/")[2].split("\\?")[0];
		} catch (Throwable e) {
			return "error in getActionName for requestUrl : "+ requestUrl;
		}
	}


	/**
	 * Generates a log message that will be logged in the server
	 */
	public String generateLogMessage(){
		//TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL
		return "TEAMMATESLOG|||" + servletName + "|||" + action + "|||" + (toShow ? "true" : "false") + "|||" 
				+ role + "|||" + name + "|||" + googleId + "|||" + email + "|||" + message + "|||" + url;
	}
	
	
	public String getDateInfo(){
		Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        appCal.setTimeInMillis(time);

		return sdf.format(appCal.getTime());
	}
	
	public String getRoleInfo(){
		return role ;
	}
	
	public String getPersonInfo(){
		return "[" + name +
				" <a href=\""+getInstructorHomePageViewLink(googleId)+"\" target=\"_blank\">" + googleId + "</a>" +
				" <a href=\"mailto:"+email+"\" target=\"_blank\">" + email +"</a>]" ;
	}
	
	public String getActionInfo(){
		String style = "";
		if(action.equals(Const.ACTION_RESULT_FAILURE) || action.equals(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT)) {
			style = "color_red bold";
		} else {
			style = "color_green bold";
		}
		return "<a href=\""+getUrlToShow()+"\" class=\""+style+"\" target=\"_blank\">"+servletName+"</a>";
	}
	
	public String getMessageInfo(){
		if (message.contains("Servlet Action Failure")){
			message = message.replace("Servlet Action Failure", "<span class=\"color_red bold\">Servlet Action Failure</span><br>");
		}
		return message;
	}
	
	public String getUrlToShow(){
		String urlToShow = url;
		//If not in masquerade mode, add masquerade mode
		if(!urlToShow.contains("user=")){
			if(!urlToShow.contains("?")){
				urlToShow += "?user=" + googleId;
			} else {
				urlToShow += "&user=" + googleId;
			}
		}
		return urlToShow;
	}
	
	public boolean toShow(){
		return toShow;
	}
	
	public long getTime(){
		return time;
	}
	
	public String getServletName(){
		return servletName;
	}
	
	public String getAction(){
		return action;
	}
	
	public String getRole(){
		return role;
	}
	
	public String getName(){
		return name;
	}
	
	public String getId(){
		return googleId;
	}
	
	public String getEmail(){
		return email;
	}


	public static String generateServletActionFailureLogMessage(HttpServletRequest req, Exception e){
		String[] actionTaken = req.getServletPath().split("/");
		String action = req.getServletPath();
		if(actionTaken.length > 0) {
			action = actionTaken[actionTaken.length-1]; //retrieve last segment in path
		}
		String url = HttpRequestHelper.getRequestedURL(req);
        
        String message = "<span class=\"color_red\">Servlet Action failure in " + action + "<br>";
        message += e.getClass() + ": " + TeammatesException.toStringWithStackTrace(e) + "<br>";
        message += HttpRequestHelper.printRequestParameters(req) + "</span>";
        
        ActivityLogEntry exceptionLog = new ActivityLogEntry(action, Const.ACTION_RESULT_FAILURE, null, message, url);
        
        return exceptionLog.generateLogMessage();
	}


	public static String generateSystemErrorReportLogMessage(HttpServletRequest req, MimeMessage errorEmail) {
		String[] actionTaken = req.getServletPath().split("/");
		String action = req.getServletPath();
		if(actionTaken.length > 0) {
			action = actionTaken[actionTaken.length-1]; //retrieve last segment in path
		}
		String url = HttpRequestHelper.getRequestedURL(req);
        
        String message = "";
        if(errorEmail != null){
        	try {
      			message += "<span class=\"color_red\">" + errorEmail.getSubject() + "</span><br>";
      			message += "<a href=\"#\" onclick=\"showHideErrorMessage('error" + errorEmail.hashCode() +"');\">Show/Hide Details >></a>";
      			message += "<br>";
      			message += "<span id=\"error" + errorEmail.hashCode() + "\" style=\"display: none;\">";
      			message += errorEmail.getContent().toString();
      			message += "</span>";
      		} catch (Exception e) {
      			message = "System Error. Unable to retrieve Email Report";
      		}
      	}
		
		ActivityLogEntry emailReportLog = new ActivityLogEntry(action, Const.ACTION_RESULT_SYSTEM_ERROR_REPORT, null, message, url);
		
		return emailReportLog.generateLogMessage();
	}
	
	private String getInstructorHomePageViewLink(String googleId){
		String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
		link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
		return link;
	}
	

}
