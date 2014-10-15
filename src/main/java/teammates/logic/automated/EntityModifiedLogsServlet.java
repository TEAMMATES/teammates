package teammates.logic.automated;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.log.AppLogLine;
import com.google.appengine.api.log.LogQuery;
import com.google.appengine.api.log.LogService;
import com.google.appengine.api.log.LogServiceFactory;
import com.google.appengine.api.log.RequestLogs;


@SuppressWarnings("serial")
public class EntityModifiedLogsServlet extends AutomatedRemindersServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {  
        servletName = "entityModifiedLogs";
        action = "extracts entities that were modified from logs";

        String message = "Compiling logs for email notification";
        logMessage(req, message);

        resp.setContentType("application/json");
   
        try {
            PrintWriter writer = resp.getWriter();

            LogService logService = LogServiceFactory.getLogService();

            long endTime = (new java.util.Date()).getTime();
            // Sets the range to 6 minutes to slightly overlap the 5 minute scheduled task timer
            long queryRange = 1000 * 60 * 60 * 24;
            long startTime = endTime - queryRange;

            LogQuery q = LogQuery.Builder.withDefaults().includeAppLogs(true)
                    .startTimeMillis(startTime).endTimeMillis(endTime);
            Iterator<RequestLogs> logIterator = logService.fetch(q).iterator();

            while (logIterator.hasNext()) {
                RequestLogs requestLogs = logIterator.next();
                List<AppLogLine> logList = requestLogs.getAppLogLines();
                
                for (int i = 0; i < logList.size(); i++) {
                    AppLogLine currentLog = logList.get(i);
                    String logMessage = currentLog.getLogMessage();
                    if(logMessage.contains("modified course::")) {
                        String tokens[] = logMessage.split("::");
                        String courseId = tokens[1];
                      
                        writer.println(courseId);
                    }
                }
            }
        } catch (IOException e) {  
            e.printStackTrace();
        }
    }
}
