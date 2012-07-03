package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Remind students for evaluation action
 */
public class CoordEvalRemindServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}


	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.sendReminderForEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_REMINDERSSENT;
		helper.redirectUrl = Common.PAGE_COORD_EVAL;
	}

}
