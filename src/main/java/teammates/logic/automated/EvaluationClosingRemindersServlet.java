package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.logic.core.EvaluationsLogic;

@SuppressWarnings("serial")
public class EvaluationClosingRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = Const.AutomatedActionNames.AUTOMATED_EVAL_CLOSING_REMINDERS;
        action = "send closing reminders";

        String message = "Generating reminders for closing evaluations";
        logMessage(req, message);

        EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
        evaluationsLogic.scheduleRemindersForClosingEvaluations();
    }
}
