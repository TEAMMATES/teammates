package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.UserType;
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
		String action = Common.INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD;
		boolean isAddEvaluation = isPost;

		if (!isAddEvaluation) {
			helper.newEvaluationToBeCreated = null;
			helper.courseIdForNewEvaluation = req.getParameter(Common.PARAM_COURSE_ID);
		} else {
			helper.newEvaluationToBeCreated = extractEvaluationData(req);
			createEvaluation(helper);
			action = Common.INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION;
		}

		populateEvaluationList(helper);
		
		setStatusMessage(helper);
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }    
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SERVLET, action,
        		true, helper, url, null);
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

	private void createEvaluation(InstructorEvalHelper helper) {
		try {
			helper.server.createEvaluation(helper.newEvaluationToBeCreated);
			helper.statusMessage = Common.MESSAGE_EVALUATION_ADDED;
			helper.formEvaluation = helper.newEvaluationToBeCreated;
			helper.newEvaluationToBeCreated = null;
		} catch (EntityAlreadyExistsException e) {
			helper.statusMessage = Common.MESSAGE_EVALUATION_EXISTS;
			helper.error = true;
		} catch (InvalidParametersException e) {
			// This will cover conditions such as start/end date is invalid
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorEvalHelper h = (InstructorEvalHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD){
			try {
				params = "instructorEval Page Load<br>";
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION){
			try {
				EvaluationData eval = h.formEvaluation;
				params = "New Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.course + "]</span> created.<br>" +
						"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
						"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
						"<span class=\"bold\">Instructions:</span> " + eval.instructions;
			} catch (NullPointerException e){
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
}
