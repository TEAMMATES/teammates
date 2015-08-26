package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class AdminEmailLogPageAction extends Action {
    
    private boolean includeAppLogs = true;
    private static final int LOGS_PER_PAGE = 50;
    private static final int MAX_LOGSEARCH_LIMIT = 15000;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminEmailLogPageData data = new AdminEmailLogPageData(account, getRequestParamValue("offset"),
                                        getRequestParamValue("filterQuery"), getRequestParamAsBoolean("all"));
        
        String pageChange = getRequestParamValue("pageChange");
       
        if (pageChange != null && pageChange.equals("true")) {
            //Reset the offset because we are performing a new search, so we start from the beginning of the logs
            data.setOffset(null);
        }
        
        if (data.getFilterQuery() == null) {
            data.setFilterQuery("");
        }
        
        //This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
        data.generateQueryParameters(data.getFilterQuery());
        
        LogQuery query = buildQuery(data.getOffset(), includeAppLogs, data.getVersions());
        data.setLogs(getEmailLogs(query, data));
        
        
        statusToAdmin = "adminEmailLogPage Page Load";
        
        if (data.getOffset() == null) {
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL_LOG, data);
        }
        
        return createAjaxResult(data);
    }
    
    
    private LogQuery buildQuery(String offset, boolean includeAppLogs, List<String> versions) {
        LogQuery query = LogQuery.Builder.withDefaults();
        
        query.includeAppLogs(includeAppLogs);
        query.batchSize(1000);
        query.minLogLevel(LogLevel.INFO);
        
        try {
            query.majorVersionIds(getVersionIdsForQuery(versions));
        } catch (Exception e) {
            isError = true;
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
        }
        
        if (offset != null && !offset.equals("null")) {
            query.offset(offset);
        }
        
        return query;
    }
    
    private List<String> getVersionIdsForQuery(List<String> versions){
        
        boolean isVersionSpecifiedInRequest = (versions != null && !versions.isEmpty());
        if (isVersionSpecifiedInRequest) {   
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
    
    private List<EmailLogEntry> getEmailLogs(LogQuery query, AdminEmailLogPageData data) {
        List<EmailLogEntry> emailLogs = new LinkedList<EmailLogEntry>();
        int totalLogsSearched = 0;
        int currentLogsInPage = 0;
        
        String lastOffset = null;
        
        //fetch request log
        Iterable<RequestLogs> records = LogServiceFactory.getLogService().fetch(query);
        for (RequestLogs record : records) {
            
            totalLogsSearched ++;
            lastOffset = record.getOffset();
            
            //End the search if we hit limits
            if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT) {
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
                if (logMsg.contains("TEAMMATESEMAILLOG")) {
                    EmailLogEntry emailLogEntry = new EmailLogEntry(appLog);    
                    if(data.shouldShowLog(emailLogEntry)){
                        emailLogs.add(emailLogEntry);
                        currentLogsInPage ++;
                    }
                }
            }    
        }
        
        String status="&nbsp;&nbsp;Total Logs gone through in last search: " + totalLogsSearched + "<br>";
        //link for Next button, will fetch older logs
        if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
            status += "<br><span class=\"red\">&nbsp;&nbsp;Maximum amount of logs per requst have been searched.</span><br>";
            status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax('" + lastOffset + "');\">Search More</button>";           
        }
        
        if (currentLogsInPage >= LOGS_PER_PAGE) {   
            status += "<button class=\"btn-link\" id=\"button_older\" onclick=\"submitFormAjax('" + lastOffset + "');\">Older Logs </button>";              
        }
        
        data.setStatusForAjax(status);
        statusToUser.add(new StatusMessage(status, 
                           totalLogsSearched >= MAX_LOGSEARCH_LIMIT ? StatusMessageColor.WARNING : StatusMessageColor.INFO));
        
        return emailLogs;
    }
    
    
    
}
