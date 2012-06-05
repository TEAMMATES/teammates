package teammates.jsp;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;

public class CoordHomeHelper extends Helper {
	public String coordID;
	
	public CourseData[] summary;
	
	public CoordHomeHelper(HttpServletRequest request){
		super(request);
		
		coordID = userID;
		
		HashMap<String, CourseData> courses = server.getCourseDetailsListForCoord(coordID);
		summary = courses.values().toArray(new CourseData[] {});
	}
	
	public static EvaluationData[] getEvaluationsForCourse(CourseData course){
		ArrayList<EvaluationData> evaluations = course.evaluations;
		EvaluationData[] evaluationsArr = evaluations.toArray(new EvaluationData[]{});
		return evaluationsArr;
	}
	
	public String getCourseViewLink(String courseID){
		return new CoordCourseAddHelper(request).getCourseViewLink(courseID);
	}
	
	public String getCourseEnrollLink(String courseID){
		return new CoordCourseAddHelper(request).getCourseEnrollLink(courseID);
	}
	
	public String getCourseDeleteLink(String courseID, String nextURL){
		return new CoordCourseAddHelper(request).getCourseDeleteLink(courseID, nextURL);
	}
}
