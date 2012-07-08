package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class StudentHomeServlet extends ActionServlet<StudentHomeHelper> {

	@Override
	protected StudentHomeHelper instantiateHelper() {
		return new StudentHomeHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, StudentHomeHelper helper){
		try{
			helper.courses = helper.server.getCourseDetailsListForStudent(helper.userId);
			sortCourses(helper.courses);
			for(CourseData course: helper.courses){
				sortEvaluationsByDeadline(course.evaluations);
			}
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
			helper.error = true;
		} catch (EntityDoesNotExistException e){
			helper.courses = new ArrayList<CourseData>();
			helper.statusMessage = Common.MESSAGE_STUDENT_FIRST_TIME;
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_HOME;
	}

}
