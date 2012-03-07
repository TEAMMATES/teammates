package teammates.jdo;

import java.util.ArrayList;

/**
 * CourseDetailsForStudent is a data class that contains some information from
 * Student objects and some information from a Course object. It provides the
 * details of a course for a specific student.
 * 
 * @author Kalpit Jain
 * 
 */
public class CourseDetailsForStudent {
	private String courseID;
	private String courseName;
	private String coordinatorName;
	
	private String teamName;
	private String studentName;
	private String studentEmail;
	private ArrayList<String> teammateList;
	
	private String profileSummary;
	private String profileDetail;

	/**
	 * Constructs a CourseDetailsForStudent object.
	 * 
	 * @param courseID
	 * @param courseName
	 * @param coordinatorName
	 * @param teamName
	 * @param studentName
	 * @param studentEmail
	 * @param teammateList
	 */
	public CourseDetailsForStudent(String courseID, String courseName,
			String coordinatorName, String teamName, String studentName,
			String studentEmail, ArrayList<String> teammateList) {
		this.setCourseID(courseID);
		this.setCourseName(courseName);
		this.setCoordinatorName(coordinatorName);
		this.setTeamName(teamName);
		this.setStudentName(studentName);
		this.setStudentEmail(studentEmail);
		this.setTeammateList(teammateList);
	}
	
	/**
	 * Constructs a CourseDetailsForStudent object.
	 * 
	 * @param courseID
	 * @param courseName
	 * @param coordinatorName
	 * @param teamName
	 * @param studentName
	 * @param studentEmail
	 * @param teammateList
	 * @param profileSummary
	 * @param profileDetail
	 */
	public CourseDetailsForStudent(String courseID, String courseName,
			String coordinatorName, String teamName, String studentName,
			String studentEmail, ArrayList<String> teammateList, 
			String profileSummary, String profileDetail) {
		this.setCourseID(courseID);
		this.setCourseName(courseName);
		this.setCoordinatorName(coordinatorName);
		this.setTeamName(teamName);
		this.setStudentName(studentName);
		this.setStudentEmail(studentEmail);
		this.setTeammateList(teammateList);
		this.setProfileSummary(profileSummary);
		this.setProfileDetail(profileDetail);
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCoordinatorName(String coordinatorName) {
		this.coordinatorName = coordinatorName;
	}

	public String getCoordinatorName() {
		return coordinatorName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}

	public String getStudentEmail() {
		return studentEmail;
	}

	public void setTeammateList(ArrayList<String> teammateList) {
		this.teammateList = teammateList;
	}

	public ArrayList<String> getTeammateList() {
		return teammateList;
	}
	
	public void setProfileSummary(String profileSummary) {
		this.profileSummary = profileSummary;
	}

	public String getProfileSummary() {
		return profileSummary;
	}
	
	public void setProfileDetail(String profileDetail) {
		this.profileDetail = profileDetail;
	}

	public String getProfileDetail() {
		return profileDetail;
	}
}
