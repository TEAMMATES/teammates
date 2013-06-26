package teammates.logic.automated;

import java.util.ArrayList;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.logic.Emails;
import teammates.logic.EvaluationsLogic;

@SuppressWarnings("serial")
public class EvaluationOpeningRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		servletName = Common.EVALUATION_OPENING_REMINDERS_SERVLET;
		action = Common.EVALUATION_OPENING_REMINDERS_SERVLET_EVALUATION_OPEN_REMINDER;
		
		EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
		
		try {
			ArrayList<MimeMessage> emails = evaluationsLogic.activateReadyEvaluations();
			logActivitySuccess(req, emails);
		
		}  catch (Throwable e) {
			String reqParam = Common.printRequestParameters(req);
			new Emails().sendErrorReport(req.getServletPath(), reqParam, e);
			logActivityFailure(req, e);
		} 
	}

}
