package teammates.common.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;

/**
 * A log entry to describe an action carried out by the app.
 */
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
    private long timeTaken;

    // id can be in the form of <googleId>%<time> e.g. bamboo3250%20151103170618465
    // or <studentemail>%<courseId>%<time> (for unregistered students)
    //     e.g. bamboo@gmail.tmt%instructor.ema-demo%20151103170618465
    private String id;

    /**
     * Constructor that creates a empty ActivityLog.
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
    }

    /**
     * Constructor that creates an ActivityLog object from scratch.
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
                                                : 0;
            } catch (NumberFormatException e) {
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
    }

    private String changeRoleToAutoIfAutomatedActions(String servletName, String role) {
        return servletName.startsWith("/auto/") ? "Auto" : role;
    }

    private String formatTimeForId(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
        return sdf.format(date.getTime());
    }

    /**
     * Assumption: the {@code requestUrl} is in the format "/something/actionName"
     *   possibly followed by "?something" e.g., "/page/studentHome?user=abc".
     * @return action name in the URL e.g., "studentHome" in the above example.
     */
    public static String getActionName(String requestUrl) {
        return requestUrl.split("/")[2].split("\\?")[0];
    }

    /**
     * Generates a log message that will be logged in the server.
     */
    public String generateLogMessage() {
        // TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||ID
        return StringHelper.join(Const.ActivityLog.FIELD_SEPARATOR, Const.ActivityLog.TEAMMATESLOG,
                servletName, action, Boolean.toString(toShow), role, name, googleId, email, message, url, id);
    }

    public String getId() {
        return id;
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

    public String getRoleWithoutMasquerade() {
        return role.replace(Const.ActivityLog.ROLE_MASQUERADE_POSTFIX, "").trim();
    }

    public boolean isMasqueradeUserRole() {
        return role.contains(Const.ActivityLog.ROLE_MASQUERADE_POSTFIX);
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

    public long getTimeTaken() {
        return timeTaken;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
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

    public boolean isTestingData() {
        return email.endsWith(Const.ActivityLog.TEST_DATA_POSTFIX);
    }

}
