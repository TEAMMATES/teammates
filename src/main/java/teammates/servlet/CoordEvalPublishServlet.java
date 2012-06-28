package teammates.servlet;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Publish evaluation action
 */
public class CoordEvalPublishServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}


	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.publishEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_PUBLISHED;
	}
	


}
