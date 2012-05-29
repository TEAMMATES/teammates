package teammates.jdo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.appengine.api.datastore.Text;

/**
 * TeamProfile is a persistent data class that holds information pertaining to an
 * Team on Teammates.
 * 
 * @author Kalpit Jain
 * 
 */
@PersistenceCapable
public class TeamProfile {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	public Long id;

	@Persistent
	@SerializedName("courseid")
	private String courseID;
	
	//TODO: Why do we need this field? this can cause problems if we allow changing course name
	@Persistent
	@SerializedName("coursename")
	private String courseName;
	
	@Persistent
	@SerializedName("teamname")
	private String teamName;
	
	@Persistent
	@SerializedName("teamprofile")
	private Text teamProfile;
	
	/**
	 * Constructs an TeamProfile object.
	 * 
	 * @param courseID	
	 * @param courseName
	 * @param teamName
	 * @param teamProfile
	 */
	public TeamProfile(String courseID, String courseName, String teamName, Text teamProfile) {
		this.setCourseID(courseID);
		this.setCourseName(courseName);
		this.setTeamName(teamName);
		this.setTeamProfile(teamProfile);		
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
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public String getTeamName() {
		return teamName;
	}
	
	public void setTeamProfile(Text teamProfile) {
		this.teamProfile = teamProfile;
	}
	
	public Text getTeamProfile() {
		return teamProfile;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("courseID: " + courseID);
		sb.append("\ncourseName: " + courseName);
		sb.append("\nteamName: " + teamName);
		sb.append("\nteamProfile: " + teamProfile);
		return sb.toString();
	}
}