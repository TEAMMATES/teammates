package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.TeamData;
import teammates.jsp.CoordEvalResultsHelper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 */
public class CoordEvalResultsServlet extends
		ActionServlet<CoordEvalResultsHelper> {

	@Override
	protected CoordEvalResultsHelper instantiateHelper() {
		return new CoordEvalResultsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			CoordEvalResultsHelper helper) throws EntityDoesNotExistException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);

		if (courseID != null && evalName != null) {
			helper.evaluation = helper.server.getEvaluationResult(courseID,
					evalName);
			long start = System.currentTimeMillis();
			sortTeams(helper.evaluation.teams);
			for (TeamData team : helper.evaluation.teams) {
				team.sortByStudentNameAscending();
				for (StudentData student : team.students) {
					sortSubmissionsByFeedback(student.result.incoming);
					sortSubmissionsByReviewee(student.result.outgoing);
				}
			}
			log.fine("Time to sort evaluation, teams, students, and results: "
					+ (System.currentTimeMillis() - start) + " ms");
			helper.statusMessage = Common.MESSAGE_LOADING;
		} else { // Incomplete request, just go back to Evaluations Page
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_EVAL_RESULTS;
	}
}
