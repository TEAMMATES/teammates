package teammates.jsp;


import javax.servlet.http.HttpServletRequest;

import java.util.*;
import teammates.api.*;
import teammates.datatransfer.*;

public class CoordCourseAddHelper extends Helper{
	public String coordID;
	
	public String newCourseID;
	public String newCourseName;
	public CourseData[] summary;

	public CoordCourseAddHelper(HttpServletRequest request) {
		super(request);

		if(error){
			newCourseID = request.getParameter(Common.PARAM_COURSE_ID);
			newCourseName = request.getParameter(Common.PARAM_COURSE_NAME);
		}
		
		coordID = userID.toLowerCase();
		
		//TODO: is to better if APIServlet returned an ArrayList?
		//load course details
		HashMap<String, CourseData> courses = server.getCourseListForCoord(coordID);
		summary = courses.values().toArray(new CourseData[]{});
		Arrays.sort(summary,new Comparator<CourseData>(){
			public int compare(CourseData obj1, CourseData obj2){
				return obj1.id.compareTo(obj2.id);
			}
		});
	}
	
	/**
	 * Returns the link to show course detail for specific courseID
	 * @param courseID
	 * @return
	 */
	public String getCourseViewLink(String courseID){
		String result = "coordCourseView.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID); 
		if(isMasqueradeMode()){
			result = addParam(result,Common.PARAM_USER_ID,requestedUser);
		}
		return result;
	}
	
	/**
	 * Returns the link to delete a course and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param nextURL
	 * @return
	 */
	public String getCourseDeleteLink(String courseID, String nextURL){
		String result = "courseDelete?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_NEXT_URL+"="+convertForURL(nextURL);
		if(isMasqueradeMode()){
			result = addParam(result,Common.PARAM_USER_ID,requestedUser);
		}
		return result;
	}

	/**
	 * Returns the link to the course enroll link for specified courseID
	 * @param courseID
	 * @return
	 */
	public String getCourseEnrollLink(String courseID){
		String result = "coordCourseEnroll.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
		if(isMasqueradeMode()){
			result = addParam(result,Common.PARAM_USER_ID,requestedUser);
		}
		return result;
	}
	
}
