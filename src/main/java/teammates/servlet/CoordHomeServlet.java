package teammates.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.datatransfer.CourseData;
import teammates.jsp.CoordHomeHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Home actions
 * @author Aldrian Obaja
 *
 */
public class CoordHomeServlet extends ActionServlet<CoordHomeHelper> {
	
	private static final String DISPLAY_URL = Common.JSP_COORD_HOME;

	@Override
	protected CoordHomeHelper instantiateHelper() {
		return new CoordHomeHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordHomeHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordHomeHelper helper) throws EntityDoesNotExistException{
		HashMap<String, CourseData> courses = helper.server.getCourseDetailsListForCoord(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		sortCourses(helper.courses);
		for(CourseData course: helper.courses){
			sortEvaluationsByDeadline(course.evaluations);
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, CoordHomeHelper helper)
			throws ServletException, IOException {

		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		
		if(helper.nextUrl.startsWith(DISPLAY_URL)){
			// Goto display page
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			// Goto next page
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
			resp.sendRedirect(helper.nextUrl);
		}
	}
}
