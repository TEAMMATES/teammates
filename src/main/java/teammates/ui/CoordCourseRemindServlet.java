package teammates.ui;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Remind to join Course action
 */
public class CoordCourseRemindServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		try{
			if(studentEmail!=null){
				helper.server.sendRegistrationInviteToStudent(courseID, studentEmail);
				helper.statusMessage = Common.MESSAGE_COURSE_REMINDER_SENT_TO+studentEmail;
			} else {
				helper.server.sendRegistrationInviteForCourse(courseID);
				helper.statusMessage = Common.MESSAGE_COURSE_REMINDERS_SENT;
			}
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
		} finally {
			helper.redirectUrl = Common.PAGE_COORD_COURSE_DETAILS;
			helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
		}
	}

}
