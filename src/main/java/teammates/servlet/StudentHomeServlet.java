package teammates.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.api.Common;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CourseData;
import teammates.jsp.StudentHomeHelper;

@SuppressWarnings("serial")
public class StudentHomeServlet extends ActionServlet<StudentHomeHelper> {

	@Override
	protected StudentHomeHelper instantiateHelper() {
		return new StudentHomeHelper();
	}

	@Override
	protected boolean doAuthenticateUser(HttpServletRequest req,
			HttpServletResponse resp, StudentHomeHelper helper)
			throws IOException {
		if(!helper.user.isStudent && !helper.user.isAdmin){
			resp.sendRedirect(Common.JSP_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	@Override
	protected void doAction(HttpServletRequest req, StudentHomeHelper helper)
			throws EntityDoesNotExistException {
		try{
			helper.courses = helper.server.getCourseDetailsListForStudent(helper.userId);
			sortCourses(helper.courses);
			for(CourseData course: helper.courses){
				sortEvaluationsByDeadline(course.evaluations);
			}
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_HOME;
	}

}
