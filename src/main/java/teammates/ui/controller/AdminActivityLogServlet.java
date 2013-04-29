package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.BuildProperties;
import teammates.common.Common;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

@SuppressWarnings("serial")
public class AdminActivityLogServlet extends ActionServlet<AdminActivityLogHelper> {

	//We want to pull out the application logs
	private boolean includeAppLogs = true;
	private static final int LOGS_PER_PAGE = 30;
	private static final int MAX_LOGSEARCH_LIMIT = 7000;
		
	@Override
	protected AdminActivityLogHelper instantiateHelper() {
		return new AdminActivityLogHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminActivityLogHelper helper) {
		helper.offset = req.getParameter("offset");
		helper.pageChange = req.getParameter("pageChange");
		if(helper.pageChange != null && !helper.pageChange.equals("true")){
			//Reset the offset because we are performing a new search, so we start from the beginning of the logs
			helper.offset = null;
		}
		helper.filterQuery = req.getParameter("filterQuery");
		if(helper.filterQuery == null){
			helper.filterQuery = "";
		}
		//This is used to parse the filterQuery. If the query is not parsed, the filter function would ignore the query
		helper.generateQueryParameters(helper.filterQuery);
		
		
		LogQuery query = buildQuery(helper.offset, includeAppLogs);
		List<ActivityLogEntry> logs = getAppLogs(query, helper);
		req.setAttribute("appLogs", logs);
		
		String url = getRequestedURL(req);
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACTIVITY_LOG_SERVLET, Common.ADMIN_ACTIVITY_LOG_SERVLET_PAGE_LOAD,
				false, helper, url, null);
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
	
	private List<ActivityLogEntry> getAppLogs(LogQuery query, AdminActivityLogHelper helper) {
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
					activityLogEntry = new ActivityLogEntry(appLog);				
					if(helper.filterLogs(activityLogEntry)){
						appLogs.add(activityLogEntry);
						currentLogsInPage ++;
					}
				}
			}	
		}
		
		helper.statusMessage = "Total logs searched: " + totalLogsSearched + "<br>";
		//link for Next button, will fetch older logs
		if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
			helper.statusMessage += "<br><span class=\"red\">Maximum amount of logs searched.</span><br>";
		}
		if (currentLogsInPage >= LOGS_PER_PAGE) {			
			helper.statusMessage += "<a href=\"#\" onclick=\"submitForm('" + lastOffset + "');\">Next</a>";
		}
		
		return appLogs;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACTIVITY_LOG;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.ADMIN_ACTIVITY_LOG_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		message = "adminActivityLog Page Load";
		
		return message;
	}
}
