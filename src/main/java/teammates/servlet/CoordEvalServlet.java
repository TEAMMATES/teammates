package teammates.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.jsp.CoordEvalHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Evaluation and Display Evaluations action
 */
public class CoordEvalServlet extends ActionServlet<CoordEvalHelper> {

	@Override
	protected CoordEvalHelper instantiateHelper() {
		return new CoordEvalHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordEvalHelper helper)
			throws IOException {
		if (!helper.user.isCoord && !helper.user.isAdmin) {
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordEvalHelper helper)
			throws EntityDoesNotExistException {

		EvaluationData newEval = extractEvaluationData(req);

		//@formatter:off
		boolean isAddEvaluation = 
				   newEval.course != null
				|| newEval.name != null 
				|| newEval.startTime != null 
				|| newEval.endTime != null;
		//@formatter:on

		if (isAddEvaluation) {
			helper.submittedEval = newEval;
		} else {
			helper.submittedEval = null;
		}

		try {
			if (isAddEvaluation) {
				helper.server.createEvaluation(newEval);
				helper.statusMessage = Common.MESSAGE_EVALUATION_ADDED;
				helper.submittedEval = null;
			}
		} catch (EntityAlreadyExistsException e) {
			helper.statusMessage = Common.MESSAGE_EVALUATION_EXISTS;
			helper.error = true;
		} catch (InvalidParametersException e) {
			// This will cover conditions such as start/end date is invalid
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}

		HashMap<String, CourseData> summary = helper.server
				.getCourseListForCoord(helper.userId);
		helper.courses = new ArrayList<CourseData>(summary.values());
		sortCourses(helper.courses);

		helper.evaluations = helper.server
				.getEvaluationsListForCoord(helper.userId);
		sortEvaluationsByDeadline(helper.evaluations);

		//TODO: extract below logic and unit test it
		String additionalMessage = null;
		if (helper.courses.size() == 0 && !helper.error) {
			additionalMessage = Common.MESSAGE_COURSE_EMPTY_IN_EVALUATION;
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

	private boolean noEvaluationsVisibleDueToEventualConsistency(CoordEvalHelper helper) {
		return helper.statusMessage != null
				&& helper.statusMessage.equals(Common.MESSAGE_EVALUATION_ADDED)
				&& helper.evaluations.size()==0;
	}

	private EvaluationData extractEvaluationData(HttpServletRequest req) {
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

	private Date combineDateTime(String inputDate, String inputTime) {
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
		return Common.JSP_COORD_EVAL;
	}
}
