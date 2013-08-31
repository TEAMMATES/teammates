package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;


public class InstructorCourseEnrollPageData extends PageData {
	
	public InstructorCourseEnrollPageData(AccountAttributes account) {
		super(account);
		enrollStudents = "";
	}

	public String courseId;
	
	//Note: Must not be null as it will be accessed by instructorCourseEnroll.jsp
	public String enrollStudents;

}
