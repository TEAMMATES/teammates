package teammates.jsp;


import javax.servlet.http.HttpServletRequest;

import java.util.*;
import teammates.api.*;
import teammates.datatransfer.*;

public class CoordCourseAddHelper extends Helper{
	APIServlet server; 
	public UserData loggedInUser;
	public String requestedUser;
	public String coordID;
	public String statusMessage = null;
	public boolean error;
	public String newCourseID;
	public String newCourseName;
	public CourseData[] summary;
	

	public CoordCourseAddHelper(HttpServletRequest request) {
		server = new APIServlet();
		loggedInUser = server.getLoggedInUser();
		requestedUser = request.getParameter(Common.PARAM_USER_ID);
		statusMessage = null;
		error = false;
		
		coordID = loggedInUser.id.toLowerCase();
		
		//if admin is trying to masquerade, modify user id
		if((loggedInUser.isAdmin())&&(requestedUser!=null)){
			coordID = requestedUser;
		}
		
		
		//if required, add course and update status accordingly
		if(isAddingCourse(request)){
			newCourseID = request.getParameter(Common.PARAM_COURSE_ID);
			newCourseName = request.getParameter(Common.PARAM_COURSE_NAME);
			try{
				server.createCourse(coordID, newCourseID, newCourseName);
				statusMessage = Common.MESSAGE_COURSE_ADDED;
			} catch (EntityAlreadyExistsException e){
				statusMessage = Common.MESSAGE_COURSE_EXISTS;
				error = true;
			} catch (InvalidParametersException e){
				statusMessage = e.getMessage();
				error = true;
			}
			// If adding was successful, do not display it again in the input boxes
			if(!error){
				newCourseID = null;
				newCourseName = null;
			}
		}
		

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
	
	
	private boolean isAddingCourse(HttpServletRequest request) {
		return request.getParameter(Common.COURSE_ID)!=null 
				&& request.getParameter(Common.PARAM_COURSE_NAME)!=null;
		
		//TODO: instead of the above, can we have this?
		//return request.getParameter(Common.PARAM_ACTION).equals(Common.ACTION_ADD_COURSE);
	}

	//TODO: enhance get*Link methods to cater for masquerade mode

	/**
	 * Returns the link to show course detail for specific courseID
	 * @param courseID
	 * @return
	 */
	public static String getCourseViewLink(String courseID){
		return "coordCourseView.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
	}
	
	/**
	 * Returns the link to delete a course and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param nextURL
	 * @return
	 */
	public static String getCourseDeleteLink(String courseID, String nextURL){
		return "coordCourseDelete.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_NEXT_URL+"="+convertForURL(nextURL);
	}

	/**
	 * Returns the link to the course enroll link for specified courseID
	 * @param courseID
	 * @return
	 */
	public static String getCourseEnrollLink(String courseID){
		return "coordCourseEnroll.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
	}
	
}
