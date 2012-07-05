package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.logic.api.EntityDoesNotExistException;
import teammates.logic.api.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Unpublish evaluation action
 */
public class CoordEvalUnpublishServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}


	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException, InvalidParametersException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.unpublishEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_UNPUBLISHED;
	}

}
