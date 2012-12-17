package teammates.storage.entity;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Account {
	@PrimaryKey
	@Persistent
	private String googleId;

	@Persistent
	private String name;
	
	@Persistent
	private boolean isInstructor = false;

	public Account(String googleId, String name, boolean isInstructor) {
		this.googleId = googleId;
		this.name = name;
		this.isInstructor = isInstructor;
	}

	public String getGoogleId() {
		return googleId;
	}
	
	public String getName() {
		return name;
	}

}
