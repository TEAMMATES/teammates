package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Course action
 */
public class CoordCourseDeleteServlet extends ActionServlet<Helper> {

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
		
		helper.server.deleteCourse(courseID);
		helper.statusMessage = Common.MESSAGE_COURSE_DELETED;
		if(helper.redirectUrl==null) {
			helper.redirectUrl = Common.PAGE_COORD_COURSE;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		// Not used
		return "";
	}
}
