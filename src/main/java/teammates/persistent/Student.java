package teammates.persistent;

import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.StudentData;

import com.google.gson.annotations.SerializedName;
import com.google.appengine.api.datastore.KeyFactory;
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

	@SuppressWarnings("unused")
	private static Logger log = Common.getLogger();
	/**
	 * The student's Google ID
	 */
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
	@SerializedName("name")
	private String name = null;

	@Persistent
	private String comments = null;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private transient Long registrationKey = null;

	@Persistent
	@SerializedName("teamname")
	private String teamName = null;

	@Persistent
	private transient boolean courseArchived;

	// TODO: remove? not seem to be used
	@Persistent
	@SerializedName("profilesummary")
	private String profileSummary;

	@Persistent
	@SerializedName("profiledetail")
	private Text profileDetail = null;

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
		this.setComments(comments);
		this.setCourseID(courseID);
		this.setTeamName(teamName);
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

	public Student(StudentData data) {
		setID(data.id);
		setEmail(data.email);
		setCourseID(data.course);
		setName(data.name);
		setComments(data.comments);
		setTeamName(data.team);
		// registration key not used
		setProfileDetail(data.profile);
	}

	public Student(String line, String courseId)
			throws InvalidParametersException {

		int TEAM_POS = 0;
		int NAME_POS = 1;
		int EMAIL_POS = 2;
		int COMMENT_POS = 3;

		if ((line == null) || (courseId == null)) {
			throw new InvalidParametersException(
					Common.ERRORCODE_NULL_PARAMETER,
					"Enrollment line cannot be null");
		}
		if ((line.equals("")) || (courseId.equals(""))) {
			throw new InvalidParametersException(Common.ERRORCODE_EMPTY_STRING,
					"Enrollment line cannot be empty");
		}

		String[] parts = line.replace("|", "\t").split("\t");

		if ((parts.length < 3) || (parts.length > 4)) {
			throw new InvalidParametersException(
					Common.ERRORCODE_INCORRECTLY_FORMATTED_STRING,
					"Enrollment line has too few or too many segments");
		}

		String paramCourseId = courseId.trim();
		Common.validateCourseId(paramCourseId);

		String paramTeam = parts[TEAM_POS].trim();
		Common.validateTeamName(paramTeam);

		String paramName = parts[NAME_POS].trim();
		Common.validateStudentName(paramName);

		String paramEmail = parts[EMAIL_POS].trim();
		Common.validateEmail(paramEmail);

		String paramComment = parts.length == 4 ? parts[COMMENT_POS].trim()
				: "";
		Common.validateComment(paramComment);

		setTeamName(paramTeam);
		setName(paramName);
		setEmail(paramEmail);
		setCourseID(paramCourseId);
		setComments(paramComment);

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
		return (ID == null || ID.isEmpty());
	}

	public static String getStringKeyForLongKey(long longKey) {
		return KeyFactory.createKeyString(Student.class.getSimpleName(),
				longKey);
	}
}
