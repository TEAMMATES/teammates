package teammates.ui.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Logger;

@SuppressWarnings("serial")
public class EntityModifiedLogsServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        try {
            PrintWriter writer = resp.getWriter();
            LogService logService = LogServiceFactory.getLogService();

            long endTime = new Date().getTime();
            long queryRange = 1000 * 60 * 60 * 24;
            long startTime = endTime - queryRange;

            LogQuery q = LogQuery.Builder.withDefaults()
                                         .includeAppLogs(true)
                                         .startTimeMillis(startTime)
                                         .endTimeMillis(endTime);
            Iterable<RequestLogs> logs = logService.fetch(q);

            for (RequestLogs requestLogs : logs) {
                List<AppLogLine> logList = requestLogs.getAppLogLines();

                for (int i = 0; i < logList.size(); i++) {
                    AppLogLine currentLog = logList.get(i);
                    String logMessage = currentLog.getLogMessage();
                    if (logMessage.contains("modified course::")) {
                        String[] tokens = logMessage.split("::");
                        String courseId = tokens[1];
                        writer.println(courseId);
                    }
                }
            }
        } catch (IOException e) {
            log.severe(TeammatesException.toStringWithStackTrace(e));
        }
    }

}
