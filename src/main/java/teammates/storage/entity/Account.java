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
	
	// Other information
	@Persistent
	private String email;
	
	@Persistent
	private String institute;

	//========================================
	
	public Account(String googleId, String name, boolean isInstructor,
					String email, String institute) {
		this.googleId = googleId;
		this.name = name;
		this.isInstructor = isInstructor;
		this.email = email;
		this.institute = institute;
	}

	//===========[Getter]=============================
	
	public String getGoogleId() {
		return googleId;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isInstructor() {
		return isInstructor;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getInstitute() {
		return institute;
	}

	//===========[Setter]=============================
	
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setIsInstructor(boolean accountIsInstructor) {
		this.isInstructor = accountIsInstructor;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setInstitute(String institute) {
		this.institute = institute;
	}
}
