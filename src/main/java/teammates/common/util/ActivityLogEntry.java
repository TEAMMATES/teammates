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
    private Long timeTaken;
    
    private String logInfoAsHtml;
    
    private static final int TIME_TAKEN_WARNING_LOWER_RANGE = 10000;
    private static final int TIME_TAKEN_WARNING_UPPER_RANGE = 20000;
    private static final int TIME_TAKEN_DANGER_UPPER_RANGE = 60000;
    
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
        message = "<span class=\"text-danger\">Error. ActivityLogEntry object is not created for this servlet action.</span><br>"
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
        
        //TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||TIME_TAKEN
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
            timeTaken = tokens.length == 11? Long.parseLong(tokens[10].trim()) : null;
        } catch (ArrayIndexOutOfBoundsException e){
            
            servletName = "Unknown";
            action = "Unknown";
            role = "Unknown";
            name = "Unknown";
            googleId = "Unknown";
            email = "Unknown";
            toShow = true;
            message = "<span class=\"text-danger\">Error. Problem parsing log message from the server.</span><br>"
                    + "System Error: " + e.getMessage() + "<br>" + appLog.getLogMessage();
            url = "Unknown";
            timeTaken = null;
        }
        
        logInfoAsHtml = getLogInfoForTableRowAsHtml();
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
    
    public String getIconRoleForShow(){
        String iconRole="";
        
        if (role.contains("Instructor")){   
           
            if(role.contains("(M)")){
                iconRole = "<span class = \"glyphicon glyphicon-user\" style=\"color:#39b3d7;\"></span>";
                iconRole = iconRole + "-<span class = \"glyphicon glyphicon-eye-open\" ></span>- ";
            }else{
                iconRole = "<span class = \"glyphicon glyphicon-user\" style=\"color:#39b3d7;\"></span>";
            }
        }else if(role.contains("Student")){
            
            if(role.contains("(M)")){
                iconRole = "<span class = \"glyphicon glyphicon-user\" style=\"color:#FFBB13;\"></span>";
                iconRole = iconRole + "-<span class = \"glyphicon glyphicon-eye-open\" ></span>- ";
            }else{
                iconRole = "<span class = \"glyphicon glyphicon-user\" style=\"color:#FFBB13;\"></span>";
            }
        }else{
            iconRole = role;
        }

        if (servletName.toLowerCase().startsWith("admin")) {
            iconRole = "<span class = \"glyphicon glyphicon-user\"></span>";
        }
            
        
        return iconRole;
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
        if(url.contains("/student")){
            return "[" + name +
                    " <a href=\""+getStudentHomePageViewLink(googleId)+"\" target=\"_blank\">" + googleId + "</a>" +
                    " <a href=\"mailto:"+email+"\" target=\"_blank\">" + email +"</a>]" ;
        } else if(url.contains("/instructor")){
            return "[" + name +
                    " <a href=\""+getInstructorHomePageViewLink(googleId)+"\" target=\"_blank\">" + googleId + "</a>" +
                    " <a href=\"mailto:"+email+"\" target=\"_blank\">" + email +"</a>]" ;
        } else { 
            return googleId; 
        }
    }
    
    public String getActionInfo(){
        String style = "";
        
        if (message.toLowerCase().contains(Const.ACTION_RESULT_FAILURE.toLowerCase())
           || message.toLowerCase().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT.toLowerCase())) {
            
                style = "text-danger";      
        } else {
            style = "text-success bold";
        }
        return "<a href=\""+getUrlToShow()+"\" class=\""+style+"\" target=\"_blank\">"+servletName+"</a>";
    }
    
    public String getMessageInfo(){
        
        Sanitizer.sanitizeForHtml(message);
        
        if (message.toLowerCase().contains(Const.ACTION_RESULT_FAILURE.toLowerCase())){
            message = message.replace(Const.ACTION_RESULT_FAILURE, "<span class=\"text-danger\"><strong>" + Const.ACTION_RESULT_FAILURE + "</strong><br>");
            message = message + "</span><br>";
        } else if (message.toLowerCase().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT.toLowerCase())){
            message = message.replace(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT, "<span class=\"text-danger\"><strong>" + Const.ACTION_RESULT_SYSTEM_ERROR_REPORT + "</strong><br>");
            message = message + "</span><br>";
        }
                
        return message;
    }
    

    public String getColorCode(Long timeTaken){
        
        if(timeTaken == null){
            return "";
        }
        
        String colorCode = "";
        if (timeTaken >= TIME_TAKEN_WARNING_LOWER_RANGE && timeTaken <= TIME_TAKEN_WARNING_UPPER_RANGE){
            colorCode = "text-warning";
        }else if(timeTaken > TIME_TAKEN_WARNING_UPPER_RANGE && timeTaken <= TIME_TAKEN_DANGER_UPPER_RANGE){
            colorCode = "text-danger";
        }
        
        return colorCode;            
    }
    
    
    public String getTableCellColorCode(Long timeTaken){
        
        if(timeTaken == null){
            return "";
        }
        
        String colorCode = "";
        if (timeTaken >= TIME_TAKEN_WARNING_LOWER_RANGE && timeTaken <= TIME_TAKEN_WARNING_UPPER_RANGE){
            colorCode = "warning";
        }else if(timeTaken > TIME_TAKEN_WARNING_UPPER_RANGE && timeTaken <= TIME_TAKEN_DANGER_UPPER_RANGE){
            colorCode = "danger";
        }    
        return colorCode;            
    }
    
    public String getLogEntryActionsButtonClass(){
        
        String className = "";
        if (message.toLowerCase().contains(Const.ACTION_RESULT_FAILURE.toLowerCase())){
            className = "btn-warning";
        } else if (message.toLowerCase().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT.toLowerCase())){
            className = "btn-danger";
        } else {
            className = "btn-info";
        }
        return className;
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
    
    public Long getTimeTaken(){
        
        return timeTaken;       
    }
    
    public static String generateServletActionFailureLogMessage(HttpServletRequest req, Exception e){
        String[] actionTaken = req.getServletPath().split("/");
        String action = req.getServletPath();
        if(actionTaken.length > 0) {
            action = actionTaken[actionTaken.length-1]; //retrieve last segment in path
        }
        String url = HttpRequestHelper.getRequestedURL(req);
        
        String message = "<span class=\"text-danger\">Servlet Action failure in " + action + "<br>";
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
                  message += "<span class=\"text-danger\">" + errorEmail.getSubject() + "</span><br>";
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
    
    private String getStudentHomePageViewLink(String googleId){
        String link = Const.ActionURIs.STUDENT_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }
    
    
    public String getLogInfoForTableRowAsHtml(){
        
        
        String result = "";
        result += "<tr> <td class=\"" + getTableCellColorCode(timeTaken) + "\" style=\"vertical-align: middle;\">"+ getDateInfo()
               + "<br> <p class=\"" + getColorCode(getTimeTaken()) + "\">"
               + "<strong>" + TimeHelper.convertToStandardDuration(getTimeTaken()) + "</strong>"
               + "</p> </td> <td class=\"" + getTableCellColorCode(timeTaken) + "\">"
               + "<form method=\"post\" action=\"" + Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE + "\"> "
               + "<h4 class=\"list-group-item-heading\">" 
               + getIconRoleForShow() + "&nbsp;" + getActionInfo() + "&nbsp;"
               + "<small>" + getPersonInfo() + "</span>" + "&nbsp;"
               + "<button type=\"submit\" class=\"btn " + getLogEntryActionsButtonClass() +  " btn-xs\">"
               + "<span class=\"glyphicon glyphicon-zoom-in\"></span>"
               + "</button> <input type=\"hidden\" name=\"filterQuery\" value=\"person:" + getId() + "\">"
               + "<input class=\"ifShowAll_button_for_person\" type=\"hidden\" name=\"all\" value=\"false\">"
               + "</small> </h4> <div>" + getMessageInfo()
               + "</div> </form> </td> </tr>";      
        return result;
        
    }
    
}
