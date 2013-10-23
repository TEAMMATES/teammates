package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;

public class InstructorCoursesPageData extends PageData {
	
	public InstructorCoursesPageData(AccountAttributes account) {
		super(account);
	}
	
	/** Used when adding a course. Null if not adding a course. */
	public CourseAttributes newCourse;
	
	public List<CourseDetailsBundle> currentCourses;
	
	/* Values to show in the form fields (in case reloading the page after a 
	 *   failed attempt to create a course)*/
	public String courseIdToShow;
	public String courseNameToShow;

}
