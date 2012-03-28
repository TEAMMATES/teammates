package teammates.jdo;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.appengine.api.datastore.Text;

/**
 * Student is a persistent data class that holds information pertaining to a
 * student on Teammates.
 * 
 * This represents a student per course.
 * 
 * @author Gerald GOH
 * 
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
	
	@Persistent
	@SerializedName("profilesummary")
	private String profileSummary;
	
	@Persistent
	@SerializedName("profiledetail")
	private Text profileDetail;

	/**
	 * Constructs a Student object.
	 * 
	 * @param email
	 * @param name
	 * @param comments
	 * @param courseID
	 * @param teamName
	 */
	public Student(String email, String name, String comments, String courseID,
			String teamName) {
		this.setEmail(email);
		this.setName(name);
		this.setID("");
		this.setComments(comments);
		this.setCourseID(courseID);
		this.setTeamName(teamName);
		this.setCourseArchived(false);
	}

	/**
	 * Constructs a Student object.
	 * 
	 * @param email
	 * @param name
	 * @param googleID
	 * @param comments
	 * @param courseID
	 * @param teamName
	 */
	public Student(String email, String name, String googleID, String comments,
			String courseID, String teamName) {
		this.setEmail(email);
		this.setName(name);
		this.setID(googleID);
		this.setComments(comments);
		this.setCourseID(courseID);
		this.setTeamName(teamName);
		this.setCourseArchived(false);
	}

	public Student() {

	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setID(String ID) {
		this.ID = ID.trim();
	}

	public String getID() {
		return ID;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}

	public void setComments(String comments) {
		this.comments = comments.trim();
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
		this.courseID = courseID.trim();
	}

	public String getCourseID() {
		return courseID;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName.trim();
	}

	public String getTeamName() {
		return teamName;
	}
	
	public void setProfileSummary(String profileSummary) {
		this.profileSummary = profileSummary.trim();
	}

	public String getProfileSummary() {
		return profileSummary;
	}
	
	public void setProfileDetail(Text profileDetail) {
		this.profileDetail = profileDetail;
	}

	public Text getProfileDetail() {
		return profileDetail;
	}

	public void setCourseArchived(boolean courseArchived) {
		this.courseArchived = courseArchived;
	}

	public boolean isCourseArchived() {
		return courseArchived;
	}
}
