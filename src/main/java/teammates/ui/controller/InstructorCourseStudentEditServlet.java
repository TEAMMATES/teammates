package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor course student edit page.
 */
public class InstructorCourseStudentEditServlet extends
		ActionServlet<InstructorCourseStudentEditHelper> {

	@Override
	protected InstructorCourseStudentEditHelper instantiateHelper() {
		return new InstructorCourseStudentEditHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			InstructorCourseStudentEditHelper helper)
			throws EntityDoesNotExistException {
		String action = Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_PAGE_LOAD;
		
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);

		boolean submit = (req.getParameter("submit") != null);
		String studentName = req.getParameter(Common.PARAM_STUDENT_NAME);
		String newEmail = req.getParameter(Common.PARAM_NEW_STUDENT_EMAIL);
		String teamName = req.getParameter(Common.PARAM_TEAM_NAME);
		String comments = req.getParameter(Common.PARAM_COMMENTS);

		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);

		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(studentEmail);
		
		if (submit) {
			helper.student.name = studentName;
			helper.student.email = newEmail;
			helper.student.team = teamName;
			helper.student.comments = comments;
			action = Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_EDIT_DETAILS;
			try {
				helper.server.editStudent(studentEmail, helper.student);
				helper.statusMessage = Common.MESSAGE_STUDENT_EDITED;
				helper.redirectUrl = helper.getInstructorCourseDetailsLink(courseID);
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
				return;
			}
		}
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }    
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET, action,
        		true, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_STUDENT_EDIT;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorCourseStudentEditHelper h = (InstructorCourseStudentEditHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_PAGE_LOAD){
			try {
				params = "instructorCourseStudentEdit Page Load<br>";
				params += "Editing Student <span class=\"bold\">" + (String)data.get(1) +"'s</span> details in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_EDIT_DETAILS){
			try {
				params = "Student <span class=\"bold\">" + h.student.name + "'s</span> details in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> edited.<br>";
				params += "New Email: " + h.student.email + "<br>New Team: " + h.student.team + "<br>Comments: " + h.student.comments;
			} catch (NullPointerException e){
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
}
