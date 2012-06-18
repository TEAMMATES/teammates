package teammates.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.jsp.CoordCourseStudentEditHelper;
import teammates.jsp.Helper;

@SuppressWarnings("serial")
/**
 * Servlet to handle Coordinator course student edit page.
 * @author Aldrian Obaja
 *
 */
public class CoordCourseStudentEditServlet extends ActionServlet<CoordCourseStudentEditHelper> {
	
	private static final String DISPLAY_URL = Common.JSP_COORD_COURSE_STUDENT_EDIT;

	@Override
	protected CoordCourseStudentEditHelper instantiateHelper() {
		return new CoordCourseStudentEditHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseStudentEditHelper helper)
			throws IOException {
		if(!helper.user.isCoord && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, CoordCourseStudentEditHelper helper) throws EntityDoesNotExistException{
		// Get parameters
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		boolean submit = (req.getParameter("submit")!=null);
		String studentName = req.getParameter(Common.PARAM_STUDENT_NAME);
		String teamName = req.getParameter(Common.PARAM_TEAM_NAME);
		String comments = req.getParameter(Common.PARAM_COMMENTS);
		
		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);
		
		if(submit){
			helper.student.name = studentName;
			helper.student.team = teamName;
			helper.student.comments = comments;
			try {
				helper.server.editStudent(studentEmail, helper.student);
				helper.statusMessage = Common.MESSAGE_STUDENT_EDITED;
				helper.nextUrl = helper.getCoordCourseDetailsLink(courseID);
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
				return;
			}
		}
	}

	@Override
	protected void doCreateResponse(HttpServletRequest req,
			HttpServletResponse resp, CoordCourseStudentEditHelper helper)
			throws ServletException, IOException {
		
		if(helper.nextUrl==null) helper.nextUrl = DISPLAY_URL;
		
		helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_STATUS_MESSAGE, helper.statusMessage);
		if(helper.error)
			helper.nextUrl = Helper.addParam(helper.nextUrl, Common.PARAM_ERROR, ""+helper.error);
		
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
