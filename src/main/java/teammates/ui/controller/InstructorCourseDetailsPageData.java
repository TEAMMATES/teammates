package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class InstructorCourseDetailsPageData extends PageData {
	
	public InstructorCourseDetailsPageData(AccountAttributes account) {
		super(account);
	}

	public CourseDetailsBundle courseDetails;
	public List<StudentAttributes> students;
	public List<InstructorAttributes> instructors;
	
	/**
	 * Returns the link to send registration key to all students<br />
	 * This includes masquerade mode as well.
	 * @param courseID
	 * @return
	 */
	public String getInstructorCourseRemindLink(){
		String link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to the student's detail page<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentDetailsLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to the student's detail edit page<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentEditLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link remind students to join the course.<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentRemindLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
	/**
	 * Returns the link to delete a student<br />
	 * This includes masquerade mode as well.
	 * @param student
	 * @return
	 */
	public String getCourseStudentDeleteLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
}
