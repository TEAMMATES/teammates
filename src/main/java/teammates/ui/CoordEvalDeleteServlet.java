package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Evaluation action
 */
public class CoordEvalDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.deleteEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_DELETED;
		if(helper.redirectUrl==null) helper.redirectUrl = Common.PAGE_COORD_EVAL;
	}

}
