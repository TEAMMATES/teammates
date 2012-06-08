package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Evaluation action
 * @author Aldrian Obaja
 *
 */
public class CoordEvalDeleteServlet extends ActionServlet {
	
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
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		System.out.println(evalName);
		
		// Process action
		helper.server.deleteEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_DELETED;
		
		// Send response
		if(helper.nextUrl==null) helper.nextUrl = "/page/coordEval";
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
		
		resp.sendRedirect(helper.nextUrl);
	}
}
