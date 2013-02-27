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
public class InstructorEvalEditServlet extends ActionServlet<InstructorEvalEditHelper> {

	@Override
	protected InstructorEvalEditHelper instantiateHelper() {
		return new InstructorEvalEditHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalEditHelper helper)
			throws EntityDoesNotExistException {

		EvaluationData newEval = InstructorEvalServlet.extractEvaluationData(req);

		if (newEval.course == null && newEval.name == null) {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			return;
		}
		
		boolean isSubmit = isPost;

		if (isSubmit) {
			helper.newEvaluationToBeCreated = newEval;
			try {
				helper.server.editEvaluation(newEval.course, newEval.name, newEval.instructions, newEval.startTime,
						newEval.endTime, newEval.timeZone, newEval.gracePeriod, newEval.p2pEnabled);
				helper.statusMessage = Common.MESSAGE_EVALUATION_EDITED;
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			} catch (InvalidParametersException ex) {
				helper.statusMessage = ex.getMessage();
				helper.error = true;
			}
		} else {
			helper.newEvaluationToBeCreated = helper.server.getEvaluation(newEval.course,
					newEval.name);
			if (helper.newEvaluationToBeCreated == null) {
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
				return;
			}
		}

	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_EDIT;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}
}
