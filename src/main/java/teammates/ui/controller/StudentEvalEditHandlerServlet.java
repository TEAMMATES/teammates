package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

@SuppressWarnings("serial")
public class StudentEvalEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper){
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		return String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED,EvalSubmissionEditHelper.escapeForHTML(evalName), courseID);
	}

	@Override
	protected String getSuccessUrl() {
		return Common.PAGE_STUDENT_HOME;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION)){
			message = generateEditSubmissionMessage(servletName, action, data); 
		}else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}
	
	
	private String generateEditSubmissionMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "<span class=\"bold\">(" + (String)data.get(2) + ") " + (String)data.get(3) + "'s</span> Submission for Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> edited.<br><br>";
			
			String[] toEmails = (String[])data.get(4);
			String[] points = (String[])data.get(5);
			String[] justifications = (String[])data.get(6);
			String[] comments = (String[])data.get(7);
			
			for (int i = 0; i < toEmails.length; i++){
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
			
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>"; 
		} catch (IndexOutOfBoundsException e) {
			message = "<span class=\"color_red\">Varlable index exceeded in " + servletName + ": " + action + ".</span>";    
		}
		
		return message;
	}
}
