package teammates.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

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
public class CoordCourseServlet extends ActionServlet<CoordCourseHelper> {
	
	private static final String DISPLAY_URL = "/jsp/coordCourse.jsp";

	@Override
	protected CoordCourseHelper instantiateHelper() {
		return new CoordCourseHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		helper.courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.courseName = req.getParameter(Common.PARAM_COURSE_NAME);
		
		// Process action
		if(helper.courseID!=null && helper.courseName!=null){
			try {
				helper.server.createCourse(helper.userId, helper.courseID, helper.courseName);
				helper.statusMessage = Common.MESSAGE_COURSE_ADDED;
			} catch (EntityAlreadyExistsException e) {
				helper.statusMessage = Common.MESSAGE_COURSE_EXISTS;
				helper.error = true;
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
			}
		}
		
		sortCourses(helper);
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseHelper helper)
			throws ServletException, IOException {
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		
		if(helper.nextUrl.startsWith(DISPLAY_URL)){
			// Goto display page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.userId);
			resp.sendRedirect(helper.nextUrl);
		}
	}

	private void sortCourses(CoordCourseHelper helper) throws EntityDoesNotExistException{
		HashMap<String, CourseData> courses = helper.server.getCourseListForCoord(helper.userId);
		helper.summary = courses.values().toArray(new CourseData[]{});
		Arrays.sort(helper.summary,new Comparator<CourseData>(){
			public int compare(CourseData obj1, CourseData obj2){
				return obj1.id.compareTo(obj2.id);
			}
		});
		if(helper.summary.length==0 && !helper.error){
			helper.statusMessage = Common.MESSAGE_COURSE_EMPTY;
		}
	}
}
