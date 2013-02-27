package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamData;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 */
public class InstructorEvalResultsServlet extends
		ActionServlet<InstructorEvalResultsHelper> {

	@Override
	protected InstructorEvalResultsHelper instantiateHelper() {
		return new InstructorEvalResultsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			InstructorEvalResultsHelper helper) throws EntityDoesNotExistException {
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
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_RESULTS;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}
}
