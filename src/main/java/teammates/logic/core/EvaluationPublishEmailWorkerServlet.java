package teammates.logic.core;


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
/**
 * Receives and executes tasks added to the "evaluation-publish-email-queue" Task Queue
 * Only accessible from within the application and not externally by users  
 */
@SuppressWarnings("serial")
public class EvaluationPublishEmailWorkerServlet extends HttpServlet {
	
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
		
		EvaluationsLogic.inst().sendEvaluationPublishedEmails(courseId, evaluationName);
	}
}
