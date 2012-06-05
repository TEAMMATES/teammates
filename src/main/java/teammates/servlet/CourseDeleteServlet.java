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
public class CourseDeleteServlet extends ActionServlet {
	// Specific variables

	// Specific parameters
	private String courseID;
	
	public void doPostAction(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		// Authenticate user
		if(!user.isCoord() && !user.isAdmin()){
			resp.sendRedirect("coordCourse.jsp");
			return;
		}
		
		// Get parameters
		courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		// Process action
		server.deleteCourse(courseID);
		
		// Send response
		if(nextUrl==null) nextUrl = "coordCourse.jsp";
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_USER_ID, requestedUser);
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_STATUS_MESSAGE, Common.MESSAGE_COURSE_DELETED);
		
		resp.sendRedirect(nextUrl);
	}
}
