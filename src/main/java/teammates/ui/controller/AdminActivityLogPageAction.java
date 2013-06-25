package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

public class AdminActivityLogPageAction extends Action {
	
	//We want to pull out the application logs
	private boolean includeAppLogs = true;
	private static final int LOGS_PER_PAGE = 30;
	private static final int MAX_LOGSEARCH_LIMIT = 7000;

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException,
			InvalidParametersException {
		
		new GateKeeper().verifyAdminPrivileges(account);
		
		AdminActivityLogPageData data = new AdminActivityLogPageData(account);
		
		data.offset = getRequestParam("offset");
		data.pageChange = getRequestParam("pageChange");
		data.filterQuery = getRequestParam("filterQuery");
		
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
		data.logs = getAppLogs(query, data);
		
		return createShowPageResult(Common.JSP_ADMIN_ACTIVITY_LOG, data);
		
	}
	
	private LogQuery buildQuery(String offset, boolean includeAppLogs) {
		LogQuery query = LogQuery.Builder.withDefaults();
		
		String currentVersion = BuildProperties.getAppVersion().replace(".", "-");
		String[] tokens = currentVersion.split("-");
		List<String> appVersions = new ArrayList<String>();
		appVersions.add(currentVersion);
		appVersions.add(tokens[0] + "-" + (Integer.parseInt(tokens[1]) - 1));
		appVersions.add(tokens[0] + "-" + (Integer.parseInt(tokens[1]) - 2));
		appVersions.add(tokens[0] + "-" + (Integer.parseInt(tokens[1]) - 3));
		query.majorVersionIds(appVersions);
		
		query.includeAppLogs(includeAppLogs);
		
		if (offset != null && !offset.equals("null")) {
			query.offset(offset);
		}
		return query;
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
				if (logMsg.contains("TEAMMATESLOG")) {
					ActivityLogEntry activityLogEntry = new ActivityLogEntry(appLog);				
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
			statusToUser.add("<a href=\"#\" onclick=\"submitForm('" + lastOffset + "');\">Next</a>");
		}
		
		return appLogs;
	}
	

}
