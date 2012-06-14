package teammates.jsp;

import java.util.List;

import teammates.api.Common;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.StudentData;

public class CoordCourseDetailsHelper extends Helper{
	// Specific parameters
	public CourseData course;
	public List<StudentData> students;
	
	/**
	 * Returns the status of the student, whether he has joined the course.
	 * This is based on googleID, if it's null or empty, then we assume he
	 * has not joined the course yet.
	 * @param student
	 * @return
	 */
	public String status(StudentData student){
		if(student.id == null || student.id.equals("")){
			return Common.STUDENT_STATUS_YET_TO_JOIN;
		} else {
			return Common.STUDENT_STATUS_JOINED;
		}
	}
	
	/**
	 * Returns the link to the student's detail page
	 * @param student
	 * @return
	 */
	public String getCourseStudentDetailsLink(StudentData student){
		String link = Common.JSP_COORD_COURSE_STUDENT_DETAILS;
		link = addParam(link,Common.PARAM_COURSE_ID,course.id);
		link = addParam(link,Common.PARAM_STUDENT_EMAIL,student.email);
		return link;
	}
	
	/**
	 * Returns the link to the student's detail edit page
	 * @param student
	 * @return
	 */
	public String getCourseStudentEditLink(StudentData student){
		String link = Common.JSP_COORD_COURSE_STUDENT_EDIT;
		link = addParam(link,Common.PARAM_COURSE_ID,course.id);
		link = addParam(link,Common.PARAM_STUDENT_EMAIL,student.email);
		return link;
	}
	
	/**
	 * Returns the link remind students to join the course, which is done
	 * through javascript.
	 * @param student
	 * @return
	 */
	public String getCourseStudentRemindLink(StudentData student){
		return "javascript: hideddrivetip(); sendRegistrationKey('"+course.id+"'," +
				"'"+student.email+"'," +
				"'"+escape(student.name)+"')";
	}
	
	/**
	 * Returns the link to delete a student
	 * @param student
	 * @return
	 */
	public String getCourseStudentDeleteLink(StudentData student){
		String link = Common.JSP_COORD_COURSE_STUDENT_DELETE;
		link = addParam(link,Common.PARAM_COURSE_ID,course.id);
		link = addParam(link,Common.PARAM_STUDENT_EMAIL,student.email);
		return link;
	}
}