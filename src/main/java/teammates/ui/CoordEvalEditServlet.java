package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.EvaluationData;

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
	protected void doAction(HttpServletRequest req, CoordEvalEditHelper helper)
			throws EntityDoesNotExistException {

		EvaluationData newEval = CoordEvalServlet.extractEvaluationData(req);

		if (newEval.course == null && newEval.name == null) {
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}
		
		boolean isSubmit = isPost;

		if (isSubmit) {
			helper.submittedEval = newEval;
			try {
				helper.server.editEvaluation(newEval.course, newEval.name, newEval.instructions, newEval.startTime,
						newEval.endTime, newEval.timeZone, newEval.gracePeriod, newEval.p2pEnabled);
				helper.statusMessage = Common.MESSAGE_EVALUATION_EDITED;
				helper.redirectUrl = Common.PAGE_COORD_EVAL;
			} catch (InvalidParametersException ex) {
				helper.statusMessage = ex.getMessage();
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
