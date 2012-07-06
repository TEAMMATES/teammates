package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Student Join Course action
 */
public class StudentCourseJoinServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper){
		helper.redirectUrl = Common.PAGE_STUDENT_HOME;
		
		String regKey = req.getParameter(Common.PARAM_REGKEY);
		if(regKey==null) return;
		
		try {
			helper.server.joinCourse(helper.userId, regKey);
		} catch (JoinCourseException e) {
			helper.statusMessage = Helper.escapeForHTML(e.getMessage());
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

}
