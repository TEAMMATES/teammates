package teammates.ui.pagedata;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.template.AdminActivityLogTableRow;

public class AdminActivityLogPageData extends PageData {

    /**
     * Stores the requests to be excluded from being shown in admin activity logs page.
     */
    private static String[] excludedLogRequestURIs = {
            Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE,
            Const.ActionURIs.AUTOMATED_LOG_COMPILATION
    };

    private String filterQuery;
    private String queryMessage;
    private List<AdminActivityLogTableRow> logs;
    private List<String> versions;
    private Long toDateValue;
    private Long fromDateValue;
    private String logLocalTime;
    private boolean isFromDateSpecifiedInQuery;
    /**
     * This determines whether the logs with requests contained in "excludedLogRequestURIs" below
     * should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
     * logs despite any action or change in the page unless the page is reloaded with "?all=false"
     * or simply reloaded with this parameter omitted.
     */
    private boolean shouldShowAllLogs;

    /**
     * This determines whether the logs related to testing data should be shown. Use "testdata=true" in URL
     * to show all testing logs. This will keep showing all logs from testing data despite any action or change in the page
     * unless the page is reloaded with "?testdata=false"  or simply reloaded with this parameter omitted.
     */
    private boolean shouldShowTestData;

    private String statusForAjax;
    private QueryParameters q;

    public AdminActivityLogPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
        setDefaultLogSearchPeriod();
    }

    public List<String> getExcludedLogRequestUris() {
        List<String> excludedList = new ArrayList<>();
        for (String excludedLogRequestUri : excludedLogRequestURIs) {
            excludedList.add(excludedLogRequestUri.substring(excludedLogRequestUri.lastIndexOf('/') + 1));
        }
        return excludedList;
    }

    private void setDefaultLogSearchPeriod() {
        fromDateValue = TimeHelper.getInstantDaysOffsetFromNow(-1).toEpochMilli();
        toDateValue = Instant.now().toEpochMilli();
    }

    public void init(List<ActivityLogEntry> logList) {
        initLogsAsTemplateRows(logList);
    }

    private void initLogsAsTemplateRows(List<ActivityLogEntry> entries) {
        logs = new ArrayList<>();
        for (ActivityLogEntry entry : entries) {
            AdminActivityLogTableRow row = new AdminActivityLogTableRow(entry);
            logs.add(row);
        }
    }

    public void setShowAllLogs(boolean val) {
        shouldShowAllLogs = val;
    }

    public void setShowTestData(boolean val) {
        shouldShowTestData = val;
    }

    public boolean getShouldShowAllLogs() {
        return shouldShowAllLogs;
    }

    public boolean getShouldShowTestData() {
        return shouldShowTestData;
    }

    public String getFilterQuery() {
        return filterQuery;
    }

    public String getQueryMessage() {
        return queryMessage;
    }

    public List<AdminActivityLogTableRow> getLogs() {
        return logs;
    }

    public List<String> getVersions() {
        return versions;
    }

    public long getFromDate() {
        return fromDateValue;
    }

    public void setFromDate(long startTime) {
        fromDateValue = startTime;
    }

    public long getToDate() {
        return toDateValue;
    }

    public void setToDate(long endTime) {
        toDateValue = endTime;
    }

    /**
     * Checks that an array contains a specific value.
     * (value is converted to lower case before comparing)
     */
    private boolean arrayContains(String[] array, String value) {
        for (String element : array) {
            if (element.equals(value.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a QueryParameters object used for filtering.
     */
    public void generateQueryParameters(String query) {
        filterQuery = query.trim();

        try {
            q = parseQuery(filterQuery);
        } catch (ParseException | InvalidParametersException e) {
            this.queryMessage = "Error with the query: " + e.getMessage();
        }
    }

    /**
     * Returns true if the current log entry should be included.
     */
    private boolean shouldIncludeLogEntry(ActivityLogEntry logEntry) {
        if (shouldShowAllLogs) {
            return true;
        }

        for (String uri : excludedLogRequestURIs) {
            if (logEntry.getActionUrl() != null && logEntry.getActionUrl().contains(uri)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Performs the actual filtering, based on QueryParameters.
     *
     * <p>Returns false if the logEntry fails the filtering process.
     *
     * <p>If the queryMessage is null, the function will return true
     * and set the queryMessage with error message.
     */
    public boolean filterLog(ActivityLogEntry logEntry) {
        if (q == null) {
            if (this.queryMessage == null) {
                this.queryMessage = "Error parsing the query. QueryParameters not created.";
            }
            return shouldIncludeLogEntry(logEntry);
        }

        //Filter based on what is in the query
        if (q.isRequestInQuery && !arrayContains(q.requestValues, logEntry.getActionName())) {
            return false;
        }
        if (q.isResponseInQuery && !arrayContains(q.responseValues, logEntry.getActionResponse())) {
            return false;
        }
        if (q.isPersonInQuery
                && !logEntry.getUserName().toLowerCase().contains(q.personValue.toLowerCase())
                && !logEntry.getUserGoogleId().toLowerCase().contains(q.personValue.toLowerCase())
                && !logEntry.getUserEmail().toLowerCase().contains(q.personValue.toLowerCase())) {
            return false;
        }
        if (q.isRoleInQuery && !arrayContains(q.roleValues, logEntry.getUserRole())) {
            return false;
        }
        if (q.isCutoffInQuery
                && (logEntry.getActionTimeTaken() == 0 || logEntry.getActionTimeTaken() < q.cutoffValue)) {
            return false;
        }
        if (q.isInfoInQuery) {
            for (String keyString : q.infoValues) {
                if (!logEntry.getLogMessage().toLowerCase().contains(keyString.toLowerCase())) {
                    return false;
                }
            }
        }
        if (q.isIdInQuery && !arrayContains(q.idValues, logEntry.getLogId())) {
            return false;
        }

        return shouldIncludeLogEntry(logEntry);
    }

    /**
     * Converts the query string into a QueryParameters object.
     */
    private QueryParameters parseQuery(String query) throws ParseException, InvalidParametersException {
        QueryParameters q = new QueryParameters();
        versions = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            return q;
        }

        String[] tokens = query.replaceAll(" \\b(?i)(and)\\b ", "|")
                               .replaceAll(", ", ",")
                               .replaceAll(": ", ":")
                               .split("\\|", -1);

        for (String token : tokens) {
            String[] pair = token.split(":", -1);

            if (pair.length != 2) {
                throw new InvalidParametersException("Invalid format");
            }

            String label = pair[0].trim().toLowerCase();
            String[] values = pair[1].split(",", -1);
            values = StringHelper.trim(values);

            // GoogleID is case-sensitive and hence not converted to lower case
            if (!("person".equals(label))) {
                values = StringHelper.toLowerCase(values);
            }

            if ("version".equals(label)) {
                //version is specified in com.google.appengine.api.log.LogQuery,
                //it does not belong to the internal class "QueryParameters"
                //so need to store here for future use
                for (String value : values) {
                    versions.add(value.replace(".", "-"));
                }

            } else if ("from".equals(label)) {
                fromDateValue = LocalDate.parse(values[0], DateTimeFormatter.ofPattern("dd/MM/yy"))
                        .atStartOfDay(Const.SystemParams.ADMIN_TIME_ZONE).toInstant().toEpochMilli();
                isFromDateSpecifiedInQuery = true;

            } else if ("to".equals(label)) {
                toDateValue = LocalDate.parse(values[0], DateTimeFormatter.ofPattern("dd/MM/yy"))
                        .atTime(LocalTime.MAX).atZone(Const.SystemParams.ADMIN_TIME_ZONE).toInstant().toEpochMilli();
            } else {
                q.add(label, values);
            }
        }
        return q;
    }

    /**
     * Returns the possible servlet requests list as html.
     */
    public String getActionListAsHtml() {
        List<String> allActionNames = getAllActionNames();
        int totalColumns = 4;
        int rowsPerCol = calculateRowsPerCol(allActionNames.size(), totalColumns);
        return convertActionListToHtml(allActionNames, rowsPerCol, totalColumns);
    }

    private String convertActionListToHtml(List<String> allActionNames, int rowsPerCol, int totalColumns) {

        StringBuilder outputHtml = new StringBuilder(100);
        outputHtml.append("<tr>");
        int count = 0;
        for (int i = 0; i < totalColumns; i++) {

            outputHtml.append("<td><ul class=\"list-group\">");
            for (int j = 0; j < rowsPerCol; j++) {

                if (count >= allActionNames.size()) {
                    break;
                }

                outputHtml.append("<li class=\"list-group-item "
                                  + getStyleForListGroupItem(allActionNames.get(count))
                                  + "\">" + allActionNames.get(count) + "</li>");

                count++;
            }
            outputHtml.append("</ul></td>");
        }

        return outputHtml.toString();
    }

    private String getStyleForListGroupItem(String actionName) {

        String style = "";

        if (actionName.startsWith("instructor")) {
            style = "list-group-item";
        } else if (actionName.startsWith("student")) {
            style = "list-group-item-success";
        } else if (actionName.startsWith("admin")) {
            style = "list-group-item-warning";
        } else {
            style = "list-group-item-danger";
        }

        return style;
    }

    private int calculateRowsPerCol(int totalNumOfActions, int totalColumns) {

        int rowsPerCol = totalNumOfActions / totalColumns;
        int remainder = totalNumOfActions % totalColumns;

        if (remainder > 0) {
            rowsPerCol++;
        }

        return rowsPerCol;
    }

    private List<String> getAllActionNames() {

        List<String> actionNameList = new ArrayList<>();

        for (Field field : Const.ActionURIs.class.getFields()) {

            String actionString = getActionNameStringFromField(field);
            actionNameList.add(actionString);
        }

        return actionNameList;
    }

    private String getActionNameStringFromField(Field field) {

        String rawActionString = "";

        try {
            rawActionString = field.get(Const.ActionURIs.class).toString();
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Assumption.fail("Fail to get action URI");
        }

        String[] splitedString = rawActionString.split("/");
        return splitedString[splitedString.length - 1];
    }

    public String getQueryKeywordsForInfo() {
        if (q == null || !q.isInfoInQuery) {
            return "";
        }

        return String.join(",", q.infoValues);
    }

    /**
     * QueryParameters inner class. Used only within this servlet, to hold the query data once it is parsed
     * The boolean variables determine if the specific label was within the query
     * The XXValue variables hold the data linked to the label in the query
     */
    private static class QueryParameters {

        public boolean isRequestInQuery;
        public String[] requestValues;

        public boolean isResponseInQuery;
        public String[] responseValues;

        public boolean isPersonInQuery;
        public String personValue;

        public boolean isRoleInQuery;
        public String[] roleValues;

        public boolean isCutoffInQuery;
        public long cutoffValue;

        public boolean isInfoInQuery;
        public String[] infoValues;

        public boolean isIdInQuery;
        public String[] idValues;

        QueryParameters() {
            isRequestInQuery = false;
            isResponseInQuery = false;
            isPersonInQuery = false;
            isRoleInQuery = false;
            isCutoffInQuery = false;
            isInfoInQuery = false;
            isIdInQuery = false;
        }

        /**
         * Add a label and values in.
         */
        public void add(String label, String[] values) throws InvalidParametersException {
            switch (label) {
            case "request":
                isRequestInQuery = true;
                requestValues = values;
                break;
            case "response":
                isResponseInQuery = true;
                responseValues = values;
                break;
            case "person":
                isPersonInQuery = true;
                personValue = values[0];
                break;
            case "role":
                isRoleInQuery = true;
                roleValues = values;
                break;
            case "time":
                isCutoffInQuery = true;
                cutoffValue = Long.parseLong(values[0]);
                break;
            case "info":
                isInfoInQuery = true;
                infoValues = values;
                break;
            case "id":
                isIdInQuery = true;
                idValues = values;
                break;
            default:
                throw new InvalidParametersException("Invalid label");
            }
        }
    }

    public void setLogLocalTime(String localTimeInfo) {
        logLocalTime = localTimeInfo;
    }

    public String getLogLocalTime() {
        return logLocalTime;
    }

    public void setStatusForAjax(String status) {
        statusForAjax = status;
    }

    public String getStatusForAjax() {
        return statusForAjax;
    }

    public boolean isPersonSpecified() {
        return q != null && q.isPersonInQuery;
    }

    public String getPersonSpecified() {
        if (q == null) {
            return null;
        }
        return q.personValue;
    }

    public boolean isFromDateSpecifiedInQuery() {
        return isFromDateSpecifiedInQuery;
    }

}
