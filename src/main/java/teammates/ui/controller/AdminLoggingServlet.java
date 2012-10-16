package teammates.ui.controller;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

@SuppressWarnings("serial")
public class AdminLoggingServlet extends ActionServlet<AdminHomeHelper> {

	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminHomeHelper helper) {
		List<RequestLogs> recordList = new LinkedList<RequestLogs>();
		List<AppLogLine> appLogs = new LinkedList<AppLogLine>();

		// We use this to break out of our iteration loop, limiting record
		// display to 5 request logs at a time.
		int limit = 500;

		// This retrieves the offset from the Next link upon user click.
		String offset = req.getParameter("offset");

		// We want the App logs for each request log
		LogQuery query = LogQuery.Builder.withDefaults();
		query.includeAppLogs(true);

		// Set the offset value retrieved from the Next link click.
		if (offset != null) {
			query.offset(offset);
		}

		// This gets filled from the last request log in the iteration
		String lastOffset = null;
		int i = 0;

		// Display a few properties of each request log.
		for (RequestLogs record : LogServiceFactory.getLogService()
				.fetch(query)) {
			recordList.add(record);

			lastOffset = record.getOffset();

			// Display all the app logs for each request log.
			for (AppLogLine appLog : record.getAppLogLines()) {
				Calendar appCal = Calendar.getInstance();
				appCal.setTimeInMillis(appLog.getTimeUsec() / 1000);

				if (appLog.getLogMessage().contains("Responded with")) {
					appLogs.add(appLog);
				}
			} // for each log line

			if (++i >= limit) {
				break;
			}
		} // for each record

		// When the user clicks this link, the offset is processed in the
		// GET handler and used to cycle through to the next 5 request logs.
		if(lastOffset != null) {
			helper.statusMessage = String.format(
				"<a href=\"%s?offset=%s\">Next</a>",Common.PAGE_ADMIN_LOGGING, lastOffset);
		}
		req.setAttribute("appLogs", appLogs);

	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_LOGGING;
	}

}
