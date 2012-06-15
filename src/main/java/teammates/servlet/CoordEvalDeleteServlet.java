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
public class CoordEvalDeleteServlet extends ActionServlet<Helper> {
	
	private static final String DISPLAY_URL = Common.PAGE_COORD_EVAL;

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		System.out.println(evalName);
		
		// Process action
		helper.server.deleteEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_DELETED;
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, Helper helper) throws ServletException,
			IOException {
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_USER_ID, helper.requestedUser);
		
		resp.sendRedirect(helper.nextUrl);
	}
}
