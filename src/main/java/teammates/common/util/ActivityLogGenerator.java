package teammates.common.util;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.TeammatesException;
import teammates.common.util.ActivityLogEntry.Builder;

/**
 * Handles operations related to logs
 * 
 * @see {@link ActivityLogEntry}
 */
public class ActivityLogGenerator {
    
    private static UserService userService = UserServiceFactory.getUserService();
    
    private static final Logger log = Logger.getLogger();
    
    public String generateNormalPageActionLogMessage(HttpServletRequest req, UserType userType,
                                                 AccountAttributes userAccount,
                                                 StudentAttributes unregisteredStudent, String logMessage) {
        ActivityLogEntry.Builder builder = generateBasicActivityLogEntry(req);
        
        boolean isUnregisteredStudent = unregisteredStudent != null;
        boolean isAccountWithGoogleId = userAccount != null && userAccount.googleId != null;
        if (isUnregisteredStudent) {
            updateInfoForUnregisteredStudent(builder, unregisteredStudent);
        } else if (isAccountWithGoogleId) {
            updateInfoForNormalUser(builder, userType, userAccount);
        }
        
        builder.withLogMessage(logMessage);
        return builder.build().generateLogMessage();
    }
    
    private void updateInfoForUnregisteredStudent(ActivityLogEntry.Builder builder,
                StudentAttributes unregisteredStudent) {
        String role = Const.ActivityLog.ROLE_UNREGISTERED;
        if (unregisteredStudent.course != null && !unregisteredStudent.course.isEmpty()) {
            // TODO: diccuss: remove this if necessary as it can be seen in log id
            role = Const.ActivityLog.ROLE_UNREGISTERED + ":" + unregisteredStudent.course;
        }
        builder.withUserRole(role)
               .withUserName(unregisteredStudent.name)
               .withUserEmail(unregisteredStudent.email);
    }
    
    private void updateInfoForNormalUser(ActivityLogEntry.Builder builder,
            UserType currUser, AccountAttributes userAccount) {
        if (currUser.isAdmin) {
            builder.withUserRole(Const.ActivityLog.ROLE_ADMIN);
            degradeRoleToStudentIfNecessary(builder);
            degradeRoleToInstructorIfNecessary(builder);
            
            checkAndUpdateForMasqueradeMode(builder, currUser, userAccount);
        } else if (currUser.isInstructor && currUser.isStudent) {
            builder.withUserRole(Const.ActivityLog.ROLE_INSTRUCTOR);
            degradeRoleToStudentIfNecessary(builder);
        } else if (currUser.isStudent) {
            builder.withUserRole(Const.ActivityLog.ROLE_STUDENT);
        } else if (currUser.isInstructor) {
            builder.withUserRole(Const.ActivityLog.ROLE_INSTRUCTOR);
        } else {
            builder.withUserRole(Const.ActivityLog.ROLE_UNREGISTERED);
        }
        
        builder.withUserGoogleId(userAccount.googleId)
               .withUserEmail(userAccount.email)
               .withUserName(userAccount.name);
    }
    
    private void degradeRoleToStudentIfNecessary(ActivityLogEntry.Builder builder) {
        if (isStudentPage(builder.getActionServletName())) {
            builder.withUserRole(Const.ActivityLog.ROLE_STUDENT);
        }
    }
    
    private void degradeRoleToInstructorIfNecessary(ActivityLogEntry.Builder builder) {
        if (isInstructorPage(builder.getActionServletName())) {
            builder.withUserRole(Const.ActivityLog.ROLE_INSTRUCTOR);
        }
    }
    
    private void checkAndUpdateForMasqueradeMode(ActivityLogEntry.Builder builder,
                                UserType loggedInUser, AccountAttributes account) {
        if (loggedInUser != null && loggedInUser.id != null && account != null) {
            boolean isMasqueradeMode = !loggedInUser.id.equals(account.googleId);
            builder.withMasqueradeUserRole(isMasqueradeMode);
        }
        
        // TODO should we rebuild it?
        builder.withLogId(generateLogIdWithGoogleId(account.googleId, builder.getLogTime()));
    }

    private boolean isInstructorPage(String servletName) {
        return servletName.toLowerCase().startsWith(Const.ActivityLog.PREFIX_INSTRUCTOR_PAGE)
                || Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE.contains(servletName); // TODO remove this one
    }

    private boolean isStudentPage(String servletName) {
        return servletName.toLowerCase().startsWith(Const.ActivityLog.PREFIX_STUDENT_PAGE);
    }

    public String generateServletActionFailureLogMessage(HttpServletRequest req, Exception e) {
        ActivityLogEntry.Builder builder = generateBasicActivityLogEntry(req);
        
        // TODO can use template to generate this
        String message = "<span class=\"text-danger\">Servlet Action failure in "
                       + builder.getActionServletName() + "<br>"
                       + e.getClass() + ": " + TeammatesException.toStringWithStackTrace(e) + "<br>"
                       + HttpRequestHelper.printRequestParameters(req) + "</span>";
        builder.withLogMessage(message);
        
        builder.withActionName(Const.ACTION_RESULT_FAILURE);
        // TODO role is unclear
        
        return builder.build().generateLogMessage();
    }
    
    public String generateSystemErrorReportLogMessage(HttpServletRequest req, EmailWrapper errorEmail) {
        ActivityLogEntry.Builder builder = generateBasicActivityLogEntry(req);
        
        // TODO can use template to generate this
        if (errorEmail != null) {
            String message = "<span class=\"text-danger\">" + errorEmail.getSubject() + "</span>"
                    + "<br>"
                    + "<a href=\"#\" onclick=\"showHideErrorMessage('error" + errorEmail.hashCode() + "');\">"
                        + "Show/Hide Details >>"
                    + "</a>"
                    + "<br>"
                    + "<span id=\"error" + errorEmail.hashCode() + "\" style=\"display: none;\">"
                        + errorEmail.getContent()
                    + "</span>";
            builder.withLogMessage(message);
        }
        
        builder.withActionName(Const.ACTION_RESULT_SYSTEM_ERROR_REPORT);
        // TODO role is unclear
       
        return builder.build().generateLogMessage();
    }
    
    private ActivityLogEntry.Builder generateBasicActivityLogEntry(HttpServletRequest req) {
        String url = HttpRequestHelper.getRequestedUrl(req);
        String servletName = getActionNameFromUrl(url);
        long currTime = System.currentTimeMillis();
        ActivityLogEntry.Builder builder = new ActivityLogEntry.Builder(servletName, url, currTime);
        builder.withActionName(servletName); // by default
        
        String logId;
        if (isAutomatedAction(url)) {
            logId = generateLogIdInAutomatedAction(currTime);
            builder.withUserRole(Const.ActivityLog.ROLE_AUTO);
        } else if (isUserGoogleLogIn()) {
            logId = generateLogIdWithGoogleId(getGoogleId(), currTime);
            builder.withUserGoogleId(getGoogleId());
        } else {
            logId = generateLogIdWithoutGoogleId(req, currTime);
            builder.withUserGoogleId(Const.ActivityLog.AUTH_UNLOGIN);
        }
        builder.withLogId(logId);
        
        return builder;
    }
    
    // TODO test case needed
    public String generateBasicActivityLogMessage(HttpServletRequest req, String message) {
        ActivityLogEntry.Builder builder = generateBasicActivityLogEntry(req);
        
        builder.withLogMessage(message);
        
        return builder.build().generateLogMessage();
    }
    
    private boolean isAutomatedAction(String url) {
        try {
            return Url.getRelativePath(url).startsWith(Const.ActivityLog.PREFIX_AUTO_PAGE);
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    private boolean isUserGoogleLogIn() {
        return userService.getCurrentUser() != null;
    }

    private String getGoogleId() {
        if (!isUserGoogleLogIn()) {
            return Const.ActivityLog.AUTH_UNLOGIN;
        }
        return userService.getCurrentUser().getNickname();
    }

    private String getActionNameFromUrl(String requestUrl) {
        try {
            return requestUrl.split("/")[2].split("\\?")[0];
        } catch (Exception e) {
            return String.format(Const.ActivityLog.MESSAGE_ERROR_ACTIONNAME, requestUrl);
        }
    }
    
    private String generateLogIdInAutomatedAction(long time) {
        return Const.ActivityLog.ROLE_AUTO + Const.ActivityLog.FIELD_CONNECTOR + formatTimeForId(new Date(time));
    }
    
    private String generateLogIdWithoutGoogleId(HttpServletRequest req, long time) {
        String courseId = HttpRequestHelper.getValueFromRequestParameterMap(req, Const.ParamsNames.COURSE_ID);
        String studentEmail = HttpRequestHelper.getValueFromRequestParameterMap(req, Const.ParamsNames.STUDENT_EMAIL);
        if (courseId != null && studentEmail != null) {
            return studentEmail + Const.ActivityLog.FIELD_CONNECTOR
                    + courseId + Const.ActivityLog.FIELD_CONNECTOR
                    + formatTimeForId(new Date(time));
        }
        return Const.ActivityLog.AUTH_UNLOGIN + Const.ActivityLog.FIELD_CONNECTOR + formatTimeForId(new Date(time));
    }
    
    private String generateLogIdWithGoogleId(String googleId, long time) {
        return googleId + Const.ActivityLog.FIELD_CONNECTOR + formatTimeForId(new Date(time));
    }
    
    private String formatTimeForId(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(Const.ActivityLog.TIME_FORMAT_LOGID);
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        return sdf.format(date.getTime());
    }
    
    public ActivityLogEntry generateActivityLogFromAppLogLine(AppLogLine appLog) {
        try {
            String[] tokens = appLog.getLogMessage().split(Pattern.quote(Const.ActivityLog.FIELD_SEPERATOR), -1);
            return initActivityLogUsingAppLogMessage(appLog, tokens);
        } catch (ArrayIndexOutOfBoundsException e) {
            return initActivityLogAsFailure(appLog, e);
        }
    }

    private ActivityLogEntry initActivityLogAsFailure(AppLogLine appLog, ArrayIndexOutOfBoundsException e) {
        Builder builder = new Builder(Const.ActivityLog.UNKNOWN, Const.ActivityLog.UNKNOWN, appLog.getTimeUsec());
        // TODO : template here
        String logMessage = "<span class=\"text-danger\">Error. Problem parsing log message from the server.</span><br>"
                + "System Error: " + e.getMessage() + "<br>" + appLog.getLogMessage();
        builder.withLogMessage(logMessage);
        return builder.build();
    }

    private ActivityLogEntry initActivityLogUsingAppLogMessage(AppLogLine appLog, String[] tokens) {
        // TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||
        // MESSAGE(IN HTML)|||URL|||ID|||TIME_TAKEN
        Builder builder = new Builder(tokens[ActivityLogEntry.POSITION_OF_ACTION_SERVLETNAME],
                            tokens[ActivityLogEntry.POSITION_OF_ACTION_URL], appLog.getTimeUsec());
        
        builder.withActionName(tokens[ActivityLogEntry.POSITION_OF_ACTION_NAME]);
        builder.withLogId(tokens[ActivityLogEntry.POSITION_OF_LOG_ID]);
        builder.withLogMessage(tokens[ActivityLogEntry.POSITION_OF_LOG_MESSAGE]);
        builder.withMasqueradeUserRole(tokens[ActivityLogEntry.POSITION_OF_USER_ROLE]
                .contains(Const.ActivityLog.MASQUERADE_ROLE_POSTFIX));
        builder.withUserEmail(tokens[ActivityLogEntry.POSITION_OF_USER_EMAIL]);
        builder.withUserGoogleId(tokens[ActivityLogEntry.POSITION_OF_USER_GOOGLEID]);
        builder.withUserName(tokens[ActivityLogEntry.POSITION_OF_USER_NAME]);
        builder.withUserRole(
                tokens[ActivityLogEntry.POSITION_OF_USER_ROLE]
                        .replace(Const.ActivityLog.MASQUERADE_ROLE_POSTFIX, ""));
        
        boolean isLogWithTimeTakenAndId = tokens.length >= ActivityLogEntry.POSITION_OF_LOG_ID + 1;
        if (isLogWithTimeTakenAndId) {
            try {
                long actionTimeTaken = tokens.length == ActivityLogEntry.POSITION_OF_LOG_TIMETAKEN + 1
                                                ? Long.parseLong(tokens[ActivityLogEntry.POSITION_OF_LOG_TIMETAKEN].trim())
                                                : 0;
                builder.withActionTimeTaken(actionTimeTaken);
            } catch (NumberFormatException e) {
                log.severe(String.format(Const.ActivityLog.MESSAGE_ERROR_LOGMESSAGE_FORMAT, Arrays.toString(tokens)));
            }
        }

        return builder.build();
    }
    
}
