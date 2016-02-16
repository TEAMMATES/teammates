package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService.LogLevel;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;
import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class AdminEmailLogPageAction extends Action {
    
    private boolean includeAppLogs = true;
    private static final int LOGS_PER_PAGE = 50;
    private static final int MAX_LOGSEARCH_LIMIT = 15000;
    
    /**
     * 6 default versions for query, including the current version and its 5 preceding versions.
     */
    private static final int MAX_DEFAULT_VERSION_NUMBER = 6;
    
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
    
    /**
     * Selects versions for query. If versions are not specified, it will return 
     * default versions used for query.
     */
    private List<String> getVersionIdsForQuery(List<String> versions){
        boolean isVersionSpecifiedInRequest = (versions != null && !versions.isEmpty());
        if (isVersionSpecifiedInRequest) {   
            return versions;
        }       
        return getDefaultVersionIdsForQuery();
    }
    
    /**
     * Gets a list of versions, including the current version and 5 preceding versions (if available).
     * @return a list of default versions for query.
     */
    private List<String> getDefaultVersionIdsForQuery() {
        List<String> defaultVersions = new ArrayList<String>();
        
        ModulesService modulesService = ModulesServiceFactory.getModulesService();
        Set<String> versionList = modulesService.getVersions(null); // null == default module
        String currentVersion = modulesService.getCurrentVersion();
        boolean isCurrentVersionFound = false;
        
        // Find the current version then get at most 5 versions below it.
        for(String version : versionList) {
            if (version.equals(currentVersion)) {
                isCurrentVersionFound = true;
            }
            if (isCurrentVersionFound) {
                defaultVersions.add(version);
                if (defaultVersions.size() >= MAX_DEFAULT_VERSION_NUMBER) {
                    return defaultVersions;
                }
            }
        }
        return defaultVersions;
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
