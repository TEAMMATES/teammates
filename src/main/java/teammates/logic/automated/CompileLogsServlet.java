package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.automated.CompileLogs;

@SuppressWarnings("serial")
public class CompileLogsServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = "logCompilation";
        action = "send severe log notifications";

        String message = "Compiling logs for email notification";
        logMessage(req, message);
        
        CompileLogs compileLog = new CompileLogs();
        compileLog.sendEmail(compileLog.doLogExam());
    }
}
