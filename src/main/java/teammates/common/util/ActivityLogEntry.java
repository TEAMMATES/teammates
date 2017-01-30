package teammates.common.util;

/** A log entry to describe an action carried out by the app */
public class ActivityLogEntry {
    // The following constants describe the positions of the attributes
    // in the log message. i.e
    // TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||TIME_TAKEN
    public static final int POSITION_OF_ACTION_SERVLETNAME = 1;
    public static final int POSITION_OF_ACTION_NAME = 2;
    public static final int POSITION_OF_LOG_TOSHOW = 3;
    public static final int POSITION_OF_USER_ROLE = 4;
    public static final int POSITION_OF_USER_NAME = 5;
    public static final int POSITION_OF_USER_GOOGLEID = 6;
    public static final int POSITION_OF_USER_EMAIL = 7;
    public static final int POSITION_OF_LOG_MESSAGE = 8;
    public static final int POSITION_OF_ACTION_URL = 9;
    public static final int POSITION_OF_LOG_ID = 10;
    public static final int POSITION_OF_LOG_TIMETAKEN = 11;
    
    // Required fields
    
    // id can be in the form of <googleId>%<time> e.g. bamboo3250%20151103170618465
    // or <studentemail>%<courseId>%<time> (for unregistered students)
    //     e.g. bamboo@gmail.tmt%instructor.ema-demo%20151103170618465
    private String logId;
    private long logTime;
    
    private String actionServletName;
    private String actionName;
    
    // Optional fields
    
    private String userRole;
    private boolean isMasqueradeUserRole;
    
    private String userName;
    private String userEmail;
    private String userGoogleId;
    
    private String logMessage;
    
    private String actionUrl;
    
    private long actionTimeTaken;
    
    // legacy of messing up UI and logic
    private boolean logToShow = true;
    
    public ActivityLogEntry(Builder builder) {
        logTime = builder.logTime;
        actionServletName = builder.actionServletName;
        actionTimeTaken = builder.actionTimeTaken;
        actionName = builder.actionName;
        userRole = builder.userRole;
        userName = builder.userName;
        userGoogleId = builder.userGoogleId;
        userEmail = builder.userEmail;
        logMessage = builder.logMessage;
        actionUrl = builder.actionUrl;
        logId = builder.logId;
        actionTimeTaken = builder.actionTimeTaken;
        isMasqueradeUserRole = builder.isMasqueradeUserRole;
    }

    /**
     * Generates a log message that will be logged in the server
     */
    public String generateLogMessage() {
        //TEAMMATESLOG|||SERVLET_NAME|||ACTION|||TO_SHOW|||ROLE|||NAME|||GOOGLE_ID|||EMAIL|||MESSAGE(IN HTML)|||URL|||ID
        return Const.ActivityLog.TEAMMATESLOG + Const.ActivityLog.FIELD_SEPERATOR
                + actionServletName + Const.ActivityLog.FIELD_SEPERATOR
                + actionName + Const.ActivityLog.FIELD_SEPERATOR
                + (logToShow ? "true" : "false") + Const.ActivityLog.FIELD_SEPERATOR
                + userRole + (isMasqueradeUserRole ? "(M)" : "") + Const.ActivityLog.FIELD_SEPERATOR
                + userName + Const.ActivityLog.FIELD_SEPERATOR + userGoogleId
                + Const.ActivityLog.FIELD_SEPERATOR
                + userEmail + Const.ActivityLog.FIELD_SEPERATOR
                + logMessage + Const.ActivityLog.FIELD_SEPERATOR
                + actionUrl + Const.ActivityLog.FIELD_SEPERATOR
                + logId;
    }
    
    public String getLogId() {
        return logId;
    }
    
    public boolean getLogToShow() {
        return logToShow;
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
    
    public String getActionServletName() {
        return actionServletName;
    }
    
    public String getActionName() {
        return actionName;
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
    
    public boolean isTestingData() {
        return userEmail.endsWith(Const.ActivityLog.TESTING_DATA_EMAIL_POSTFIX);
    }
    
    public static class Builder {
        // Required parameters
        private String actionServletName;
        private String actionUrl;
        private long logTime;
        
        // Optional parameters - initialized to default values
        private String actionName = Const.ActivityLog.UNKNOWN;
        private long actionTimeTaken;
        private String userRole = Const.ActivityLog.UNKNOWN;
        private String userName = Const.ActivityLog.UNKNOWN;
        private String userGoogleId = Const.ActivityLog.UNKNOWN;
        private String userEmail = Const.ActivityLog.UNKNOWN;
        private String logMessage = Const.ActivityLog.UNKNOWN;
        private String logId = Const.ActivityLog.UNKNOWN;
        private boolean isMasqueradeUserRole;
        
        public Builder(String servletName, String url, long time) {
            actionServletName = servletName;
            actionUrl = url;
            logTime = time;
        }
        
        public Builder withActionName(String val) {
            actionName = val;
            return this;
        }
        
        public Builder withUserRole(String val) {
            userRole = val;
            return this;
        }
        
        public Builder withUserName(String val) {
            userName = val;
            return this;
        }
        
        public Builder withUserGoogleId(String val) {
            userGoogleId = val;
            return this;
        }
        
        public Builder withUserEmail(String val) {
            userEmail = val;
            return this;
        }
        
        public Builder withMasqueradeUserRole(Boolean val) {
            isMasqueradeUserRole = val;
            return this;
        }
        
        public Builder withLogId(String val) {
            logId = val;
            return this;
        }
        
        public Builder withLogMessage(String val) {
            logMessage = val;
            return this;
        }
        
        public Builder withActionTimeTaken(long val) {
            actionTimeTaken = val;
            return this;
        }
        
        public long getLogTime() {
            return logTime;
        }
        
        public String getActionServletName() {
            return actionServletName;
        }
        
        public ActivityLogEntry build() {
            return new ActivityLogEntry(this);
        }
    }

}
