package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

public class AdminActivityLogPageAction extends Action {
    
    //We want to pull out the application logs
    private boolean includeAppLogs = true;
    private static final int LOGS_PER_PAGE = 50;
    private static final int MAX_LOGSEARCH_LIMIT = 15000;
    
    
    
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminActivityLogPageData data = new AdminActivityLogPageData(account);
        
        data.offset = getRequestParamValue("offset");
        data.pageChange = getRequestParamValue("pageChange");
        data.filterQuery = getRequestParamValue("filterQuery");
        
        
//      This parameter determines whether the logs with requests contained in "excludedLogRequestURIs" in AdminActivityLogPageData
//      should be shown. Use "?all=true" in URL to show all logs. This will keep showing all
//      logs despite any action or change in the page unless the the page is reloaded with "?all=false" 
//      or simply reloaded with this parameter omitted.
        data.ifShowAll = getRequestParamAsBoolean("all");
        data.ifShowTestData = getRequestParamAsBoolean("testdata");
        
        
        if(data.pageChange != null && !data.pageChange.equals("true")){
            //Reset the offset because we are performing a new search, so we start from the beginning of the logs
            data.offset = null;
        }
        if(data.filterQuery == null){
            data.filterQuery = "";
        }
        //This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
        data.generateQueryParameters(data.filterQuery);
        
        
        LogQuery query = buildQuery(data.offset, includeAppLogs, data.versions);
        data.logs = getAppLogs(query, data);
        
        if(data.offset == null){
            return createShowPageResult(Const.ViewURIs.ADMIN_ACTIVITY_LOG, data);
        }
        
        return createAjaxResult(Const.ViewURIs.ADMIN_ACTIVITY_LOG, data);
    }
    
    private LogQuery buildQuery(String offset, boolean includeAppLogs, List<String> versions) {
        LogQuery query = LogQuery.Builder.withDefaults();
        
        query.includeAppLogs(includeAppLogs);
        query.batchSize(1000);
        
        try {
            query.majorVersionIds(getVersionIdsForQuery(versions));
        } catch (Exception e) {
            isError = true;
            statusToUser.add(e.getMessage());
        }
        
        if (offset != null && !offset.equals("null")) {
            query.offset(offset);
        }
        return query;
    }
    
    private List<String> getVersionIdsForQuery(List<String> versions){
        
        boolean isVersionSpecifiedInRequest = (versions != null && !versions.isEmpty());
        if(isVersionSpecifiedInRequest){   
            return versions;        
        }       
        return getDefaultVersionIdsForQuery();
    }
    
    private List<String> getDefaultVersionIdsForQuery(){
    
        String currentVersion = Config.inst().getAppVersion();
        List<String> defaultVersions = new ArrayList<String>();
        
        //Check whether version Id contains alphabet 
        //Eg. 5.05rc
        if (currentVersion.matches(".*[A-z.*]")) {
            //if current version contains alphatet,
            //by default just prepare current version as a single element for the query
            defaultVersions.add(currentVersion.replace(".", "-"));
            
        } else {
            //current version does not contain alphabet
            //by default prepare current version with preceding 3 versions
            defaultVersions = getRecentVersionIdsWithDigitOnly(currentVersion);
        }
        
        return defaultVersions;        
    }
    
    private List<String> getRecentVersionIdsWithDigitOnly(String currentVersion){
        
        List<String> recentVersions = new ArrayList<String>();
        
        double curVersionAsDouble = Double.parseDouble(currentVersion);
        recentVersions.add(currentVersion.replace(".", "-"));
        
        //preceding versions
        String[] preVer = { null, null, null };
        
        //go back for three preceding versions
        //subtract from double form of current version id
        //Eg. current version is 4.01 --> 4.00, 3.99, 3.98  --> 4-00, 3-99, 3-98
        for (int i = 1; i < 4; i++) {

            double preVersionAsDouble = curVersionAsDouble - 0.01 * i;
            if (preVersionAsDouble > 0) {
                String preVersion = String.format("%.2f", preVersionAsDouble)
                                          .replace(".", "-");
                
                recentVersions.add(preVersion);
            }
        }
        
        return recentVersions;
    }
    
    private List<ActivityLogEntry> getAppLogs(LogQuery query, AdminActivityLogPageData data) {
        List<ActivityLogEntry> appLogs = new LinkedList<ActivityLogEntry>();
        int totalLogsSearched = 0;
        int currentLogsInPage = 0;
        
        String lastOffset = null;
        
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query);
        for (RequestLogs record : records) {
            
            totalLogsSearched ++;
            lastOffset = record.getOffset();
            
            //End the search if we hit limits
            if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
                break;
            }
            if (currentLogsInPage >= LOGS_PER_PAGE) {
                break;
            }
            
            //fetch application log
            List<AppLogLine> appLogLines = record.getAppLogLines();
            for (AppLogLine appLog : appLogLines) {
                if (currentLogsInPage >= LOGS_PER_PAGE) {
                    break;
                }
                String logMsg = appLog.getLogMessage();
                if (logMsg.contains("TEAMMATESLOG") && !logMsg.contains("adminActivityLogPage")) {
                    ActivityLogEntry activityLogEntry = new ActivityLogEntry(appLog);                
                    if(data.filterLogs(activityLogEntry)){
                        appLogs.add(activityLogEntry);
                        currentLogsInPage ++;
                    }
                }
            }    
        }
        
        String status="&nbsp;&nbsp;Total Logs gone through in last search: " + totalLogsSearched + "<br>";
        //link for Next button, will fetch older logs
        if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
            status += "<br><span class=\"red\">&nbsp;&nbsp;Maximum amount of logs per requst have been searched.</span><br>";
            status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax('" + lastOffset + "','" + data.ifShowAll + "');\">Search More</button>";           
        }
        
        if (currentLogsInPage >= LOGS_PER_PAGE) {   
            status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax('" + lastOffset + "','" + data.ifShowAll + "');\">Older Logs </button>";              
        }
        
        status += "<input id=\"ifShowAll\" type=\"hidden\" value=\""+ data.ifShowAll +"\"/>";
        
        data.statusForAjax = status;
        statusToUser.add(status);
        
        return appLogs;
    }
    

}
