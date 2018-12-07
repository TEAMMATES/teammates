package teammates.ui.pagedata;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.template.AdminEmailTableRow;

public class AdminEmailLogPageData extends PageData {

    private String filterQuery;
    private String queryMessage;
    private List<AdminEmailTableRow> logs;
    private List<String> versions;

    private boolean shouldShowAll;
    private String statusForAjax;
    private QueryParameters q;

    public AdminEmailLogPageData(AccountAttributes account, String sessionToken, String filterQuery,
            boolean shouldShowAll) {
        super(account, sessionToken);
        this.filterQuery = filterQuery;
        this.shouldShowAll = shouldShowAll;
    }

    // Getter methods

    public String getFilterQuery() {
        return filterQuery;
    }

    public String getQueryMessage() {
        return queryMessage;
    }

    public List<AdminEmailTableRow> getLogs() {
        return logs;
    }

    public List<String> getVersions() {
        return versions;
    }

    public boolean isShouldShowAll() {
        return shouldShowAll;
    }

    public String getStatusForAjax() {
        return statusForAjax;
    }

    /**
     * Returns now if toDate is not present in the query.
     */
    public long getToDate() {
        if (this.q == null || !this.q.isToDateInQuery) {
            return Instant.now().toEpochMilli();
        }
        return this.q.toDateValue;
    }

    public boolean isFromDateInQuery() {
        return this.q != null && this.q.isFromDateInQuery;
    }

    /**
     * Returns 0 if fromDate is not present in the query.
     */
    public long getFromDate() {
        if (this.q == null) {
            return 0;
        }
        return this.q.fromDateValue;
    }

    public String getQueryKeywordsForReceiver() {
        if (q == null || !q.isReceiverInQuery) {
            return "";
        }

        return String.join(",", q.receiverValues);
    }

    public String getQueryKeywordsForSubject() {
        if (q == null || !q.isSubjectInQuery) {
            return "";
        }

        return String.join(",", q.subjectValues);
    }

    public String getQueryKeywordsForContent() {
        if (q == null || !q.isInfoInQuery) {
            return "";
        }

        return String.join(",", q.infoValues);
    }

    // Setter methods

    public void setFilterQuery(String filterQuery) {
        this.filterQuery = filterQuery;
    }

    public void setQueryMessage(String queryMessage) {
        this.queryMessage = queryMessage;
    }

    public void setLogs(List<EmailLogEntry> logs) {
        initLogsAsTemplateRows(logs);
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    public void setShouldShowAll(boolean shouldShowAll) {
        this.shouldShowAll = shouldShowAll;
    }

    public void setStatusForAjax(String statusForAjax) {
        this.statusForAjax = statusForAjax;
    }

    public void setToDate(long date) {
        if (this.q == null) {
            return;
        }
        this.q.isToDateInQuery = true;
        this.q.toDateValue = date;
    }

    /**
     * Creates a QueryParameters object used for filtering.
     */
    public void generateQueryParameters(String query) {

        try {
            q = parseQuery(query.toLowerCase());
        } catch (ParseException | InvalidParametersException e) {
            this.queryMessage = "Error with the query: " + e.getMessage();
        }
    }

    /**
     * Converts the query string into a QueryParameters object.
     */
    private QueryParameters parseQuery(String query) throws ParseException, InvalidParametersException {
        QueryParameters q = new QueryParameters();
        setVersions(new ArrayList<String>());

        if (query == null || query.isEmpty()) {
            return q;
        }

        String[] tokens = query.replaceAll(" and ", "|")
                               .replaceAll(", ", ",")
                               .replaceAll(": ", ":")
                               .split("\\|", -1);

        for (String token : tokens) {
            String[] pair = token.split(":", -1);

            if (pair.length != 2) {
                throw new InvalidParametersException("Invalid format");
            }

            String[] values = pair[1].split(",", -1);
            values = StringHelper.trim(values);
            String label = pair[0].trim();

            if ("version".equals(label)) {
                //version is specified in com.google.appengine.api.log.LogQuery,
                //it does not belong to the internal class "QueryParameters"
                //so need to store here for future use
                for (String value : values) {
                    getVersions().add(value.replace(".", "-"));
                }

            } else {
                q.add(label, values);
            }
        }

        return q;
    }

    /**
     * Performs the actual filtering, based on QueryParameters.
     *
     * @return false if the logEntry fails the filtering process
     */
    public boolean shouldShowLog(EmailLogEntry logEntry) {
        // Skip test data if the request is not showing all logs
        boolean isShowTestData = !logEntry.isTestData() || shouldShowAll;

        if (q == null) {
            if (this.queryMessage == null) {
                this.queryMessage = "Error parsing the query. QueryParameters not created.";
            }
            return isShowTestData;
        }

        // filter based on what is in the query
        if (q.isReceiverInQuery) {

            for (String keyString : q.receiverValues) {
                if (!logEntry.getReceiver().toLowerCase().contains(keyString.toLowerCase())) {
                    return false;
                }
            }
        }
        if (q.isSubjectInQuery) {

            for (String keyString : q.subjectValues) {
                if (!logEntry.getSubject().toLowerCase().contains(keyString.toLowerCase())) {
                    return false;
                }
            }
        }
        if (q.isInfoInQuery) {

            for (String keyString : q.infoValues) {
                if (!logEntry.getContent().toLowerCase().contains(keyString.toLowerCase())) {
                    return false;
                }
            }
        }

        return isShowTestData;
    }

    /**
     * QueryParameters inner class. Used only within this servlet, to hold the query data once it is parsed
     * The boolean variables determine if the specific label was within the query
     * The XXValue variables hold the data linked to the label in the query
     */
    private static class QueryParameters {
        public boolean isToDateInQuery;
        public long toDateValue;

        public boolean isFromDateInQuery;
        public long fromDateValue;

        public boolean isReceiverInQuery;
        public String[] receiverValues;

        public boolean isSubjectInQuery;
        public String[] subjectValues;

        public boolean isInfoInQuery;
        public String[] infoValues;

        QueryParameters() {
            isToDateInQuery = false;
            isFromDateInQuery = false;
            isReceiverInQuery = false;
            isSubjectInQuery = false;
            isInfoInQuery = false;
        }

        /**
         * Add a label and values in.
         */
        public void add(String label, String[] values) throws ParseException, InvalidParametersException {
            if ("after".equals(label)) {
                isFromDateInQuery = true;
                LocalDateTime localDateTime = TimeHelper.parseLocalDate(values[0], "dd/MM/yyyy").atStartOfDay();
                fromDateValue = TimeHelper.convertLocalDateTimeToInstant(
                        localDateTime, Const.SystemParams.ADMIN_TIME_ZONE).toEpochMilli();
                return;
            }
            if ("before".equals(label)) {
                isToDateInQuery = true;
                LocalDateTime localDateTime = TimeHelper.parseLocalDate(values[0], "dd/MM/yyyy").atTime(LocalTime.MAX);
                toDateValue = TimeHelper.convertLocalDateTimeToInstant(
                        localDateTime, Const.SystemParams.ADMIN_TIME_ZONE).toEpochMilli();
                return;
            }
            if ("receiver".equals(label)) {
                isReceiverInQuery = true;
                receiverValues = values;
                return;
            }
            if ("subject".equals(label)) {
                isSubjectInQuery = true;
                subjectValues = values;
                return;
            }
            if ("info".equals(label)) {
                isInfoInQuery = true;
                infoValues = values;
                return;
            }
            throw new InvalidParametersException("Invalid label");
        }
    }

    private void initLogsAsTemplateRows(List<EmailLogEntry> entries) {
        logs = new ArrayList<>();
        for (EmailLogEntry entry : entries) {
            AdminEmailTableRow row = new AdminEmailTableRow(entry);
            logs.add(row);
        }
    }
}
