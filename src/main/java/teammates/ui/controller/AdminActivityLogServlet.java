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

	/*
	 * number of logs to query each time,
	 * this is larger than number of logs shown on each page because we drop request log
	 */
	private int queryLimit = 20;
	private int maxLogSearchLimit = 1000;
	/*
	 * parameter to indicate whether to include application log in the result.
	 * default case only return request log
	 * https://developers.google.com/appengine/docs/java/logservice/
	 */
	private boolean includeAppLogs = true;
	@Override
	protected AdminActivityLogHelper instantiateHelper() {
		return new AdminActivityLogHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminActivityLogHelper helper) {
		helper.searchServlets = req.getParameterValues("toggle_servlets");
		helper.servletCheckAll = req.getParameter("selectAll");
		if (helper.searchServlets == null) {
			helper.searchServlets = helper.listOfServlets.toArray(new String[helper.listOfServlets.size()]);
			helper.servletCheckAll = "on";
		}
		helper.searchPerson = req.getParameter("searchPerson");
		helper.searchRole = req.getParameter("searchRole");
		String queryOffset = req.getParameter("offset");
		helper.offset = queryOffset;
		
		if(req.getParameter("pageChange") != null && !req.getParameter("pageChange").equals("true")){
			helper.offset = null;
			queryOffset = null;
		}
		
		
		LogQuery query = buildQuery(queryOffset, includeAppLogs);
		List<AppLogLine> logs = getAppLogs(query, queryLimit, helper);
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

	private List<AppLogLine> getAppLogs(LogQuery query, int queryLimit, AdminActivityLogHelper helper) {
		List<AppLogLine> appLogs = new LinkedList<AppLogLine>();
		int totalLogsSearched = 0;
		
		String lastOffset = null;
		int totalTeammatesLogs = 0;
		
		//fetch request log
		for (RequestLogs record : LogServiceFactory.getLogService()
				.fetch(query)) {
			
			totalLogsSearched ++;
			lastOffset = record.getOffset();
			
			//fetch application log
			for (AppLogLine appLog : record.getAppLogLines()) {
				String logMsg = appLog.getLogMessage();
				String[] tokens = logMsg.split("\\|");
				//Old format logs. Do not search or parse
				if (tokens.length < 8){
					totalLogsSearched = maxLogSearchLimit;
					break;
				}
				if (logMsg.contains("TEAMMATES_LOG") || logMsg.contains("TEAMMATES_ERROR")) {
					if(AdminActivityLogHelper.performFiltering(helper, logMsg)){
						appLogs.add(appLog);
						if (++totalTeammatesLogs >= queryLimit) {
							break;
						}
					}
				}
			}
			if (totalTeammatesLogs >= queryLimit) {
				break;
			}
			if (totalLogsSearched >= maxLogSearchLimit){
				break;
			}
			
		}

		//link for Next button, will fetch older logs
		if (lastOffset != null && (totalLogsSearched < maxLogSearchLimit || appLogs.size() == 0)) {
			helper.statusMessage = "<a href=\"#\" onclick=\"submitForm('" + lastOffset + "');\">Next</a>";
		}
		return appLogs;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACTIVITY_LOG;
	}

}
