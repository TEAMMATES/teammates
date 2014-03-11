package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teammates.common.datatransfer.LogEntryAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.LogEntryLogic;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

public class AdminActivityLogPageAction extends Action {
	
	//We want to pull out the application logs
	private boolean includeAppLogs = true;
	private static final int LOGS_PER_PAGE = 50;
	private static final int MAX_LOGSEARCH_LIMIT = 15000;
	private boolean usingDataStore = true;
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException{
		
		new GateKeeper().verifyAdminPrivileges(account);
		
		AdminActivityLogPageData data = new AdminActivityLogPageData(account);
		
		data.offset = getRequestParamValue("offset");
		data.pageChange = getRequestParamValue("pageChange");
		data.filterQuery = getRequestParamValue("filterQuery");
		
		if(data.pageChange != null && !data.pageChange.equals("true")){
			//Reset the offset because we are performing a new search, so we start from the beginning of the logs
			data.offset = null;
		}
		if(data.filterQuery == null){
			data.filterQuery = "";
		}
		//This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
		data.generateQueryParameters(data.filterQuery);
		
		
		LogQuery query = buildQuery(data.offset, includeAppLogs);
		if (usingDataStore) {
			data.logs = getAppLogsFromDataStore(query, data);
		} else {
			data.logs = getAppLogs(query, data);
		}
		
		return createShowPageResult(Const.ViewURIs.ADMIN_ACTIVITY_LOG, data);
		
	}
	
	private List<LogEntryAttributes> getAppLogsFromDataStore(LogQuery query, AdminActivityLogPageData data) {
		List<LogEntryAttributes> appLogs = new LinkedList<LogEntryAttributes>();
		int currentLogsInPage = 0;
		int totalLogsSearched = 0;
		String lastOffset = data.offset;
		int offset;
		try {
			offset = Integer.parseInt(lastOffset);
		} catch (NumberFormatException e) {
			offset = 0;
		}
		Iterable<LogEntryAttributes> records = LogEntryLogic.inst().getAllLogsFrom(offset);
		for (LogEntryAttributes record : records) {
			offset++;
			if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
				break;
			}
			if (currentLogsInPage >= LOGS_PER_PAGE) {
				break;
			}
			
			if(data.filterLogs(record)){
				appLogs.add(record);
				currentLogsInPage ++;
			}
		}
		if (currentLogsInPage == MAX_LOGSEARCH_LIMIT) {			
			statusToUser.add("<br><span class=\"red\">End of results.</span><br>");
		} else {
			statusToUser.add("<a href=\"#\" onclick=\"submitForm('" + offset + "');\">Older</a>");
		}
		return appLogs;
	}
	
	private LogQuery buildQuery(String offset, boolean includeAppLogs) {
		LogQuery query = LogQuery.Builder.withDefaults();
		
		String currentVersion = Config.inst().getAppVersion().replace(".", "-");
		String[] tokens = currentVersion.split("-");
		List<String> appVersions = new ArrayList<String>();
		appVersions.add(currentVersion);
		appVersions.add(tokens[0] + "-" + (Integer.parseInt(tokens[1]) - 1));
		appVersions.add(tokens[0] + "-" + (Integer.parseInt(tokens[1]) - 2));
		appVersions.add(tokens[0] + "-" + (Integer.parseInt(tokens[1]) - 3));
		query.majorVersionIds(appVersions);
		
		query.includeAppLogs(includeAppLogs);
		query.batchSize(1000);
		
		if (offset != null && !offset.equals("null")) {
			query.offset(offset);
		}
		return query;
	}
	
	private List<LogEntryAttributes> getAppLogs(LogQuery query, AdminActivityLogPageData data) {
		List<LogEntryAttributes> appLogs = new LinkedList<LogEntryAttributes>();
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
					LogEntryAttributes activityLogEntry = new LogEntryAttributes(appLog);				
					if(data.filterLogs(activityLogEntry)){
						appLogs.add(activityLogEntry);
						currentLogsInPage ++;
					}
				}
			}	
		}
		
		statusToUser.add("Total logs searched: " + totalLogsSearched + "<br>");
		//link for Next button, will fetch older logs
		if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
			statusToUser.add("<br><span class=\"red\">Maximum amount of logs searched.</span><br>");
		}
		if (currentLogsInPage >= LOGS_PER_PAGE) {			
			statusToUser.add("<a href=\"#\" onclick=\"submitForm('" + lastOffset + "');\">Older</a>");
		}
		
		return appLogs;
	}
	

}
