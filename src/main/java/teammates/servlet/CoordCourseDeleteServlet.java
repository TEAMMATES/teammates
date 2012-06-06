package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Course action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseDeleteServlet extends ActionServlet {
	
	protected void doPostAction(HttpServletRequest req, HttpServletResponse resp, Helper help)
			throws IOException, ServletException {
		Helper helper = help;
		
		// Authenticate user
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect("unauthorized.html");
			return;
		}
		
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		// Process action
		helper.server.deleteCourse(courseID);
		helper.statusMessage = Common.MESSAGE_COURSE_DELETED;
		
		// Send response
		if(helper.nextUrl==null) helper.nextUrl = "coordCourse";
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
		
		resp.sendRedirect(helper.nextUrl);
	}
}
