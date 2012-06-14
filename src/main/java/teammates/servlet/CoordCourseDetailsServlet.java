package teammates.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.StudentData;
import teammates.jsp.CoordCourseDetailsHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator View Course Details action
 * @author Aldrian Obaja
 *
 */
public class CoordCourseDetailsServlet extends ActionServlet<CoordCourseDetailsHelper> {
	
	private static final String DISPLAY_URL = "/coordCourseDetails.jsp";

	@Override
	protected CoordCourseDetailsHelper instantiateHelper() {
		return new CoordCourseDetailsHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseDetailsHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseDetailsHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		// Process action
		if(courseID!=null){
			helper.course = helper.server.getCourseDetails(courseID);
			helper.students = helper.server.getStudentListForCourse(courseID);
		} else {
			helper.nextUrl = Common.JSP_COORD_COURSE;
		}
		
		sortStudents(helper.students);
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseDetailsHelper helper)
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
	
	private void sortStudents(List<StudentData> students){
		Collections.sort(students,new Comparator<StudentData>(){
			public int compare(StudentData s1, StudentData s2){
				return s1.name.compareTo(s2.name);
			}
		});
	}
}
