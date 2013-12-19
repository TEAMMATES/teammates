package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentListPageAction extends Action {
	
	private InstructorStudentListPageData data;
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		new GateKeeper().verifyInstructorPrivileges(account);
		
		data = new InstructorStudentListPageData(account);
		
		HashMap<String, CourseDetailsBundle> courses = logic.getCourseDetailsListForInstructor(account.googleId);
		data.courses = new ArrayList<CourseDetailsBundle>(courses.values());
		CourseDetailsBundle.sortDetailedCourses(data.courses);
		
		data.students = new ArrayList<StudentAttributes>();
		for(CourseDetailsBundle courseDetails: data.courses){
			List<StudentAttributes> courseStudents = logic.getStudentsForCourse(courseDetails.course.id);
			StudentAttributes.sortByNameAndThenByEmail(courseStudents);
			data.students.addAll(courseStudents);
		}
		
		if(data.courses.size() == 0){
			statusToUser.add(Const.StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS);
		}
		   
		statusToAdmin = "instructorStudentList Page Load<br>" + "Total Courses: " + data.courses.size()
				+ "<br>Total Students: " + data.students.size();
		
		ShowPageResult response = createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, data);
		return response;

	}

}
