package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.core.EvaluationsLogic;

@SuppressWarnings("serial")
public class EvaluationClosingRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
		evaluationsLogic.scheduleRemindersForClosingEvaluations();
	}
	
}
