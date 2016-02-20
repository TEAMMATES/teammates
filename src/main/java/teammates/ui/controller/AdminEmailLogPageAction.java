package teammates.ui.controller;

import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.log.AppLogLine;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.LogHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class AdminEmailLogPageAction extends Action {
    
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
        
        LogHelper logHelper = new LogHelper();
        logHelper.setQuery(data.getVersions(), null, null, data.getOffset());
        data.setLogs(getEmailLogs(logHelper, data));
        
        statusToAdmin = "adminEmailLogPage Page Load";
        
        if (data.getOffset() == null) {
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL_LOG, data);
        }
        
        return createAjaxResult(data);
    }
    
    private List<EmailLogEntry> getEmailLogs(LogHelper logHelper, AdminEmailLogPageData data) {
        List<EmailLogEntry> emailLogs = new LinkedList<EmailLogEntry>();
        int totalLogsSearched = 0;
        int currentLogsInPage = 0;
        
        String lastOffset = null;
        
        List<AppLogLine> appLogLines = logHelper.fetchLogs();
        for (AppLogLine appLog : appLogLines) {
            if (currentLogsInPage >= LOGS_PER_PAGE) break;
            String logMsg = appLog.getLogMessage();
            boolean isNotEmailLog = (!logMsg.contains("TEAMMATESEMAILLOG"));
            if (isNotEmailLog) continue;
            
            EmailLogEntry emailLogEntry = new EmailLogEntry(appLog);
            if(!data.shouldShowLog(emailLogEntry)) continue;
            
            emailLogs.add(emailLogEntry);
            currentLogsInPage++;
        }
        
        totalLogsSearched = appLogLines.size();
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
