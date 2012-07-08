package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.StudentData;

public class CoordCourseDetailsHelper extends Helper{
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
	 * Returns the link to send registration key to all students<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getCoordCourseRemindLink(){
		String link = Common.PAGE_COORD_COURSE_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,course.id);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to the student's detail page<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentDetailsLink(StudentData student){
		String link = Common.PAGE_COORD_COURSE_STUDENT_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to the student's detail edit page<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentEditLink(StudentData student){
		String link = Common.PAGE_COORD_COURSE_STUDENT_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link remind students to join the course.<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentRemindLink(StudentData student){
		String link = Common.PAGE_COORD_COURSE_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = processMasquerade(link);
		return link;
	}
	
	/**
	 * Returns the link to delete a student<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentDeleteLink(StudentData student){
		String link = Common.PAGE_COORD_COURSE_STUDENT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = processMasquerade(link);
		return link;
	}
}