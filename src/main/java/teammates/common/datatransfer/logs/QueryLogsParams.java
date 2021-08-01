package teammates.common.datatransfer.logs;

import java.time.Instant;

/**
 * Represents the parameters used for querying logs.
 */
public class QueryLogsParams {
    private String severity;
    private String minSeverity;
    private Instant startTime;
    private Instant endTime;
    private String traceId;
    private String actionClass;
    private RequestLogUser userInfoParams;
    private String logEvent;
    private SourceLocation sourceLocation;
    private String exceptionClass;
    private String latency;
    private String status;
    private String extraFilters;
    private String order;
    private Integer pageSize;

    private QueryLogsParams(Instant startTime, Instant endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Returns a builder for {@link QueryLogsParams}.
     */
    public static Builder builder(Instant startTime, Instant endTime) {
        return new Builder(startTime, endTime);
    }

    public String getSeverity() {
        return severity;
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

    public RequestLogUser getUserInfoParams() {
        return userInfoParams;
    }

    public String getLogEvent() {
        return logEvent;
    }

    public SourceLocation getSourceLocation() {
        return sourceLocation;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public String getLatency() {
        return latency;
    }

    public String getStatus() {
        return status;
    }

    public String getExtraFilters() {
        return extraFilters;
    }

    public String getOrder() {
        return order;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    /**
     * Builder for {@link QueryLogsParams}.
     */
    public static class Builder {
        private QueryLogsParams queryLogsParams;

        private Builder(Instant startTime, Instant endTime) {
            this.queryLogsParams = new QueryLogsParams(startTime, endTime);
        }

        public Builder withSeverityLevel(String severityLevel) {
            queryLogsParams.severity = severityLevel;
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

        public Builder withUserInfo(RequestLogUser userInfoParams) {
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

        public Builder withLatency(String latency) {
            queryLogsParams.latency = latency;
            return this;
        }

        public Builder withStatus(String status) {
            queryLogsParams.status = status;
            return this;
        }

        public Builder withExtraFilters(String extraFilters) {
            queryLogsParams.extraFilters = extraFilters;
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

