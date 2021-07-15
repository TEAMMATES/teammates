package teammates.common.datatransfer;

import java.time.Instant;

/**
 * Represents the parameters used for querying logs.
 */
public class QueryLogsParams {
    String severityLevel;
    String minSeverity;
    Instant startTime;
    Instant endTime;
    String traceId;
    String actionClass;
    UserInfoParams userInfoParams;
    String logEvent;
    GeneralLogEntry.SourceLocation sourceLocation;
    String exceptionClass;
    Integer pageSize;
    String pageToken;

    public QueryLogsParams(String severityLevel, String minSeverity, Instant startTime, Instant endTime,
            String traceId, String actionClass, String googleId, String regkey, String email, String logEvent,
            GeneralLogEntry.SourceLocation sourceLocation, String exceptionClass, Integer pageSize, String pageToken) {
        this.severityLevel = severityLevel;
        this.minSeverity = minSeverity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.traceId = traceId;
        this.actionClass = actionClass;
        this.userInfoParams = new UserInfoParams(googleId, regkey, email);
        this.logEvent = logEvent;
        this.sourceLocation = sourceLocation;
        this.exceptionClass = exceptionClass;
        this.pageSize = pageSize;
        this.pageToken = pageToken;
    }

    public String getSeverityLevel() {
        return severityLevel;
    }

    public String getMinSeverity() {
        return minSeverity;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getActionClass() {
        return actionClass;
    }

    public UserInfoParams getUserInfoParams() {
        return userInfoParams;
    }

    public String getLogEvent() {
        return logEvent;
    }

    public GeneralLogEntry.SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public String getPageToken() {
        return pageToken;
    }

    /**
     * Represents parameter of user information used for querying logs.
     */
    public static class UserInfoParams {
        String googleId;
        String regkey;
        String email;

        public UserInfoParams(String google, String regkey, String email) {
            this.googleId = google;
            this.regkey = regkey;
            this.email = email;
        }

        public String getGoogleId() {
            return googleId;
        }

        public String getRegkey() {
            return regkey;
        }

        public String getEmail() {
            return email;
        }
    }
}

