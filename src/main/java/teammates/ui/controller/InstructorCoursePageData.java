package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

public class InstructorCoursePageData extends PageData {
	
	public InstructorCoursePageData(AccountAttributes account) {
		super(account);
	}
	
	public CourseAttributes newCourse;
	public List<CourseDetailsBundle> currentCourses;
	
	public String courseIdToShow;
	public String courseNameToShow;
	public String instructorListToShow;

}
