package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.jsp.Helper;

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
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		CourseData course = helper.server.getCourse(courseID);
		if(course!=null && !course.coord.equals(helper.userId)){
			helper.statusMessage = "You are not authorized to remind students for the evaluation " +
					Helper.escapeForHTML(evalName)+" in course "+courseID;
			helper.redirectUrl = Common.PAGE_COORD_EVAL;
			return;
		}
		
		helper.server.sendReminderForEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_REMINDERSSENT;
		helper.redirectUrl = Common.PAGE_COORD_EVAL;
	}

	@Override
	protected String getDefaultForwardUrl() {
		// Not used
		return "";
	}
}
