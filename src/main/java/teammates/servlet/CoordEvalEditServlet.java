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
		if (!helper.user.isCoord && !helper.user.isAdmin) {
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordEvalEditHelper helper)
			throws EntityDoesNotExistException {

		EvaluationData newEval = CoordEvalServlet.extractEvaluationData(req);

		if (newEval.course == null && newEval.name == null) {
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}
		
		CourseData course = helper.server.getCourse(newEval.course);
		if (course != null && !course.coord.equals(helper.userId)) {
			helper.statusMessage = "You are not authorized to edit the evaluation "
					+ Helper.escapeForHTML(newEval.name)
					+ " in course "
					+ newEval.course;
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}

		// decide whether this is a request to edit or a submission of edited
		// values
//		boolean isSubmit = newEval.startTime != null 
//				|| newEval.endTime != null
//				|| newEval.instructions != null;
		//TODO: implement a better check for isSubmit
		
		boolean isSubmit = isPost;

		if (isSubmit) {
			helper.submittedEval = newEval;
			try {
				helper.server.editEvaluation(newEval);
				helper.statusMessage = Common.MESSAGE_EVALUATION_EDITED;
				helper.redirectUrl = Common.PAGE_COORD_EVAL;
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
			}
		} else {
			helper.submittedEval = helper.server.getEvaluation(newEval.course,
					newEval.name);
			if (helper.submittedEval == null) {
				helper.redirectUrl = Common.PAGE_COORD_EVAL;
				return;
			}
		}

	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_COORD_EVAL_EDIT;
	}
}
