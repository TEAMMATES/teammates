package teammates.common.util;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.google.appengine.api.log.AppLogLine;

/**
 * A log entry to describe an action carried out by the app.
 */
public final class ActivityLogEntry {
    // The following constants describe the positions of the attributes
    // in the log message. i.e
    // TEAMMATESLOG|||ACTION_NAME|||ACTION_RESPONSE|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL
    // |||MESSAGE(IN HTML)|||URL|||TIME_TAKEN
    private static final int POSITION_OF_ACTION_NAME = 1;
    private static final int POSITION_OF_ACTION_RESPONSE = 2;
    private static final int POSITION_OF_USER_ROLE = 4;
    private static final int POSITION_OF_USER_NAME = 5;
    private static final int POSITION_OF_USER_GOOGLEID = 6;
    private static final int POSITION_OF_USER_EMAIL = 7;
    private static final int POSITION_OF_LOG_MESSAGE = 8;
    private static final int POSITION_OF_ACTION_URL = 9;
    private static final int POSITION_OF_LOG_ID = 10;
    private static final int POSITION_OF_LOG_TIMETAKEN = 11;

    private static final Logger log = Logger.getLogger();

    // Required fields

    // id can be in the form of <googleId>%<time> e.g. bamboo3250%20151103170618465
    // or <studentemail>%<courseId>%<time> (for unregistered students)
    //     e.g. bamboo@gmail.tmt%instructor.ema-demo%20151103170618465
    private String logId;

    private long logTime;
    private String actionUrl;
    private String actionName;
    private String actionResponse;

    // Optional fields

    private String userRole;
    private boolean isMasqueradeUserRole;

    private String userName;
    private String userEmail;
    private String userGoogleId;

    private String logMessage;

    private long actionTimeTaken;

    // this field will always be true in log message for history reason
    private boolean shouldShowLog = true;

    private ActivityLogEntry() {
        // private constructor to prevent instantiation
    }

    /**
     * Generates a log message that will be logged in the server.
     */
    public String generateLogMessage() {
        // TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||ID
        String userRoleSuffix = isMasqueradeUserRole ? Const.ActivityLog.ROLE_MASQUERADE_POSTFIX : "";
        return String.join(Const.ActivityLog.FIELD_SEPARATOR, Const.ActivityLog.TEAMMATESLOG,
                actionName, actionResponse, Boolean.toString(shouldShowLog), userRole + userRoleSuffix,
                userName, userGoogleId, userEmail, logMessage, actionUrl, logId);
    }

    public String getLogId() {
        return logId;
    }

    public boolean getShouldShowLog() {
        return shouldShowLog;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public long getLogTime() {
        return logTime;
    }

    public long getActionTimeTaken() {
        return actionTimeTaken;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionResponse() {
        return actionResponse;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean isMasqueradeUserRole() {
        return isMasqueradeUserRole;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserGoogleId() {
        return userGoogleId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Checks whether the activity log is related to testing data or not.
     */
    public boolean isTestingData() {
        return userEmail.endsWith(Const.ActivityLog.TESTING_DATA_EMAIL_POSTFIX);
    }

    /**
     * Builds an {@link ActivityLogEntry} from {@link AppLogLine} provided by GAE.
     *
     * <p>If the log message in {@link AppLogLine} is not in desired format, an instance will still
     * be constructed, but with log message: {@link Const.ActivityLog.MESSAGE_ERROR_LOG_MESSAGE_FORMAT}
     */
    public static ActivityLogEntry buildFromAppLog(AppLogLine appLog) {
        String[] tokens = appLog.getLogMessage().split(Pattern.quote(Const.ActivityLog.FIELD_SEPARATOR), -1);
        if (tokens.length < POSITION_OF_LOG_ID + 1) {
            // the number of fields is not enough, which should be an error
            return initActivityLogAsFailure(appLog);
        }
        return initActivityLogUsingAppLogMessage(appLog, tokens);
    }

    private static ActivityLogEntry initActivityLogAsFailure(AppLogLine appLog) {
        Builder builder =
                new Builder(Const.ActivityLog.UNKNOWN, Const.ActivityLog.UNKNOWN, appLog.getTimeUsec() / 1000);
        String logMessage = "<span class=\"text-danger\">"
                            + Const.ActivityLog.MESSAGE_ERROR_LOG_MESSAGE_FORMAT + "</span><br>";
        builder.withLogMessage(logMessage);
        return builder.build();
    }

    private static ActivityLogEntry initActivityLogUsingAppLogMessage(AppLogLine appLog, String[] tokens) {
        // TEAMMATESLOG|||ACTION_NAME|||ACTION_RESPONSE|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL
        // |||MESSAGE(IN HTML)|||URL|||TIME_TAKEN
        String actionName = tokens[POSITION_OF_ACTION_NAME];
        String actionUrl = tokens[POSITION_OF_ACTION_URL];
        long time = appLog.getTimeUsec() / 1000;
        Builder builder = new Builder(actionName, actionUrl, time);

        builder.withActionResponse(tokens[POSITION_OF_ACTION_RESPONSE])
                .withLogId(tokens[POSITION_OF_LOG_ID])
                .withLogMessage(tokens[POSITION_OF_LOG_MESSAGE])
                .withMasqueradeUserRole(
                        tokens[POSITION_OF_USER_ROLE].contains(Const.ActivityLog.ROLE_MASQUERADE_POSTFIX))
                .withUserEmail(tokens[POSITION_OF_USER_EMAIL])
                .withUserGoogleId(tokens[POSITION_OF_USER_GOOGLEID])
                .withUserName(tokens[POSITION_OF_USER_NAME]);
        String userRole = tokens[POSITION_OF_USER_ROLE];
        builder.withUserRole(userRole.replace(Const.ActivityLog.ROLE_MASQUERADE_POSTFIX, ""));

        try {
            long actionTimeTaken = tokens.length == ActivityLogEntry.POSITION_OF_LOG_TIMETAKEN + 1
                                 ? Long.parseLong(tokens[ActivityLogEntry.POSITION_OF_LOG_TIMETAKEN].trim())
                                 : 0;
            builder.withActionTimeTaken(actionTimeTaken);
        } catch (NumberFormatException e) {
            log.severe(String.format(Const.ActivityLog.MESSAGE_ERROR_LOG_MESSAGE_FORMAT, Arrays.toString(tokens)));
        }

        return builder.build();
    }

    /**
     * A builder class for {@link ActivityLogEntry}.
     *
     * <p>All optional fields are initialized to {@link Const.ActivityLog.UNKNOWN}.
     * Field actionResponse will be initialized to have the same value as actionName.
     * All null values (if possible) that are passed into the builder will be ignored.
     *
     * @see ActivityLogEntry
     */
    public static class Builder {
        // Required parameters
        private String actionName;
        private String actionUrl;
        private long logTime;
        private String actionResponse;

        // Optional parameters - initialized to default values
        private long actionTimeTaken;
        private String userRole = Const.ActivityLog.UNKNOWN;
        private String userName = Const.ActivityLog.UNKNOWN;
        private String userGoogleId = Const.ActivityLog.UNKNOWN;
        private String userEmail = Const.ActivityLog.UNKNOWN;
        private String logMessage = Const.ActivityLog.UNKNOWN;
        private String logId = Const.ActivityLog.UNKNOWN;
        private boolean isMasqueradeUserRole;

        public Builder(String name, String url, long time) {
            actionName = name == null ? Const.ActivityLog.UNKNOWN : name;
            actionUrl = url == null ? Const.ActivityLog.UNKNOWN : url;
            logTime = time;

            actionResponse = actionName;
        }

        public Builder withActionResponse(String val) {
            if (val != null) {
                actionResponse = val;
            }
            return this;
        }

        public Builder withUserRole(String val) {
            if (val != null) {
                userRole = val;
            }
            return this;
        }

        public Builder withUserName(String val) {
            if (val != null) {
                userName = val;
            }
            return this;
        }

        public Builder withUserGoogleId(String val) {
            if (val != null) {
                userGoogleId = val;
            }
            return this;
        }

        public Builder withUserEmail(String val) {
            if (val != null) {
                userEmail = val;
            }
            return this;
        }

        public Builder withMasqueradeUserRole(boolean val) {
            isMasqueradeUserRole = val;
            return this;
        }

        public Builder withLogId(String val) {
            if (val != null) {
                logId = val;
            }
            return this;
        }

        public Builder withLogMessage(String val) {
            if (val != null) {
                logMessage = val;
            }
            return this;
        }

        public Builder withActionTimeTaken(long val) {
            actionTimeTaken = val;
            return this;
        }

        public long getLogTime() {
            return logTime;
        }

        public String getActionName() {
            return actionName;
        }

        // use builder to build ActivityLogEntry
        @SuppressWarnings({"PMD.AccessorMethodGeneration", "PMD.AccessorClassGeneration"})
        public ActivityLogEntry build() {
            ActivityLogEntry entry = new ActivityLogEntry();

            entry.logTime = logTime;
            entry.actionName = actionName;
            entry.actionTimeTaken = actionTimeTaken;
            entry.actionResponse = actionResponse;
            entry.userRole = userRole;
            entry.userName = userName;
            entry.userGoogleId = userGoogleId;
            entry.userEmail = userEmail;
            entry.logMessage = logMessage;
            entry.actionUrl = actionUrl;
            entry.logId = logId;
            entry.actionTimeTaken = actionTimeTaken;
            entry.isMasqueradeUserRole = isMasqueradeUserRole;
            return entry;
        }
    }
}
