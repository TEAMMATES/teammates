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
			resp.sendRedirect("unauthorized.jsp");
			return;
		}
		helper.coordID = helper.userId;
		
		// Process data
		HashMap<String, CourseData> courses = helper.server.getCourseDetailsListForCoord(helper.coordID);
		helper.summary = courses.values().toArray(new CourseData[] {});

		// Goto display page
		if(helper.nextUrl==null) helper.nextUrl = "coordHome.jsp";
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.userId);
		
		if(helper.nextUrl.startsWith("coordHome.jsp")){
			req.setAttribute("helper", helper);
			req.getRequestDispatcher(helper.nextUrl).forward(req, resp);
		} else {
			resp.sendRedirect(helper.nextUrl);
		}
	}
}
