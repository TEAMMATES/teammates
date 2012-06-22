package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.jsp.CoordEvalEditHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Edit Evaluation action
 * @author Aldrian Obaja
 *
 */
public class CoordEvalEditServlet extends ActionServlet<CoordEvalEditHelper> {

	@Override
	protected CoordEvalEditHelper instantiateHelper() {
		return new CoordEvalEditHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordEvalEditHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordEvalEditHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null && evalName==null){
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}
		CourseData course = helper.server.getCourse(courseID);
		if(course!=null && !course.coord.equals(helper.userId)){
			helper.statusMessage = "You are not authorized to edit the evaluation " +
					Helper.escapeForHTML(evalName)+" in course "+courseID;
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}
		
		boolean isSubmit = false; // Flag whether this request is an edit evaluation request
		EvaluationData newEval = new EvaluationData();
		newEval.course = courseID;
		newEval.name = evalName;
		newEval.p2pEnabled = "true".equalsIgnoreCase(req.getParameter(Common.PARAM_EVALUATION_COMMENTSENABLED));
		String startDate = req.getParameter(Common.PARAM_EVALUATION_START);
		String paramStartTime = req.getParameter(Common.PARAM_EVALUATION_STARTTIME);
		int startTime = 0;
		if(paramStartTime!=null){
			isSubmit = true;
			startTime = Integer.parseInt(paramStartTime)*100;
		}
		newEval.startTime = Common.convertToDate(startDate, startTime);
		if(newEval.startTime!=null) isSubmit = true;
		String endDate = req.getParameter(Common.PARAM_EVALUATION_DEADLINE);
		if(endDate!=null) isSubmit = true;
		String paramEndTime = req.getParameter(Common.PARAM_EVALUATION_DEADLINETIME);
		int endTime = 0;
		if(paramEndTime!=null){
			isSubmit = true;
			endTime = Integer.parseInt(paramEndTime)*100;
		}
		newEval.endTime = Common.convertToDate(endDate, endTime);
		if(newEval.endTime!=null) isSubmit = true;
		String paramTimeZone = req.getParameter(Common.PARAM_EVALUATION_TIMEZONE);
		if(paramTimeZone!=null){
			isSubmit = true;
			newEval.timeZone = Double.parseDouble(paramTimeZone);
		}
		String paramGracePeriod = req.getParameter(Common.PARAM_EVALUATION_GRACEPERIOD);
		if(paramGracePeriod!=null){
			isSubmit = true;
			newEval.gracePeriod = Integer.parseInt(paramGracePeriod);
		}
		newEval.instructions = req.getParameter(Common.PARAM_EVALUATION_INSTRUCTIONS);
		if(newEval.instructions!=null){
			isSubmit = true;
		}
		if(isSubmit){
			helper.submittedEval = newEval;
		} else { 
			helper.submittedEval = helper.server.getEvaluation(courseID, evalName);
			if(helper.submittedEval==null){
				helper.redirectUrl = Common.PAGE_COORD_EVAL;
				return;
			}
		}
		
		// Process action
		try {
			if(isSubmit){
				helper.server.editEvaluation(newEval);
				helper.statusMessage = Common.MESSAGE_EVALUATION_EDITED;
				helper.redirectUrl = Common.PAGE_COORD_EVAL;
			}
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			// TODO When is the case where no teams yet?
			// TODO When is the case where evaluation name is invalid (too long or non-alphanumeric)?
			// TODO When is the case where start/end date is invalid?
			helper.error = true;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_EVAL_EDIT;
	}
}
