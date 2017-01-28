package teammates.ui.pagedata;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

public class AdminActivityLogPageData extends PageData {
    
    /**
     * this array stores the requests to be excluded from being shown in admin activity logs page
     */
    private static String[] excludedLogRequestURIs = {
            Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE,
            Const.ActionURIs.AUTOMATED_LOG_COMPILATION
    };
    
    private String filterQuery;
    private String queryMessage;
    private List<ActivityLogEntry> logs;
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
    private boolean ifShowAll;
    
    /**
     * This determines whether the logs related to testing data should be shown. Use "testdata=true" in URL
     * to show all testing logs. This will keep showing all logs from testing data despite any action or change in the page
     * unless the page is reloaded with "?testdata=false"  or simply reloaded with this parameter omitted.
     */
    private boolean ifShowTestData;
    
    private String statusForAjax;
    private QueryParameters q;
    
    public AdminActivityLogPageData(AccountAttributes account) {
        super(account);
        setDefaultLogSearchPeriod();
    }
    
    public List<String> getExcludedLogRequestUris() {
        List<String> excludedList = new ArrayList<String>();
        for (String excludedLogRequestUri : excludedLogRequestURIs) {
            excludedList.add(excludedLogRequestUri.substring(excludedLogRequestUri.lastIndexOf('/') + 1));
        }
        return excludedList;
    }
    
    private void setDefaultLogSearchPeriod() {
        Calendar fromCalendarDate = TimeHelper.now(0.0);
        fromCalendarDate.add(Calendar.DAY_OF_MONTH, -1);
        
        fromDateValue = fromCalendarDate.getTimeInMillis();
        toDateValue = TimeHelper.now(0.0).getTimeInMillis();
    }

    public void init(boolean ifShowAll, boolean ifShowTestData, List<ActivityLogEntry> logs) {
        this.ifShowAll = ifShowAll;
        this.ifShowTestData = ifShowTestData;
        this.logs = logs;
        
    }
    
    public boolean getIfShowAll() {
        return ifShowAll;
    }
    
    public boolean getIfShowTestData() {
        return ifShowTestData;
    }
    
    public String getFilterQuery() {
        return filterQuery;
    }
    
    public String getQueryMessage() {
        return queryMessage;
    }
    
    public List<ActivityLogEntry> getLogs() {
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
     * Checks in an array contains a specific value
     * value is converted to lower case before comparing
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
     * Creates a QueryParameters object used for filtering
     */
    public void generateQueryParameters(String query) {
        filterQuery = query.trim();
        
        try {
            q = parseQuery(filterQuery.toLowerCase());
        } catch (ParseException | InvalidParametersException e) {
            this.queryMessage = "Error with the query: " + e.getMessage();
        }
    }
    
    /**
     * check current log entry should be excluded as rubbish logs
     * returns false if the logEntry is regarded as rubbish
     */
    private boolean shouldExcludeLogEntry(ActivityLogEntry logEntry) {
        
        if (ifShowAll) {
            return false;
        }
        
        for (String uri : excludedLogRequestURIs) {
            
            if (uri.contains(logEntry.getServletName())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Performs the actual filtering, based on QueryParameters
     * returns false if the logEntry fails the filtering process
     */
    public ActivityLogEntry filterLogs(ActivityLogEntry logEntry) {
        if (!logEntry.toShow()) {
            return logEntry;
        }
        
        if (q == null) {
            if (this.queryMessage == null) {
                this.queryMessage = "Error parsing the query. QueryParameters not created.";
            }
            logEntry.setToShow(true);
            return logEntry;
        }
        
        //Filter based on what is in the query
        if (q.isRequestInQuery && !arrayContains(q.requestValues, logEntry.getServletName())) {
            logEntry.setToShow(false);
            return logEntry;
        }
        if (q.isResponseInQuery && !arrayContains(q.responseValues, logEntry.getAction())) {
            logEntry.setToShow(false);
            return logEntry;
        }
        if (q.isPersonInQuery
                && !logEntry.getName().toLowerCase().contains(q.personValue.toLowerCase())
                && !logEntry.getGoogleId().toLowerCase().contains(q.personValue.toLowerCase())
                && !logEntry.getEmail().toLowerCase().contains(q.personValue.toLowerCase())) {
            logEntry.setToShow(false);
            return logEntry;
        }
        if (q.isRoleInQuery && !arrayContains(q.roleValues, logEntry.getRole())) {
            logEntry.setToShow(false);
            return logEntry;
        }
        if (q.isCutoffInQuery) {
            if (logEntry.getTimeTaken() == null) {
                logEntry.setToShow(false);
                return logEntry;
            }
            
            if (logEntry.getTimeTaken() < q.cutoffValue) {
                logEntry.setToShow(false);
                return logEntry;
            }
        }
        if (q.isInfoInQuery) {
            
            for (String keyString : q.infoValues) {
                if (!logEntry.getMessageInfo().toLowerCase().contains(keyString.toLowerCase())) {
                    logEntry.setToShow(false);
                    return logEntry;
                }
            }
            
            logEntry.setToShow(true);
            logEntry.setKeyStringsToHighlight(q.infoValues);
            logEntry.highlightKeyStringInMessageInfoHtml();
            return logEntry;
        }
        if (q.isIdInQuery && !arrayContains(q.idValues, logEntry.getId())) {
            logEntry.setToShow(false);
            return logEntry;
        }
        
        if (shouldExcludeLogEntry(logEntry)) {
            logEntry.setToShow(false);
            return logEntry;
        }
        
        logEntry.setToShow(true);
        return logEntry;
    }
    
    /**
     * Converts the query string into a QueryParameters object
     * 
     */
    private QueryParameters parseQuery(String query) throws ParseException, InvalidParametersException {
        QueryParameters q = new QueryParameters();
        versions = new ArrayList<String>();
        
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
                    versions.add(value.replace(".", "-"));
                }
                
            } else if ("from".equals(label)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
                Date d = sdf.parse(values[0] + " 00:00");
                Calendar cal = TimeHelper.now(0.0);
                cal.setTime(d);
                fromDateValue = cal.getTime().getTime();
                isFromDateSpecifiedInQuery = true;

            } else if ("to".equals(label)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");

                sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
                Date d = sdf.parse(values[0] + " 23:59");
                Calendar cal = TimeHelper.now(0.0);
                cal.setTime(d);
                toDateValue = cal.getTime().getTime();
            } else {
                q.add(label, values);
            }
        }
        return q;
    }
    
    /** 
     * @return possible servlet requests list as html
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
       
        List<String> actionNameList = new ArrayList<String>();
        
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
        String actionString = splitedString[splitedString.length - 1];
        
        return actionString;
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
         * add a label and values in
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
