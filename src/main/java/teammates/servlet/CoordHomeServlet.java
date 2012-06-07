package teammates.servlet;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.jsp.CoordHomeHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Home actions
 * @author Aldrian Obaja
 *
 */
public class CoordHomeServlet extends ActionServlet {
	
	protected void doPostAction(HttpServletRequest req, HttpServletResponse resp, Helper help)
			throws IOException, ServletException {
		CoordHomeHelper helper = new CoordHomeHelper(help);
		
		// Authenticate user
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return;
		}
		helper.coordID = helper.userId;
		
		// Process data
		HashMap<String, CourseData> courses = helper.server.getCourseDetailsListForCoord(helper.coordID);
		helper.summary = courses.values().toArray(new CourseData[] {});

		if(helper.nextUrl==null) helper.nextUrl = "/coordHome.jsp";
		
		if(helper.nextUrl.startsWith("/coordHome.jsp")){
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
