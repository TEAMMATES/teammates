package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.api.JoinCourseException;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Student Join Course action
 * @author Aldrian Obaja
 *
 */
public class StudentCourseJoinServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper){
		// Get parameters
		String regKey = req.getParameter(Common.PARAM_JOIN_COURSE);
		if(regKey==null) return;
		
		// Process action
		try {
			helper.server.joinCourse(helper.userId, regKey);
		} catch (JoinCourseException e) {
			helper.statusMessage = Helper.escapeHTML(e.getMessage());
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.PAGE_STUDENT_HOME;
	}
}
