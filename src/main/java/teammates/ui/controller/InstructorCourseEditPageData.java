package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;


public class InstructorCourseEditPageData extends PageData {

	public InstructorCourseEditPageData(AccountAttributes account) {
		super(account);
	}
	
	public CourseAttributes course;
	public List<InstructorAttributes> instructorList;

}
