package teammates.ui.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

@SuppressWarnings("serial")
public class AdminActivityLogServlet extends ActionServlet<AdminActivityLogHelper> {

	//We want to pull out the application logs
	private boolean includeAppLogs = true;
	private static final int LOGS_PER_PAGE = 1000;
	private static final int MAX_LOGSEARCH_LIMIT = 100000;
		
	@Override
	protected AdminActivityLogHelper instantiateHelper() {
		return new AdminActivityLogHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminActivityLogHelper helper) {
		//TODO: new search
		/*
		helper.servletSearchList = req.getParameterValues("toggle_servlets");		
		helper.checkAllServlets = req.getParameter("selectAll");
		helper.searchPerson = req.getParameter("searchPerson");
		helper.searchRole = req.getParameter("searchRole");
		helper.offset = req.getParameter("offset");
		
		//For fresh load of the page, default to check and search all servlets on the form
		if (helper.servletSearchList == null) {
			helper.servletSearchList = helper.listOfServlets.toArray(new String[helper.listOfServlets.size()]);
			helper.checkAllServlets = "on";
		}
	
		//New search, reset the page offset
		if(req.getParameter("pageChange") != null && !req.getParameter("pageChange").equals("true")){
			helper.offset = null;
		}*/
		
		LogQuery query = buildQuery(helper.offset, includeAppLogs);
		List<ActivityLogEntry> logs = getAppLogs(query, helper);
		req.setAttribute("appLogs", logs);
		
		String url = getRequestedURL(req);
		activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACTIVITY_LOG_SERVLET, Common.ADMIN_ACTIVITY_LOG_SERVLET_PAGE_LOAD,
				false, helper, url, null);
	}

	private LogQuery buildQuery(String offset, boolean includeAppLogs) {
		LogQuery query = LogQuery.Builder.withDefaults();
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
		for (RequestLogs record : LogServiceFactory.getLogService().fetch(query)) {
			
			totalLogsSearched ++;
			lastOffset = record.getOffset();
			
			if (totalLogsSearched >= MAX_LOGSEARCH_LIMIT){
				break;
			}
			if (currentLogsInPage >= LOGS_PER_PAGE) {
				break;
			}
			
			//fetch application log
			for (AppLogLine appLog : record.getAppLogLines()) {
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
		
		//TODO: check the offset problem again
		//link for Next button, will fetch older logs
		if (lastOffset != null && totalLogsSearched < MAX_LOGSEARCH_LIMIT && appLogs.size() != 0) {
			helper.statusMessage = "<a href=\"#\" onclick=\"submitForm('" + lastOffset + "');\">Next</a>";
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
