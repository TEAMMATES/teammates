package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

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
