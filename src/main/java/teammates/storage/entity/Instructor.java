package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * An association class that represents the association Account
 * --> [is an instructor for] --> Course.
 */
@PersistenceCapable
public class Instructor {
	/**
	 * The primary key. Format: googleId%courseId e.g., adam%cs1101
	 */
	@PrimaryKey
	@Persistent
	private String id;

	/**
	 * The Google id of the instructor, used as the foreign key to locate the
	 * Account object.
	 */
	@Persistent
	private String googleId;

	/** The foreign key to locate the Course object. */
	@Persistent
	private String courseId;

	/** The instructor's name used for this course. */
	@Persistent
	private String name;

	/** The instructor's email used for this course. */
	@Persistent
	private String email;

	// TODO: this approach is going to be problematic when we require
	// instructors to 'join' first (because we won't have the GoogleId from the
	// beginning)
	/**
	 * @param instructorGoogleId
	 * @param courseId
	 * @param instructorName
	 * @param instructorEmail
	 */
	public Instructor(String instructorGoogleId, String courseId,
			String instructorName, String instructorEmail) {
		this.setGoogleId(instructorGoogleId);
		this.setCourseId(courseId);
		// setId should be called after setting GoogleId and CourseId
		this.setUniqueId(this.getGoogleId() + '%' + this.getCourseId());
		this.setName(instructorName);
		this.setEmail(instructorEmail);
	}

	/**
	 * @return The unique ID of the entity (format: googleId%courseId).
	 */
	public String getUniqueId() {
		return id;
	}

	/**
	 * @param uniqueId
	 *          The unique ID of the entity (format: googleId%courseId).
	 */
	public void setUniqueId(String uniqueId) {
		this.id = uniqueId;
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String instructorGoogleId) {
		this.googleId = instructorGoogleId;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String instructorName) {
		this.name = instructorName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String instructorEmail) {
		this.email = instructorEmail;
	}
}
