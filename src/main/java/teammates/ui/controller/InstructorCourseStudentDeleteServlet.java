package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Course action
 * @author Aldrian Obaja
 *
 */
public class InstructorCourseStudentDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.server.deleteStudent(courseID, studentEmail);
		helper.statusMessage = Common.MESSAGE_STUDENT_DELETED;
		helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName,
			String action, boolean toShow, Helper helper) {
		// TODO Auto-generated method stub
		return null;
	}

}
