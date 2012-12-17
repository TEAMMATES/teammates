package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Instructor {
	@PrimaryKey
	@Persistent
	private String googleId;

	@Persistent
	private String courseId;
	
	@Persistent
	private int accessLevelId;

	public Instructor(String googleId, String courseId, int accessLevelId) {
		this.googleId = googleId;
		this.courseId = courseId;
		this.accessLevelId = accessLevelId;
	}
	
	public String getGoogleId() {
		return googleId;
	}
	
	public String getCourseId() {
		return courseId;
	}
	
	public void setAccessLevel(int accessLevelId) {
		this.accessLevelId = accessLevelId;
	}
	
	public int getAccessLevel() {
		return this.accessLevelId;
	}
	
}
