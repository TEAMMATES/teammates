package teammates.ui.controller;

import java.util.List;

import teammates.common.Common;
import teammates.common.Url;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

public class AdminAccountDetailsPageData extends PageData {
	
	public AccountAttributes accountInformation;
	public List<CourseDetailsBundle> instructorCourseList;
	public List<CourseAttributes> studentCourseList;

	public AdminAccountDetailsPageData(AccountAttributes account) {
		super(account);
	}
	
	public String getAdminDeleteInstructorFromCourseLink(String instructorId, String courseId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DELETE;
		link = Url.addParamToUrl(link,Common.PARAM_INSTRUCTOR_ID,instructorId);
		link = Url.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		return link;
	}
	
	public String getAdminDeleteStudentFromCourseLink(String studentId, String courseId){
		String link = Common.PAGE_ADMIN_ACCOUNT_DELETE;
		link = Url.addParamToUrl(link,Common.PARAM_STUDENT_ID,studentId);
		link = Url.addParamToUrl(link,Common.PARAM_COURSE_ID,courseId);
		return link;
	}

}
