package teammates.ui.controller;

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
	private int logsPerPage = 50;
	private int maxLogSearchLimit = 100000;
		
	@Override
	protected AdminActivityLogHelper instantiateHelper() {
		return new AdminActivityLogHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminActivityLogHelper helper) {
		
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
		}
		
		LogQuery query = buildQuery(helper.offset, includeAppLogs);
		List<AppLogLine> logs = getAppLogs(query, helper);
		req.setAttribute("appLogs", logs);
	}

	private LogQuery buildQuery(String offset, boolean includeAppLogs) {
		LogQuery query = LogQuery.Builder.withDefaults();
		query.includeAppLogs(includeAppLogs);
		if (offset != null && !offset.equals("null")) {
			query.offset(offset);
		}
		return query;
	}
	
	private List<AppLogLine> getAppLogs(LogQuery query, AdminActivityLogHelper helper) {
		List<AppLogLine> appLogs = new LinkedList<AppLogLine>();
		int totalLogsSearched = 0;
		int currentLogsInPage = 0;
		
		String lastOffset = null;
		
		//fetch request log
		for (RequestLogs record : LogServiceFactory.getLogService().fetch(query)) {
			
			totalLogsSearched ++;
			lastOffset = record.getOffset();
			
			if (totalLogsSearched >= maxLogSearchLimit){
				break;
			}
			if (currentLogsInPage >= logsPerPage) {
				break;
			}
			
			//fetch application log
			for (AppLogLine appLog : record.getAppLogLines()) {
				if (currentLogsInPage >= logsPerPage) {
					break;
				}
				String logMsg = appLog.getLogMessage();
				if (logMsg.contains("TEAMMATES_LOG") || logMsg.contains("TEAMMATES_ERROR")) {
					String[] tokens = logMsg.split("\\|\\|\\|", -1);
					//Old format logs. Do not search or parse any other older logs
					if (tokens.length != 7){
						totalLogsSearched = maxLogSearchLimit;
					}
					if(helper.performFiltering(logMsg)){
						appLogs.add(appLog);
						currentLogsInPage ++;
					}
				}
			}	
		}

		//link for Next button, will fetch older logs
		if (lastOffset != null && totalLogsSearched < maxLogSearchLimit && appLogs.size() != 0) {
			helper.statusMessage = "<a href=\"#\" onclick=\"submitForm('" + lastOffset + "');\">Next</a>";
		}
		return appLogs;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACTIVITY_LOG;
	}

}
