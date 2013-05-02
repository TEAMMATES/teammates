package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.CourseDetailsBundle;

public class AdminAccountDetailsHelper extends Helper{
	public AccountData accountInformation;
	public List<CourseDetailsBundle> instructorCourseList;
	public List<CourseData> studentCourseList;
	
	public String getInstructorCourseDeleteLink(String instructorId, String courseId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_INSTRUCTOR_ID,instructorId);
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		return link;
	}
	
	public String getStudentCourseDeleteLink(String studentId, String courseId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DELETE;
		link = Common.addParamToUrl(link,Common.PARAM_STUDENT_ID,studentId);
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		return link;
	}
}
