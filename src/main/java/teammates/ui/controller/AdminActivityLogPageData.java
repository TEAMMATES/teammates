package teammates.ui.controller;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

public class AdminActivityLogPageData extends PageData {
    
    private String offset;
    private String filterQuery;
    private String queryMessage;
    private List<ActivityLogEntry> logs;
    private List<String> versions;
    private String logLocalTime;
    
    /**
     * This determines whether the logs with requests contained in "excludedLogRequestURIs" below 
     * should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
     * logs despite any action or change in the page unless the the page is reloaded with "?all=false" 
     * or simply reloaded with this parameter omitted.
     */
    private boolean ifShowAll = false;
    
    /**
     * This determines whether the logs related to testing data should be shown. Use "testdata=true" in URL
     * to show all testing logs. This will keep showing all logs from testing data despite any action or change in the page
     * unless the the page is reloaded with "?testdata=false"  or simply reloaded with this parameter omitted.
     */
    private boolean ifShowTestData = false;
    
    private String statusForAjax;
    private QueryParameters q;
    
    /**
     * this array stores the requests to be excluded from being shown in admin activity logs page
     */
    private static String[] excludedLogRequestURIs = { Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE,                                                      
                                                      //this servlet name is set in CompileLogsServlet
                                                      Const.AutomatedActionNames.AUTOMATED_LOG_COMILATION};
    
    public List<String> getExcludedLogRequestURIs() {
        List<String> excludedList = new ArrayList<String>();
        for (String excludedLogRequestURI : excludedLogRequestURIs) {
            excludedList.add(excludedLogRequestURI.substring(excludedLogRequestURI.lastIndexOf("/") + 1));
        }
        return excludedList;
    }
    
    public AdminActivityLogPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(String offset, String filterQuery, boolean ifShowAll,
                     boolean ifShowTestData, List<ActivityLogEntry> logs) {
        this.offset = offset;
        this.filterQuery = filterQuery;
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
    
    public String getOffset() {
        return offset;
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
    
    /**
     * Checks in an array contains a specific value
     * value is converted to lower case before comparing
     */
    private boolean arrayContains(String[] arr, String value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(value.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Creates a QueryParameters object used for filtering
     */
    public void generateQueryParameters(String query) {
        query = query.toLowerCase();
        
        try {
            q = parseQuery(query);
        } catch (Exception e) {
            this.queryMessage = "Error with the query: " + e.getMessage();
        }
    }
    
    
    
    /**
     * check current log entry should be excluded as rubbish logs 
     * returns false if the logEntry is regarded as rubbish
     */   
    private boolean shouldExcludeLogEntry(ActivityLogEntry logEntry) {
        
        if (ifShowAll == true) {        
            return false;
        }
        
        for (String uri: excludedLogRequestURIs) {
            
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
        if (q.isToDateInQuery) {
            if (logEntry.getTime() > q.toDateValue) {
                logEntry.setToShow(false);
                return logEntry;
            }
        }
        if (q.isFromDateInQuery) {
            if (logEntry.getTime() < q.fromDateValue) {
                logEntry.setToShow(false);
                return logEntry;
            }
        }
        if (q.isRequestInQuery) {
            if (!arrayContains(q.requestValues, logEntry.getServletName())) {
                logEntry.setToShow(false);
                return logEntry;
            }
        }
        if (q.isResponseInQuery) {
            if (!arrayContains(q.responseValues, logEntry.getAction())) {
                logEntry.setToShow(false);
                return logEntry;
            }
        }
        if (q.isPersonInQuery) {
            if (!logEntry.getName().toLowerCase().contains(q.personValue.toLowerCase()) && 
                    !logEntry.getId().toLowerCase().contains(q.personValue.toLowerCase()) && 
                    !logEntry.getEmail().toLowerCase().contains(q.personValue.toLowerCase())) {
                logEntry.setToShow(false);
                return logEntry;
            }
        }
        if (q.isRoleInQuery) {
            if (!arrayContains(q.roleValues, logEntry.getRole())) {
                logEntry.setToShow(false);
                return logEntry;
            }
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
    private QueryParameters parseQuery(String query) throws Exception{
        QueryParameters q = new QueryParameters();
        versions = new ArrayList<String>();
        
        if (query == null || query.equals("")) {
            return q;
        }
        
        query = query.replaceAll(" and ", "|");
        query = query.replaceAll(", ", ",");
        query = query.replaceAll(": ", ":");
        String[] tokens = query.split("\\|", -1); 
       
        System.out.print(tokens.length);
        
        for (int i = 0; i < tokens.length; i++) {           
            String[] pair = tokens[i].split(":", -1);
            
            if (pair.length != 2) {
                throw new Exception("Invalid format");
            }
            
            String[] values = pair[1].split(",", -1);
            String label = pair[0];
            
            if (label.equals("version")) {
                //version is specified in com.google.appengine.api.log.LogQuery,
                //it does not belong to the internal class "QueryParameters"
                //so need to store here for future use
                for (int j = 0; j < values.length; j++) {
                    versions.add(values[j].replace(".", "-"));
                }
                
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
        
        String outputHtml = "<tr>";      
        int count = 0;      
        for (int i = 0; i < totalColumns; i++) {
            
            outputHtml += "<td>";
            outputHtml += "<ul class=\"list-group\">";
            for (int j = 0; j < rowsPerCol; j++) {
                
                if (count >= allActionNames.size()) {
                    break;
                }
                
                outputHtml += "<li class=\"list-group-item " 
                              + getStyleForListGroupItem(allActionNames.get(count))
                              + "\">" + allActionNames.get(count) + "</li>";
                              
                count++;
            }
            outputHtml += "</ul>";
            outputHtml += "</td>";
        }
        
       
        return outputHtml;    

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
            rowsPerCol ++;
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
    private class QueryParameters{        
        public boolean isToDateInQuery;
        public long toDateValue;
        
        public boolean isFromDateInQuery;
        public long fromDateValue;
        
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
        
        public QueryParameters() {
            isToDateInQuery = false;
            isFromDateInQuery = false;
            isRequestInQuery = false;
            isResponseInQuery = false;
            isPersonInQuery = false;
            isRoleInQuery = false;
            isCutoffInQuery = false;
            isInfoInQuery = false;
        }
        
        /**
         * add a label and values in
         */
        public void add(String label, String[] values) throws Exception{
            if (label.equals("from")) {
                isFromDateInQuery = true;                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                Date d = sdf.parse(values[0] + " 00:00");                
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal = TimeHelper.convertToUserTimeZone(cal, - Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE);
                fromDateValue = cal.getTime().getTime();
                
            } else if (label.equals("to")) {
                isToDateInQuery = true;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
                Date d = sdf.parse(values[0] + " 23:59");                
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal = TimeHelper.convertToUserTimeZone(cal, - Const.SystemParams.ADMIN_TIMZE_ZONE_DOUBLE);
                toDateValue = cal.getTime().getTime();       
                
            } else if (label.equals("request")) {
                isRequestInQuery = true;
                requestValues = values;
            } else if (label.equals("response")) {
                isResponseInQuery = true;
                responseValues = values;
            } else if (label.equals("person")) {
                isPersonInQuery = true;
                personValue = values[0];
            } else if (label.equals("role")) {
                isRoleInQuery = true;
                roleValues = values;
            } else if (label.equals("time")) {
                isCutoffInQuery = true;
                cutoffValue = Long.parseLong(values[0]);
            } else if (label.equals("info")) {
                isInfoInQuery = true;
                infoValues = values;
            } else {
                throw new Exception("Invalid label");
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
}
