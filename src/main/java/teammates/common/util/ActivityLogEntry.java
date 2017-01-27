package teammates.common.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.TeammatesException;

import com.google.appengine.api.log.AppLogLine;

/** A log entry to describe an action carried out by the app */
public class ActivityLogEntry {
    
    // The following constants describe the positions of the attributes
    // in the log message. i.e
    // TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||TIME_TAKEN
    public static final int POSITION_OF_SERVLETNAME = 1;
    public static final int POSITION_OF_ACTION = 2;
    public static final int POSITION_OF_TOSHOW = 3;
    public static final int POSITION_OF_ROLE = 4;
    public static final int POSITION_OF_NAME = 5;
    public static final int POSITION_OF_GOOGLEID = 6;
    public static final int POSITION_OF_EMAIL = 7;
    public static final int POSITION_OF_MESSAGE = 8;
    public static final int POSITION_OF_URL = 9;
    public static final int POSITION_OF_ID = 10;
    public static final int POSITION_OF_TIMETAKEN = 11;
    
    private static final int TIME_TAKEN_WARNING_LOWER_RANGE = 10000;
    private static final int TIME_TAKEN_WARNING_UPPER_RANGE = 20000;
    private static final int TIME_TAKEN_DANGER_UPPER_RANGE = 60000;
    
    private static final Logger log = Logger.getLogger();

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
    
    // id can be in the form of <googleId>%<time> e.g. bamboo3250%20151103170618465
    // or <studentemail>%<courseId>%<time> (for unregistered students)
    //     e.g. bamboo@gmail.tmt%instructor.ema-demo%20151103170618465
    private String id;
    private boolean isFirstRow;
    
    @SuppressWarnings("unused") // used by js
    private String logInfoAsHtml;
    
    private String[] keyStringsToHighlight;
    
    /**
     * Constructor that creates a empty ActivityLog
     */
    public ActivityLogEntry(String servlet, String params, String link) {
        time = System.currentTimeMillis();
        servletName = servlet;
        action = "Unknown";
        role = "Unknown";
        name = "Unknown";
        googleId = "Unknown";
        email = "Unknown";
        toShow = true;
        message = "<span class=\"text-danger\">"
                    + "Error. ActivityLogEntry object is not created for this servlet action."
                + "</span>"
                + "<br>"
                + params;
        url = link;
        id = "Unknown";
    }

    /**
     * Constructor that creates an ActivityLog object from a app log on the server.
     * Used in AdminActivityLogServlet.
     */
    public ActivityLogEntry(AppLogLine appLog) {
        time = appLog.getTimeUsec() / 1000;
        
        try {
            String[] tokens = appLog.getLogMessage().split("\\|\\|\\|", -1);
            initUsingAppLogMessage(tokens);
        } catch (ArrayIndexOutOfBoundsException e) {
            initAsFailure(appLog, e);
        }
        
        keyStringsToHighlight = null;
        logInfoAsHtml = getLogInfoForTableRowAsHtml();
    }

    /**
     * Constructor that creates an ActivityLog object from scratch
     * Used in the various servlets in the application
     */
    public ActivityLogEntry(String servlet, String act, AccountAttributes acc, String params, String link,
                            UserType userType) {
        this(servlet, act, acc, params, link, null, null, userType);
    }

    /**
     * Constructs a ActivityLogEntry.
     * The googleId in the log will be based on the {@code acc} or {@code userType} passed in
     * For the log id, if the googleId is unknown, the {@code unregisteredUserCourse} and {@code unregisteredUserEmail}
     * will be used to construct the id.
     */
    public ActivityLogEntry(String servlet, String act, AccountAttributes acc, String params, String link,
                            String unregisteredUserCourse, String unregisteredUserEmail, UserType userType) {
        time = System.currentTimeMillis();
        servletName = servlet;
        action = act;
        toShow = true;
        message = params;
        url = link;
        
        if (acc == null) {
            role = "Unknown";
            name = "Unknown";
            email = "Unknown";
            googleId = userType == null ? "Unknown" : userType.id;
        
        } else {
            role = acc.isInstructor ? "Instructor" : "Student";
            name = acc.name;
            googleId = acc.googleId;
            email = acc.email;
        }
        id = generateLogId(googleId, unregisteredUserEmail, unregisteredUserCourse, time);
        
        role = changeRoleToAutoIfAutomatedActions(servletName, role);
    }
    
    public ActivityLogEntry(AccountAttributes userAccount, boolean isMasquerade, String logMessage,
                            String requestUrl, StudentAttributes unregisteredStudent, UserType userType) {
        time = System.currentTimeMillis();
        try {
            servletName = getActionName(requestUrl);
        } catch (Exception e) {
            servletName = "error in getActionName for requestUrl : " + requestUrl;
        }
        action = servletName; //TODO: remove this?
        toShow = true;
        message = logMessage;
        url = requestUrl;
       
        boolean isAccountWithGoogleId = userAccount != null && userAccount.googleId != null;
        boolean isUnregisteredStudent = unregisteredStudent != null;
        if (isAccountWithGoogleId) {
            if (userType.isInstructor && !userType.isStudent && !userType.isAdmin) {
                role = "Instructor";
            } else if (!userType.isInstructor && userType.isStudent && !userType.isAdmin) {
                role = "Student";
            } else if (userType.isInstructor && userType.isStudent && !userType.isAdmin) {
                role = servletName.toLowerCase().startsWith("instructor") ? "Instructor" : "Student";
                role = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE.contains(servletName) ? "Instructor" : role;
            } else if (userType.isAdmin) {
                role = "Admin";
                role = servletName.toLowerCase().startsWith("instructor") ? "Instructor" : role;
                role = servletName.toLowerCase().startsWith("student") ? "Student" : role;
                role = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE.contains(servletName) ? "Instructor" : role;
            } else {
                role = "Unregistered";
            }
            
            role = role + (isMasquerade ? "(M)" : "");
            name = userAccount.name;
            googleId = userAccount.googleId;
            email = userAccount.email;
        } else if (isUnregisteredStudent) {
            role = "Unregistered";
            if (unregisteredStudent.course != null && !unregisteredStudent.course.isEmpty()) {
                role = "Unregistered" + ":" + unregisteredStudent.course;
            }
            
            name = unregisteredStudent.name;
            googleId = "Unregistered";
            email = unregisteredStudent.email;
        } else {
            
            //this is a shallow fix for logging redirected student to join authenticated action
            if (Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED.toLowerCase().contains(servletName.toLowerCase())) {
                role = "Unregistered";
            } else {
                role = "Unknown";
            }
            name = "Unknown";
            googleId = "Unknown";
            email = "Unknown";
        }
        
        role = changeRoleToAutoIfAutomatedActions(servletName, role);
        id = generateLogId(googleId, unregisteredStudent, time);
    }
    
    private void initUsingAppLogMessage(String[] tokens) {
        // TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||
        // MESSAGE(IN HTML)|||URL|||ID|||TIME_TAKEN
        servletName = tokens[POSITION_OF_SERVLETNAME];
        action = tokens[POSITION_OF_ACTION];
        toShow = Boolean.parseBoolean(tokens[POSITION_OF_TOSHOW]);
        role = tokens[POSITION_OF_ROLE];
        name = tokens[POSITION_OF_NAME];
        googleId = tokens[POSITION_OF_GOOGLEID];
        email = tokens[POSITION_OF_EMAIL];
        message = tokens[POSITION_OF_MESSAGE];
        url = tokens[POSITION_OF_URL];
        
        boolean isLogWithTimeTakenAndId = tokens.length >= POSITION_OF_ID + 1;
        if (isLogWithTimeTakenAndId) {
            id = tokens[POSITION_OF_ID];
            try {
                timeTaken = tokens.length == 12 ? Long.parseLong(tokens[POSITION_OF_TIMETAKEN].trim())
                                                : null;
            } catch (NumberFormatException e) {
                timeTaken = null;
                log.severe("Log message format not as expected: " + Arrays.toString(tokens));
            }
        }
    }

    private void initAsFailure(AppLogLine appLog, Exception e) {
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
        id = "Unknown" + "%" + formatTimeForId(new Date(time));
        timeTaken = null;
    }

    private String changeRoleToAutoIfAutomatedActions(String servletName, String role) {
        return servletName.startsWith("/auto/") ? "Auto" : role;
    }
    
    private String formatTimeForId(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        return sdf.format(date.getTime());
    }

    public String getIconRoleForShow() {
        StringBuilder iconRole = new StringBuilder(100);
        
        if (role.contains("Instructor")) {
            iconRole.append("<span class = \"glyphicon glyphicon-user\" style=\"color:#39b3d7;\"></span>");
            if (role.contains("(M)")) {
                iconRole.append("-<span class = \"glyphicon glyphicon-eye-open\" style=\"color:#E61E1E;\"></span>- ");
            }
        } else if (role.contains("Student")) {
            iconRole.append("<span class = \"glyphicon glyphicon-user\" style=\"color:#FFBB13;\"></span>");
            if (role.contains("(M)")) {
                iconRole.append("-<span class = \"glyphicon glyphicon-eye-open\" style=\"color:#E61E1E;\"></span>- ");
            }
        } else if (role.contains("Unregistered")) {
            iconRole.append("<span class = \"glyphicon glyphicon-user\"></span>");
        } else if (role.contains("Auto")) {
            iconRole.append("<span class = \"glyphicon glyphicon-cog\"></span>");
        } else {
            iconRole.append(role);
        }

        if (role.contains("Admin")) {
            iconRole.append("<span class = \"glyphicon glyphicon-user\" style=\"color:#E61E1E;\"></span>");
        }

        return iconRole.toString();
    }
    
    /**
     * Assumption: the {@code requestUrl} is in the format "/something/actionName"
     *   possibly followed by "?something" e.g., "/page/studentHome?user=abc"
     * @return action name in the URL e.g., "studentHome" in the above example.
     */
    public static String getActionName(String requestUrl) {
        return requestUrl.split("/")[2].split("\\?")[0];
    }

    /**
     * Generates a log message that will be logged in the server
     */
    public String generateLogMessage() {
        //TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||ID
        return "TEAMMATESLOG|||" + servletName + "|||" + action + "|||" + (toShow ? "true" : "false") + "|||"
                + role + "|||" + name + "|||" + googleId + "|||" + email + "|||" + message + "|||" + url + "|||" + id;
    }

    public String getDateInfo() {
        Calendar appCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        appCal.setTimeInMillis(time);

        return sdf.format(appCal.getTime());
    }
    
    public String getPersonInfo() {
        if (url.contains("/student")) {
            if (googleId.contentEquals("Unregistered")) {
                return "[" + name
                        + " (Unregistered User) "
                        + " <a href=\"mailto:" + email + "\" target=\"_blank\">" + email + "</a>]";
            }
            return "[" + name
                    + " <a href=\"" + getStudentHomePageViewLink(googleId) + "\" target=\"_blank\">" + googleId + "</a>"
                    + " <a href=\"mailto:" + email + "\" target=\"_blank\">" + email + "</a>]";
        } else if (url.contains("/instructor")) {
            return "[" + name
                    + " <a href=\"" + getInstructorHomePageViewLink(googleId) + "\" target=\"_blank\">" + googleId + "</a>"
                    + " <a href=\"mailto:" + email + "\" target=\"_blank\">" + email + "</a>]";
        } else {
            return googleId;
        }
    }
    
    public String getActionInfo() {
        String style = "";
        
        if (message.toLowerCase().contains(Const.ACTION_RESULT_FAILURE.toLowerCase())
                || message.toLowerCase().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT.toLowerCase())) {
            style = "text-danger";
        } else {
            style = "text-success bold";
        }
        return "<a href=\"" + getUrlToShow() + "\" class=\"" + style + "\" target=\"_blank\">" + servletName + "</a>";
    }
    
    public String getMessageInfo() {
        
        if (message.toLowerCase().contains(Const.ACTION_RESULT_FAILURE.toLowerCase())) {
            message = message.replace(Const.ACTION_RESULT_FAILURE, "<span class=\"text-danger\"><strong>"
                      + Const.ACTION_RESULT_FAILURE + "</strong><br>");
            message = message + "</span><br>";
        } else if (message.toLowerCase().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT.toLowerCase())) {
            message = message.replace(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT, "<span class=\"text-danger\"><strong>"
                      + Const.ACTION_RESULT_SYSTEM_ERROR_REPORT + "</strong><br>");
            message = message + "</span><br>";
        }
                
        return message;
    }

    public String getColorCode(Long timeTaken) {
        
        if (timeTaken == null) {
            return "";
        }
        
        String colorCode = "";
        if (timeTaken >= TIME_TAKEN_WARNING_LOWER_RANGE && timeTaken <= TIME_TAKEN_WARNING_UPPER_RANGE) {
            colorCode = "text-warning";
        } else if (timeTaken > TIME_TAKEN_WARNING_UPPER_RANGE && timeTaken <= TIME_TAKEN_DANGER_UPPER_RANGE) {
            colorCode = "text-danger";
        }
        
        return colorCode;
    }

    public String getTableCellColorCode(Long timeTaken) {
        
        if (timeTaken == null) {
            return "";
        }
        
        String colorCode = "";
        if (timeTaken >= TIME_TAKEN_WARNING_LOWER_RANGE && timeTaken <= TIME_TAKEN_WARNING_UPPER_RANGE) {
            colorCode = "warning";
        } else if (timeTaken > TIME_TAKEN_WARNING_UPPER_RANGE && timeTaken <= TIME_TAKEN_DANGER_UPPER_RANGE) {
            colorCode = "danger";
        }
        return colorCode;
    }
    
    public String getLogEntryActionsButtonClass() {
        
        String className = "";
        if (message.toLowerCase().contains(Const.ACTION_RESULT_FAILURE.toLowerCase())) {
            className = "btn-warning";
        } else if (message.toLowerCase().contains(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT.toLowerCase())) {
            className = "btn-danger";
        } else {
            className = "btn-info";
        }
        return className;
    }

    public String getUrlToShow() {
        if (url.contains("user=")) {
            return url;
        }
        // If not in masquerade mode, add masquerade mode
        if (url.contains("?")) {
            return url + "&user=" + googleId;
        }
        return url + "?user=" + googleId;
    }
    
    public String getId() {
        return id;
    }
    
    public void setKeyStringsToHighlight(String[] strings) {
        this.keyStringsToHighlight = strings;
    }
    
    public boolean toShow() {
        return toShow;
    }
    
    public void setToShow(boolean toShow) {
        this.toShow = toShow;
    }
    
    public long getTime() {
        return time;
    }
    
    public String getServletName() {
        return servletName;
    }
    
    public String getAction() {
        return action;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getName() {
        return name;
    }
    
    public String getGoogleId() {
        return googleId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Long getTimeTaken() {
        
        return timeTaken;
    }
    
    /**
     * Generates the ID for the log. If the googleId is unknown or unregistered,
     * the email and course of the {@code student} will be used to construct the id.
     * @param googleId the google ID
     * @param student StudentAttributes object
     * @return log ID
     */
    public String generateLogId(String googleId, StudentAttributes student, long time) {
        return student == null ? generateLogId(googleId, null, null, time)
                               : generateLogId(googleId, student.email, student.course, time);
    }
    
    /**
     * Generates the ID for the log. If the googleId is unknown or unregistered,
     * the {@code email} and {@code course} will be used to construct the id.
     * @param googleId the google ID
     * @param email the email
     * @param course the course
     * @return log ID
     */
    public String generateLogId(String googleId, String email, String course, long time) {
        boolean isUnregisteredStudent = (googleId.contentEquals("Unknown") || googleId.contentEquals("Unregistered"))
                                        && email != null && course != null;
        
        return isUnregisteredStudent ? email + "%" + course + "%" + formatTimeForId(new Date(time))
                                     : googleId + "%" + formatTimeForId(new Date(time));
    }
    
    public static String generateServletActionFailureLogMessage(HttpServletRequest req, Exception e, UserType userType) {
        String[] actionTaken = req.getServletPath().split("/");
        String action = req.getServletPath();
        if (actionTaken.length > 0) {
            action = actionTaken[actionTaken.length - 1]; //retrieve last segment in path
        }
        String url = HttpRequestHelper.getRequestedUrl(req);
        
        String message = "<span class=\"text-danger\">Servlet Action failure in " + action + "<br>"
                       + e.getClass() + ": " + TeammatesException.toStringWithStackTrace(e) + "<br>"
                       + HttpRequestHelper.printRequestParameters(req) + "</span>";
        
        String courseId = HttpRequestHelper.getValueFromRequestParameterMap(req, Const.ParamsNames.COURSE_ID);
        String studentEmail = HttpRequestHelper.getValueFromRequestParameterMap(req, Const.ParamsNames.STUDENT_EMAIL);
        ActivityLogEntry exceptionLog = new ActivityLogEntry(action, Const.ACTION_RESULT_FAILURE, null, message,
                                                             url, courseId, studentEmail, userType);
        
        return exceptionLog.generateLogMessage();
    }

    public static String generateSystemErrorReportLogMessage(HttpServletRequest req, EmailWrapper errorEmail,
                                                             UserType userType) {
        String[] actionTaken = req.getServletPath().split("/");
        String action = req.getServletPath();
        if (actionTaken.length > 0) {
            action = actionTaken[actionTaken.length - 1]; //retrieve last segment in path
        }
        String url = HttpRequestHelper.getRequestedUrl(req);
        
        String message;
        
        try {
            message = "<span class=\"text-danger\">" + errorEmail.getSubject() + "</span>"
                    + "<br>"
                    + "<a href=\"#\" onclick=\"showHideErrorMessage('error" + errorEmail.hashCode() + "');\">"
                        + "Show/Hide Details >>"
                    + "</a>"
                    + "<br>"
                    + "<span id=\"error" + errorEmail.hashCode() + "\" style=\"display: none;\">"
                        + errorEmail.getContent()
                    + "</span>";
        } catch (Exception e) {
            message = "System Error: Unable to retrieve Email Report: "
                    + TeammatesException.toStringWithStackTrace(e);
        }
        
        String courseId = HttpRequestHelper.getValueFromRequestParameterMap(req, Const.ParamsNames.COURSE_ID);
        String studentEmail = HttpRequestHelper.getValueFromRequestParameterMap(req, Const.ParamsNames.STUDENT_EMAIL);
        
        ActivityLogEntry emailReportLog = new ActivityLogEntry(action, Const.ACTION_RESULT_SYSTEM_ERROR_REPORT, null,
                                                               message, url, courseId, studentEmail, userType);
        
        return emailReportLog.generateLogMessage();
    }
    
    private String getInstructorHomePageViewLink(String googleId) {
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }
    
    private String getStudentHomePageViewLink(String googleId) {
        String link = Const.ActionURIs.STUDENT_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }

    public String getLogInfoForTableRowAsHtml() {
        return "<tr" + (isFirstRow ? " id=\"first-row\"" : "") + ">"
                 + "<td class=\"" + getTableCellColorCode(timeTaken) + "\" style=\"vertical-align: middle;\">"
                     + "<a onclick=\"submitLocalTimeAjaxRequest('" + time + "','" + googleId + "','" + role + "',this);\">"
                         + getDateInfo()
                     + "</a>"
                     + "<p class=\"localTime\"></p>"
                     + "<p class=\"" + getColorCode(getTimeTaken()) + "\">"
                         + "<strong>" + TimeHelper.convertToStandardDuration(getTimeTaken()) + "</strong>"
                     + "</p>"
                 + "</td>"
                 + "<td class=\"" + getTableCellColorCode(timeTaken) + "\">"
                     + "<form method=\"get\" action=\"" + Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE + "\">"
                         + "<h4 class=\"list-group-item-heading\">"
                             + getIconRoleForShow() + "&nbsp;" + getActionInfo() + "&nbsp;"
                             + "<small> id:" + id + " " + getPersonInfo() + "</small>" + "&nbsp;"
                             + "<button type=\"submit\" class=\"btn " + getLogEntryActionsButtonClass() + " btn-xs\">"
                                 + "<span class=\"glyphicon glyphicon-zoom-in\"></span>"
                             + "</button>"
                             + "<input type=\"hidden\" name=\"filterQuery\""
                                     + " value=\"person:" + getAvailableIdenficationString() + "\">"
                             + "<input class=\"ifShowAll_for_person\" type=\"hidden\" name=\"all\""
                                     + " value=\"false\">"
                             + "<input class=\"ifShowTestData_for_person\" type=\"hidden\" name=\"testdata\""
                                     + " value=\"false\">"
                         + "</h4>"
                         + "<div>" + getMessageInfo() + "</div>"
                     + "</form>"
                 + "</td>"
             + "</tr>";
    }
    
    private String getAvailableIdenficationString() {
        if (!getGoogleId().contentEquals("Unregistered") && !getGoogleId().contentEquals("Unknown")) {
            return getGoogleId();
        }
        if (getEmail() != null && !getEmail().contentEquals("Unknown")) {
            return getEmail();
        }
        if (getName() != null && !getName().contentEquals("Unknown")) {
            return getName();
        }
        return "";
    }
    
    public void highlightKeyStringInMessageInfoHtml() {
        
        if (keyStringsToHighlight == null) {
            return;
        }
        
        for (String stringToHighlight : keyStringsToHighlight) {
            if (message.toLowerCase().contains(stringToHighlight.toLowerCase())) {
                
                int startIndex = message.toLowerCase().indexOf(stringToHighlight.toLowerCase());
                int endIndex = startIndex + stringToHighlight.length();
                String realStringToHighlight = message.substring(startIndex, endIndex);
                message = message.replace(realStringToHighlight, "<mark>" + realStringToHighlight + "</mark>");
            }
        }
        
        logInfoAsHtml = getLogInfoForTableRowAsHtml();
        
    }
    
    public void setFirstRow() {
        isFirstRow = true;
    }
    
    public boolean isTestingData() {
        return email.endsWith(".tmt");
    }

}
