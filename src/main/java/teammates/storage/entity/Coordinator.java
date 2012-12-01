package teammates.storage.entity;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Coordinator is a persistent data class that holds information pertaining to a
 * instructor on Teammates. It is called Coordinator for legacy reasons. To be
 * changed to Instructor. 
 */
@PersistenceCapable
public class Coordinator {
	@PrimaryKey
	@Persistent
	private String googleID;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String name;

	@Persistent
	@Extension(vendorName="datanucleus", key="gae.unindexed", value="true")
	private String email;
	
	public Coordinator(String googleId, String name, String email) {
		this.googleID = googleId;
		this.name = name;
		this.email = email;
	}
	
	public void setGoogleID(String googleID) {
		this.googleID = googleID.trim();
	}

	public String getGoogleID() {
		return googleID;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public String getName() {
		return name;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public String getEmail() {
		return email;
	}
}
