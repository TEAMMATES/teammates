package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.util.Constants;
import teammates.common.util.Url;

public class AdminAccountDetailsPageData extends PageData {
	
	public AccountAttributes accountInformation;
	public List<CourseDetailsBundle> instructorCourseList;
	public List<CourseAttributes> studentCourseList;

	public AdminAccountDetailsPageData(AccountAttributes account) {
		super(account);
	}
	
	public String getAdminDeleteInstructorFromCourseLink(String instructorId, String courseId){
		String link = Constants.ACTION_ADMIN_ACCOUNT_DELETE;
		link = Url.addParamToUrl(link,Constants.PARAM_INSTRUCTOR_ID,instructorId);
		link = Url.addParamToUrl(link,Constants.PARAM_COURSE_ID,courseId);
		return link;
	}
	
	public String getAdminDeleteStudentFromCourseLink(String studentId, String courseId){
		String link = Constants.ACTION_ADMIN_ACCOUNT_DELETE;
		link = Url.addParamToUrl(link,Constants.PARAM_STUDENT_ID,studentId);
		link = Url.addParamToUrl(link,Constants.PARAM_COURSE_ID,courseId);
		return link;
	}

}
