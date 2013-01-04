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
	
	public Instructor(String googleId, String courseId) {
		// '%' is not allowed for courseId, we can use it for generating unique key
		this.id = googleId + '%' + courseId;
		this.googleId = googleId;
		this.courseId = courseId;
	}
	
	public String getGoogleId() {
		return googleId;
	}
	
	public String getCourseId() {
		return courseId;
	}
	
}
