package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.logic.core.EvaluationsLogic;

@SuppressWarnings("serial")
public class EvaluationOpeningRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = Const.AutomatedActionNames.AUTOMATED_EVAL_OPENING_REMINDERS;
        action = "send opening reminders";

        String message = "Generating reminders for opening evaluations.";
        logMessage(req, message);
        
        EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
        evaluationsLogic.activateReadyEvaluations();
    }

}
