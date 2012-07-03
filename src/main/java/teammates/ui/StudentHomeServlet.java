package teammates.ui;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

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
