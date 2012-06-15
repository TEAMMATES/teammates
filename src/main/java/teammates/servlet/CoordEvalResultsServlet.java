package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.TeamData;
import teammates.jsp.CoordEvalResultsHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 * @author Aldrian Obaja
 *
 */
public class CoordEvalResultsServlet extends ActionServlet<CoordEvalResultsHelper> {
	
	private static final String DISPLAY_URL = Common.JSP_COORD_EVAL_RESULTS;

	@Override
	protected CoordEvalResultsHelper instantiateHelper() {
		return new CoordEvalResultsHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordEvalResultsHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordEvalResultsHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		// Process action
		if(courseID!=null && evalName!=null){
			helper.evaluation = helper.server.getEvaluationResult(courseID, evalName);
			long start = System.currentTimeMillis();
			sortTeams(helper.evaluation.teams);
			for(TeamData team: helper.evaluation.teams){
				team.sortByStudentNameAscending();
				for(StudentData student: team.students){
					sortSubmissionsByFeedback(student.result.incoming);
					sortSubmissionsByReviewee(student.result.outgoing);
				}
			}
			log.fine("Time to sort evaluation, teams, students, and results: "+(System.currentTimeMillis()-start)+" ms");
		} else { // Incomplete request, just go back to Evaluations Page
			helper.nextUrl = Common.PAGE_COORD_EVAL;
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, CoordEvalResultsHelper helper)
			throws ServletException, IOException {
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		
		if(helper.nextUrl.startsWith(DISPLAY_URL)){
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
