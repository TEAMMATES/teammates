package teammates.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.jsp.CoordCourseHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Add Course and Display Courses action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseServlet extends ActionServlet {
	
	Logger log = Common.getLogger();
	
	protected void doPostAction(HttpServletRequest req, HttpServletResponse resp, Helper help)
			throws IOException, ServletException {
		CoordCourseHelper helper = new CoordCourseHelper(help);
		
		// Authenticate user
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return;
		}
		helper.coordID = helper.userId;
		
		// Get parameters
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		
		// Process action
		if(helper.courseID!=null && helper.courseName!=null){
			try {
				helper.server.createCourse(helper.coordID, helper.courseID, helper.courseName);
				helper.statusMessage = Common.MESSAGE_COURSE_ADDED;
			} catch (EntityAlreadyExistsException e) {
				helper.statusMessage = Common.MESSAGE_COURSE_EXISTS;
				helper.error = true;
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
			}
		}
		
		// Process data for display
		//TODO: is to better if APIServlet returned an ArrayList?
		HashMap<String, CourseData> courses;
		try {
			courses= helper.server.getCourseListForCoord(helper.coordID);
		} catch (EntityDoesNotExistException e) {
			//TODO: handle this in a better way, probably redirect to error page
			log.severe("unexpected exception "+e.getMessage());
			courses = new HashMap<String, CourseData>();
		}
		helper.summary = courses.values().toArray(new CourseData[]{});
		Arrays.sort(helper.summary,new Comparator<CourseData>(){
			public int compare(CourseData obj1, CourseData obj2){
				return obj1.id.compareTo(obj2.id);
			}
		});
		
		if(helper.nextUrl==null) helper.nextUrl = "/coordCourse.jsp";
		
		if(helper.nextUrl.startsWith("/coordCourse.jsp")){
			// Goto display page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.userId);
			resp.sendRedirect(helper.nextUrl);
		}
	}
}
