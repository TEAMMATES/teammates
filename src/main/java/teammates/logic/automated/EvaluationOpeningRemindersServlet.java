package teammates.logic.automated;

import java.util.ArrayList;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.HttpRequestHelper;
import teammates.logic.Emails;
import teammates.logic.EvaluationsLogic;

@SuppressWarnings("serial")
public class EvaluationOpeningRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		servletName = "evaluationOpeningReminders";
		action = "send opening reminders";
		
		EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
		
		try {
			ArrayList<MimeMessage> emails = evaluationsLogic.activateReadyEvaluations();
			logActivitySuccess(req, emails);
		
		}  catch (Throwable e) {
			String reqParam = HttpRequestHelper.printRequestParameters(req);
			new Emails().sendErrorReport(req.getServletPath(), reqParam, e);
			logActivityFailure(req, e);
		} 
	}

}
