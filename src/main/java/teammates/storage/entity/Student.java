package teammates.storage.entity;

import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Extension;
import teammates.common.Common;

import com.google.gson.annotations.SerializedName;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Student is a persistent data class that holds information pertaining to a
 * student on Teammates.
 * This represents a student per course.
 * 
 */
@PersistenceCapable
public class Student {

	@SuppressWarnings("unused")
	private static Logger log = Common.getLogger();
	/**
	 * The student's Google ID
	 */
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private transient Long registrationKey = null;
	
	@Persistent
	@SerializedName("google_id")
	private String ID = null;

	@Persistent
	@SerializedName("email")
	private String email;

	@Persistent
	@SerializedName("coursename")
	private String courseID;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("name")
	private String name = null;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String comments = null;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("teamname")
	private String teamName = null;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private transient boolean courseArchived;

	// TODO: remove? not seem to be used
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("profilesummary")
	private String profileSummary;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	@SerializedName("profiledetail")
	private Text profileDetail = null;



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

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setID(String ID) {
		this.ID = (ID == null ? null : ID.trim());
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
		this.comments = (comments == null ? null : comments.trim());
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
		this.teamName = (teamName == null ? null : teamName.trim());
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

	public boolean isRegistered() {
		// Null or "" => unregistered
		return ID != null && !ID.isEmpty();
	}

	public static String getStringKeyForLongKey(long longKey) {
		return KeyFactory.createKeyString(Student.class.getSimpleName(),
				longKey);
	}
}
