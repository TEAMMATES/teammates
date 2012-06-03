package teammates.jsp;


import javax.servlet.http.HttpServletRequest;

import java.util.*;
import teammates.api.*;
import teammates.datatransfer.*;

public class CoordCourseAddHelper extends Helper{
	APIServlet server = new APIServlet();
	public UserData loggedInUser;
	public String requestedUser;
	public String coordID;
	public String statusMessage = null;
	public boolean error;
	public String newCourseID;
	public String newCourseName;
	public CourseData[] summary;
	

	public void init(HttpServletRequest request) {
		loggedInUser = server.getLoggedInUser();
		requestedUser = request.getParameter("user");
		statusMessage = null;
		error = false;
		
		coordID = loggedInUser.id.toLowerCase();
		
		//if admin is trying to masquerade, modify user id
		if((loggedInUser.isAdmin())&&(requestedUser!=null)){
			coordID = requestedUser;
		}
		
		
		//create course, update status accordingly
		String newCourseID = request.getParameter(Common.COURSE_ID);
		String newCourseName = request.getParameter(Common.COURSE_NAME);
		if(newCourseID!=null && newCourseName!=null){
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
		}
		
		// If adding was successful, do not display it again in the input boxes
		if(error==false){
			newCourseID = null;
			newCourseName = null;
		}
		
		//load course details
		HashMap<String, CourseData> courses = server.getCourseListForCoord(coordID);
		summary = courses.values().toArray(new CourseData[]{});
		Arrays.sort(summary,new Comparator<CourseData>(){
			public int compare(CourseData obj1, CourseData obj2){
				return obj1.id.compareTo(obj2.id);
			}
		});
	}
	
	
	public boolean isUserLoggedIn(){
		return server.isUserLoggedIn();
	}
	
	public String getLoginUrl(HttpServletRequest request){
		String queryString = request.getQueryString();
		String redirectUrl = request.getRequestURI()+(queryString!=null?"?"+queryString:"");
		return server.getLoginUrl(redirectUrl);
	}
	
	/**
	 * Returns the link to show course detail for specific courseID
	 * @param courseID
	 * @return
	 */
	public String getCourseViewLink(String courseID){
		return "coordCourseView.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
	}
	
	/**
	 * Returns the link to delete a course and redirects to the nextURL after deletion<br />
	 * The nextURL is usually used to refresh the page after deletion
	 * @param courseID
	 * @param nextURL
	 * @return
	 */
	public String getCourseDeleteLink(String courseID, String nextURL){
		return "coordCourseDelete.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID)+"&"+Common.PARAM_NEXT_URL+"="+convertForURL(nextURL);
	}

	/**
	 * Returns the link to the course enroll link for specified courseID
	 * @param courseID
	 * @return
	 */
	public String getCourseEnrollLink(String courseID){
		return "coordCourseEnroll.jsp?"+Common.PARAM_COURSE_ID+"="+convertForURL(courseID);
	}
	
}
