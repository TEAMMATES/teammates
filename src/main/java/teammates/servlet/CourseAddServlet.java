package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.InvalidParametersException;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Course action
 * @author Aldrian Obaja
 *
 */
public class CourseAddServlet extends ActionServlet {
	// Specific variables
	private String coordID;

	// Specific parameters
	private String courseID;
	private String courseName;
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		
		// Don't forget this one!
		super.doPost(req, resp);
		
		// Authenticate user
		if(!user.isCoord() && !user.isAdmin()){
			resp.sendRedirect("coordCourse.jsp");
			return;
		}
		coordID = userID;
		
		// Get parameters
		courseID = req.getParameter(Common.PARAM_COURSE_ID);
		courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		
		// Process action
		if(courseID!=null && courseName!=null){
			try {
				server.createCourse(coordID, courseID, courseName);
				statusMessage = Common.MESSAGE_COURSE_ADDED;
			} catch (EntityAlreadyExistsException e) {
				statusMessage = Common.MESSAGE_COURSE_EXISTS;
				error = true;
			} catch (InvalidParametersException e) {
				statusMessage = e.getMessage();
				error = true;
			}
		}
		
		// Send response
		if(nextUrl==null) nextUrl = "coordCourse.jsp";
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_COURSE_ID, courseID);
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_COURSE_NAME, courseName);
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_STATUS_MESSAGE,statusMessage);
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_ERROR,error+"");
		nextUrl = Helper.addParam(nextUrl, Common.PARAM_USER_ID, requestedUser);
		
		resp.sendRedirect(nextUrl);
	}
}
