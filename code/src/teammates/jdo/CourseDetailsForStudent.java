package teammates.jdo;

import java.util.ArrayList;

/**
 * CourseDetailsForStudent is a data class that contains some information from
 * Student objects and some information from a Course object. It provides the details
 * of a course for a specific student.
 *  
 * @author Gerald GOH
 *
 */
public class CourseDetailsForStudent 
{
	private String courseID;
	private String courseName;
	private String coordinatorName;
	
	private String teamName;
	private String studentName;
	private String studentEmail;
	private ArrayList<String> teammateList;
	
	public CourseDetailsForStudent(String courseID, String courseName,String coordinatorName, String teamName, 
			String studentName, String studentEmail, ArrayList<String> teammateList) 
	{
		this.courseID = courseID;
		this.courseName = courseName;
		this.coordinatorName = coordinatorName;
		this.teamName = teamName;
		this.studentName = studentName;
		this.studentEmail = studentEmail;
		this.teammateList = teammateList;
	}

	public void setCourseID(String courseID) 
	{
		this.courseID = courseID;
	}
	
	public String getCourseID() 
	{
		return courseID;
	}
	
	public void setCourseName(String courseName) 
	{
		this.courseName = courseName;
	}
	
	public String getCourseName()
	{
		return courseName;
	}
	
	public void setCoordinatorName(String coordinatorName) 
	{
		this.coordinatorName = coordinatorName;
	}
	
	public String getCoordinatorName()
	{
		return coordinatorName;
	}
	public void setTeamName(String teamName)
	{
		this.teamName = teamName;
	}
	
	public String getTeamName() 
	{
		return teamName;
	}
	
	public void setStudentName(String studentName)
	{
		this.studentName = studentName;
	}
	
	public String getStudentName()
	{
		return studentName;
	}
	
	public void setStudentEmail(String studentEmail) 
	{
		this.studentEmail = studentEmail;
	}
	
	public String getStudentEmail() 
	{
		return studentEmail;
	}
	
	public void setTeammateList(ArrayList<String> teammateList) 
	{
		this.teammateList = teammateList;
	}
	
	public ArrayList<String> getTeammateList() 
	{
		return teammateList;
	}
	
	
	
	
}
