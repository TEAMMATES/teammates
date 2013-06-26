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
	
	
	public String getInstructorCourseRemindLink(){
		String link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = addUserIdToUrl(link);
		return link;
	}
	
	
	public String getCourseStudentDetailsLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
	
	public String getCourseStudentEditLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
	
	public String getCourseStudentRemindLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_REMIND;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
	
	public String getCourseStudentDeleteLink(StudentAttributes student){
		String link = Common.PAGE_INSTRUCTOR_COURSE_STUDENT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseDetails.course.id);
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_EMAIL,student.email);
		link = addUserIdToUrl(link);
		return link;
	}
	
}
