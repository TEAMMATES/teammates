package teammates.common.datatransfer;

import java.time.Instant;

import teammates.common.datatransfer.GeneralLogEntry.SourceLocation;

/**
 * Represents the parameters used for querying logs.
 */
public class QueryLogsParams {
    private String severityLevel;
    private String minSeverity;
    private Instant startTime;
    private Instant endTime;
    private String traceId;
    private String actionClass;
    private UserInfoParams userInfoParams;
    private String logEvent;
    private SourceLocation sourceLocation;
    private String exceptionClass;
    private String order;
    private Integer pageSize;

    private QueryLogsParams(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Builder builder(Instant startTime, Instant endTime) {
        return new Builder(startTime, endTime);
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

    public String getOrder() {
        return order;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Represents parameter of user information used for querying logs.
     */
    public static class UserInfoParams {
        private final String googleId;
        private final String regkey;
        private final String email;

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

    public static class Builder {
        private QueryLogsParams queryLogsParams;

        private Builder(Instant startTime, Instant endTime) {
            this.queryLogsParams = new QueryLogsParams(startTime, endTime);
        }

        public Builder withSeverityLevel(String severityLevel) {
            queryLogsParams.severityLevel = severityLevel;
            return this;
        }

        public Builder withMinSeverity(String minSeverity) {
            queryLogsParams.minSeverity = minSeverity;
            return this;
        }

        public Builder withTraceId(String traceId) {
            queryLogsParams.traceId = traceId;
            return this;
        }

        public Builder withActionClass(String actionClass) {
            queryLogsParams.actionClass = actionClass;
            return this;
        }

        public Builder withUserInfo(UserInfoParams userInfoParams) {
            queryLogsParams.userInfoParams = userInfoParams;
            return this;
        }

        public Builder withLogEvent(String logEvent) {
            queryLogsParams.logEvent = logEvent;
            return this;
        }

        public Builder withSourceLocation(SourceLocation sourceLocation) {
            queryLogsParams.sourceLocation = sourceLocation;
            return this;
        }

        public Builder withExceptionClass(String exceptionClass) {
            queryLogsParams.exceptionClass = exceptionClass;
            return this;
        }

        public Builder withOrder(String order) {
            queryLogsParams.order = order;
            return this;
        }

        public Builder withPageSize(Integer pageSize) {
            queryLogsParams.pageSize = pageSize;
            return this;
        }

        public QueryLogsParams build() {
            return queryLogsParams;
        }
    }
}

