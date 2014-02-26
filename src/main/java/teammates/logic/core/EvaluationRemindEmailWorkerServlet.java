package teammates.logic.core;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
/**
 * Receives and executes tasks added to the "evaluation-remind-email-queue" Task Queue
 * Only accessible from within the application and not externally by users  
 */
@SuppressWarnings("serial")
public class EvaluationRemindEmailWorkerServlet extends HttpServlet {
	private static Logger log = Utils.getLogger();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		String evaluationName = HttpRequestHelper
				.getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_EVAL);
		Assumption.assertNotNull(evaluationName);
		
		String courseId = HttpRequestHelper
				.getValueFromRequestParameterMap(req, ParamsNames.SUBMISSION_COURSE);
		Assumption.assertNotNull(courseId);
		
		try {
			EvaluationsLogic.inst().sendReminderForEvaluation(courseId, evaluationName);
		} catch (EntityDoesNotExistException e) {
			log.severe("Unexpected error while sending emails " + e.getMessage());
		}
	}
}
