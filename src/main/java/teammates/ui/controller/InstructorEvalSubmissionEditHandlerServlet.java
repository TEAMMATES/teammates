package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;

@SuppressWarnings("serial")
public class InstructorEvalSubmissionEditHandlerServlet extends EvalSubmissionEditHandlerServlet {

	@Override
	protected String getSuccessMessage(HttpServletRequest req, Helper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String fromEmail = req.getParameter(Common.PARAM_FROM_EMAIL);
		StudentData student = helper.server.getStudent(courseID, fromEmail);
		String fromName;
		if(student==null) fromName = fromEmail;
		else fromName = student.name;
		return String.format(Common.MESSAGE_INSTRUCTOR_EVALUATION_SUBMISSION_RECEIVED,
				EvalSubmissionEditHelper.escapeForHTML(fromName),
				EvalSubmissionEditHelper.escapeForHTML(evalName), courseID);
	}


	@Override
	protected String getSuccessUrl() {
		return Common.JSP_SHOW_MESSAGE;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
	}



	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.STUDENT_EVAL_EDIT_HANDLER_SERVLET_EDIT_SUBMISSION)){
			message = generateEditSubmissionMessage(servletName, action, data);
		} else {
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
