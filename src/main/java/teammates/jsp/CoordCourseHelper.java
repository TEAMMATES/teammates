package teammates.jsp;

import teammates.datatransfer.CourseData;

public class CoordCourseHelper extends Helper{
	// Specific parameters
	public String coordID;
	public String courseID;
	public String courseName;
	public CourseData[] summary;
	
	public CoordCourseHelper(Helper helper){
		super(helper);
	}
	
}
