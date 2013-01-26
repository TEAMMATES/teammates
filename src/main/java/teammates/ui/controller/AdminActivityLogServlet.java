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
	private int queryLimit = 50;
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
		String queryOffset = req.getParameter("offset");
		LogQuery query = buildQuery(queryOffset, includeAppLogs);
		List<AppLogLine> logs = getAppLogs(query, queryLimit, helper);
		req.setAttribute("appLogs", logs);

	}

	private LogQuery buildQuery(String offset, boolean includeAppLogs) {
		LogQuery query = LogQuery.Builder.withDefaults();
		query.includeAppLogs(includeAppLogs);
		if (offset != null) {
			query.offset(offset);

		}
		return query;
	}

	private List<AppLogLine> getAppLogs(LogQuery query, int queryLimit, AdminActivityLogHelper helper) {
		List<AppLogLine> appLogs = new LinkedList<AppLogLine>();

		String lastOffset = null;
		int i = 0;

		//fetch request log
		for (RequestLogs record : LogServiceFactory.getLogService()
				.fetch(query)) {

			lastOffset = record.getOffset();
			
			//fetch application log
			for (AppLogLine appLog : record.getAppLogLines()) {
				String logMsg = appLog.getLogMessage();
				if (logMsg.contains("TEAMMATES_LOG") || logMsg.contains("TEAMMATES_ERROR")) {
					appLogs.add(appLog);
				}
			}
			if (++i >= queryLimit) {
				break;
			}
		}

		//link for Next button, will fetch older logs
		if (lastOffset != null) {
			helper.statusMessage = String.format(
					"<a href=\"%s?offset=%s\">Next</a>",
					Common.PAGE_ADMIN_ACTIVITY_LOG, lastOffset);
		}
		return appLogs;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACTIVITY_LOG;
	}

}
