package teammates.jsp;

import teammates.api.Common;
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
	
	/**
	 * Returns the link to the course enroll link for specified courseID
	 * @param courseID
	 * @return
	 */
	public String getCourseEnrollLink(String courseID){
		String link = Common.JSP_COORD_COURSE_ENROLL;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}

	/**
	 * Returns the link to show course detail for specific courseID
	 * @param courseID
	 * @return
	 */
	public String getCourseViewLink(String courseID){
		String link = Common.JSP_COORD_COURSE_DETAILS;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID); 
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
	/**
	 * Returns the link to delete a course and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param nextURL
	 * @return
	 */
	public String getCourseDeleteLink(String courseID){
		String link = Common.JSP_COORD_COURSE_DELETE;
		link = addParam(link,Common.PARAM_COURSE_ID,courseID);
		link = addParam(link,Common.PARAM_NEXT_URL,Common.JSP_COORD_COURSE);
		if(isMasqueradeMode()){
			link = addParam(link,Common.PARAM_USER_ID,requestedUser);
		}
		return link;
	}
	
}
