package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.api.JoinCourseException;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Student Join Course action
 * @author Aldrian Obaja
 *
 */
public class StudentCourseJoinServlet extends ActionServlet<Helper> {
	
	private static final String DISPLAY_URL = Common.PAGE_STUDENT_HOME;

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws IOException {
		if(!helper.user.isStudent){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper){
		// Get parameters
		String regKey = req.getParameter(Common.PARAM_JOIN_COURSE);
		if(regKey==null) return;
		
		// Process action
		try {
			helper.server.joinCourse(helper.user.id, regKey);
		} catch (JoinCourseException e) {
			helper.statusMessage = Helper.escapeHTML(e.getMessage());
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws ServletException,
			IOException {
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_STATUS_MESSAGE, helper.statusMessage);
		if(helper.error)
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_ERROR, ""+helper.error);
		
		resp.sendRedirect(helper.nextUrl);
	}
}
