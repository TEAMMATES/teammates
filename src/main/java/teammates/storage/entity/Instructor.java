package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Instructor {
	@PrimaryKey
	@Persistent
	private String id;
	
	@Persistent
	private String googleId;

	@Persistent
	private String courseId;
	
	@Persistent
	private String name;
	
	@Persistent
	private String email;
	
	public Instructor(String googleId, String courseId, String name, String email) {
		// '%' is not allowed for courseId, we can use it for generating unique key
		this.id = googleId + '%' + courseId;
		this.googleId = googleId;
		this.courseId = courseId;
		this.name = name;
		this.email = email;
	}
	
	public String getGoogleId() {
		return googleId;
	}
	
	public String getCourseId() {
		return courseId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getEmail() {
		return email;
	}
	
	//==========================================
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
}
