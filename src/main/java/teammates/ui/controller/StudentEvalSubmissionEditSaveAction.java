package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

import com.google.appengine.api.datastore.Text;

public class StudentEvalSubmissionEditSaveAction extends Action {
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String evalName = getRequestParam(Common.PARAM_EVALUATION_NAME);
		Assumption.assertNotNull(evalName);
		
		String fromEmail = getRequestParam(Common.PARAM_FROM_EMAIL);
		Assumption.assertNotNull(fromEmail);
		
		new GateKeeper().verifyEmailOwnerAndEvalInState(courseId, evalName, fromEmail, EvalStatus.OPEN);
		
		String teamName = getRequestParam(Common.PARAM_TEAM_NAME);
		String[] toEmails = getRequestParamValues(Common.PARAM_TO_EMAIL);
		String[] points = getRequestParamValues(Common.PARAM_POINTS);
		String[] justifications = getRequestParamValues(Common.PARAM_JUSTIFICATION);
		String[] comments = getRequestParamValues(Common.PARAM_COMMENTS);
		
		EvaluationAttributes eval = logic.getEvaluation(courseId, evalName);
		
		//extract submission data
		ArrayList<SubmissionAttributes> submissionData = new ArrayList<SubmissionAttributes>();
		int submissionCount = ((toEmails == null ? 0 : toEmails.length));
		for(int i=0; i<submissionCount ; i++){
			SubmissionAttributes sub = new SubmissionAttributes();
			sub.course = courseId;
			sub.evaluation = evalName;
			sub.justification = new Text(justifications[i]);
			
			if (eval.p2pEnabled) {
				sub.p2pFeedback = new Text(comments[i]);
			}
			
			sub.points = Integer.parseInt(points[i]);
			sub.reviewee = toEmails[i];
			sub.reviewer = fromEmail;
			sub.team = teamName;
			submissionData.add(sub);
		}
		
		try{
			logic.updateSubmissions(submissionData);
			statusToUser.add(String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED, PageData.escapeForHTML(evalName), courseId));
			statusToAdmin = createLogMesage(courseId, evalName, teamName, fromEmail, toEmails, points, justifications, comments);
			
		} catch (InvalidParametersException e) {
			//TODO: redirect to the same page?
			statusToUser.add(e.getMessage());
			isError = true;
			statusToAdmin = Common.LOG_SERVLET_ACTION_FAILURE + " : " + e.getMessage();
		}		
		
		RedirectResult response = createRedirectResult(Common.PAGE_STUDENT_HOME);
		return response;

	}

	private String createLogMesage(
			String courseId, 
			String evalName,
			String teamName,
			String fromEmail, 
			String[] toEmails, 
			String[] points,
			String[] justifications, 
			String[] comments) {
		
		String message = "<span class=\"bold\">(" + teamName + ") " + fromEmail + "'s</span> Submission for Evaluation <span class=\"bold\">(" + evalName + ")</span> for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br><br>";
		
		int submissionCount = ((toEmails == null ? 0 : toEmails.length));
		for (int i = 0; i < submissionCount; i++){
			message += "<span class=\"bold\">To:</span> " + toEmails[i] + "<br>";
			message += "<span class=\"bold\">Points:</span> " + points[i] + "<br>";
			if (comments == null){	//p2pDisabled
				message += "<span class=\"bold\">Comments: </span>Disabled<br>";
			} else {
				message += "<span class=\"bold\">Comments:</span> " + comments[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>") + "<br>";
			}
			message += "<span class=\"bold\">Justification:</span> " + justifications[i].replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
			message += "<br><br>";
		}  
		return message;
	}

	
	
	
}
