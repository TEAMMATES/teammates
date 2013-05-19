package teammates.storage.entity;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents a unique user in the system. 
 */
@PersistenceCapable
public class Account {

	@PrimaryKey
	@Persistent
	private String googleId;

	@Persistent
	private String name;

	@Persistent
	private boolean isInstructor;

	@Persistent
	private String email;

	@Persistent
	private String institute;

	@Persistent
	private Date createdAt;

	/**
	 * Instantiates a new account. 
	 * 
	 * @param googleId
	 *            the Google ID of the user. 
	 * @param name
	 *            The name of the user.
	 * @param isInstructor
	 *            Does this account has instructor privileges?
	 * @param email
	 *            The official email of the user.
	 * @param institute
	 *            The university/school/institute e.g., "Abrons State University, Alaska" 
	 */
	public Account(String googleId, String name, boolean isInstructor,
			String email, String institute) {
		this.setGoogleId(googleId);
		this.setName(name);
		this.setIsInstructor(isInstructor);
		this.setEmail(email);
		this.setInstitute(institute);
		this.setCreatedAt(new Date());
	}

	public String getGoogleId() {
		return googleId;
	}

	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInstructor() {
		return isInstructor;
	}

	public void setIsInstructor(boolean accountIsInstructor) {
		this.isInstructor = accountIsInstructor;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInstitute() {
		return institute;
	}

	public void setInstitute(String institute) {
		this.institute = institute;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
