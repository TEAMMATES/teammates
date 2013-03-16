package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Evaluation and Display Evaluations action
 */
public class InstructorEvalServlet extends ActionServlet<InstructorEvalHelper> {

	@Override
	protected InstructorEvalHelper instantiateHelper() {
		return new InstructorEvalHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalHelper helper)
			throws EntityDoesNotExistException {
		String url = getRequestedURL(req);
        
		boolean isAddEvaluation = isPost;

		if (!isAddEvaluation) {
			helper.newEvaluationToBeCreated = null;
			helper.courseIdForNewEvaluation = req.getParameter(Common.PARAM_COURSE_ID);
			
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SERVLET, Common.INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD,
	        		true, helper, url, null);
		} else {
			try{
				helper.newEvaluationToBeCreated = extractEvaluationData(req);
				helper.server.createEvaluation(helper.newEvaluationToBeCreated);
				helper.statusMessage = Common.MESSAGE_EVALUATION_ADDED;
				
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(helper.newEvaluationToBeCreated);
				helper.newEvaluationToBeCreated = null;
				activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SERVLET, Common.INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION,
		        		true, helper, url, data);
				
			} catch (EntityAlreadyExistsException e) {
				helper.statusMessage = Common.MESSAGE_EVALUATION_EXISTS;
				helper.error = true;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add(helper.statusMessage);		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
		        
			} catch (InvalidParametersException e) {
				// This will cover conditions such as start/end date is invalid
				helper.statusMessage = e.getMessage();
				helper.error = true;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add(helper.statusMessage);	                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
			}
		}
		
		populateEvaluationList(helper);
		setStatusMessage(helper);
		
	}

	//TODO: unit test this
	private void populateEvaluationList(InstructorEvalHelper helper)
			throws EntityDoesNotExistException {
		HashMap<String, CourseData> summary = helper.server
				.getCourseListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseData>(summary.values());
		sortCourses(helper.courses);

		helper.evaluations = helper.server
				.getEvaluationsListForInstructor(helper.userId);
		sortEvaluationsByDeadline(helper.evaluations);
	}

	//TODO: unit test this
	private void setStatusMessage(InstructorEvalHelper helper) {
		String additionalMessage = null;
		if (helper.courses.size() == 0 && !helper.error) {
			additionalMessage = Common.MESSAGE_COURSE_EMPTY_IN_EVALUATION.replace("${user}", "?user="+helper.userId);
		} else	if (helper.evaluations.size() == 0 && !helper.error
				&& !noEvaluationsVisibleDueToEventualConsistency(helper)) {
			additionalMessage = Common.MESSAGE_EVALUATION_EMPTY;
		}
		
		if (additionalMessage != null) {
			if (helper.statusMessage == null) {
				helper.statusMessage = "";
			} else {
				helper.statusMessage += "<br />";
			}
			helper.statusMessage += additionalMessage;
		}
	}

	private boolean noEvaluationsVisibleDueToEventualConsistency(InstructorEvalHelper helper) {
		return helper.statusMessage != null
				&& helper.statusMessage.equals(Common.MESSAGE_EVALUATION_ADDED)
				&& helper.evaluations.size()==0;
	}

	public static EvaluationData extractEvaluationData(HttpServletRequest req) {
		EvaluationData newEval = new EvaluationData();
		newEval.course = req.getParameter(Common.PARAM_COURSE_ID);
		newEval.name = req.getParameter(Common.PARAM_EVALUATION_NAME);
		newEval.p2pEnabled = Boolean.parseBoolean(req
				.getParameter(Common.PARAM_EVALUATION_COMMENTSENABLED));

		newEval.startTime = combineDateTime(
				req.getParameter(Common.PARAM_EVALUATION_START),
				req.getParameter(Common.PARAM_EVALUATION_STARTTIME));

		newEval.endTime = combineDateTime(
				req.getParameter(Common.PARAM_EVALUATION_DEADLINE),
				req.getParameter(Common.PARAM_EVALUATION_DEADLINETIME));

		String paramTimeZone = req
				.getParameter(Common.PARAM_EVALUATION_TIMEZONE);
		if (paramTimeZone != null) {
			newEval.timeZone = Double.parseDouble(paramTimeZone);
		}

		String paramGracePeriod = req
				.getParameter(Common.PARAM_EVALUATION_GRACEPERIOD);
		if (paramGracePeriod != null) {
			newEval.gracePeriod = Integer.parseInt(paramGracePeriod);
		}

		newEval.instructions = req
				.getParameter(Common.PARAM_EVALUATION_INSTRUCTIONS);

		return newEval;
	}

	public static Date combineDateTime(String inputDate, String inputTime) {
		if (inputDate == null || inputTime == null) {
			return null;
		}

		int inputTimeInt = 0;
		if (inputTime != null) {
			inputTimeInt = Integer.parseInt(inputTime) * 100;
		}
		return Common.convertToDate(inputDate, inputTimeInt);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION)){
			message = generateNewEvaluationMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorEval Page Load<br>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	
	private String generateNewEvaluationMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			EvaluationData eval = (EvaluationData)data.get(0);
			message = "New Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.course + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
					"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
					"<span class=\"bold\">Instructions:</span> " + eval.instructions;
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
