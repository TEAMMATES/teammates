package teammates.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.jsp.CoordEvalHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Evaluation and Display Evaluations action
 * @author Aldrian Obaja
 *
 */
public class CoordEvalServlet extends ActionServlet {
	
	Logger log = Common.getLogger();
	
	protected void doPostAction(HttpServletRequest req, HttpServletResponse resp, Helper help)
			throws IOException, ServletException {
		CoordEvalHelper helper = new CoordEvalHelper(help);
		
		// Authenticate user
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return;
		}
		helper.coordID = helper.userId;
		
		// Get parameters
		boolean isSubmit = false; // Flag whether this request is an add evaluation request
		EvaluationData newEval = new EvaluationData();
		newEval.course = req.getParameter(Common.PARAM_COURSE_ID);
		if(newEval.course!=null) isSubmit = true;
		newEval.name = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(newEval.name!=null) isSubmit = true;
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
			helper.submittedEval = null;
		}
		
		// Process action
		try {
			if(isSubmit){
				helper.server.createEvaluation(newEval);
				helper.statusMessage = Common.MESSAGE_EVALUATION_ADDED;
			}
		} catch (EntityAlreadyExistsException e) {
			helper.statusMessage = Common.MESSAGE_EVALUATION_EXISTS;
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			// TODO When is the case where no teams yet?
			// TODO When is the case where evaluation name is invalid (too long or non-alphanumeric)?
			// TODO When is the case where start/end date is invalid?
			helper.error = true;
		}
		
		// Process data for display
		//TODO: is to better if APIServlet returned an ArrayList?
		HashMap<String, CourseData> summary;
		try {
			summary = helper.server.getCourseListForCoord(helper.coordID);
		} catch (EntityDoesNotExistException e) {
			//TODO: handle this in a better way, probably redirect to error page
			summary = new HashMap<String, CourseData>();
			log.severe("Unexpected exception :"+e.getMessage());
		}
		helper.courses = summary.values().toArray(new CourseData[]{});
		Arrays.sort(helper.courses,new Comparator<CourseData>(){
			public int compare(CourseData obj1, CourseData obj2){
				return obj1.id.compareTo(obj2.id);
			}
		});
		helper.evaluations = helper.server.getEvaluationsListForCoord(helper.coordID);
		Collections.sort(helper.evaluations, new Comparator<EvaluationData>(){
			public int compare(EvaluationData obj1, EvaluationData obj2){
				int result = obj1.course.compareTo(obj2.course);
				if(result==0) result = obj1.name.compareTo(obj2.name);
				return result;
			}
		});
		
		if(helper.nextUrl==null) helper.nextUrl = "/coordEval.jsp";
		
		if(helper.nextUrl.startsWith("/coordEval.jsp")){
			// Goto display page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.userId);
			resp.sendRedirect(helper.nextUrl);
		}
	}
}
