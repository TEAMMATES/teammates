package teammates.jdo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Student is a persistent data class that holds information pertaining to a
 * student on Teammates.
 * 
 * This represents a student per course.
 *  
 * @author Gerald GOH
 */
@PersistenceCapable
public class Student {
	
	/**
	 * The student's Google ID
	 */
	@Persistent
	@SerializedName("google_id")
	private String ID;

	@Persistent
	@SerializedName("email")
	private String email;
	
	@Persistent
	@SerializedName("coursename")
	private String courseID;
	
	@Persistent
	@SerializedName("name")
	private String name;
	
	@Persistent
	private String comments;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private transient Long registrationKey;
	
	@Persistent
	@SerializedName("teamname")
	private String teamName;
	
	@Persistent
	private transient boolean courseArchived;

	public Student(String email, String name, String comments, String courseID,
			String teamName) {
		this.email = email;
		this.name = name;
		this.ID = "";
		this.comments = comments;
		this.courseID = courseID;
		this.teamName = teamName;
		setCourseArchived(false);
	}

	public Student() {

	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * Set the student's Google ID
	 */
	public void setID(String ID) {
		this.ID = ID;
	}

	/**
	 * Retrieve the student's Google ID
	 */
	public String getID() {
		return ID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setRegistrationKey(Long registrationKey) {
		this.registrationKey = registrationKey;
	}

	public Long getRegistrationKey() {

		return registrationKey;
	}

	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}

	public String getCourseID() {
		return courseID;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setCourseArchived(boolean courseArchived) {
		this.courseArchived = courseArchived;
	}

	public boolean isCourseArchived() {
		return courseArchived;
	}
}
